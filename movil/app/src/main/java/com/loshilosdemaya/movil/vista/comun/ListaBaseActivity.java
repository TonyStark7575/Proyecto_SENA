package com.loshilosdemaya.movil.vista.comun;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityListaBinding;

import java.util.List;

/**
 * Pantalla base de las listas de los módulos (productos, clientes, pedidos, pagos, usuarios).
 * Resuelve lo que todas comparten: barra con buscador, deslizar hacia abajo para recargar,
 * mensaje de lista vacía y botón flotante para crear. Cada módulo solo indica sus datos.
 */
public abstract class ListaBaseActivity<T> extends BaseActivity {

    protected ActivityListaBinding binding;
    protected AdaptadorFiltrable<T, ?> adaptador;
    protected ListaViewModel<T> viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarToolbar(binding.toolbar, getTitulo(), true);

        viewModel = crearViewModel();
        adaptador = crearAdaptador(this::abrirDetalle);

        binding.lista.setLayoutManager(new LinearLayoutManager(this));
        binding.lista.setAdapter(adaptador);

        binding.recargar.setColorSchemeResources(R.color.primario);
        binding.recargar.setOnRefreshListener(viewModel::recargarLista);

        binding.botonNuevo.setOnClickListener(v -> abrirFormularioNuevo());
        binding.botonNuevo.setContentDescription(getTextoBotonNuevo());

        viewModel.getLista().observe(this, resultado -> {
            binding.recargar.setRefreshing(resultado.estaCargando() && adaptador.hayDatosSinFiltrar());
            binding.cargando.setVisibility(resultado.estaCargando() && !adaptador.hayDatosSinFiltrar() ? View.VISIBLE : View.GONE);

            if (resultado.esExito()) {
                mostrarDatos(resultado.getDatos());
            } else if (resultado.esError()) {
                manejarError(resultado);
            }
        });
    }

    // Se recarga cada vez que la pantalla vuelve a verse (por ejemplo al regresar de crear o editar)
    @Override
    protected void onResume() {
        super.onResume();
        viewModel.recargarLista();
    }

    private void mostrarDatos(List<T> datos) {
        adaptador.actualizarLista(datos);
        actualizarMensajeVacio();
    }

    private void actualizarMensajeVacio() {
        boolean vacia = adaptador.getItemCount() == 0;
        binding.mensajeVacio.setVisibility(vacia ? View.VISIBLE : View.GONE);
        binding.mensajeVacio.setText(adaptador.hayDatosSinFiltrar()
                ? getString(R.string.lista_sin_coincidencias)
                : getTextoListaVacia());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);

        SearchView buscador = (SearchView) menu.findItem(R.id.accion_buscar).getActionView();
        if (buscador != null) {
            buscador.setQueryHint(getString(R.string.buscar));
            buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String texto) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String texto) {
                    adaptador.filtrar(texto);
                    actualizarMensajeVacio();
                    return true;
                }
            });
        }
        return true;
    }

    // ===== Lo que cada módulo define =====

    protected abstract String getTitulo();

    protected abstract String getTextoListaVacia();

    protected abstract String getTextoBotonNuevo();

    protected abstract ListaViewModel<T> crearViewModel();

    protected abstract AdaptadorFiltrable<T, ?> crearAdaptador(AdaptadorFiltrable.AlSeleccionar<T> alSeleccionar);

    protected abstract void abrirDetalle(T elemento);

    protected abstract void abrirFormularioNuevo();
}
