package com.loshilosdemaya.movil.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.loshilosdemaya.movil.modelo.LineaPedido;
import com.loshilosdemaya.movil.modelo.Producto;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

/** Pruebas del carrito con el que se arma un pedido nuevo antes de enviarlo. */
public class CarritoPedidoTest {

    private CarritoPedido carrito;
    private Producto gorro;
    private Producto bufanda;

    @Before
    public void prepararCarrito() {
        carrito = new CarritoPedido();
        gorro = crearProducto(1, "Gorro", "25000");
        bufanda = crearProducto(2, "Bufanda", "40000.50");
    }

    private static Producto crearProducto(int id, String nombre, String precio) {
        Producto producto = new Producto(nombre, new BigDecimal(precio), null);
        producto.setIdPro(id);
        return producto;
    }

    @Test
    public void carritoNuevoEstaVacio() {
        assertTrue(carrito.estaVacio());
        assertEquals(0, BigDecimal.ZERO.compareTo(carrito.calcularTotal()));
    }

    @Test
    public void agregarProducto_calculaElTotalConDecimales() {
        assertNull(carrito.agregarProducto(gorro, 2));
        assertNull(carrito.agregarProducto(bufanda, 1));

        // 2 × 25.000 + 1 × 40.000,50
        assertEquals(new BigDecimal("90000.50"), carrito.calcularTotal());
    }

    @Test
    public void agregarElMismoProducto_sumaLasCantidades() {
        carrito.agregarProducto(gorro, 2);
        carrito.agregarProducto(gorro, 3);

        assertEquals(1, carrito.obtenerLineas().size());
        assertEquals(5, carrito.obtenerLineas().get(0).getCantidad());
    }

    @Test
    public void agregarProducto_rechazaPasarDe9999Unidades() {
        carrito.agregarProducto(gorro, 9998);
        assertNotNull(carrito.agregarProducto(gorro, 2));
        assertEquals(9998, carrito.obtenerLineas().get(0).getCantidad());
    }

    @Test
    public void quitarProducto_loSacaDelCarrito() {
        carrito.agregarProducto(gorro, 1);
        carrito.agregarProducto(bufanda, 1);
        carrito.quitarProducto(1);

        assertEquals(1, carrito.obtenerLineas().size());
        assertEquals("Bufanda", carrito.obtenerLineas().get(0).getProducto().getNombreP());
    }

    @Test
    public void convertirAPeticion_soloEnviaProductoYCantidad() {
        carrito.agregarProducto(gorro, 2);
        carrito.agregarProducto(bufanda, 1);

        List<LineaPedido> peticion = carrito.convertirAPeticion();

        assertEquals(2, peticion.size());
        assertEquals(Integer.valueOf(1), peticion.get(0).getIdPro());
        assertEquals(Integer.valueOf(2), peticion.get(0).getCantidad());
        // El precio nunca lo envía la app: lo pone la API desde la BD
        assertNull(peticion.get(0).getPrecio());
    }
}
