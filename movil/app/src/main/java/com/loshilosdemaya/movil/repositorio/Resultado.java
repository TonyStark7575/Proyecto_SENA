package com.loshilosdemaya.movil.repositorio;

import com.loshilosdemaya.movil.red.ErrorApi;

import java.util.Collections;
import java.util.Map;

/**
 * Estado de una operación contra la API, para que la vista sepa qué mostrar:
 * CARGANDO (indicador de progreso), EXITO (los datos) o ERROR (mensaje y errores por campo).
 */
public class Resultado<T> {

    public enum Estado { CARGANDO, EXITO, ERROR }

    private final Estado estado;
    private final T datos;
    private final ErrorApi error;

    private Resultado(Estado estado, T datos, ErrorApi error) {
        this.estado = estado;
        this.datos = datos;
        this.error = error;
    }

    public static <T> Resultado<T> cargando() {
        return new Resultado<>(Estado.CARGANDO, null, null);
    }

    public static <T> Resultado<T> exito(T datos) {
        return new Resultado<>(Estado.EXITO, datos, null);
    }

    public static <T> Resultado<T> error(ErrorApi error) {
        return new Resultado<>(Estado.ERROR, null, error);
    }

    public Estado getEstado() {
        return estado;
    }

    public boolean estaCargando() {
        return estado == Estado.CARGANDO;
    }

    public boolean esExito() {
        return estado == Estado.EXITO;
    }

    public boolean esError() {
        return estado == Estado.ERROR;
    }

    // En operaciones que responden 204 (eliminar), datos es null aunque haya sido un éxito
    public T getDatos() {
        return datos;
    }

    public String getMensajeError() {
        return error != null ? error.getMensaje() : null;
    }

    public Map<String, String> getErroresCampos() {
        return error != null ? error.getErrores() : Collections.emptyMap();
    }

    // 401: la sesión venció o el usuario fue desactivado; la vista vuelve al inicio de sesión
    public boolean esNoAutenticado() {
        return error != null && error.getCodigoHttp() == 401;
    }
}
