package com.loshilosdemaya.movil.vista.comun;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.loshilosdemaya.movil.util.Formato;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador base de las listas (RecyclerView) con buscador: guarda todos los elementos
 * y muestra solo los que coinciden con el texto buscado.
 * Cada módulo dice cómo se dibuja un elemento y en qué textos se busca.
 */
public abstract class AdaptadorFiltrable<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /** Qué hacer al tocar un elemento de la lista (normalmente abrir su detalle). */
    public interface AlSeleccionar<T> {
        void seleccionar(T elemento);
    }

    private final List<T> todos = new ArrayList<>();
    private final List<T> visibles = new ArrayList<>();
    private String busqueda = "";

    @Nullable
    protected final AlSeleccionar<T> alSeleccionar;

    protected AdaptadorFiltrable(@Nullable AlSeleccionar<T> alSeleccionar) {
        this.alSeleccionar = alSeleccionar;
    }

    public void actualizarLista(List<T> elementos) {
        todos.clear();
        if (elementos != null) {
            todos.addAll(elementos);
        }
        aplicarFiltro();
    }

    public void filtrar(String texto) {
        busqueda = texto == null ? "" : texto;
        aplicarFiltro();
    }

    // Las listas son cortas (un emprendimiento): redibujar todo es simple y suficiente
    @SuppressLint("NotifyDataSetChanged")
    private void aplicarFiltro() {
        visibles.clear();
        for (T elemento : todos) {
            if (Formato.coincide(busqueda, textosBusqueda(elemento))) {
                visibles.add(elemento);
            }
        }
        notifyDataSetChanged();
    }

    protected T obtener(int posicion) {
        return visibles.get(posicion);
    }

    @Override
    public int getItemCount() {
        return visibles.size();
    }

    public boolean hayDatosSinFiltrar() {
        return !todos.isEmpty();
    }

    /** Textos del elemento en los que busca el buscador (nombre, correo, estado...). */
    protected abstract String[] textosBusqueda(T elemento);
}
