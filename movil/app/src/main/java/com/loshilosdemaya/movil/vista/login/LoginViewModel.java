package com.loshilosdemaya.movil.vista.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.loshilosdemaya.movil.modelo.Sesion;
import com.loshilosdemaya.movil.repositorio.AuthRepositorio;
import com.loshilosdemaya.movil.repositorio.Resultado;

/** Lógica de la pantalla de inicio de sesión; la vista no habla directo con la API. */
public class LoginViewModel extends ViewModel {

    private final AuthRepositorio authRepositorio = new AuthRepositorio();

    public LiveData<Resultado<Sesion>> iniciarSesion(String email, String password) {
        return authRepositorio.iniciarSesion(email, password);
    }

    public LiveData<Resultado<Sesion>> consultarSesion() {
        return authRepositorio.consultarSesion();
    }
}
