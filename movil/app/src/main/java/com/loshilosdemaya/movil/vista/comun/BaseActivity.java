package com.loshilosdemaya.movil.vista.comun;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.loshilosdemaya.movil.red.ConexionApi;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.util.SesionActual;
import com.loshilosdemaya.movil.vista.login.LoginActivity;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Pantalla base: todas las actividades de la app heredan de aquí lo que repiten
 * (barra superior, mensajes, confirmaciones, manejo de errores de la API y de la sesión vencida).
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // La app dibuja detrás de las barras del sistema; los íconos de esas barras van en blanco
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.dark(Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
    }

    /**
     * Deja libre el espacio de la barra de estado, la barra de navegación y el teclado.
     * Se usa margen (no padding) para que en ese espacio se vea el fondo morado de la ventana
     * y no el fondo claro de la pantalla, donde los íconos blancos del sistema no se verían.
     */
    @Override
    public void setContentView(View vista) {
        super.setContentView(vista);
        ViewCompat.setOnApplyWindowInsetsListener(vista, (raiz, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            if (raiz.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams margenes = (ViewGroup.MarginLayoutParams) raiz.getLayoutParams();
                margenes.setMargins(barras.left, barras.top, barras.right, Math.max(barras.bottom, teclado.bottom));
                raiz.setLayoutParams(margenes);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }

    protected void configurarToolbar(MaterialToolbar toolbar, CharSequence titulo, boolean conRegreso) {
        setSupportActionBar(toolbar);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            barra.setTitle(titulo);
            barra.setDisplayHomeAsUpEnabled(conRegreso);
        }
    }

    // Flecha de regreso de la barra superior
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    /**
     * Observa una operación de la API: muestra el indicador mientras carga, llama a alTenerExito con
     * los datos si salió bien y, si falló, muestra el mensaje (o vuelve al login si la sesión venció).
     */
    protected <T> void observar(LiveData<Resultado<T>> operacion, @Nullable View indicadorCarga, Consumer<T> alTenerExito) {
        observar(operacion, indicadorCarga, alTenerExito, this::manejarError);
    }

    // Igual, pero quien llama decide qué hacer con el error (los formularios marcan los campos)
    protected <T> void observar(LiveData<Resultado<T>> operacion, @Nullable View indicadorCarga,
                                Consumer<T> alTenerExito, Consumer<Resultado<T>> alFallar) {
        operacion.observe(this, resultado -> {
            if (indicadorCarga != null) {
                indicadorCarga.setVisibility(resultado.estaCargando() ? View.VISIBLE : View.GONE);
            }

            if (resultado.esExito()) {
                alTenerExito.accept(resultado.getDatos());
            } else if (resultado.esError()) {
                if (resultado.esNoAutenticado()) {
                    irALogin();
                } else {
                    alFallar.accept(resultado);
                }
            }
        });
    }

    protected void manejarError(Resultado<?> resultado) {
        if (resultado.esNoAutenticado()) {
            irALogin();
        } else {
            mostrarMensaje(resultado.getMensajeError());
        }
    }

    /**
     * Marca en cada campo el error que mandó la API (422) o la validación local.
     * Los errores que no corresponden a ningún campo de la pantalla se muestran como mensaje.
     * Devuelve true si había errores.
     */
    protected boolean mostrarErroresCampos(Map<String, String> errores, Map<String, TextInputLayout> campos) {
        for (Map.Entry<String, TextInputLayout> campo : campos.entrySet()) {
            campo.getValue().setError(errores.get(campo.getKey()));
        }

        for (Map.Entry<String, String> error : errores.entrySet()) {
            if (!campos.containsKey(error.getKey())) {
                mostrarMensaje(error.getValue());
                break;
            }
        }

        return !errores.isEmpty();
    }

    // Error de un formulario: si trae errores por campo se marcan; si no, se muestra el mensaje general
    protected void mostrarErrorFormulario(Resultado<?> resultado, Map<String, TextInputLayout> campos) {
        if (resultado.getErroresCampos().isEmpty()) {
            mostrarMensaje(resultado.getMensajeError());
        } else {
            mostrarErroresCampos(resultado.getErroresCampos(), campos);
        }
    }

    protected void mostrarMensaje(String mensaje) {
        if (mensaje == null) {
            return;
        }
        Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG).show();
    }

    protected void confirmar(String titulo, String mensaje, String textoAceptar, Runnable alAceptar) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(textoAceptar, (dialogo, boton) -> alAceptar.run())
                .show();
    }

    // La sesión venció o el usuario fue desactivado: se vuelve al inicio de sesión sin dejar pantallas atrás
    protected void irALogin() {
        SesionActual.limpiar();
        ConexionApi.obtener().borrarSesionLocal();
        Toast.makeText(this, "Tu sesión terminó. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show();

        Intent intencion = new Intent(this, LoginActivity.class);
        intencion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intencion);
        finish();
    }

    // Texto de un campo sin espacios a los lados ("" si está vacío)
    protected static String leerTexto(TextInputLayout campo) {
        return campo.getEditText() == null || campo.getEditText().getText() == null
                ? ""
                : campo.getEditText().getText().toString().trim();
    }

    protected static void escribirTexto(TextInputLayout campo, String texto) {
        if (campo.getEditText() != null) {
            campo.getEditText().setText(texto == null ? "" : texto);
        }
    }
}
