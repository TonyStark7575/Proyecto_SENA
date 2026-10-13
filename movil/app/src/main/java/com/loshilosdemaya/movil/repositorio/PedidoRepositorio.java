package com.loshilosdemaya.movil.repositorio;

import androidx.lifecycle.LiveData;

import com.loshilosdemaya.movil.modelo.LineaPedido;
import com.loshilosdemaya.movil.modelo.Pedido;

import java.util.List;

/** Operaciones del módulo de pedidos contra la API. */
public class PedidoRepositorio extends RepositorioBase {

    public LiveData<Resultado<List<Pedido>>> consultarPedidos() {
        return ejecutar(api().consultarPedidos());
    }

    // Incluye las líneas (productos), los pagos y quién lo registró
    public LiveData<Resultado<Pedido>> consultarPedido(int idPedido) {
        return ejecutar(api().consultarPedido(idPedido));
    }

    // El pedido se crea con todos sus productos de una vez; la API lo hace en una transacción
    public LiveData<Resultado<Pedido>> registrarPedido(int idCliente, String estado, List<LineaPedido> lineas) {
        return ejecutar(api().registrarPedido(new Pedido(idCliente, estado, lineas)));
    }

    // Solo cambia cliente y estado
    public LiveData<Resultado<Pedido>> actualizarPedido(int idPedido, int idCliente, String estado) {
        return ejecutar(api().actualizarPedido(idPedido, new Pedido(idCliente, estado, null)));
    }

    public LiveData<Resultado<Void>> eliminarPedido(int idPedido) {
        return ejecutar(api().eliminarPedido(idPedido));
    }

    public LiveData<Resultado<Pedido>> agregarLinea(int idPedido, int idProducto, int cantidad) {
        return ejecutar(api().agregarLineaPedido(idPedido, new LineaPedido(idProducto, cantidad)));
    }

    public LiveData<Resultado<Pedido>> quitarLinea(int idLinea) {
        return ejecutar(api().quitarLineaPedido(idLinea));
    }
}
