package com.loshilosdemaya.movil.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

/** Pruebas de Validador: mismas reglas y llaves de error que los controladores PHP. */
public class ValidadorTest {

    // ===== Productos =====

    @Test
    public void validarProducto_datosCorrectosNoTieneErrores() {
        assertTrue(Validador.validarProducto("Muñeco amigurumi", "45000").isEmpty());
    }

    @Test
    public void validarProducto_nombreVacioYPrecioCero() {
        Map<String, String> errores = Validador.validarProducto("   ", "0");
        assertEquals("El nombre es obligatorio", errores.get("nombre_p"));
        assertTrue(errores.containsKey("precio"));
    }

    @Test
    public void validarProducto_nombreDeMasDe50Caracteres() {
        String nombreLargo = "a".repeat(51);
        assertEquals("El nombre no puede tener más de 50 caracteres",
                Validador.validarProducto(nombreLargo, "1000").get("nombre_p"));
    }

    @Test
    public void validarProducto_precioFueraDeRangoOTexto() {
        assertTrue(Validador.validarProducto("Gorro", "100000000").containsKey("precio"));
        assertTrue(Validador.validarProducto("Gorro", "abc").containsKey("precio"));
        assertTrue(Validador.validarProducto("Gorro", "-5").containsKey("precio"));
        assertFalse(Validador.validarProducto("Gorro", "99999999.99").containsKey("precio"));
    }

    @Test
    public void convertirMonto_aceptaComaDecimalYRedondeaADosDecimales() {
        assertEquals(new BigDecimal("1500.50"), Validador.convertirMonto("1500,5"));
        assertEquals(new BigDecimal("10.01"), Validador.convertirMonto("10.005"));
        assertNull(Validador.convertirMonto(""));
        assertNull(Validador.convertirMonto("diez"));
    }

    // ===== Clientes =====

    @Test
    public void validarCliente_correoInvalido() {
        Map<String, String> errores = Validador.validarCliente("Ana", "ana@", "");
        assertEquals("El correo no tiene un formato válido (ejemplo: nombre@correo.com)", errores.get("email_c"));
        assertFalse(errores.containsKey("nombre_c"));
    }

    @Test
    public void validarCliente_telefonoEsOpcional() {
        assertTrue(Validador.validarCliente("Ana", "ana@correo.com", "").isEmpty());
    }

    // ===== Usuarios =====

    @Test
    public void validarUsuario_nuevoExigeContrasena() {
        Map<String, String> errores = Validador.validarUsuario("Carlos", "carlos@correo.com", "", "", "REPARTIDOR", true);
        assertEquals("La contraseña es obligatoria", errores.get("password"));
    }

    @Test
    public void validarUsuario_alEditarLaContrasenaVaciaSeConserva() {
        assertTrue(Validador.validarUsuario("Carlos", "carlos@correo.com", "", "", "REPARTIDOR", false).isEmpty());
    }

    @Test
    public void validarUsuario_contrasenaCortaYRolInvalido() {
        Map<String, String> errores = Validador.validarUsuario("Carlos", "carlos@correo.com", "", "1234", "JEFE", true);
        assertEquals("La contraseña debe tener al menos 8 caracteres", errores.get("password"));
        assertEquals("Selecciona un rol válido", errores.get("rol"));
    }

    // ===== Pagos =====

    @Test
    public void validarPago_montoMayorQueElSaldo() {
        Map<String, String> errores = Validador.validarPago("30000", "Efectivo", new BigDecimal("20000"));
        assertEquals("El monto máximo para este pago es $20.000 (lo que falta por pagar del pedido)", errores.get("monto"));
    }

    @Test
    public void validarPago_pedidoSinSaldo() {
        assertEquals("Este pedido no tiene saldo pendiente",
                Validador.validarPago("1000", "Efectivo", BigDecimal.ZERO).get("monto"));
    }

    @Test
    public void validarPago_metodoInvalido() {
        assertEquals("El método debe ser Efectivo o Transferencia",
                Validador.validarPago("1000", "Tarjeta", null).get("metodo"));
    }

    @Test
    public void validarPago_montoIgualAlSaldoEsValido() {
        assertTrue(Validador.validarPago("20000", "Transferencia", new BigDecimal("20000.00")).isEmpty());
    }

    // ===== Cantidades =====

    @Test
    public void validarCantidad_entre1Y9999() {
        assertNull(Validador.validarCantidad("1"));
        assertNull(Validador.validarCantidad("9999"));
        assertEquals("La cantidad debe ser un número entero entre 1 y 9999", Validador.validarCantidad("0"));
        assertEquals("La cantidad debe ser un número entero entre 1 y 9999", Validador.validarCantidad("10000"));
        assertEquals("La cantidad debe ser un número entero entre 1 y 9999", Validador.validarCantidad("2.5"));
    }
}
