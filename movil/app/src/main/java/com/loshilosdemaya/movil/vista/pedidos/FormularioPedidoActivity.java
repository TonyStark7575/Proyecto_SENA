package com.loshilosdemaya.movil.vista.pedidos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputLayout;
import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityFormularioPedidoBinding;
import com.loshilosdemaya.movil.modelo.Cliente;
import com.loshilosdemaya.movil.modelo.Pedido;
import com.loshilosdemaya.movil.modelo.Producto;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.util.CarritoPedido;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.util.Validador;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;
import com.loshilosdemaya.movil.vista.comun.UtilVista;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Crear o editar un pedido. Funciona distinto en cada caso porque así trabaja la API:
 *
 * - Nuevo: los productos se van juntando en un carrito local y se envían todos con el pedido
 *   (POST api/pedidos), que la API guarda en una sola transacción.
 * - Editar: "Guardar" solo cambia cliente y estado (PUT api/pedidos). Los productos se agregan
 *   o se quitan uno por uno y se guardan de inmediato (api/pedidos/lineas).
 */
public class FormularioPedidoActivity extends BaseActivity {

    private static final String EXTRA_ID_PEDIDO = "id_pedido";

    private ActivityFormularioPedidoBinding binding;
    private PedidoViewModel viewModel;
    private LineaPedidoAdapter adaptadorLineas;
    private Map<String, TextInputLayout> campos;

    private Integer idPedido;
    private Producto productoSeleccionado;

    /** idPedido null = pedido nuevo. */
    public static Intent crearIntent(Context contexto, @Nullable Integer idPedido) {
        Intent intencion = new Intent(contexto, FormularioPedidoActivity.class);
        if (idPedido != null) {
            intencion.putExtra(EXTRA_ID_PEDIDO, idPedido.intValue());
        }
        return intencion;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormularioPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idPedido = getIntent().hasExtra(EXTRA_ID_PEDIDO) ? getIntent().getIntExtra(EXTRA_ID_PEDIDO, 0) : null;
        configurarToolbar(binding.toolbar,
                idPedido == null ? getString(R.string.nuevo_pedido) : getString(R.string.editar_pedido_numero, idPedido), true);
        viewModel = new ViewModelProvider(this).get(PedidoViewModel.class);

        // Llaves de los errores que devuelve la API (crear, actualizar y agregar línea)
        campos = new LinkedHashMap<>();
        campos.put("id_cliente", binding.campoCliente);
        campos.put("estado", binding.campoEstado);
        campos.put("id_pro", binding.campoProducto);
        campos.put("cantidad", binding.campoCantidad);

        adaptadorLineas = new LineaPedidoAdapter(this::quitarLinea);
        binding.listaLineas.setLayoutManager(new LinearLayoutManager(this));
        binding.listaLineas.setAdapter(adaptadorLineas);

        UtilVista.llenarDesplegable(this, binding.selectorEstado, Arrays.asList(UtilVista.ESTADOS_PEDIDO));
        if (savedInstanceState == null) {
            binding.selectorEstado.setText(Pedido.ESTADO_PENDIENTE, false);
            escribirTexto(binding.campoCantidad, "1");
        }

        binding.selectorCliente.setOnItemClickListener((padre, vista, posicion, id) -> {
            Cliente cliente = (Cliente) padre.getItemAtPosition(posicion);
            viewModel.setIdClienteSeleccionado(cliente.getIdCliente());
            binding.campoCliente.setError(null);
        });
        binding.selectorProducto.setOnItemClickListener((padre, vista, posicion, id) -> {
            productoSeleccionado = (Producto) padre.getItemAtPosition(posicion);
            binding.campoProducto.setError(null);
        });

        binding.botonAgregar.setOnClickListener(v -> agregarProducto());
        binding.botonGuardar.setOnClickListener(v -> guardar());

        cargarOpciones();

        if (idPedido == null) {
            mostrarCarrito();
        } else {
            // Al girar el celular, Android conserva lo escrito en cliente y estado: solo se recargan los productos
            boolean primeraCarga = savedInstanceState == null;
            observar(viewModel.consultarPedido(idPedido), binding.cargando, pedido -> {
                if (primeraCarga) {
                    mostrarPedidoGuardado(pedido);
                }
                actualizarLineasGuardadas(pedido);
            });
        }
    }

    // Clientes y productos para los menús desplegables
    private void cargarOpciones() {
        observar(viewModel.consultarClientes(), binding.cargando,
                clientes -> UtilVista.llenarDesplegable(this, binding.selectorCliente, clientes));
        observar(viewModel.consultarProductos(), binding.cargando,
                productos -> UtilVista.llenarDesplegable(this, binding.selectorProducto, productos));
    }

    // ===== Pedido nuevo: carrito local =====

    private void mostrarCarrito() {
        CarritoPedido carrito = viewModel.getCarrito();
        adaptadorLineas.mostrarCarrito(carrito);
        binding.sinLineas.setVisibility(carrito.estaVacio() ? View.VISIBLE : View.GONE);
        binding.total.setText(getString(R.string.total_valor, Formato.formatearPrecio(carrito.calcularTotal())));
    }

    // ===== Pedido existente: líneas guardadas en la BD =====

    private void mostrarPedidoGuardado(Pedido pedido) {
        viewModel.setIdClienteSeleccionado(pedido.getIdClienteP());
        binding.selectorCliente.setText(pedido.getNombreC(), false);
        binding.selectorEstado.setText(pedido.getEstado(), false);
    }

    private void actualizarLineasGuardadas(Pedido pedido) {
        adaptadorLineas.mostrarLineas(pedido.getLineas());
        boolean sinLineas = pedido.getLineas() == null || pedido.getLineas().isEmpty();
        binding.sinLineas.setVisibility(sinLineas ? View.VISIBLE : View.GONE);
        binding.total.setText(getString(R.string.total_valor, Formato.formatearPrecio(pedido.getTotal())));

        // Entregado o Cancelado: la API ya no deja cambiar los productos
        boolean cerrado = pedido.estaCerrado();
        binding.filaAgregar.setVisibility(cerrado ? View.GONE : View.VISIBLE);
        binding.botonAgregar.setVisibility(cerrado ? View.GONE : View.VISIBLE);
        adaptadorLineas.permitirQuitar(cerrado ? null : this::quitarLinea);
        binding.notaProductos.setVisibility(View.VISIBLE);
        binding.notaProductos.setText(cerrado
                ? getString(R.string.nota_pedido_cerrado, pedido.getEstado())
                : getString(R.string.nota_lineas_inmediatas));
    }

    // ===== Acciones =====

    private void agregarProducto() {
        if (productoSeleccionado == null) {
            binding.campoProducto.setError(getString(R.string.error_elige_producto));
            return;
        }

        String errorCantidad = Validador.validarCantidad(leerTexto(binding.campoCantidad));
        binding.campoCantidad.setError(errorCantidad);
        if (errorCantidad != null) {
            return;
        }
        int cantidad = Integer.parseInt(leerTexto(binding.campoCantidad));

        if (idPedido == null) {
            String error = viewModel.getCarrito().agregarProducto(productoSeleccionado, cantidad);
            binding.campoCantidad.setError(error);
            if (error == null) {
                limpiarSeleccionProducto();
                mostrarCarrito();
            }
        } else {
            observar(viewModel.agregarLinea(idPedido, productoSeleccionado.getIdPro(), cantidad), binding.cargando, pedido -> {
                limpiarSeleccionProducto();
                actualizarLineasGuardadas(pedido);
            }, resultado -> mostrarErrorFormulario(resultado, campos));
        }
    }

    private void quitarLinea(LineaPedidoAdapter.FilaLinea fila) {
        if (idPedido == null) {
            viewModel.getCarrito().quitarProducto(fila.getId());
            mostrarCarrito();
            return;
        }

        // En un pedido guardado se quita de inmediato: se confirma primero
        confirmar(getString(R.string.quitar_producto), getString(R.string.confirmar_quitar_producto, fila.getNombre()),
                getString(R.string.quitar), () ->
                        observar(viewModel.quitarLinea(fila.getId()), binding.cargando, this::actualizarLineasGuardadas));
    }

    private void limpiarSeleccionProducto() {
        productoSeleccionado = null;
        binding.selectorProducto.setText("", false);
        escribirTexto(binding.campoCantidad, "1");
        binding.campoProducto.setError(null);
        binding.campoCantidad.setError(null);
    }

    private void guardar() {
        String estado = binding.selectorEstado.getText().toString();

        // Validación local con los mismos mensajes de la API
        Map<String, String> errores = new LinkedHashMap<>();
        if (viewModel.getIdClienteSeleccionado() == null) {
            errores.put("id_cliente", "Selecciona un cliente válido");
        }
        if (!Arrays.asList(UtilVista.ESTADOS_PEDIDO).contains(estado)) {
            errores.put("estado", "El estado debe ser Pendiente, En proceso, Entregado o Cancelado");
        }
        if (idPedido == null && viewModel.getCarrito().estaVacio()) {
            errores.put("lineas", "Agrega al menos un producto al pedido");
        }
        if (mostrarErroresCampos(errores, campos)) {
            return;
        }

        binding.botonGuardar.setEnabled(false);
        if (idPedido == null) {
            observar(viewModel.registrarPedido(estado), binding.cargando, pedido -> {
                Toast.makeText(this, R.string.pedido_guardado, Toast.LENGTH_SHORT).show();
                startActivity(DetallePedidoActivity.crearIntent(this, pedido.getIdPedido()));
                finish();
            }, this::alFallarGuardado);
        } else {
            observar(viewModel.actualizarPedido(idPedido, estado), binding.cargando, pedido -> {
                Toast.makeText(this, R.string.pedido_guardado, Toast.LENGTH_SHORT).show();
                finish();
            }, this::alFallarGuardado);
        }
    }

    private void alFallarGuardado(Resultado<Pedido> resultado) {
        binding.botonGuardar.setEnabled(true);
        mostrarErrorFormulario(resultado, campos);
    }
}
