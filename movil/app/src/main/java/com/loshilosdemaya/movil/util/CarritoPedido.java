package com.loshilosdemaya.movil.util;

import com.loshilosdemaya.movil.modelo.LineaPedido;
import com.loshilosdemaya.movil.modelo.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Productos que se van eligiendo al crear un pedido, antes de enviarlo a la API.
 * Igual que la API, si un producto se agrega dos veces se suman las cantidades.
 * El total es solo una vista previa: el valor real lo calculan los triggers de la BD.
 */
public class CarritoPedido {

    /** Un producto del carrito con su cantidad. */
    public static class LineaCarrito {

        private final Producto producto;
        private int cantidad;

        LineaCarrito(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public Producto getProducto() {
            return producto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public BigDecimal calcularSubtotal() {
            return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
        }
    }

    // LinkedHashMap: conserva el orden en que se agregaron los productos
    private final Map<Integer, LineaCarrito> lineas = new LinkedHashMap<>();

    /**
     * Agrega un producto o suma la cantidad si ya estaba.
     * Devuelve un mensaje de error si la cantidad total no es válida, o null si se agregó.
     */
    public String agregarProducto(Producto producto, int cantidad) {
        LineaCarrito existente = lineas.get(producto.getIdPro());
        int cantidadTotal = existente == null ? cantidad : existente.cantidad + cantidad;

        if (cantidad < 1 || cantidadTotal > Validador.CANTIDAD_MAXIMA) {
            return "La cantidad debe ser un número entero entre 1 y " + Validador.CANTIDAD_MAXIMA;
        }

        if (existente == null) {
            lineas.put(producto.getIdPro(), new LineaCarrito(producto, cantidad));
        } else {
            existente.cantidad = cantidadTotal;
        }
        return null;
    }

    public void quitarProducto(int idProducto) {
        lineas.remove(idProducto);
    }

    public List<LineaCarrito> obtenerLineas() {
        return new ArrayList<>(lineas.values());
    }

    public boolean estaVacio() {
        return lineas.isEmpty();
    }

    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LineaCarrito linea : lineas.values()) {
            total = total.add(linea.calcularSubtotal());
        }
        return total;
    }

    /** Las líneas en el formato que espera la API: [ { id_pro, cantidad } ]. */
    public List<LineaPedido> convertirAPeticion() {
        List<LineaPedido> peticion = new ArrayList<>();
        for (LineaCarrito linea : lineas.values()) {
            peticion.add(new LineaPedido(linea.producto.getIdPro(), linea.cantidad));
        }
        return peticion;
    }
}
