package com.loshilosdemaya.movil.vista.pagos;

import androidx.lifecycle.ViewModelProvider;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.modelo.Pago;
import com.loshilosdemaya.movil.vista.comun.AdaptadorFiltrable;
import com.loshilosdemaya.movil.vista.comun.ListaBaseActivity;
import com.loshilosdemaya.movil.vista.comun.ListaViewModel;

/** Lista de pagos (GET api/pagos). */
public class ListaPagosActivity extends ListaBaseActivity<Pago> {

    @Override
    protected String getTitulo() {
        return getString(R.string.modulo_pagos);
    }

    @Override
    protected String getTextoListaVacia() {
        return getString(R.string.sin_pagos);
    }

    @Override
    protected String getTextoBotonNuevo() {
        return getString(R.string.registrar_pago);
    }

    @Override
    protected ListaViewModel<Pago> crearViewModel() {
        return new ViewModelProvider(this).get(PagoViewModel.class);
    }

    @Override
    protected AdaptadorFiltrable<Pago, ?> crearAdaptador(AdaptadorFiltrable.AlSeleccionar<Pago> alSeleccionar) {
        return new PagoAdapter(alSeleccionar);
    }

    @Override
    protected void abrirDetalle(Pago pago) {
        startActivity(DetallePagoActivity.crearIntent(this, pago.getIdPago()));
    }

    @Override
    protected void abrirFormularioNuevo() {
        startActivity(FormularioPagoActivity.crearIntentNuevo(this, null));
    }
}
