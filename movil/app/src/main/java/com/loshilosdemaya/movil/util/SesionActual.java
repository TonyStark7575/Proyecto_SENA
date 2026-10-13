package com.loshilosdemaya.movil.util;

import com.loshilosdemaya.movil.modelo.Sesion;

/**
 * Usuario que tiene la sesión abierta en la app (nombre y rol para mostrar en pantalla).
 * La sesión real la controla la API con la cookie; esto es solo para la interfaz.
 */
public final class SesionActual {

    private static Sesion sesion;

    private SesionActual() {
    }

    public static void guardar(Sesion nuevaSesion) {
        sesion = nuevaSesion;
    }

    public static Sesion obtener() {
        return sesion;
    }

    public static void limpiar() {
        sesion = null;
    }

    // Para no permitir que el usuario se desactive o se elimine a sí mismo (la API también lo impide)
    public static boolean esUsuarioDeLaSesion(Integer idUsuario) {
        return sesion != null && idUsuario != null && idUsuario.equals(sesion.getIdUsuario());
    }
}
