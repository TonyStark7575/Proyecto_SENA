package com.loshilosdemaya.movil.vista.pedidos;

import androidx.lifecycle.LiveData;

import com.loshilosdemaya.movil.modelo.Cliente;
import com.loshilosdemaya.movil.modelo.Pedido;
import com.loshilosdemaya.movil.modelo.Producto;
import com.loshilosdemaya.movil.repositorio.ClienteRepositorio;
import com.loshilosdemaya.movil.repositorio.PedidoRepositorio;
import com.loshilosdemaya.movil.repositorio.ProductoRepositorio;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.util.CarritoPedido;
import com.loshilosdemaya.movil.vista.comun.ListaViewModel;

import java.util.List;

/**
 * ViewModel del módulo de pedidos. Además de los pedidos, consulta clientes y productos
 * para los menús del formulario, y guarda el carrito para no perderlo al girar el celular.
 */
public class PedidoViewModel extends ListaViewModel<Pedido> {

    private final PedidoRepositorio pedidoRepositorio = new PedidoRepositorio();
    private final ClienteRepositorio clienteRepositorio = new ClienteRepositorio();
    private final ProductoRepositorio productoRepositorio = new ProductoRepositorio();

    private final CarritoPedido carrito = new CarritoPedido();
    private Integer idClienteSeleccionado;

    @Override
    protected LiveData<Resultado<List<Pedido>>> consultarLista() {
        return pedidoRepositorio.consultarPedidos();
    }

    public LiveData<Resultado<Pedido>> consultarPedido(int idPedido) {
        return pedidoRepositorio.consultarPedido(idPedido);
    }

    public LiveData<Resultado<List<Cliente>>> consultarClientes() {
        return clienteRepositorio.consultarClientes();
    }

    public LiveData<Resultado<List<Producto>>> consultarProductos() {
        return productoRepositorio.consultarProductos();
    }

    // Pedido nuevo: se envía con todos los productos del carrito
    public LiveData<Resultado<Pedido>> registrarPedido(String estado) {
        return pedidoRepositorio.registrarPedido(idClienteSeleccionado, estado, carrito.convertirAPeticion());
    }

    public LiveData<Resultado<Pedido>> actualizarPedido(int idPedido, String estado) {
        return pedidoRepositorio.actualizarPedido(idPedido, idClienteSeleccionado, estado);
    }

    public LiveData<Resultado<Void>> eliminarPedido(int idPedido) {
        return pedidoRepositorio.eliminarPedido(idPedido);
    }

    public LiveData<Resultado<Pedido>> agregarLinea(int idPedido, int idProducto, int cantidad) {
        return pedidoRepositorio.agregarLinea(idPedido, idProducto, cantidad);
    }

    public LiveData<Resultado<Pedido>> quitarLinea(int idLinea) {
        return pedidoRepositorio.quitarLinea(idLinea);
    }

    public CarritoPedido getCarrito() {
        return carrito;
    }

    public Integer getIdClienteSeleccionado() {
        return idClienteSeleccionado;
    }

    public void setIdClienteSeleccionado(Integer idCliente) {
        idClienteSeleccionado = idCliente;
    }
}
