package com.loshilosdemaya.movil.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.math.BigDecimal;

/** Pruebas de Formato: deben dar lo mismo que frontend/src/formato.js. */
public class FormatoTest {

    @Test
    public void formatearPrecio_usaPuntoDeMilesYSinDecimales() {
        assertEquals("$45.000", Formato.formatearPrecio(new BigDecimal("45000.00")));
        assertEquals("$1.250.000", Formato.formatearPrecio(new BigDecimal("1250000")));
        assertEquals("$0", Formato.formatearPrecio(BigDecimal.ZERO));
    }

    @Test
    public void formatearPrecio_redondeaLosCentavos() {
        assertEquals("$1.001", Formato.formatearPrecio(new BigDecimal("1000.50")));
        assertEquals("$1.000", Formato.formatearPrecio(new BigDecimal("1000.49")));
    }

    @Test
    public void formatearPrecio_conNullMuestraCero() {
        assertEquals("$0", Formato.formatearPrecio(null));
    }

    @Test
    public void formatearFecha_conviertePrimeroElDia() {
        assertEquals("11/10/2026", Formato.formatearFecha("2026-10-11 15:29:21"));
        assertEquals("01/02/2026", Formato.formatearFecha("2026-02-01"));
    }

    @Test
    public void formatearFecha_conTextoInvalidoDevuelveVacio() {
        assertEquals("", Formato.formatearFecha(null));
        assertEquals("", Formato.formatearFecha("2026"));
    }

    @Test
    public void codigoPago_rellenaConCeros() {
        assertEquals("P007", Formato.codigoPago(7));
        assertEquals("P123", Formato.codigoPago(123));
        assertEquals("P1234", Formato.codigoPago(1234));
    }

    @Test
    public void coincide_ignoraMayusculasYEspacios() {
        assertTrue(Formato.coincide("  ana ", "Ana María", null));
        assertTrue(Formato.coincide("", "cualquier cosa"));
        assertTrue(Formato.coincide("correo.com", "Ana", "ana@correo.com"));
        assertFalse(Formato.coincide("pedro", "Ana", "ana@correo.com"));
    }
}
