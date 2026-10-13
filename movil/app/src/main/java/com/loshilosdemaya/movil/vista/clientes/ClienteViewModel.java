package com.loshilosdemaya.movil.vista.clientes;

import androidx.lifecycle.LiveData;

import com.loshilosdemaya.movil.modelo.Cliente;
import com.loshilosdemaya.movil.repositorio.ClienteRepositorio;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.vista.comun.ListaViewModel;

import java.util.List;

/** ViewModel del módulo de clientes. */
public class ClienteViewModel extends ListaViewModel<Cliente> {

    private final ClienteRepositorio clienteRepositorio = new ClienteRepositorio();

    @Override
    protected LiveData<Resultado<List<Cliente>>> consultarLista() {
        return clienteRepositorio.consultarClientes();
    }

    public LiveData<Resultado<Cliente>> consultarCliente(int idCliente) {
        return clienteRepositorio.consultarCliente(idCliente);
    }

    public LiveData<Resultado<Cliente>> registrarCliente(String nombre, String correo, String telefono) {
        return clienteRepositorio.registrarCliente(new Cliente(nombre, correo, telefono));
    }

    public LiveData<Resultado<Cliente>> actualizarCliente(int idCliente, String nombre, String correo, String telefono) {
        return clienteRepositorio.actualizarCliente(idCliente, new Cliente(nombre, correo, telefono));
    }

    public LiveData<Resultado<Void>> eliminarCliente(int idCliente) {
        return clienteRepositorio.eliminarCliente(idCliente);
    }
}
