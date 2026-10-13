package com.loshilosdemaya.movil.vista.clientes;

import androidx.lifecycle.ViewModelProvider;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.modelo.Cliente;
import com.loshilosdemaya.movil.vista.comun.AdaptadorFiltrable;
import com.loshilosdemaya.movil.vista.comun.ListaBaseActivity;
import com.loshilosdemaya.movil.vista.comun.ListaViewModel;

/** Lista de clientes (GET api/clientes). */
public class ListaClientesActivity extends ListaBaseActivity<Cliente> {

    @Override
    protected String getTitulo() {
        return getString(R.string.modulo_clientes);
    }

    @Override
    protected String getTextoListaVacia() {
        return getString(R.string.sin_clientes);
    }

    @Override
    protected String getTextoBotonNuevo() {
        return getString(R.string.nuevo_cliente);
    }

    @Override
    protected ListaViewModel<Cliente> crearViewModel() {
        return new ViewModelProvider(this).get(ClienteViewModel.class);
    }

    @Override
    protected AdaptadorFiltrable<Cliente, ?> crearAdaptador(AdaptadorFiltrable.AlSeleccionar<Cliente> alSeleccionar) {
        return new ClienteAdapter(alSeleccionar);
    }

    @Override
    protected void abrirDetalle(Cliente cliente) {
        startActivity(DetalleClienteActivity.crearIntent(this, cliente.getIdCliente()));
    }

    @Override
    protected void abrirFormularioNuevo() {
        startActivity(FormularioClienteActivity.crearIntent(this, null));
    }
}
