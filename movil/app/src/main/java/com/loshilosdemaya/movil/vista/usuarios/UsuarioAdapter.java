package com.loshilosdemaya.movil.vista.usuarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loshilosdemaya.movil.databinding.ItemUsuarioBinding;
import com.loshilosdemaya.movil.modelo.Usuario;
import com.loshilosdemaya.movil.vista.comun.AdaptadorFiltrable;

import java.util.Locale;

/** Dibuja cada usuario: inicial, nombre, correo, rol y si está inactivo. */
public class UsuarioAdapter extends AdaptadorFiltrable<Usuario, UsuarioAdapter.UsuarioViewHolder> {

    public UsuarioAdapter(AlSeleccionar<Usuario> alSeleccionar) {
        super(alSeleccionar);
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        return new UsuarioViewHolder(ItemUsuarioBinding.inflate(LayoutInflater.from(padre.getContext()), padre, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int posicion) {
        Usuario usuario = obtener(posicion);
        String nombre = usuario.getNombreU() == null ? "" : usuario.getNombreU();

        holder.binding.inicial.setText(nombre.isEmpty() ? "?" : nombre.substring(0, 1).toUpperCase(Locale.ROOT));
        holder.binding.nombre.setText(nombre);
        holder.binding.correo.setText(usuario.getEmailU());
        holder.binding.rol.setText(usuario.getRol());
        holder.binding.inactivo.setVisibility(usuario.estaActivo() ? View.GONE : View.VISIBLE);
        holder.itemView.setAlpha(usuario.estaActivo() ? 1f : 0.6f);

        holder.itemView.setOnClickListener(v -> {
            if (alSeleccionar != null) {
                alSeleccionar.seleccionar(usuario);
            }
        });
    }

    @Override
    protected String[] textosBusqueda(Usuario usuario) {
        return new String[]{usuario.getNombreU(), usuario.getEmailU(), usuario.getRol(), usuario.getTelefonoU()};
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {

        final ItemUsuarioBinding binding;

        UsuarioViewHolder(ItemUsuarioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
