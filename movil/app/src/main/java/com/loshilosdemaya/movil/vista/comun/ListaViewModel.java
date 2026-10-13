package com.loshilosdemaya.movil.vista.comun;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.loshilosdemaya.movil.repositorio.Resultado;

import java.util.List;

/**
 * ViewModel base de los módulos con lista (patrón MVVM). Guarda la lista mientras la pantalla
 * existe (sobrevive a girar el celular) y la vuelve a pedir a la API con recargarLista().
 */
public abstract class ListaViewModel<T> extends ViewModel {

    private final MediatorLiveData<Resultado<List<T>>> lista = new MediatorLiveData<>();
    private LiveData<Resultado<List<T>>> consultaActual;

    public LiveData<Resultado<List<T>>> getLista() {
        return lista;
    }

    public void recargarLista() {
        // Se deja de escuchar la consulta anterior para que una respuesta vieja no pise a la nueva
        if (consultaActual != null) {
            lista.removeSource(consultaActual);
        }
        consultaActual = consultarLista();
        lista.addSource(consultaActual, lista::setValue);
    }

    /** Cada módulo dice de dónde sale su lista (su repositorio). */
    protected abstract LiveData<Resultado<List<T>>> consultarLista();
}
