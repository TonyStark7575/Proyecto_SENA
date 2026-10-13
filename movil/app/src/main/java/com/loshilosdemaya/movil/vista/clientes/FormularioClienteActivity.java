package com.loshilosdemaya.movil.vista.clientes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityFormularioClienteBinding;
import com.loshilosdemaya.movil.modelo.Cliente;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.util.Validador;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;

import java.util.LinkedHashMap;
import java.util.Map;

/** Crear (sin id) o editar (con id) un cliente. */
public class FormularioClienteActivity extends BaseActivity {

    private static final String EXTRA_ID_CLIENTE = "id_cliente";

    private ActivityFormularioClienteBinding binding;
    private ClienteViewModel viewModel;
    private Integer idCliente;
    private Map<String, TextInputLayout> campos;

    /** idCliente null = cliente nuevo. */
    public static Intent crearIntent(Context contexto, @Nullable Integer idCliente) {
        Intent intencion = new Intent(contexto, FormularioClienteActivity.class);
        if (idCliente != null) {
            intencion.putExtra(EXTRA_ID_CLIENTE, idCliente.intValue());
        }
        return intencion;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormularioClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idCliente = getIntent().hasExtra(EXTRA_ID_CLIENTE) ? getIntent().getIntExtra(EXTRA_ID_CLIENTE, 0) : null;
        configurarToolbar(binding.toolbar,
                getString(idCliente == null ? R.string.nuevo_cliente : R.string.editar_cliente), true);
        viewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        campos = new LinkedHashMap<>();
        campos.put("nombre_c", binding.campoNombre);
        campos.put("email_c", binding.campoCorreo);
        campos.put("telefono_c", binding.campoTelefono);

        binding.botonGuardar.setOnClickListener(v -> guardar());

        if (idCliente != null && savedInstanceState == null) {
            observar(viewModel.consultarCliente(idCliente), binding.cargando, this::llenarCampos);
        }
    }

    private void llenarCampos(Cliente cliente) {
        escribirTexto(binding.campoNombre, cliente.getNombreC());
        escribirTexto(binding.campoCorreo, cliente.getEmailC());
        escribirTexto(binding.campoTelefono, cliente.getTelefonoC());
    }

    private void guardar() {
        String nombre = leerTexto(binding.campoNombre);
        String correo = leerTexto(binding.campoCorreo);
        String telefono = leerTexto(binding.campoTelefono);

        if (mostrarErroresCampos(Validador.validarCliente(nombre, correo, telefono), campos)) {
            return;
        }

        LiveData<Resultado<Cliente>> operacion = idCliente == null
                ? viewModel.registrarCliente(nombre, correo, telefono)
                : viewModel.actualizarCliente(idCliente, nombre, correo, telefono);

        binding.botonGuardar.setEnabled(false);
        // Si el correo ya existe, la API responde 422 con el error en email_c y se marca en el campo
        observar(operacion, binding.cargando, cliente -> {
            Toast.makeText(this, R.string.cliente_guardado, Toast.LENGTH_SHORT).show();
            if (idCliente == null) {
                startActivity(DetalleClienteActivity.crearIntent(this, cliente.getIdCliente()));
            }
            finish();
        }, resultado -> {
            binding.botonGuardar.setEnabled(true);
            mostrarErrorFormulario(resultado, campos);
        });
    }
}
