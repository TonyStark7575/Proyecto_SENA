package com.loshilosdemaya.movil.modelo;

import com.google.gson.annotations.SerializedName;

/**
 * Tabla usuario (personas que usan el sistema). La API nunca devuelve la contraseña;
 * el campo password solo se usa para enviarla al crear o cambiarla al editar.
 */
public class Usuario {

    public static final String ROL_ADMINISTRADORA = "ADMINISTRADORA";
    public static final String ROL_REPARTIDOR = "REPARTIDOR";

    @SerializedName("id_usuario")
    private Integer idUsuario;

    @SerializedName("nombre_u")
    private String nombreU;

    @SerializedName("email_u")
    private String emailU;

    @SerializedName("telefono_u")
    private String telefonoU;

    @SerializedName("rol")
    private String rol;

    // TINYINT(1): llega como 1 o 0
    @SerializedName("activo")
    private Integer activo;

    @SerializedName("fecha_registro")
    private String fechaRegistro;

    @SerializedName("password")
    private String password;

    // Solo en el detalle: cuántos clientes y pedidos registró (si tiene, al eliminar se desactiva)
    @SerializedName("registros")
    private RegistrosUsuario registros;

    public Usuario() {
    }

    // Cuerpo para crear/actualizar: { nombre_u, email_u, telefono_u, password, rol }
    public Usuario(String nombreU, String emailU, String telefonoU, String password, String rol) {
        this.nombreU = nombreU;
        this.emailU = emailU;
        this.telefonoU = telefonoU;
        this.password = password;
        this.rol = rol;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombreU() {
        return nombreU;
    }

    public String getEmailU() {
        return emailU;
    }

    public String getTelefonoU() {
        return telefonoU;
    }

    public String getRol() {
        return rol;
    }

    public boolean estaActivo() {
        return activo != null && activo == 1;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public RegistrosUsuario getRegistros() {
        return registros;
    }

    /** Clientes y pedidos que registró el usuario. */
    public static class RegistrosUsuario {

        @SerializedName("clientes")
        private int clientes;

        @SerializedName("pedidos")
        private int pedidos;

        public int getClientes() {
            return clientes;
        }

        public int getPedidos() {
            return pedidos;
        }

        public boolean tieneRegistros() {
            return clientes > 0 || pedidos > 0;
        }
    }
}
