package com.loshilosdemaya.movil.red;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Guarda la cookie de sesión de PHP (PHPSESSID) igual que lo hace el navegador con la app React.
 * Sin esto, cada petición llegaría sin sesión y la API respondería 401.
 *
 * Las cookies se guardan también en SharedPreferences para que la sesión siga abierta
 * si se cierra la app. En las pruebas unitarias se usa sin preferencias (solo en memoria).
 */
public class GestorCookies implements CookieJar {

    private static final String PREFIJO_CLAVE = "cookies_";

    private final Map<String, List<Cookie>> cookiesPorServidor = new HashMap<>();
    private final SharedPreferences preferencias;

    public GestorCookies(SharedPreferences preferencias) {
        this.preferencias = preferencias;
    }

    // OkHttp lo llama con las cookies de cada respuesta (Set-Cookie)
    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookiesNuevas) {
        List<Cookie> guardadas = obtenerCookiesDelServidor(url);

        for (Cookie nueva : cookiesNuevas) {
            // Una cookie con el mismo nombre reemplaza a la anterior (por ejemplo al regenerar la sesión)
            guardadas.removeIf(guardada -> guardada.name().equals(nueva.name()));
            if (nueva.expiresAt() > System.currentTimeMillis()) {
                guardadas.add(nueva);
            }
        }

        guardarEnPreferencias(url.host(), guardadas);
    }

    // OkHttp lo llama antes de cada petición para saber qué cookies enviar
    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> validas = new ArrayList<>();
        long ahora = System.currentTimeMillis();

        for (Cookie cookie : obtenerCookiesDelServidor(url)) {
            if (cookie.expiresAt() > ahora && cookie.matches(url)) {
                validas.add(cookie);
            }
        }

        return validas;
    }

    // Al cerrar sesión o cambiar de servidor
    public synchronized void limpiarCookies() {
        if (preferencias != null) {
            SharedPreferences.Editor editor = preferencias.edit();
            for (String servidor : cookiesPorServidor.keySet()) {
                editor.remove(PREFIJO_CLAVE + servidor);
            }
            editor.apply();
        }
        cookiesPorServidor.clear();
    }

    // Busca primero en memoria y, si no está, en las preferencias (la primera vez después de abrir la app)
    private List<Cookie> obtenerCookiesDelServidor(HttpUrl url) {
        List<Cookie> cookies = cookiesPorServidor.get(url.host());

        if (cookies == null) {
            cookies = new ArrayList<>();
            if (preferencias != null) {
                Set<String> textos = preferencias.getStringSet(PREFIJO_CLAVE + url.host(), new HashSet<>());
                for (String texto : textos) {
                    Cookie cookie = Cookie.parse(url, texto);
                    if (cookie != null) {
                        cookies.add(cookie);
                    }
                }
            }
            cookiesPorServidor.put(url.host(), cookies);
        }

        return cookies;
    }

    private void guardarEnPreferencias(String servidor, List<Cookie> cookies) {
        if (preferencias == null) {
            return;
        }

        Set<String> textos = new HashSet<>();
        for (Cookie cookie : cookies) {
            textos.add(cookie.toString());
        }
        preferencias.edit().putStringSet(PREFIJO_CLAVE + servidor, textos).apply();
    }
}
