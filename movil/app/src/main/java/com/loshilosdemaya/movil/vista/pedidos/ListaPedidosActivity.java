package com.loshilosdemaya.movil.vista.pedidos;

import androidx.lifecycle.ViewModelProvider;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.modelo.Pedido;
import com.loshilosdemaya.movil.vista.comun.AdaptadorFiltrable;
import com.loshilosdemaya.movil.vista.comun.ListaBaseActivity;
import com.loshilosdemaya.movil.vista.comun.ListaViewModel;

/** Lista de pedidos (GET api/pedidos). Se puede buscar por número, cliente o estado. */
public class ListaPedidosActivity extends ListaBaseActivity<Pedido> {

    @Override
    protected String getTitulo() {
        return getString(R.string.modulo_pedidos);
    }

    @Override
    protected String getTextoListaVacia() {
        return getString(R.string.sin_pedidos);
    }

    @Override
    protected String getTextoBotonNuevo() {
        return getString(R.string.nuevo_pedido);
    }

    @Override
    protected ListaViewModel<Pedido> crearViewModel() {
        return new ViewModelProvider(this).get(PedidoViewModel.class);
    }

    @Override
    protected AdaptadorFiltrable<Pedido, ?> crearAdaptador(AdaptadorFiltrable.AlSeleccionar<Pedido> alSeleccionar) {
        return new PedidoAdapter(alSeleccionar);
    }

    @Override
    protected void abrirDetalle(Pedido pedido) {
        startActivity(DetallePedidoActivity.crearIntent(this, pedido.getIdPedido()));
    }

    @Override
    protected void abrirFormularioNuevo() {
        startActivity(FormularioPedidoActivity.crearIntent(this, null));
    }
}
