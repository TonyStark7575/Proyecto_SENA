package com.loshilosdemaya.movil.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Tabla cliente. El detalle (api/clientes&id=N) además trae quién lo registró
 * y su historial de pedidos y pagos.
 */
public class Cliente {

    @SerializedName("id_cliente")
    private Integer idCliente;

    @SerializedName("nombre_c")
    private String nombreC;

    @SerializedName("email_c")
    private String emailC;

    @SerializedName("telefono_c")
    private String telefonoC;

    @SerializedName("fecha_registro")
    private String fechaRegistro;

    @SerializedName("id_usuario_c")
    private Integer idUsuarioC;

    // Solo en el detalle
    @SerializedName("registrado_por")
    private String registradoPor;

    @SerializedName("pedidos")
    private List<Pedido> pedidos;

    @SerializedName("pagos")
    private List<Pago> pagos;

    public Cliente() {
    }

    // Constructor para el cuerpo de crear/actualizar: { nombre_c, email_c, telefono_c }
    public Cliente(String nombreC, String emailC, String telefonoC) {
        this.nombreC = nombreC;
        this.emailC = emailC;
        this.telefonoC = telefonoC;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public String getNombreC() {
        return nombreC;
    }

    public String getEmailC() {
        return emailC;
    }

    public String getTelefonoC() {
        return telefonoC;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public Integer getIdUsuarioC() {
        return idUsuarioC;
    }

    public String getRegistradoPor() {
        return registradoPor;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    // Lo que muestra el menú desplegable al elegir el cliente de un pedido
    @Override
    public String toString() {
        return nombreC;
    }
}
