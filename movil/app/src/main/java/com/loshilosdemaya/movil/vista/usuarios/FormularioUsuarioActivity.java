package com.loshilosdemaya.movil.vista.usuarios;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityFormularioUsuarioBinding;
import com.loshilosdemaya.movil.modelo.Usuario;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.util.SesionActual;
import com.loshilosdemaya.movil.util.Validador;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;
import com.loshilosdemaya.movil.vista.comun.UtilVista;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Crear (sin id) o editar (con id) un usuario. Al editar, la contraseña es opcional:
 * si se deja vacía, la API conserva la actual.
 */
public class FormularioUsuarioActivity extends BaseActivity {

    private static final String EXTRA_ID_USUARIO = "id_usuario";

    private ActivityFormularioUsuarioBinding binding;
    private UsuarioViewModel viewModel;
    private Integer idUsuario;
    private Map<String, TextInputLayout> campos;

    /** idUsuario null = usuario nuevo. */
    public static Intent crearIntent(Context contexto, @Nullable Integer idUsuario) {
        Intent intencion = new Intent(contexto, FormularioUsuarioActivity.class);
        if (idUsuario != null) {
            intencion.putExtra(EXTRA_ID_USUARIO, idUsuario.intValue());
        }
        return intencion;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormularioUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idUsuario = getIntent().hasExtra(EXTRA_ID_USUARIO) ? getIntent().getIntExtra(EXTRA_ID_USUARIO, 0) : null;
        configurarToolbar(binding.toolbar,
                getString(idUsuario == null ? R.string.nuevo_usuario : R.string.editar_usuario), true);
        viewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);

        campos = new LinkedHashMap<>();
        campos.put("nombre_u", binding.campoNombre);
        campos.put("email_u", binding.campoCorreo);
        campos.put("telefono_u", binding.campoTelefono);
        campos.put("password", binding.campoPassword);
        campos.put("rol", binding.campoRol);

        UtilVista.llenarDesplegable(this, binding.selectorRol,
                Arrays.asList(Usuario.ROL_ADMINISTRADORA, Usuario.ROL_REPARTIDOR));
        binding.botonGuardar.setOnClickListener(v -> guardar());

        if (idUsuario == null) {
            binding.campoPassword.setHelperText(getString(R.string.ayuda_password_nueva));
        } else {
            binding.campoPassword.setHelperText(getString(R.string.ayuda_password_editar));
            // La API no deja cambiar el propio rol (para no quedar sin administradora)
            binding.campoRol.setEnabled(!SesionActual.esUsuarioDeLaSesion(idUsuario));
            if (savedInstanceState == null) {
                observar(viewModel.consultarUsuario(idUsuario), binding.cargando, this::llenarCampos);
            }
        }
    }

    private void llenarCampos(Usuario usuario) {
        escribirTexto(binding.campoNombre, usuario.getNombreU());
        escribirTexto(binding.campoCorreo, usuario.getEmailU());
        escribirTexto(binding.campoTelefono, usuario.getTelefonoU());
        binding.selectorRol.setText(usuario.getRol(), false);
    }

    private void guardar() {
        String nombre = leerTexto(binding.campoNombre);
        String correo = leerTexto(binding.campoCorreo);
        String telefono = leerTexto(binding.campoTelefono);
        // La contraseña no se recorta: los espacios pueden ser parte de ella (igual que en la API)
        String password = binding.campoPassword.getEditText() == null ? ""
                : binding.campoPassword.getEditText().getText().toString();
        String rol = binding.selectorRol.getText().toString();

        if (mostrarErroresCampos(Validador.validarUsuario(nombre, correo, telefono, password, rol, idUsuario == null), campos)) {
            return;
        }

        LiveData<Resultado<Usuario>> operacion = idUsuario == null
                ? viewModel.registrarUsuario(nombre, correo, telefono, password, rol)
                : viewModel.actualizarUsuario(idUsuario, nombre, correo, telefono, password, rol);

        binding.botonGuardar.setEnabled(false);
        observar(operacion, binding.cargando, usuario -> {
            Toast.makeText(this, R.string.usuario_guardado, Toast.LENGTH_SHORT).show();
            if (idUsuario == null) {
                startActivity(DetalleUsuarioActivity.crearIntent(this, usuario.getIdUsuario()));
            }
            finish();
        }, resultado -> {
            binding.botonGuardar.setEnabled(true);
            mostrarErrorFormulario(resultado, campos);
        });
    }
}
