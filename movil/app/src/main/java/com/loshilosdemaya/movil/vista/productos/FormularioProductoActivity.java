package com.loshilosdemaya.movil.vista.productos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityFormularioProductoBinding;
import com.loshilosdemaya.movil.modelo.Producto;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.util.Validador;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Crear (sin id) o editar (con id) un producto. La imagen no va aquí: la API la recibe aparte,
 * así que después de crear se abre el detalle para agregarla.
 */
public class FormularioProductoActivity extends BaseActivity {

    private static final String EXTRA_ID_PRODUCTO = "id_producto";

    private ActivityFormularioProductoBinding binding;
    private ProductoViewModel viewModel;
    private Integer idProducto;
    private Map<String, TextInputLayout> campos;

    /** idProducto null = producto nuevo. */
    public static Intent crearIntent(Context contexto, @Nullable Integer idProducto) {
        Intent intencion = new Intent(contexto, FormularioProductoActivity.class);
        if (idProducto != null) {
            intencion.putExtra(EXTRA_ID_PRODUCTO, idProducto.intValue());
        }
        return intencion;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormularioProductoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idProducto = getIntent().hasExtra(EXTRA_ID_PRODUCTO) ? getIntent().getIntExtra(EXTRA_ID_PRODUCTO, 0) : null;
        configurarToolbar(binding.toolbar,
                getString(idProducto == null ? R.string.nuevo_producto : R.string.editar_producto), true);
        viewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

        campos = new LinkedHashMap<>();
        campos.put("nombre_p", binding.campoNombre);
        campos.put("precio", binding.campoPrecio);
        campos.put("descripcion", binding.campoDescripcion);

        binding.botonGuardar.setOnClickListener(v -> guardar());

        // Al editar se cargan los datos actuales (solo la primera vez: al girar el celular, Android conserva lo escrito)
        if (idProducto != null) {
            binding.notaImagen.setVisibility(View.GONE);
            if (savedInstanceState == null) {
                observar(viewModel.consultarProducto(idProducto), binding.cargando, this::llenarCampos);
            }
        }
    }

    private void llenarCampos(Producto producto) {
        escribirTexto(binding.campoNombre, producto.getNombreP());
        escribirTexto(binding.campoPrecio, producto.getPrecio() == null ? "" : producto.getPrecio().stripTrailingZeros().toPlainString());
        escribirTexto(binding.campoDescripcion, producto.getDescripcion());
    }

    private void guardar() {
        String nombre = leerTexto(binding.campoNombre);
        String precioTexto = leerTexto(binding.campoPrecio);
        String descripcion = leerTexto(binding.campoDescripcion);

        if (mostrarErroresCampos(Validador.validarProducto(nombre, precioTexto), campos)) {
            return;
        }

        BigDecimal precio = Validador.convertirMonto(precioTexto);
        LiveData<Resultado<Producto>> operacion = idProducto == null
                ? viewModel.registrarProducto(nombre, precio, descripcion)
                : viewModel.actualizarProducto(idProducto, nombre, precio, descripcion);

        binding.botonGuardar.setEnabled(false);
        observar(operacion, binding.cargando, this::alGuardar, resultado -> {
            binding.botonGuardar.setEnabled(true);
            mostrarErrorFormulario(resultado, campos);
        });
    }

    private void alGuardar(Producto producto) {
        Toast.makeText(this, R.string.producto_guardado, Toast.LENGTH_SHORT).show();
        if (idProducto == null) {
            // Producto nuevo: se abre su detalle para que pueda agregarle la imagen
            startActivity(DetalleProductoActivity.crearIntent(this, producto.getIdPro()));
        }
        finish();
    }
}
