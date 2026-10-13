package com.loshilosdemaya.movil.modelo;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

/** Respuesta de api/dashboard: los indicadores de la pantalla de inicio. */
public class ResumenDashboard {

    @SerializedName("total_clientes")
    private int totalClientes;

    @SerializedName("total_productos")
    private int totalProductos;

    @SerializedName("pedidos_pendientes")
    private int pedidosPendientes;

    // Lo que se vendió hoy: pedidos registrados hoy que no están cancelados
    @SerializedName("ventas_hoy")
    private BigDecimal ventasHoy;

    @SerializedName("pedidos_hoy")
    private int pedidosHoy;

    // El dinero que entró hoy: pagos registrados hoy, de pedidos de cualquier fecha
    @SerializedName("cobrado_hoy")
    private BigDecimal cobradoHoy;

    @SerializedName("pagos_hoy")
    private int pagosHoy;

    @SerializedName("pedidos_recientes")
    private List<Pedido> pedidosRecientes;

    public int getTotalClientes() {
        return totalClientes;
    }

    public int getTotalProductos() {
        return totalProductos;
    }

    public int getPedidosPendientes() {
        return pedidosPendientes;
    }

    public BigDecimal getVentasHoy() {
        return ventasHoy;
    }

    public int getPedidosHoy() {
        return pedidosHoy;
    }

    public BigDecimal getCobradoHoy() {
        return cobradoHoy;
    }

    public int getPagosHoy() {
        return pagosHoy;
    }

    public List<Pedido> getPedidosRecientes() {
        return pedidosRecientes;
    }
}
