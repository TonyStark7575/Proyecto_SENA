package com.loshilosdemaya.movil.red;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Pruebas de la lectura de errores que devuelve la API. */
public class ErrorApiTest {

    @Test
    public void leer_errorGeneral() {
        ErrorApi error = ErrorApi.leer(401, "{\"error\":\"Credenciales incorrectas\"}");

        assertEquals(401, error.getCodigoHttp());
        assertEquals("Credenciales incorrectas", error.getMensaje());
        assertTrue(error.getErrores().isEmpty());
    }

    @Test
    public void leer_erroresPorCampo() {
        ErrorApi error = ErrorApi.leer(422,
                "{\"errores\":{\"email_c\":\"Ya existe un cliente con ese correo\",\"nombre_c\":\"El nombre es obligatorio\"}}");

        assertEquals("Ya existe un cliente con ese correo", error.getErrores().get("email_c"));
        assertEquals("El nombre es obligatorio", error.getErrores().get("nombre_c"));
        assertEquals(ErrorApi.MENSAJE_REVISAR_CAMPOS, error.getMensaje());
    }

    @Test
    public void leer_ignoraLosDetallesQueNoSonTexto() {
        // Así responde la API cuando falla una línea del pedido
        ErrorApi error = ErrorApi.leer(422,
                "{\"errores\":{\"lineas\":\"Revisa los productos del pedido\",\"detalle_lineas\":[{\"cantidad\":\"...\"}]}}");

        assertEquals(1, error.getErrores().size());
        assertEquals("Revisa los productos del pedido", error.getErrores().get("lineas"));
    }

    @Test
    public void leer_cuerpoQueNoEsJsonUsaMensajePorCodigo() {
        ErrorApi error = ErrorApi.leer(404, "<html>Not Found</html>");

        assertEquals("No se encontró lo que buscabas. Revisa la URL del servidor.", error.getMensaje());
    }

    @Test
    public void sinConexion_tieneCodigoCero() {
        ErrorApi error = ErrorApi.sinConexion();

        assertEquals(0, error.getCodigoHttp());
        assertEquals(ErrorApi.MENSAJE_SIN_CONEXION, error.getMensaje());
    }
}
