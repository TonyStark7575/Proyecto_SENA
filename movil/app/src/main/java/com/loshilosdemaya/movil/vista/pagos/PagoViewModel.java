package com.loshilosdemaya.movil.vista.pagos;

import androidx.lifecycle.LiveData;

import com.loshilosdemaya.movil.modelo.Pago;
import com.loshilosdemaya.movil.modelo.Pedido;
import com.loshilosdemaya.movil.repositorio.PagoRepositorio;
import com.loshilosdemaya.movil.repositorio.PedidoRepositorio;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.vista.comun.ListaViewModel;

import java.math.BigDecimal;
import java.util.List;

/** ViewModel del módulo de pagos. También consulta los pedidos para elegir a cuál se abona. */
public class PagoViewModel extends ListaViewModel<Pago> {

    private final PagoRepositorio pagoRepositorio = new PagoRepositorio();
    private final PedidoRepositorio pedidoRepositorio = new PedidoRepositorio();

    @Override
    protected LiveData<Resultado<List<Pago>>> consultarLista() {
        return pagoRepositorio.consultarPagos();
    }

    public LiveData<Resultado<Pago>> consultarPago(int idPago) {
        return pagoRepositorio.consultarPago(idPago);
    }

    public LiveData<Resultado<List<Pedido>>> consultarPedidos() {
        return pedidoRepositorio.consultarPedidos();
    }

    public LiveData<Resultado<Pago>> registrarPago(int idPedido, BigDecimal monto, String metodo) {
        return pagoRepositorio.registrarPago(idPedido, monto, metodo);
    }

    public LiveData<Resultado<Pago>> actualizarPago(int idPago, BigDecimal monto, String metodo) {
        return pagoRepositorio.actualizarPago(idPago, monto, metodo);
    }

    public LiveData<Resultado<Void>> eliminarPago(int idPago) {
        return pagoRepositorio.eliminarPago(idPago);
    }
}
