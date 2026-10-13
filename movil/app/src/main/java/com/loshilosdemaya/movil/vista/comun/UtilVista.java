package com.loshilosdemaya.movil.vista.comun;

import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.modelo.Pedido;
import com.loshilosdemaya.movil.red.ConexionApi;

import java.util.List;

/** Ayudas de interfaz que usan varias pantallas. */
public final class UtilVista {

    public static final String[] ESTADOS_PEDIDO = {
            Pedido.ESTADO_PENDIENTE, Pedido.ESTADO_EN_PROCESO, Pedido.ESTADO_ENTREGADO, Pedido.ESTADO_CANCELADO
    };

    private UtilVista() {
    }

    /** Etiqueta de color según el estado del pedido (mismos colores en todas las pantallas). */
    public static void pintarEstado(TextView etiqueta, String estado) {
        int color;
        if (Pedido.ESTADO_PENDIENTE.equals(estado)) {
            color = R.color.estado_pendiente;
        } else if (Pedido.ESTADO_EN_PROCESO.equals(estado)) {
            color = R.color.estado_en_proceso;
        } else if (Pedido.ESTADO_ENTREGADO.equals(estado)) {
            color = R.color.estado_entregado;
        } else {
            color = R.color.estado_cancelado;
        }
        etiqueta.setText(estado);
        etiqueta.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(etiqueta.getContext(), color)));
    }

    /** Muestra la imagen del producto desde el servidor, o el logo si no tiene. */
    public static void cargarImagenProducto(ImageView vista, String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            Glide.with(vista).clear(vista);
            vista.setImageResource(R.drawable.logo_maya);
            return;
        }

        Glide.with(vista)
                .load(ConexionApi.obtener().construirUrlImagen(nombreImagen))
                .placeholder(R.drawable.logo_maya)
                .error(R.drawable.logo_maya)
                .centerCrop()
                .into(vista);
    }

    /** Llena un campo desplegable (menú tipo "select") con las opciones dadas. */
    public static <T> void llenarDesplegable(Context contexto, MaterialAutoCompleteTextView campo, List<T> opciones) {
        campo.setAdapter(new ArrayAdapter<>(contexto, android.R.layout.simple_list_item_1, opciones));
    }
}
