package com.loshilosdemaya.movil.repositorio;

import androidx.lifecycle.LiveData;

import com.loshilosdemaya.movil.modelo.Cliente;

import java.util.List;

/** Operaciones del módulo de clientes contra la API. */
public class ClienteRepositorio extends RepositorioBase {

    public LiveData<Resultado<List<Cliente>>> consultarClientes() {
        return ejecutar(api().consultarClientes());
    }

    // Incluye quién lo registró y su historial de pedidos y pagos
    public LiveData<Resultado<Cliente>> consultarCliente(int idCliente) {
        return ejecutar(api().consultarCliente(idCliente));
    }

    public LiveData<Resultado<Cliente>> registrarCliente(Cliente cliente) {
        return ejecutar(api().registrarCliente(cliente));
    }

    public LiveData<Resultado<Cliente>> actualizarCliente(int idCliente, Cliente cliente) {
        return ejecutar(api().actualizarCliente(idCliente, cliente));
    }

    public LiveData<Resultado<Void>> eliminarCliente(int idCliente) {
        return ejecutar(api().eliminarCliente(idCliente));
    }
}
