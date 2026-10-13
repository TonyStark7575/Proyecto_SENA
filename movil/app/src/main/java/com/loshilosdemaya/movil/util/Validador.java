package com.loshilosdemaya.movil.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validaciones de los formularios antes de enviarlos a la API. Usan las mismas reglas,
 * llaves y mensajes que los controladores PHP, así el usuario ve el error sin esperar a la red.
 * La API vuelve a validar todo: esta validación es una ayuda, no la seguridad del sistema.
 *
 * Cada método devuelve un mapa campo → mensaje (vacío si todo está bien).
 */
public final class Validador {

    public static final int LARGO_MAXIMO_NOMBRE = 50;
    public static final int LARGO_MAXIMO_CORREO = 255;
    public static final int LARGO_MAXIMO_TELEFONO = 50;
    public static final int PASSWORD_MINIMO = 8;
    public static final int PASSWORD_MAXIMO_BYTES = 72;
    public static final int CANTIDAD_MAXIMA = 9999;
    public static final BigDecimal PRECIO_MAXIMO = new BigDecimal("99999999.99");

    // Formato básico nombre@dominio.ext (la API usa FILTER_VALIDATE_EMAIL, que es más estricto)
    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validador() {
    }

    public static boolean esCorreoValido(String correo) {
        return correo != null && PATRON_CORREO.matcher(correo.trim()).matches();
    }

    /**
     * Convierte el texto de un campo de dinero a BigDecimal con 2 decimales (como DECIMAL(10,2)).
     * Acepta coma o punto decimal. Devuelve null si no es un número.
     */
    public static BigDecimal convertirMonto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(texto.trim().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Reglas de ProductoApiController::validar. */
    public static Map<String, String> validarProducto(String nombre, String precioTexto) {
        Map<String, String> errores = new LinkedHashMap<>();

        validarNombre(errores, "nombre_p", nombre);

        BigDecimal precio = convertirMonto(precioTexto);
        if (precio == null || precio.signum() <= 0 || precio.compareTo(PRECIO_MAXIMO) > 0) {
            errores.put("precio", "El precio debe ser un número mayor que 0 y menor que 100.000.000");
        }

        return errores;
    }

    /** Reglas de ClienteApiController::validar. */
    public static Map<String, String> validarCliente(String nombre, String correo, String telefono) {
        Map<String, String> errores = new LinkedHashMap<>();

        validarNombre(errores, "nombre_c", nombre);
        validarCorreo(errores, "email_c", correo);
        validarTelefono(errores, "telefono_c", telefono);

        return errores;
    }

    /** Reglas de UsuarioApiController::validar. Al editar, la contraseña vacía significa "no cambiarla". */
    public static Map<String, String> validarUsuario(String nombre, String correo, String telefono,
                                                     String password, String rol, boolean esNuevo) {
        Map<String, String> errores = new LinkedHashMap<>();

        validarNombre(errores, "nombre_u", nombre);
        validarCorreo(errores, "email_u", correo);
        validarTelefono(errores, "telefono_u", telefono);

        String clave = password == null ? "" : password;
        if (clave.isEmpty()) {
            if (esNuevo) {
                errores.put("password", "La contraseña es obligatoria");
            }
        } else if (clave.codePointCount(0, clave.length()) < PASSWORD_MINIMO) {
            errores.put("password", "La contraseña debe tener al menos " + PASSWORD_MINIMO + " caracteres");
        } else if (clave.getBytes(StandardCharsets.UTF_8).length > PASSWORD_MAXIMO_BYTES) {
            errores.put("password", "La contraseña no puede tener más de " + PASSWORD_MAXIMO_BYTES + " caracteres");
        }

        if (!"ADMINISTRADORA".equals(rol) && !"REPARTIDOR".equals(rol)) {
            errores.put("rol", "Selecciona un rol válido");
        }

        return errores;
    }

    /**
     * Reglas de PagoApiController::validarMontoYMetodo.
     * maximo: lo que falta por pagar del pedido (null si todavía no se eligió pedido).
     */
    public static Map<String, String> validarPago(String montoTexto, String metodo, BigDecimal maximo) {
        Map<String, String> errores = new LinkedHashMap<>();

        BigDecimal monto = convertirMonto(montoTexto);
        if (monto == null || monto.signum() <= 0) {
            errores.put("monto", "El monto debe ser un número mayor que 0");
        } else if (maximo != null && monto.compareTo(maximo) > 0) {
            errores.put("monto", maximo.signum() == 0
                    ? "Este pedido no tiene saldo pendiente"
                    : "El monto máximo para este pago es " + Formato.formatearPrecio(maximo) + " (lo que falta por pagar del pedido)");
        }

        if (!"Efectivo".equals(metodo) && !"Transferencia".equals(metodo)) {
            errores.put("metodo", "El método debe ser Efectivo o Transferencia");
        }

        return errores;
    }

    /** Cantidad de un producto en un pedido: entero entre 1 y 9999. Devuelve null si es válida. */
    public static String validarCantidad(String texto) {
        try {
            int cantidad = Integer.parseInt(texto == null ? "" : texto.trim());
            if (cantidad >= 1 && cantidad <= CANTIDAD_MAXIMA) {
                return null;
            }
        } catch (NumberFormatException e) {
            // Sigue al mensaje de error
        }
        return "La cantidad debe ser un número entero entre 1 y " + CANTIDAD_MAXIMA;
    }

    // ===== Reglas compartidas =====

    private static void validarNombre(Map<String, String> errores, String campo, String nombre) {
        String valor = nombre == null ? "" : nombre.trim();
        if (valor.isEmpty()) {
            errores.put(campo, "El nombre es obligatorio");
        } else if (valor.codePointCount(0, valor.length()) > LARGO_MAXIMO_NOMBRE) {
            errores.put(campo, "El nombre no puede tener más de " + LARGO_MAXIMO_NOMBRE + " caracteres");
        }
    }

    private static void validarCorreo(Map<String, String> errores, String campo, String correo) {
        String valor = correo == null ? "" : correo.trim();
        if (!esCorreoValido(valor)) {
            errores.put(campo, "El correo no tiene un formato válido (ejemplo: nombre@correo.com)");
        } else if (valor.length() > LARGO_MAXIMO_CORREO) {
            errores.put(campo, "El correo no puede tener más de " + LARGO_MAXIMO_CORREO + " caracteres");
        }
    }

    private static void validarTelefono(Map<String, String> errores, String campo, String telefono) {
        String valor = telefono == null ? "" : telefono.trim();
        if (valor.codePointCount(0, valor.length()) > LARGO_MAXIMO_TELEFONO) {
            errores.put(campo, "El teléfono no puede tener más de " + LARGO_MAXIMO_TELEFONO + " caracteres");
        }
    }
}
