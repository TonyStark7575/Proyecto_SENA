package com.loshilosdemaya.movil.modelo;

import com.google.gson.annotations.SerializedName;

/** Cuerpo de api/login: { "email": "...", "password": "..." } */
public class Credenciales {

    @SerializedName("email")
    private final String email;

    @SerializedName("password")
    private final String password;

    public Credenciales(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
