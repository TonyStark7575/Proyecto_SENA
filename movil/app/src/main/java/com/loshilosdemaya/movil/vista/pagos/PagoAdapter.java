package com.loshilosdemaya.movil.vista.pagos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ItemPagoBinding;
import com.loshilosdemaya.movil.modelo.Pago;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.vista.comun.AdaptadorFiltrable;

/**
 * Dibuja cada pago: código (P007), pedido, cliente, fecha, monto y método.
 * Se reutiliza en la lista de pagos y en los detalles de pedido y cliente.
 */
public class PagoAdapter extends AdaptadorFiltrable<Pago, PagoAdapter.PagoViewHolder> {

    public PagoAdapter(AlSeleccionar<Pago> alSeleccionar) {
        super(alSeleccionar);
    }

    @NonNull
    @Override
    public PagoViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        return new PagoViewHolder(ItemPagoBinding.inflate(LayoutInflater.from(padre.getContext()), padre, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PagoViewHolder holder, int posicion) {
        Pago pago = obtener(posicion);
        Context contexto = holder.itemView.getContext();

        holder.binding.codigo.setText(Formato.codigoPago(pago.getIdPago()));
        // El nombre del cliente solo viene en la lista general de pagos
        String referencia = Formato.codigoPedido(pago.getIdPedido());
        if (pago.getNombreC() != null) {
            referencia += " · " + pago.getNombreC();
        }
        holder.binding.referencia.setText(referencia);
        holder.binding.fecha.setText(Formato.formatearFecha(pago.getFechaP()));
        holder.binding.monto.setText(Formato.formatearPrecio(pago.getMonto()));
        holder.binding.metodo.setText(pago.getMetodo());

        holder.itemView.setOnClickListener(v -> {
            if (alSeleccionar != null) {
                alSeleccionar.seleccionar(pago);
            }
        });
        holder.itemView.setContentDescription(contexto.getString(
                R.string.descripcion_pago, Formato.codigoPago(pago.getIdPago())));
    }

    @Override
    protected String[] textosBusqueda(Pago pago) {
        return new String[]{Formato.codigoPago(pago.getIdPago()), String.valueOf(pago.getIdPedido()),
                pago.getNombreC(), pago.getMetodo()};
    }

    static class PagoViewHolder extends RecyclerView.ViewHolder {

        final ItemPagoBinding binding;

        PagoViewHolder(ItemPagoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
