package com.loshilosdemaya.movil.vista.productos;

import androidx.lifecycle.ViewModelProvider;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.modelo.Producto;
import com.loshilosdemaya.movil.vista.comun.AdaptadorFiltrable;
import com.loshilosdemaya.movil.vista.comun.ListaBaseActivity;
import com.loshilosdemaya.movil.vista.comun.ListaViewModel;

/** Catálogo de productos (GET api/productos). */
public class ListaProductosActivity extends ListaBaseActivity<Producto> {

    @Override
    protected String getTitulo() {
        return getString(R.string.modulo_productos);
    }

    @Override
    protected String getTextoListaVacia() {
        return getString(R.string.sin_productos);
    }

    @Override
    protected String getTextoBotonNuevo() {
        return getString(R.string.nuevo_producto);
    }

    @Override
    protected ListaViewModel<Producto> crearViewModel() {
        return new ViewModelProvider(this).get(ProductoViewModel.class);
    }

    @Override
    protected AdaptadorFiltrable<Producto, ?> crearAdaptador(AdaptadorFiltrable.AlSeleccionar<Producto> alSeleccionar) {
        return new ProductoAdapter(alSeleccionar);
    }

    @Override
    protected void abrirDetalle(Producto producto) {
        startActivity(DetalleProductoActivity.crearIntent(this, producto.getIdPro()));
    }

    @Override
    protected void abrirFormularioNuevo() {
        startActivity(FormularioProductoActivity.crearIntent(this, null));
    }
}
