package com.loshilosdemaya.movil.red;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.loshilosdemaya.movil.modelo.Cliente;
import com.loshilosdemaya.movil.modelo.Credenciales;
import com.loshilosdemaya.movil.modelo.LineaPedido;
import com.loshilosdemaya.movil.modelo.Pedido;
import com.loshilosdemaya.movil.modelo.Producto;
import com.loshilosdemaya.movil.modelo.ResultadoEliminacion;
import com.loshilosdemaya.movil.modelo.Sesion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;

/**
 * Pruebas de la capa de red contra un servidor falso (MockWebServer) que responde
 * como la API PHP. Verifican las rutas, el JSON enviado, la lectura de las respuestas
 * y que la cookie de sesión viaje en las peticiones siguientes.
 */
public class ApiServiceTest {

    private MockWebServer servidor;
    private ApiService api;

    @Before
    public void iniciarServidor() throws Exception {
        servidor = new MockWebServer();
        servidor.start();
        // Sin SharedPreferences: las cookies quedan solo en memoria
        api = ConexionApi.crearServicio(servidor.url("/ProyectoSENA/public/").toString(), new GestorCookies(null), false);
    }

    @After
    public void detenerServidor() throws Exception {
        servidor.shutdown();
    }

    private static MockResponse json(int codigo, String cuerpo) {
        return new MockResponse().setResponseCode(codigo)
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(cuerpo);
    }

    @Test
    public void iniciarSesion_enviaJsonYGuardaLaCookieParaLasSiguientesPeticiones() throws Exception {
        servidor.enqueue(json(200, "{\"id_usuario\":1,\"nombre\":\"Maya\",\"rol\":\"ADMINISTRADORA\"}")
                .addHeader("Set-Cookie", "PHPSESSID=abc123; path=/"));
        servidor.enqueue(json(200, "[]"));

        Response<Sesion> respuesta = api.iniciarSesion(new Credenciales("admin@correo.com", "secreta123")).execute();

        assertTrue(respuesta.isSuccessful());
        assertNotNull(respuesta.body());
        assertEquals("Maya", respuesta.body().getNombre());
        assertTrue(respuesta.body().esAdministradora());

        RecordedRequest login = servidor.takeRequest();
        assertEquals("POST", login.getMethod());
        assertEquals("/ProyectoSENA/public/index.php?ruta=api/login", login.getPath());
        // La API rechaza (415) cualquier cuerpo que no sea application/json
        assertTrue(login.getHeader("Content-Type").startsWith("application/json"));
        assertTrue(login.getBody().readUtf8().contains("\"email\":\"admin@correo.com\""));

        api.consultarClientes().execute();
        RecordedRequest siguiente = servidor.takeRequest();
        assertEquals("PHPSESSID=abc123", siguiente.getHeader("Cookie"));
    }

    @Test
    public void consultarProductos_leeLosDecimalesQueLleganComoTexto() throws Exception {
        servidor.enqueue(json(200,
                "[{\"id_pro\":3,\"nombre_p\":\"Gorro\",\"precio\":\"25000.00\",\"descripcion\":null,\"imagen\":\"1_a.jpg\"}]"));

        List<Producto> productos = api.consultarProductos().execute().body();

        assertNotNull(productos);
        assertEquals(1, productos.size());
        assertEquals(Integer.valueOf(3), productos.get(0).getIdPro());
        assertEquals(new BigDecimal("25000.00"), productos.get(0).getPrecio());
        assertEquals("1_a.jpg", productos.get(0).getImagen());
    }

    @Test
    public void actualizarCliente_enviaElIdEnLaUrlYSoloLosCamposDelFormulario() throws Exception {
        servidor.enqueue(json(200, "{\"id_cliente\":5,\"nombre_c\":\"Ana\",\"email_c\":\"ana@correo.com\"}"));

        api.actualizarCliente(5, new Cliente("Ana", "ana@correo.com", "300")).execute();

        RecordedRequest peticion = servidor.takeRequest();
        assertEquals("PUT", peticion.getMethod());
        assertEquals("/ProyectoSENA/public/index.php?ruta=api/clientes&id=5", peticion.getPath());
        String cuerpo = peticion.getBody().readUtf8();
        assertTrue(cuerpo.contains("\"nombre_c\":\"Ana\""));
        // Gson no envía los campos en null (id, fechas, historial...)
        assertFalse(cuerpo.contains("id_cliente"));
    }

    @Test
    public void registrarPedido_enviaLasLineasConProductoYCantidad() throws Exception {
        servidor.enqueue(json(201, "{\"id_pedido\":9,\"estado\":\"Pendiente\",\"total\":\"50000.00\",\"saldo\":\"50000.00\"}"));

        Pedido creado = api.registrarPedido(new Pedido(4, "Pendiente",
                Collections.singletonList(new LineaPedido(3, 2)))).execute().body();

        assertNotNull(creado);
        assertEquals(Integer.valueOf(9), creado.getIdPedido());
        assertTrue(creado.admitePagos());

        String cuerpo = servidor.takeRequest().getBody().readUtf8();
        assertTrue(cuerpo.contains("\"id_cliente\":4"));
        assertTrue(cuerpo.contains("\"lineas\":[{\"id_pro\":3,\"cantidad\":2}]"));
    }

    @Test
    public void respuestaConError_seLeeConErrorApi() throws Exception {
        servidor.enqueue(json(422, "{\"errores\":{\"precio\":\"El precio debe ser un número mayor que 0 y menor que 100.000.000\"}}"));

        Response<Producto> respuesta = api.registrarProducto(new Producto("Gorro", BigDecimal.ZERO, "")).execute();

        assertFalse(respuesta.isSuccessful());
        ErrorApi error = ErrorApi.leer(respuesta.code(), respuesta.errorBody().string());
        assertTrue(error.getErrores().containsKey("precio"));
    }

    @Test
    public void eliminarUsuario_204SinCuerpoY200ConDesactivacion() throws Exception {
        servidor.enqueue(new MockResponse().setResponseCode(204));
        servidor.enqueue(json(200, "{\"resultado\":\"desactivado\",\"mensaje\":\"Se desactivó\",\"usuario\":{\"id_usuario\":2,\"activo\":0}}"));

        Response<ResultadoEliminacion> borrado = api.eliminarUsuario(2).execute();
        assertTrue(borrado.isSuccessful());
        assertNull(borrado.body());

        ResultadoEliminacion desactivado = api.eliminarUsuario(2).execute().body();
        assertNotNull(desactivado);
        assertTrue(desactivado.fueDesactivado());
        assertFalse(desactivado.getUsuario().estaActivo());
    }

    @Test
    public void todasLasPeticionesLlevanXRequestedWith() throws Exception {
        servidor.enqueue(json(200, "[]"));

        api.consultarPagos().execute();

        // La API exige esta cabecera para aceptar la imagen de un producto (multipart)
        assertEquals("XMLHttpRequest", servidor.takeRequest().getHeader("X-Requested-With"));
    }

    @Test
    public void normalizarUrl_agregaLaBarraFinalYRechazaTextoInvalido() {
        assertEquals("http://10.0.2.2/ProyectoSENA/public/", ConexionApi.normalizarUrl(" http://10.0.2.2/ProyectoSENA/public "));
        assertNull(ConexionApi.normalizarUrl("10.0.2.2/ProyectoSENA"));
        assertNull(ConexionApi.normalizarUrl(null));
    }
}
