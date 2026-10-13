package com.loshilosdemaya.movil.modelo;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Tabla detalle_pedido: un producto dentro de un pedido.
 * El precio lo pone la API desde la BD (nunca lo envía la app), así el pedido
 * conserva el precio del momento aunque el producto cambie después.
 */
public class LineaPedido {

    @SerializedName("id_detalle")
    private Integer idDetalle;

    @SerializedName("id_pedido_d")
    private Integer idPedidoD;

    // Así llega en las respuestas
    @SerializedName("id_pro_d")
    private Integer idProD;

    // Así se envía al crear un pedido o agregar una línea
    @SerializedName("id_pro")
    private Integer idPro;

    @SerializedName("cantidad")
    private Integer cantidad;

    @SerializedName("precio")
    private BigDecimal precio;

    @SerializedName("subtotal")
    private BigDecimal subtotal;

    // Del JOIN con producto
    @SerializedName("nombre_p")
    private String nombreP;

    public LineaPedido() {
    }

    // Cuerpo para enviar: { id_pro, cantidad }
    public LineaPedido(Integer idPro, Integer cantidad) {
        this.idPro = idPro;
        this.cantidad = cantidad;
    }

    public Integer getIdDetalle() {
        return idDetalle;
    }

    public Integer getIdPedidoD() {
        return idPedidoD;
    }

    public Integer getIdProD() {
        return idProD;
    }

    public Integer getIdPro() {
        return idPro;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public String getNombreP() {
        return nombreP;
    }
}
