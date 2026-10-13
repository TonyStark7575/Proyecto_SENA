package com.loshilosdemaya.movil.vista.pagos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityFormularioPagoBinding;
import com.loshilosdemaya.movil.modelo.Pago;
import com.loshilosdemaya.movil.modelo.Pedido;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.util.Validador;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;
import com.loshilosdemaya.movil.vista.comun.UtilVista;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registrar un pago (eligiendo el pedido) o editar uno existente (solo monto y método).
 * El monto no puede pasar de lo que falta por pagar: la app lo revisa antes y la API también.
 */
public class FormularioPagoActivity extends BaseActivity {

    private static final String EXTRA_ID_PAGO = "id_pago";
    private static final String EXTRA_ID_PEDIDO = "id_pedido";
    private static final String CLAVE_ID_PEDIDO = "id_pedido_elegido";

    private ActivityFormularioPagoBinding binding;
    private PagoViewModel viewModel;
    private Map<String, TextInputLayout> campos;

    private Integer idPago;
    private Integer idPedido;
    // Lo máximo que se puede pagar (saldo del pedido; al editar, saldo + lo que ya valía este pago)
    private BigDecimal montoMaximo;

    /** Pedido que se muestra en el menú, con el texto "Pedido #12 · Ana · debe $20.000". */
    private static class OpcionPedido {

        final Pedido pedido;
        final String texto;

        OpcionPedido(Pedido pedido, String texto) {
            this.pedido = pedido;
            this.texto = texto;
        }

        @NonNull
        @Override
        public String toString() {
            return texto;
        }
    }

    /** Pago nuevo. idPedido: el pedido ya elegido (desde su detalle) o null para elegirlo aquí. */
    public static Intent crearIntentNuevo(Context contexto, @Nullable Integer idPedido) {
        Intent intencion = new Intent(contexto, FormularioPagoActivity.class);
        if (idPedido != null) {
            intencion.putExtra(EXTRA_ID_PEDIDO, idPedido.intValue());
        }
        return intencion;
    }

    public static Intent crearIntentEditar(Context contexto, int idPago) {
        return new Intent(contexto, FormularioPagoActivity.class).putExtra(EXTRA_ID_PAGO, idPago);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormularioPagoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idPago = getIntent().hasExtra(EXTRA_ID_PAGO) ? getIntent().getIntExtra(EXTRA_ID_PAGO, 0) : null;
        if (savedInstanceState != null && savedInstanceState.containsKey(CLAVE_ID_PEDIDO)) {
            idPedido = savedInstanceState.getInt(CLAVE_ID_PEDIDO);
        } else if (getIntent().hasExtra(EXTRA_ID_PEDIDO)) {
            idPedido = getIntent().getIntExtra(EXTRA_ID_PEDIDO, 0);
        }

        configurarToolbar(binding.toolbar,
                getString(idPago == null ? R.string.registrar_pago : R.string.editar_pago), true);
        viewModel = new ViewModelProvider(this).get(PagoViewModel.class);

        campos = new LinkedHashMap<>();
        campos.put("id_pedido", binding.campoPedido);
        campos.put("monto", binding.campoMonto);
        campos.put("metodo", binding.campoMetodo);

        UtilVista.llenarDesplegable(this, binding.selectorMetodo,
                Arrays.asList(Pago.METODO_EFECTIVO, Pago.METODO_TRANSFERENCIA));

        binding.selectorPedido.setOnItemClickListener((padre, vista, posicion, id) ->
                elegirPedido(((OpcionPedido) padre.getItemAtPosition(posicion)).pedido));
        binding.botonGuardar.setOnClickListener(v -> guardar());

        if (idPago == null) {
            cargarPedidosConSaldo();
        } else {
            // Al editar, el pedido no se puede cambiar
            binding.campoPedido.setEnabled(false);
            boolean primeraCarga = savedInstanceState == null;
            observar(viewModel.consultarPago(idPago), binding.cargando, pago -> mostrarPagoGuardado(pago, primeraCarga));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle estado) {
        super.onSaveInstanceState(estado);
        if (idPedido != null) {
            estado.putInt(CLAVE_ID_PEDIDO, idPedido);
        }
    }

    // Solo se ofrecen los pedidos que admiten pagos: no cancelados y con saldo pendiente
    private void cargarPedidosConSaldo() {
        observar(viewModel.consultarPedidos(), binding.cargando, pedidos -> {
            List<OpcionPedido> opciones = new ArrayList<>();
            for (Pedido pedido : pedidos) {
                if (pedido.admitePagos()) {
                    opciones.add(new OpcionPedido(pedido, getString(R.string.opcion_pedido,
                            pedido.getIdPedido(), pedido.getNombreC(), Formato.formatearPrecio(pedido.getSaldo()))));
                }
            }
            UtilVista.llenarDesplegable(this, binding.selectorPedido, opciones);

            if (opciones.isEmpty()) {
                binding.campoPedido.setError(getString(R.string.sin_pedidos_con_saldo));
            }

            // Si llegó desde el detalle de un pedido (o se giró el celular), queda elegido
            if (idPedido != null) {
                for (OpcionPedido opcion : opciones) {
                    if (opcion.pedido.getIdPedido().equals(idPedido)) {
                        binding.selectorPedido.setText(opcion.texto, false);
                        elegirPedido(opcion.pedido);
                        return;
                    }
                }
                // El pedido ya no admite pagos (por ejemplo, se pagó completo mientras tanto)
                idPedido = null;
            }
        });
    }

    private void elegirPedido(Pedido pedido) {
        idPedido = pedido.getIdPedido();
        montoMaximo = pedido.getSaldo();
        binding.campoPedido.setError(null);
        binding.infoPedido.setVisibility(View.VISIBLE);
        binding.infoPedido.setText(getString(R.string.info_pedido_pago,
                Formato.formatearPrecio(pedido.getTotal()), Formato.formatearPrecio(pedido.getSaldo())));
    }

    private void mostrarPagoGuardado(Pago pago, boolean llenarCampos) {
        idPedido = pago.getIdPedido();
        BigDecimal saldo = pago.getSaldo() == null ? BigDecimal.ZERO : pago.getSaldo();
        BigDecimal monto = pago.getMonto() == null ? BigDecimal.ZERO : pago.getMonto();
        montoMaximo = saldo.add(monto);

        binding.selectorPedido.setText(getString(R.string.opcion_pedido,
                pago.getIdPedido(), Formato.textoOGuion(pago.getNombreC()), Formato.formatearPrecio(saldo)), false);
        binding.infoPedido.setVisibility(View.VISIBLE);
        binding.infoPedido.setText(getString(R.string.info_editar_pago, Formato.formatearPrecio(montoMaximo)));

        if (llenarCampos) {
            escribirTexto(binding.campoMonto, monto.stripTrailingZeros().toPlainString());
            binding.selectorMetodo.setText(pago.getMetodo(), false);
        }
    }

    private void guardar() {
        String montoTexto = leerTexto(binding.campoMonto);
        String metodo = binding.selectorMetodo.getText().toString();

        Map<String, String> errores = new LinkedHashMap<>(Validador.validarPago(montoTexto, metodo, montoMaximo));
        if (idPago == null && idPedido == null) {
            errores.put("id_pedido", "Selecciona un pedido válido");
        }
        if (mostrarErroresCampos(errores, campos)) {
            return;
        }

        BigDecimal monto = Validador.convertirMonto(montoTexto);
        LiveData<Resultado<Pago>> operacion = idPago == null
                ? viewModel.registrarPago(idPedido, monto, metodo)
                : viewModel.actualizarPago(idPago, monto, metodo);

        binding.botonGuardar.setEnabled(false);
        // 409 si el pedido fue cancelado mientras tanto; 422 si el monto pasa del saldo
        observar(operacion, binding.cargando, pago -> {
            Toast.makeText(this, R.string.pago_guardado, Toast.LENGTH_SHORT).show();
            finish();
        }, resultado -> {
            binding.botonGuardar.setEnabled(true);
            mostrarErrorFormulario(resultado, campos);
        });
    }
}
