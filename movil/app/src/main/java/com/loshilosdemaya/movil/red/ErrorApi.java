package com.loshilosdemaya.movil.red;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduce el cuerpo de una respuesta con error de la API. La API responde de dos formas:
 * - Error general:       { "error": "Credenciales incorrectas" }               (400, 401, 403, 404, 409...)
 * - Errores por campo:   { "errores": { "email_c": "El correo no tiene..." } } (422)
 */
public class ErrorApi {

    public static final String MENSAJE_SIN_CONEXION =
            "No se pudo conectar con el servidor. Revisa la URL y que XAMPP (Apache y MySQL) esté encendido.";
    public static final String MENSAJE_REVISAR_CAMPOS = "Revisa los datos del formulario";

    private final int codigoHttp;
    private final String mensaje;
    private final Map<String, String> errores;

    private ErrorApi(int codigoHttp, String mensaje, Map<String, String> errores) {
        this.codigoHttp = codigoHttp;
        this.mensaje = mensaje;
        this.errores = errores;
    }

    /** Lee el cuerpo JSON de una respuesta con error. Si no es JSON, usa un mensaje según el código. */
    public static ErrorApi leer(int codigoHttp, String cuerpo) {
        Map<String, String> errores = new LinkedHashMap<>();
        String mensaje = null;

        try {
            JsonElement json = JsonParser.parseString(cuerpo == null ? "" : cuerpo);

            if (json.isJsonObject()) {
                JsonObject objeto = json.getAsJsonObject();

                if (objeto.has("error") && objeto.get("error").isJsonPrimitive()) {
                    mensaje = objeto.get("error").getAsString();
                }

                if (objeto.has("errores") && objeto.get("errores").isJsonObject()) {
                    // Solo los mensajes de texto: "detalle_lineas" es una lista y la app ya valida las líneas antes de enviar
                    for (Map.Entry<String, JsonElement> campo : objeto.getAsJsonObject("errores").entrySet()) {
                        if (campo.getValue().isJsonPrimitive()) {
                            errores.put(campo.getKey(), campo.getValue().getAsString());
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            // El cuerpo no era JSON (por ejemplo una página de error de Apache): se usa el mensaje por código
        }

        if (mensaje == null) {
            mensaje = errores.isEmpty() ? mensajePorCodigo(codigoHttp) : MENSAJE_REVISAR_CAMPOS;
        }

        return new ErrorApi(codigoHttp, mensaje, errores);
    }

    /** Error cuando ni siquiera hubo respuesta (servidor apagado, URL equivocada, sin red). */
    public static ErrorApi sinConexion() {
        return new ErrorApi(0, MENSAJE_SIN_CONEXION, new LinkedHashMap<>());
    }

    private static String mensajePorCodigo(int codigoHttp) {
        switch (codigoHttp) {
            case 401:
                return "Tu sesión terminó. Inicia sesión de nuevo.";
            case 403:
                return "No tienes permiso para hacer esto.";
            case 404:
                return "No se encontró lo que buscabas. Revisa la URL del servidor.";
            case 500:
                return "Error interno del servidor.";
            default:
                return "Ocurrió un error (código " + codigoHttp + ").";
        }
    }

    public int getCodigoHttp() {
        return codigoHttp;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
