package com.loshilosdemaya.movil.repositorio;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.loshilosdemaya.movil.modelo.Credenciales;
import com.loshilosdemaya.movil.modelo.ResumenDashboard;
import com.loshilosdemaya.movil.modelo.Sesion;
import com.loshilosdemaya.movil.red.ConexionApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Inicio y cierre de sesión, y el resumen del dashboard (lo primero que se ve al entrar). */
public class AuthRepositorio extends RepositorioBase {

    public LiveData<Resultado<Sesion>> iniciarSesion(String email, String password) {
        return ejecutar(api().iniciarSesion(new Credenciales(email, password)));
    }

    // Si ya hay una cookie de sesión válida (de una vez anterior), entra sin pedir la contraseña
    public LiveData<Resultado<Sesion>> consultarSesion() {
        return ejecutar(api().consultarSesion());
    }

    /**
     * Cierra la sesión en el servidor y borra la cookie del celular. La cookie se borra cuando
     * llega la respuesta (no antes, porque la petición la necesita) y también si falla la red:
     * en este celular la sesión queda cerrada de todas formas.
     */
    public LiveData<Resultado<Void>> cerrarSesion() {
        MutableLiveData<Resultado<Void>> resultado = new MutableLiveData<>(Resultado.cargando());

        api().cerrarSesion().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> respuesta) {
                terminar();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable falla) {
                terminar();
            }

            private void terminar() {
                ConexionApi.obtener().borrarSesionLocal();
                resultado.setValue(Resultado.exito(null));
            }
        });

        return resultado;
    }

    public LiveData<Resultado<ResumenDashboard>> consultarResumen() {
        return ejecutar(api().consultarResumen());
    }
}
