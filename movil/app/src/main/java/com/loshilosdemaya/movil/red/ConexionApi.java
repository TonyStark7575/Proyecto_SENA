package com.loshilosdemaya.movil.red;

import android.content.Context;
import android.content.SharedPreferences;

import com.loshilosdemaya.movil.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Punto único de acceso a la API (patrón Singleton): arma Retrofit con la URL del servidor
 * y el gestor de cookies, y entrega el ApiService a los repositorios.
 *
 * La URL se guarda en SharedPreferences porque cambia según dónde corra la app:
 * - Emulador: http://10.0.2.2/ProyectoSENA/public/
 * - Celular en la misma red Wi-Fi: http://IP-DEL-PC/ProyectoSENA/public/
 */
public final class ConexionApi {

    private static final String PREFERENCIAS = "conexion_api";
    private static final String CLAVE_URL_SERVIDOR = "url_servidor";
    private static final int SEGUNDOS_ESPERA = 15;

    private static ConexionApi instancia;

    private final SharedPreferences preferencias;
    private final GestorCookies gestorCookies;
    private String urlServidor;
    private ApiService servicio;

    private ConexionApi(Context contexto) {
        preferencias = contexto.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        gestorCookies = new GestorCookies(preferencias);
        urlServidor = preferencias.getString(CLAVE_URL_SERVIDOR, BuildConfig.URL_SERVIDOR_INICIAL);
        servicio = crearServicio(urlServidor, gestorCookies, BuildConfig.DEBUG);
    }

    // Se llama una sola vez, al arrancar la app (AplicacionMaya)
    public static synchronized void inicializar(Context contexto) {
        if (instancia == null) {
            instancia = new ConexionApi(contexto.getApplicationContext());
        }
    }

    public static ConexionApi obtener() {
        if (instancia == null) {
            throw new IllegalStateException("ConexionApi no se ha inicializado");
        }
        return instancia;
    }

    /**
     * Crea el ApiService. Es estático y no depende de Android para poder usarlo
     * en las pruebas unitarias con un servidor falso (MockWebServer).
     */
    public static ApiService crearServicio(String urlServidor, CookieJar cookies, boolean registrarPeticiones) {
        OkHttpClient.Builder clienteHttp = new OkHttpClient.Builder()
                .cookieJar(cookies)
                .connectTimeout(SEGUNDOS_ESPERA, TimeUnit.SECONDS)
                .readTimeout(SEGUNDOS_ESPERA, TimeUnit.SECONDS)
                // X-Requested-With: la API lo exige para aceptar archivos (imagen del producto)
                .addInterceptor(cadena -> cadena.proceed(cadena.request().newBuilder()
                        .header("Accept", "application/json")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .build()));

        // En depuración, las peticiones y respuestas se ven en el Logcat (sin el cuerpo, por las contraseñas)
        if (registrarPeticiones) {
            HttpLoggingInterceptor registro = new HttpLoggingInterceptor();
            registro.setLevel(HttpLoggingInterceptor.Level.BASIC);
            clienteHttp.addInterceptor(registro);
        }

        return new Retrofit.Builder()
                .baseUrl(urlServidor)
                .client(clienteHttp.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    /**
     * Revisa que el texto sea una URL http(s) válida y le agrega la "/" final que Retrofit necesita.
     * Devuelve null si no es válida.
     */
    public static String normalizarUrl(String texto) {
        if (texto == null) {
            return null;
        }

        String url = texto.trim();
        if (!url.endsWith("/")) {
            url += "/";
        }

        HttpUrl httpUrl = HttpUrl.parse(url);
        return httpUrl != null ? httpUrl.toString() : null;
    }

    public ApiService getServicio() {
        return servicio;
    }

    public String getUrlServidor() {
        return urlServidor;
    }

    // Cambiar de servidor reinicia la sesión: la cookie del servidor anterior no sirve en el nuevo
    public synchronized void cambiarUrlServidor(String urlNueva) {
        if (urlNueva.equals(urlServidor)) {
            return;
        }

        urlServidor = urlNueva;
        preferencias.edit().putString(CLAVE_URL_SERVIDOR, urlNueva).apply();
        gestorCookies.limpiarCookies();
        servicio = crearServicio(urlServidor, gestorCookies, BuildConfig.DEBUG);
    }

    // Las imágenes de productos las sirve Apache desde public/img/
    public String construirUrlImagen(String nombreImagen) {
        return urlServidor + "img/" + nombreImagen;
    }

    public void borrarSesionLocal() {
        gestorCookies.limpiarCookies();
    }
}
