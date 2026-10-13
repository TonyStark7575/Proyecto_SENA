package com.loshilosdemaya.movil.modelo;

import com.google.gson.annotations.SerializedName;

/**
 * Usuario que inició sesión. La API la devuelve en api/login y api/sesion:
 * { "id_usuario": 1, "nombre": "Maya", "rol": "ADMINISTRADORA" }
 */
public class Sesion {

    public static final String ROL_ADMINISTRADORA = "ADMINISTRADORA";

    @SerializedName("id_usuario")
    private Integer idUsuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("rol")
    private String rol;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }

    public boolean esAdministradora() {
        return ROL_ADMINISTRADORA.equals(rol);
    }
}
