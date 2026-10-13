package com.loshilosdemaya.movil.modelo;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

/**
 * Tabla pedido. El total y el saldo los calculan los triggers de la BD.
 *
 * Para enviar: crear usa { id_cliente, estado, lineas: [ { id_pro, cantidad } ] }
 * y actualizar usa { id_cliente, estado } (los productos se cambian con api/pedidos/lineas).
 */
public class Pedido {

    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_EN_PROCESO = "En proceso";
    public static final String ESTADO_ENTREGADO = "Entregado";
    public static final String ESTADO_CANCELADO = "Cancelado";

    @SerializedName("id_pedido")
    private Integer idPedido;

    @SerializedName("id_usuario_p")
    private Integer idUsuarioP;

    // Así llega en las respuestas (columna de la tabla)
    @SerializedName("id_cliente_p")
    private Integer idClienteP;

    // Así se envía al crear o actualizar
    @SerializedName("id_cliente")
    private Integer idCliente;

    @SerializedName("fecha_registro")
    private String fechaRegistro;

    @SerializedName("estado")
    private String estado;

    @SerializedName("total")
    private BigDecimal total;

    @SerializedName("saldo")
    private BigDecimal saldo;

    // Del JOIN con cliente (lista y detalle)
    @SerializedName("nombre_c")
    private String nombreC;

    @SerializedName("telefono_c")
    private String telefonoC;

    // Solo en el detalle
    @SerializedName("registrado_por")
    private String registradoPor;

    @SerializedName("lineas")
    private List<LineaPedido> lineas;

    @SerializedName("pagos")
    private List<Pago> pagos;

    public Pedido() {
    }

    // Cuerpo para crear (con líneas) o actualizar (lineas = null: no se envía)
    public Pedido(Integer idCliente, String estado, List<LineaPedido> lineas) {
        this.idCliente = idCliente;
        this.estado = estado;
        this.lineas = lineas;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public Integer getIdUsuarioP() {
        return idUsuarioP;
    }

    public Integer getIdClienteP() {
        return idClienteP;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public String getEstado() {
        return estado;
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

    public String getTelefonoC() {
        return telefonoC;
    }

    public String getRegistradoPor() {
        return registradoPor;
    }

    public List<LineaPedido> getLineas() {
        return lineas;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    // Entregado y Cancelado: ya no se agregan ni se quitan productos (misma regla que la API)
    public boolean estaCerrado() {
        return ESTADO_ENTREGADO.equals(estado) || ESTADO_CANCELADO.equals(estado);
    }

    // Se le puede registrar un pago si no está cancelado y le queda saldo
    public boolean admitePagos() {
        return !ESTADO_CANCELADO.equals(estado) && saldo != null && saldo.signum() > 0;
    }
}
