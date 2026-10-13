package com.loshilosdemaya.movil.vista.clientes;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loshilosdemaya.movil.databinding.ItemClienteBinding;
import com.loshilosdemaya.movil.modelo.Cliente;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.vista.comun.AdaptadorFiltrable;

import java.util.Locale;

/** Dibuja cada cliente: inicial, nombre, correo y teléfono. */
public class ClienteAdapter extends AdaptadorFiltrable<Cliente, ClienteAdapter.ClienteViewHolder> {

    public ClienteAdapter(AlSeleccionar<Cliente> alSeleccionar) {
        super(alSeleccionar);
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        return new ClienteViewHolder(ItemClienteBinding.inflate(LayoutInflater.from(padre.getContext()), padre, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int posicion) {
        Cliente cliente = obtener(posicion);
        String nombre = cliente.getNombreC() == null ? "" : cliente.getNombreC();

        holder.binding.inicial.setText(nombre.isEmpty() ? "?" : nombre.substring(0, 1).toUpperCase(Locale.ROOT));
        holder.binding.nombre.setText(nombre);
        holder.binding.correo.setText(cliente.getEmailC());
        holder.binding.telefono.setText(Formato.textoOGuion(cliente.getTelefonoC()));

        holder.itemView.setOnClickListener(v -> {
            if (alSeleccionar != null) {
                alSeleccionar.seleccionar(cliente);
            }
        });
    }

    @Override
    protected String[] textosBusqueda(Cliente cliente) {
        return new String[]{cliente.getNombreC(), cliente.getEmailC(), cliente.getTelefonoC()};
    }

    static class ClienteViewHolder extends RecyclerView.ViewHolder {

        final ItemClienteBinding binding;

        ClienteViewHolder(ItemClienteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
