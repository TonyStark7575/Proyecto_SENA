package com.loshilosdemaya.movil.vista.pedidos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ItemPedidoBinding;
import com.loshilosdemaya.movil.modelo.Pedido;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.vista.comun.AdaptadorFiltrable;
import com.loshilosdemaya.movil.vista.comun.UtilVista;

/**
 * Dibuja cada pedido: código, estado, cliente, fecha, total y saldo.
 * Se reutiliza en la lista de pedidos, en el dashboard y en el historial del cliente.
 */
public class PedidoAdapter extends AdaptadorFiltrable<Pedido, PedidoAdapter.PedidoViewHolder> {

    public PedidoAdapter(AlSeleccionar<Pedido> alSeleccionar) {
        super(alSeleccionar);
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        return new PedidoViewHolder(ItemPedidoBinding.inflate(LayoutInflater.from(padre.getContext()), padre, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int posicion) {
        Pedido pedido = obtener(posicion);
        Context contexto = holder.itemView.getContext();

        holder.binding.codigo.setText(Formato.codigoPedido(pedido.getIdPedido()));
        UtilVista.pintarEstado(holder.binding.estado, pedido.getEstado());

        // En el historial de un cliente no viene el nombre (ya se sabe de quién es)
        holder.binding.cliente.setVisibility(pedido.getNombreC() == null ? View.GONE : View.VISIBLE);
        holder.binding.cliente.setText(pedido.getNombreC());

        holder.binding.fecha.setText(Formato.formatearFecha(pedido.getFechaRegistro()));
        holder.binding.total.setText(Formato.formatearPrecio(pedido.getTotal()));

        boolean tieneSaldo = pedido.getSaldo() != null && pedido.getSaldo().signum() > 0;
        holder.binding.saldo.setText(tieneSaldo
                ? contexto.getString(R.string.saldo_pendiente_valor, Formato.formatearPrecio(pedido.getSaldo()))
                : contexto.getString(R.string.pagado));
        holder.binding.saldo.setTextColor(ContextCompat.getColor(contexto, tieneSaldo ? R.color.peligro : R.color.estado_entregado));

        holder.itemView.setOnClickListener(v -> {
            if (alSeleccionar != null) {
                alSeleccionar.seleccionar(pedido);
            }
        });
    }

    @Override
    protected String[] textosBusqueda(Pedido pedido) {
        return new String[]{String.valueOf(pedido.getIdPedido()), pedido.getNombreC(), pedido.getEstado(), pedido.getTelefonoC()};
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {

        final ItemPedidoBinding binding;

        PedidoViewHolder(ItemPedidoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
