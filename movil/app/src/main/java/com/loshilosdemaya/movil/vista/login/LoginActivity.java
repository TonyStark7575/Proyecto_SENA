package com.loshilosdemaya.movil.vista.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityLoginBinding;
import com.loshilosdemaya.movil.modelo.Sesion;
import com.loshilosdemaya.movil.red.ConexionApi;
import com.loshilosdemaya.movil.util.SesionActual;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;
import com.loshilosdemaya.movil.vista.dashboard.DashboardActivity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Inicio de sesión contra api/login. Si quedó una sesión abierta de antes (cookie guardada),
 * entra directo al dashboard sin pedir la contraseña.
 *
 * Aquí un 401 significa "credenciales incorrectas", no "sesión vencida": por eso esta pantalla
 * observa las respuestas por su cuenta en vez de usar observar() de BaseActivity.
 */
public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private Map<String, TextInputLayout> campos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Llaves iguales a las que usa la API en sus errores de validación
        campos = new LinkedHashMap<>();
        campos.put("email", binding.campoCorreo);
        campos.put("password", binding.campoPassword);

        escribirTexto(binding.campoServidor, ConexionApi.obtener().getUrlServidor());

        binding.botonIngresar.setOnClickListener(v -> iniciarSesion());
        if (binding.campoPassword.getEditText() != null) {
            binding.campoPassword.getEditText().setOnEditorActionListener((vista, accion, evento) -> {
                if (accion == EditorInfo.IME_ACTION_DONE) {
                    iniciarSesion();
                    return true;
                }
                return false;
            });
        }

        if (savedInstanceState == null) {
            verificarSesionExistente();
        }
    }

    private void verificarSesionExistente() {
        mostrarCargando(true);
        viewModel.consultarSesion().observe(this, resultado -> {
            if (resultado.esExito()) {
                entrar(resultado.getDatos());
            } else if (resultado.esError()) {
                // Sin sesión o sin conexión: simplemente se muestra el formulario
                mostrarCargando(false);
            }
        });
    }

    private void iniciarSesion() {
        String email = leerTexto(binding.campoCorreo);
        String password = binding.campoPassword.getEditText() == null ? ""
                : binding.campoPassword.getEditText().getText().toString();

        // Validación local con los mismos mensajes que la API
        Map<String, String> errores = new LinkedHashMap<>();
        if (email.isEmpty()) {
            errores.put("email", "El correo es obligatorio");
        }
        if (password.isEmpty()) {
            errores.put("password", "La contraseña es obligatoria");
        }

        String url = ConexionApi.normalizarUrl(leerTexto(binding.campoServidor));
        binding.campoServidor.setError(url == null ? getString(R.string.error_servidor) : null);

        if (mostrarErroresCampos(errores, campos) || url == null) {
            return;
        }

        ConexionApi.obtener().cambiarUrlServidor(url);

        viewModel.iniciarSesion(email, password).observe(this, resultado -> {
            mostrarCargando(resultado.estaCargando());

            if (resultado.esExito()) {
                entrar(resultado.getDatos());
            } else if (resultado.esError()) {
                // 401: credenciales incorrectas · 403: usuario desactivado · 422: campos vacíos
                mostrarErrorFormulario(resultado, campos);
            }
        });
    }

    private void entrar(Sesion sesion) {
        SesionActual.guardar(sesion);
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    private void mostrarCargando(boolean cargando) {
        binding.cargando.setVisibility(cargando ? View.VISIBLE : View.GONE);
        binding.botonIngresar.setEnabled(!cargando);
    }
}
