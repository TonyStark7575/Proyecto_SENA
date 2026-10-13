package com.loshilosdemaya.movil.red;

import com.loshilosdemaya.movil.modelo.Cliente;
import com.loshilosdemaya.movil.modelo.Credenciales;
import com.loshilosdemaya.movil.modelo.LineaPedido;
import com.loshilosdemaya.movil.modelo.Pago;
import com.loshilosdemaya.movil.modelo.Pedido;
import com.loshilosdemaya.movil.modelo.Producto;
import com.loshilosdemaya.movil.modelo.ResultadoEliminacion;
import com.loshilosdemaya.movil.modelo.ResumenDashboard;
import com.loshilosdemaya.movil.modelo.Sesion;
import com.loshilosdemaya.movil.modelo.Usuario;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Endpoints de la API PHP (public/index.php?ruta=api/...). Son los mismos que usa la app React:
 * la app móvil no necesita un backend propio.
 *
 * Retrofit genera la implementación de esta interfaz; cada método devuelve un Call que el
 * repositorio ejecuta en segundo plano. El id va como parámetro de consulta (&id=N).
 */
public interface ApiService {

    String RUTA = "index.php?ruta=api/";

    // ===== Autenticación =====
    @POST(RUTA + "login")
    Call<Sesion> iniciarSesion(@Body Credenciales credenciales);

    @POST(RUTA + "logout")
    Call<Void> cerrarSesion();

    @GET(RUTA + "sesion")
    Call<Sesion> consultarSesion();

    // ===== Dashboard =====
    @GET(RUTA + "dashboard")
    Call<ResumenDashboard> consultarResumen();

    // ===== Productos =====
    @GET(RUTA + "productos")
    Call<List<Producto>> consultarProductos();

    @GET(RUTA + "productos")
    Call<Producto> consultarProducto(@Query("id") int idProducto);

    @POST(RUTA + "productos")
    Call<Producto> registrarProducto(@Body Producto producto);

    @PUT(RUTA + "productos")
    Call<Producto> actualizarProducto(@Query("id") int idProducto, @Body Producto producto);

    @DELETE(RUTA + "productos")
    Call<Void> eliminarProducto(@Query("id") int idProducto);

    // La imagen viaja como multipart/form-data en el campo "imagen"
    @Multipart
    @POST(RUTA + "productos/imagen")
    Call<Producto> subirImagenProducto(@Query("id") int idProducto, @Part MultipartBody.Part imagen);

    @DELETE(RUTA + "productos/imagen")
    Call<Producto> quitarImagenProducto(@Query("id") int idProducto);

    // ===== Clientes =====
    @GET(RUTA + "clientes")
    Call<List<Cliente>> consultarClientes();

    @GET(RUTA + "clientes")
    Call<Cliente> consultarCliente(@Query("id") int idCliente);

    @POST(RUTA + "clientes")
    Call<Cliente> registrarCliente(@Body Cliente cliente);

    @PUT(RUTA + "clientes")
    Call<Cliente> actualizarCliente(@Query("id") int idCliente, @Body Cliente cliente);

    @DELETE(RUTA + "clientes")
    Call<Void> eliminarCliente(@Query("id") int idCliente);

    // ===== Pedidos =====
    @GET(RUTA + "pedidos")
    Call<List<Pedido>> consultarPedidos();

    @GET(RUTA + "pedidos")
    Call<Pedido> consultarPedido(@Query("id") int idPedido);

    @POST(RUTA + "pedidos")
    Call<Pedido> registrarPedido(@Body Pedido pedido);

    @PUT(RUTA + "pedidos")
    Call<Pedido> actualizarPedido(@Query("id") int idPedido, @Body Pedido pedido);

    @DELETE(RUTA + "pedidos")
    Call<Void> eliminarPedido(@Query("id") int idPedido);

    // id = pedido al que se agrega el producto
    @POST(RUTA + "pedidos/lineas")
    Call<Pedido> agregarLineaPedido(@Query("id") int idPedido, @Body LineaPedido linea);

    // id = línea que se quita; responde con el pedido y sus totales recalculados
    @DELETE(RUTA + "pedidos/lineas")
    Call<Pedido> quitarLineaPedido(@Query("id") int idLinea);

    // ===== Pagos =====
    @GET(RUTA + "pagos")
    Call<List<Pago>> consultarPagos();

    @GET(RUTA + "pagos")
    Call<Pago> consultarPago(@Query("id") int idPago);

    @POST(RUTA + "pagos")
    Call<Pago> registrarPago(@Body Pago pago);

    @PUT(RUTA + "pagos")
    Call<Pago> actualizarPago(@Query("id") int idPago, @Body Pago pago);

    @DELETE(RUTA + "pagos")
    Call<Void> eliminarPago(@Query("id") int idPago);

    // ===== Usuarios =====
    @GET(RUTA + "usuarios")
    Call<List<Usuario>> consultarUsuarios();

    @GET(RUTA + "usuarios")
    Call<Usuario> consultarUsuario(@Query("id") int idUsuario);

    @POST(RUTA + "usuarios")
    Call<Usuario> registrarUsuario(@Body Usuario usuario);

    @PUT(RUTA + "usuarios")
    Call<Usuario> actualizarUsuario(@Query("id") int idUsuario, @Body Usuario usuario);

    // 204 sin cuerpo si se borró; 200 con ResultadoEliminacion si se desactivó
    @DELETE(RUTA + "usuarios")
    Call<ResultadoEliminacion> eliminarUsuario(@Query("id") int idUsuario);

    // Cuerpo: { "activo": true | false }
    @PUT(RUTA + "usuarios/activo")
    Call<Usuario> cambiarActivoUsuario(@Query("id") int idUsuario, @Body Map<String, Boolean> activo);
}
