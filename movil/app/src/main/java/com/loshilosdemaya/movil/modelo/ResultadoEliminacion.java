package com.loshilosdemaya.movil.modelo;

import com.google.gson.annotations.SerializedName;

/**
 * Respuesta de eliminar un usuario. Si el usuario no tiene registros, la API responde 204 (sin cuerpo)
 * y aquí llega null. Si tiene clientes o pedidos, no se borra sino que se desactiva y responde:
 * { "resultado": "desactivado", "mensaje": "...", "usuario": { ... } }
 */
public class ResultadoEliminacion {

    @SerializedName("resultado")
    private String resultado;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("usuario")
    private Usuario usuario;

    public String getResultado() {
        return resultado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public boolean fueDesactivado() {
        return "desactivado".equals(resultado);
    }
}
