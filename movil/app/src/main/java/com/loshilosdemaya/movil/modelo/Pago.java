package com.loshilosdemaya.movil.modelo;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Tabla pago. Al registrar o editar un pago, el trigger de la BD recalcula el saldo del pedido.
 *
 * Para enviar: crear usa { id_pedido, monto, metodo } y actualizar { monto, metodo }.
 */
public class Pago {

    public static final String METODO_EFECTIVO = "Efectivo";
    public static final String METODO_TRANSFERENCIA = "Transferencia";

    @SerializedName("id_pago")
    private Integer idPago;

    @SerializedName("id_pedido_p")
    private Integer idPedidoP;

    // Llega del JOIN con pedido y también es el nombre del campo al crear
    @SerializedName("id_pedido")
    private Integer idPedido;

    @SerializedName("monto")
    private BigDecimal monto;

    @SerializedName("fecha_p")
    private String fechaP;

    @SerializedName("metodo")
    private String metodo;

    // Del JOIN con pedido y cliente
    @SerializedName("total")
    private BigDecimal total;

    @SerializedName("saldo")
    private BigDecimal saldo;

    @SerializedName("nombre_c")
    private String nombreC;

    public Pago() {
    }

    // Cuerpo para crear (con idPedido) o actualizar (idPedido = null: no se envía)
    public Pago(Integer idPedido, BigDecimal monto, String metodo) {
        this.idPedido = idPedido;
        this.monto = monto;
        this.metodo = metodo;
    }

    public Integer getIdPago() {
        return idPago;
    }

    // El pedido del pago: viene en id_pedido_p (siempre) o en id_pedido (cuando hay JOIN)
    public Integer getIdPedido() {
        return idPedidoP != null ? idPedidoP : idPedido;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getFechaP() {
        return fechaP;
    }

    public String getMetodo() {
        return metodo;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public String getNombreC() {
        return nombreC;
    }
}
