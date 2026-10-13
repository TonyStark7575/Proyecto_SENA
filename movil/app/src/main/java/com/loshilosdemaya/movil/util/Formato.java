package com.loshilosdemaya.movil.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Funciones para mostrar los datos de la API en pantalla.
 * Son las mismas reglas de frontend/src/formato.js, para que web y móvil se vean igual.
 */
public final class Formato {

    private Formato() {
    }

    /** 45000.00 → "$45.000" (pesos colombianos, sin decimales, punto de miles). */
    public static String formatearPrecio(BigDecimal valor) {
        if (valor == null) {
            return "$0";
        }

        // Se arma a mano para no depender del idioma configurado en el celular
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');

        DecimalFormat formato = new DecimalFormat("#,##0", simbolos);
        formato.setRoundingMode(RoundingMode.HALF_UP);

        return "$" + formato.format(valor);
    }

    /** "2026-10-11 15:29:21" → "11/10/2026". */
    public static String formatearFecha(String fechaHora) {
        if (fechaHora == null || fechaHora.length() < 10) {
            return "";
        }

        String[] partes = fechaHora.substring(0, 10).split("-");
        if (partes.length != 3) {
            return fechaHora;
        }

        return partes[2] + "/" + partes[1] + "/" + partes[0];
    }

    /** 7 → "P007", como en la web. */
    public static String codigoPago(Integer idPago) {
        return String.format(Locale.ROOT, "P%03d", idPago == null ? 0 : idPago);
    }

    /** 12 → "Pedido #12". */
    public static String codigoPedido(Integer idPedido) {
        return "Pedido #" + (idPedido == null ? "" : idPedido);
    }

    /** Texto vacío o null → "—", para que los campos opcionales no queden en blanco. */
    public static String textoOGuion(String texto) {
        return texto == null || texto.trim().isEmpty() ? "—" : texto;
    }

    /** Para los buscadores: true si alguno de los textos contiene la búsqueda (sin distinguir mayúsculas). */
    public static boolean coincide(String busqueda, String... textos) {
        String buscado = busqueda == null ? "" : busqueda.trim().toLowerCase(Locale.ROOT);

        if (buscado.isEmpty()) {
            return true;
        }

        for (String texto : textos) {
            if (texto != null && texto.toLowerCase(Locale.ROOT).contains(buscado)) {
                return true;
            }
        }
        return false;
    }
}
