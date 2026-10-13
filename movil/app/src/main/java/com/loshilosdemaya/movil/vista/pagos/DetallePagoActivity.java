package com.loshilosdemaya.movil.vista.pagos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityDetallePagoBinding;
import com.loshilosdemaya.movil.modelo.Pago;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;
import com.loshilosdemaya.movil.vista.pedidos.DetallePedidoActivity;

/** Detalle de un pago y del pedido al que abona (GET api/pagos&id=N). */
public class DetallePagoActivity extends BaseActivity {

    private static final String EXTRA_ID_PAGO = "id_pago";

    private ActivityDetallePagoBinding binding;
    private PagoViewModel viewModel;
    private int idPago;
    private Pago pago;

    public static Intent crearIntent(Context contexto, int idPago) {
        return new Intent(contexto, DetallePagoActivity.class).putExtra(EXTRA_ID_PAGO, idPago);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetallePagoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idPago = getIntent().getIntExtra(EXTRA_ID_PAGO, 0);
        configurarToolbar(binding.toolbar, getString(R.string.detalle_pago), true);
        viewModel = new ViewModelProvider(this).get(PagoViewModel.class);

        binding.tarjetaPedido.setOnClickListener(v -> {
            if (pago != null) {
                startActivity(DetallePedidoActivity.crearIntent(this, pago.getIdPedido()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        observar(viewModel.consultarPago(idPago), binding.cargando, this::mostrarPago);
    }

    private void mostrarPago(Pago pagoConsultado) {
        pago = pagoConsultado;
        binding.codigo.setText(Formato.codigoPago(pago.getIdPago()));
        binding.monto.setText(Formato.formatearPrecio(pago.getMonto()));
        binding.metodo.setText(pago.getMetodo());
        binding.fecha.setText(Formato.formatearFecha(pago.getFechaP()));
        binding.cliente.setText(Formato.textoOGuion(pago.getNombreC()));
        binding.pedido.setText(Formato.codigoPedido(pago.getIdPedido()));
        binding.totalesPedido.setText(getString(R.string.totales_pedido,
                Formato.formatearPrecio(pago.getTotal()), Formato.formatearPrecio(pago.getSaldo())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.accion_editar) {
            startActivity(FormularioPagoActivity.crearIntentEditar(this, idPago));
            return true;
        }
        if (item.getItemId() == R.id.accion_eliminar) {
            confirmar(getString(R.string.eliminar_pago),
                    getString(R.string.confirmar_eliminar_pago, Formato.codigoPago(idPago)),
                    getString(R.string.eliminar), this::eliminarPago);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Al borrar el pago, el trigger de la BD devuelve ese dinero al saldo del pedido
    private void eliminarPago() {
        observar(viewModel.eliminarPago(idPago), binding.cargando, sinDatos -> {
            Toast.makeText(this, R.string.pago_eliminado, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
