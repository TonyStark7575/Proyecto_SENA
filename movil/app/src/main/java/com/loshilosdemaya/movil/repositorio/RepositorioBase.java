package com.loshilosdemaya.movil.repositorio;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.loshilosdemaya.movil.red.ApiService;
import com.loshilosdemaya.movil.red.ConexionApi;
import com.loshilosdemaya.movil.red.ErrorApi;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Lo que comparten todos los repositorios: ejecutar una llamada de Retrofit en segundo plano
 * y publicar el resultado en un LiveData que la vista observa.
 */
public abstract class RepositorioBase {

    // Se pide cada vez: si el usuario cambia la URL del servidor, ConexionApi crea un ApiService nuevo
    protected ApiService api() {
        return ConexionApi.obtener().getServicio();
    }

    protected <T> LiveData<Resultado<T>> ejecutar(Call<T> llamada) {
        MutableLiveData<Resultado<T>> resultado = new MutableLiveData<>(Resultado.cargando());

        // enqueue: la petición corre en otro hilo y los métodos de Callback vuelven al hilo principal
        llamada.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> respuesta) {
                if (respuesta.isSuccessful()) {
                    resultado.setValue(Resultado.exito(respuesta.body()));
                } else {
                    resultado.setValue(Resultado.error(ErrorApi.leer(respuesta.code(), leerCuerpo(respuesta.errorBody()))));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable falla) {
                resultado.setValue(Resultado.error(ErrorApi.sinConexion()));
            }
        });

        return resultado;
    }

    private static String leerCuerpo(ResponseBody cuerpo) {
        if (cuerpo == null) {
            return "";
        }
        try {
            return cuerpo.string();
        } catch (IOException e) {
            return "";
        }
    }
}
