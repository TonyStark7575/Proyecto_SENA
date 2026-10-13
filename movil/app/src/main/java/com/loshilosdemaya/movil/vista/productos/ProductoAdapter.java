package com.loshilosdemaya.movil.vista.productos;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loshilosdemaya.movil.databinding.ItemProductoBinding;
import com.loshilosdemaya.movil.modelo.Producto;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.vista.comun.AdaptadorFiltrable;
import com.loshilosdemaya.movil.vista.comun.UtilVista;

/** Dibuja cada producto del catálogo: imagen, nombre, descripción y precio. */
public class ProductoAdapter extends AdaptadorFiltrable<Producto, ProductoAdapter.ProductoViewHolder> {

    public ProductoAdapter(AlSeleccionar<Producto> alSeleccionar) {
        super(alSeleccionar);
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        return new ProductoViewHolder(ItemProductoBinding.inflate(LayoutInflater.from(padre.getContext()), padre, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int posicion) {
        Producto producto = obtener(posicion);
        holder.binding.nombre.setText(producto.getNombreP());
        holder.binding.descripcion.setText(Formato.textoOGuion(producto.getDescripcion()));
        holder.binding.precio.setText(Formato.formatearPrecio(producto.getPrecio()));
        UtilVista.cargarImagenProducto(holder.binding.imagen, producto.getImagen());

        holder.itemView.setOnClickListener(v -> {
            if (alSeleccionar != null) {
                alSeleccionar.seleccionar(producto);
            }
        });
    }

    @Override
    protected String[] textosBusqueda(Producto producto) {
        return new String[]{producto.getNombreP(), producto.getDescripcion()};
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {

        final ItemProductoBinding binding;

        ProductoViewHolder(ItemProductoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
