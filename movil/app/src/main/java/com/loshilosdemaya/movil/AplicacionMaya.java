package com.loshilosdemaya.movil;

import android.app.Application;

import com.loshilosdemaya.movil.red.ConexionApi;

/** Se ejecuta una vez al abrir la app, antes de cualquier pantalla: prepara la conexión con la API. */
public class AplicacionMaya extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ConexionApi.inicializar(this);
    }
}
