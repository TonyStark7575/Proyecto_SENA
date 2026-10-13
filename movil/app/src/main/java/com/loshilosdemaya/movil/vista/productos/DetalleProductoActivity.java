package com.loshilosdemaya.movil.vista.productos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityDetalleProductoBinding;
import com.loshilosdemaya.movil.modelo.Producto;
import com.loshilosdemaya.movil.util.CompresorImagen;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;
import com.loshilosdemaya.movil.vista.comun.UtilVista;

import java.io.IOException;

/**
 * Detalle de un producto. Desde aquí se cambia o se quita su imagen
 * (api/productos/imagen), igual que en la web.
 */
public class DetalleProductoActivity extends BaseActivity {

    private static final String EXTRA_ID_PRODUCTO = "id_producto";

    private ActivityDetalleProductoBinding binding;
    private ProductoViewModel viewModel;
    private int idProducto;
    private Producto producto;

    // Selector de fotos del sistema: no necesita pedir permiso de almacenamiento
    private final ActivityResultLauncher<PickVisualMediaRequest> selectorImagen =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::subirImagen);

    public static Intent crearIntent(Context contexto, int idProducto) {
        return new Intent(contexto, DetalleProductoActivity.class).putExtra(EXTRA_ID_PRODUCTO, idProducto);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalleProductoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idProducto = getIntent().getIntExtra(EXTRA_ID_PRODUCTO, 0);
        configurarToolbar(binding.toolbar, getString(R.string.detalle_producto), true);
        viewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

        binding.botonCambiarImagen.setOnClickListener(v -> selectorImagen.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));
        binding.botonQuitarImagen.setOnClickListener(v -> confirmar(getString(R.string.quitar_imagen),
                getString(R.string.confirmar_quitar_imagen), getString(R.string.quitar_imagen), this::quitarImagen));
    }

    // Se consulta cada vez que se muestra, por si se editó en el formulario
    @Override
    protected void onResume() {
        super.onResume();
        observar(viewModel.consultarProducto(idProducto), binding.cargando, this::mostrarProducto);
    }

    private void mostrarProducto(Producto productoConsultado) {
        producto = productoConsultado;
        binding.nombre.setText(producto.getNombreP());
        binding.precio.setText(Formato.formatearPrecio(producto.getPrecio()));
        binding.descripcion.setText(Formato.textoOGuion(producto.getDescripcion()));
        binding.fecha.setText(Formato.formatearFecha(producto.getFechaRegistro()));
        UtilVista.cargarImagenProducto(binding.imagen, producto.getImagen());
        binding.botonQuitarImagen.setVisibility(producto.getImagen() == null ? View.GONE : View.VISIBLE);
    }

    private void subirImagen(@Nullable Uri imagen) {
        if (imagen == null) {
            return; // El usuario cerró el selector sin elegir
        }

        byte[] contenido;
        try {
            contenido = CompresorImagen.comprimir(getContentResolver(), imagen);
        } catch (IOException | RuntimeException e) {
            mostrarMensaje(getString(R.string.error_leer_imagen));
            return;
        }

        observar(viewModel.subirImagen(idProducto, contenido, CompresorImagen.TIPO_MIME), binding.cargando, productoNuevo -> {
            mostrarProducto(productoNuevo);
            mostrarMensaje(getString(R.string.imagen_actualizada));
        });
    }

    private void quitarImagen() {
        observar(viewModel.quitarImagen(idProducto), binding.cargando, this::mostrarProducto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.accion_editar) {
            startActivity(FormularioProductoActivity.crearIntent(this, idProducto));
            return true;
        }
        if (item.getItemId() == R.id.accion_eliminar) {
            String nombre = producto != null ? producto.getNombreP() : "";
            confirmar(getString(R.string.eliminar_producto), getString(R.string.confirmar_eliminar, nombre),
                    getString(R.string.eliminar), this::eliminarProducto);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Si el producto está en algún pedido, la API responde 409 y se muestra su mensaje
    private void eliminarProducto() {
        observar(viewModel.eliminarProducto(idProducto), binding.cargando, sinDatos -> {
            Toast.makeText(this, R.string.producto_eliminado, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
