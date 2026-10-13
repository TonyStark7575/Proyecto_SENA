package com.loshilosdemaya.movil.vista.pedidos;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ItemLineaPedidoBinding;
import com.loshilosdemaya.movil.modelo.LineaPedido;
import com.loshilosdemaya.movil.util.CarritoPedido;
import com.loshilosdemaya.movil.util.Formato;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Productos de un pedido. Sirve para dos casos con la misma apariencia:
 * - Las líneas guardadas en la BD (detalle y edición de un pedido).
 * - Las líneas del carrito (pedido nuevo que todavía no se ha enviado).
 * Por eso trabaja con FilaLinea, que se arma desde cualquiera de los dos.
 */
public class LineaPedidoAdapter extends RecyclerView.Adapter<LineaPedidoAdapter.LineaViewHolder> {

    /** Qué hacer al tocar el botón de quitar; null = las líneas no se pueden quitar. */
    public interface AlQuitar {
        void quitar(FilaLinea fila);
    }

    /** Lo que se muestra de una línea, sin importar de dónde viene. */
    public static class FilaLinea {

        // id_detalle si viene de la BD, id_pro si viene del carrito
        private final int id;
        private final String nombre;
        private final int cantidad;
        private final BigDecimal precio;
        private final BigDecimal subtotal;

        FilaLinea(int id, String nombre, int cantidad, BigDecimal precio, BigDecimal subtotal) {
            this.id = id;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precio = precio;
            this.subtotal = subtotal;
        }

        public static FilaLinea desdeLinea(LineaPedido linea) {
            return new FilaLinea(linea.getIdDetalle(), linea.getNombreP(),
                    linea.getCantidad(), linea.getPrecio(), linea.getSubtotal());
        }

        public static FilaLinea desdeCarrito(CarritoPedido.LineaCarrito linea) {
            return new FilaLinea(linea.getProducto().getIdPro(), linea.getProducto().getNombreP(),
                    linea.getCantidad(), linea.getProducto().getPrecio(), linea.calcularSubtotal());
        }

        public int getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }
    }

    private final List<FilaLinea> filas = new ArrayList<>();
    @Nullable
    private AlQuitar alQuitar;

    public LineaPedidoAdapter(@Nullable AlQuitar alQuitar) {
        this.alQuitar = alQuitar;
    }

    // En un pedido Entregado o Cancelado ya no se pueden quitar productos
    @SuppressLint("NotifyDataSetChanged")
    public void permitirQuitar(@Nullable AlQuitar alQuitar) {
        this.alQuitar = alQuitar;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void mostrarLineas(List<LineaPedido> lineas) {
        filas.clear();
        if (lineas != null) {
            for (LineaPedido linea : lineas) {
                filas.add(FilaLinea.desdeLinea(linea));
            }
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void mostrarCarrito(CarritoPedido carrito) {
        filas.clear();
        for (CarritoPedido.LineaCarrito linea : carrito.obtenerLineas()) {
            filas.add(FilaLinea.desdeCarrito(linea));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LineaViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        return new LineaViewHolder(ItemLineaPedidoBinding.inflate(LayoutInflater.from(padre.getContext()), padre, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LineaViewHolder holder, int posicion) {
        FilaLinea fila = filas.get(posicion);
        holder.binding.nombre.setText(fila.nombre);
        holder.binding.cantidadPrecio.setText(holder.itemView.getContext().getString(
                R.string.cantidad_por_precio, fila.cantidad, Formato.formatearPrecio(fila.precio)));
        holder.binding.subtotal.setText(Formato.formatearPrecio(fila.subtotal));

        holder.binding.botonQuitar.setVisibility(alQuitar == null ? View.GONE : View.VISIBLE);
        holder.binding.botonQuitar.setOnClickListener(v -> {
            if (alQuitar != null) {
                alQuitar.quitar(fila);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filas.size();
    }

    static class LineaViewHolder extends RecyclerView.ViewHolder {

        final ItemLineaPedidoBinding binding;

        LineaViewHolder(ItemLineaPedidoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
