package com.loshilosdemaya.movil.repositorio;

import androidx.lifecycle.LiveData;

import com.loshilosdemaya.movil.modelo.Pago;

import java.math.BigDecimal;
import java.util.List;

/** Operaciones del módulo de pagos contra la API. */
public class PagoRepositorio extends RepositorioBase {

    public LiveData<Resultado<List<Pago>>> consultarPagos() {
        return ejecutar(api().consultarPagos());
    }

    public LiveData<Resultado<Pago>> consultarPago(int idPago) {
        return ejecutar(api().consultarPago(idPago));
    }

    public LiveData<Resultado<Pago>> registrarPago(int idPedido, BigDecimal monto, String metodo) {
        return ejecutar(api().registrarPago(new Pago(idPedido, monto, metodo)));
    }

    // El pedido de un pago no se cambia: solo monto y método
    public LiveData<Resultado<Pago>> actualizarPago(int idPago, BigDecimal monto, String metodo) {
        return ejecutar(api().actualizarPago(idPago, new Pago(null, monto, metodo)));
    }

    public LiveData<Resultado<Void>> eliminarPago(int idPago) {
        return ejecutar(api().eliminarPago(idPago));
    }
}
