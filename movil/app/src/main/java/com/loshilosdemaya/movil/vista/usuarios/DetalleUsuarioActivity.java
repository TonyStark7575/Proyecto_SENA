package com.loshilosdemaya.movil.vista.usuarios;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityDetalleUsuarioBinding;
import com.loshilosdemaya.movil.modelo.ResultadoEliminacion;
import com.loshilosdemaya.movil.modelo.Usuario;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.util.SesionActual;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;

/**
 * Detalle de un usuario. Desde aquí se activa o desactiva su acceso (api/usuarios/activo).
 * Nadie puede desactivarse ni eliminarse a sí mismo: la app lo oculta y la API lo bloquea.
 */
public class DetalleUsuarioActivity extends BaseActivity {

    private static final String EXTRA_ID_USUARIO = "id_usuario";

    private ActivityDetalleUsuarioBinding binding;
    private UsuarioViewModel viewModel;
    private int idUsuario;
    private Usuario usuario;

    // Se guarda para poder cambiar el interruptor desde el código sin disparar otra petición
    private final CompoundButton.OnCheckedChangeListener alCambiarActivo =
            (boton, activo) -> cambiarActivo(activo);

    public static Intent crearIntent(Context contexto, int idUsuario) {
        return new Intent(contexto, DetalleUsuarioActivity.class).putExtra(EXTRA_ID_USUARIO, idUsuario);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalleUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idUsuario = getIntent().getIntExtra(EXTRA_ID_USUARIO, 0);
        configurarToolbar(binding.toolbar, getString(R.string.detalle_usuario), true);
        viewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        observar(viewModel.consultarUsuario(idUsuario), binding.cargando, this::mostrarUsuario);
    }

    private void mostrarUsuario(Usuario usuarioConsultado) {
        usuario = usuarioConsultado;
        binding.nombre.setText(usuario.getNombreU());
        binding.rol.setText(usuario.getRol());
        binding.correo.setText(usuario.getEmailU());
        binding.telefono.setText(Formato.textoOGuion(usuario.getTelefonoU()));
        binding.fecha.setText(Formato.formatearFecha(usuario.getFechaRegistro()));

        Usuario.RegistrosUsuario registros = usuario.getRegistros();
        int clientes = registros == null ? 0 : registros.getClientes();
        int pedidos = registros == null ? 0 : registros.getPedidos();
        binding.registros.setText(getString(R.string.registros_valor, clientes, pedidos));

        boolean esYo = SesionActual.esUsuarioDeLaSesion(usuario.getIdUsuario());
        binding.interruptorActivo.setOnCheckedChangeListener(null);
        binding.interruptorActivo.setChecked(usuario.estaActivo());
        binding.interruptorActivo.setEnabled(!esYo);
        binding.interruptorActivo.setOnCheckedChangeListener(alCambiarActivo);
        binding.notaActivo.setText(esYo
                ? getString(R.string.nota_no_desactivarse)
                : getString(usuario.estaActivo() ? R.string.nota_activo : R.string.nota_inactivo));

        invalidateOptionsMenu();
    }

    private void cambiarActivo(boolean activo) {
        observar(viewModel.cambiarActivo(idUsuario, activo), binding.cargando, usuarioNuevo -> {
            mostrarUsuario(usuarioNuevo);
            mostrarMensaje(getString(activo ? R.string.usuario_activado : R.string.usuario_desactivado));
        }, resultado -> {
            // Si la API lo rechaza, el interruptor vuelve a como estaba
            if (usuario != null) {
                mostrarUsuario(usuario);
            }
            manejarError(resultado);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        // No se ofrece eliminar el propio usuario
        boolean esYo = usuario != null && SesionActual.esUsuarioDeLaSesion(usuario.getIdUsuario());
        menu.findItem(R.id.accion_eliminar).setVisible(!esYo);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.accion_editar) {
            startActivity(FormularioUsuarioActivity.crearIntent(this, idUsuario));
            return true;
        }
        if (item.getItemId() == R.id.accion_eliminar && usuario != null) {
            // Si tiene clientes o pedidos, se avisa que en realidad se va a desactivar
            Usuario.RegistrosUsuario registros = usuario.getRegistros();
            String mensaje = registros != null && registros.tieneRegistros()
                    ? getString(R.string.confirmar_eliminar_usuario_con_registros, usuario.getNombreU(),
                    registros.getClientes(), registros.getPedidos())
                    : getString(R.string.confirmar_eliminar, usuario.getNombreU());
            confirmar(getString(R.string.eliminar_usuario), mensaje, getString(R.string.eliminar), this::eliminarUsuario);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void eliminarUsuario() {
        observar(viewModel.eliminarUsuario(idUsuario), binding.cargando, this::alEliminar);
    }

    // 204 (sin cuerpo): se borró. 200 con resultado "desactivado": se conservó desactivado
    private void alEliminar(@Nullable ResultadoEliminacion resultado) {
        if (resultado != null && resultado.fueDesactivado()) {
            if (resultado.getUsuario() != null) {
                mostrarUsuario(resultado.getUsuario());
            }
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.usuario_desactivado)
                    .setMessage(resultado.getMensaje())
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }

        Toast.makeText(this, R.string.usuario_eliminado, Toast.LENGTH_SHORT).show();
        finish();
    }
}
