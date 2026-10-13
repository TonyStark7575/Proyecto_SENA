package com.loshilosdemaya.movil.vista.pedidos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityDetallePedidoBinding;
import com.loshilosdemaya.movil.modelo.Pedido;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.vista.clientes.DetalleClienteActivity;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;
import com.loshilosdemaya.movil.vista.comun.UtilVista;
import com.loshilosdemaya.movil.vista.pagos.DetallePagoActivity;
import com.loshilosdemaya.movil.vista.pagos.FormularioPagoActivity;
import com.loshilosdemaya.movil.vista.pagos.PagoAdapter;

import java.math.BigDecimal;

/** Detalle de un pedido: cliente, estado, totales, productos y pagos (GET api/pedidos&id=N). */
public class DetallePedidoActivity extends BaseActivity {

    private static final String EXTRA_ID_PEDIDO = "id_pedido";

    private ActivityDetallePedidoBinding binding;
    private PedidoViewModel viewModel;
    private LineaPedidoAdapter adaptadorLineas;
    private PagoAdapter adaptadorPagos;
    private int idPedido;
    private Pedido pedido;

    public static Intent crearIntent(Context contexto, int idPedido) {
        return new Intent(contexto, DetallePedidoActivity.class).putExtra(EXTRA_ID_PEDIDO, idPedido);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetallePedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idPedido = getIntent().getIntExtra(EXTRA_ID_PEDIDO, 0);
        configurarToolbar(binding.toolbar, getString(R.string.detalle_pedido), true);
        viewModel = new ViewModelProvider(this).get(PedidoViewModel.class);

        // En el detalle los productos solo se ven; se cambian desde "Editar"
        adaptadorLineas = new LineaPedidoAdapter(null);
        binding.listaLineas.setLayoutManager(new LinearLayoutManager(this));
        binding.listaLineas.setAdapter(adaptadorLineas);

        adaptadorPagos = new PagoAdapter(pago -> startActivity(DetallePagoActivity.crearIntent(this, pago.getIdPago())));
        binding.listaPagos.setLayoutManager(new LinearLayoutManager(this));
        binding.listaPagos.setAdapter(adaptadorPagos);

        binding.botonRegistrarPago.setOnClickListener(v ->
                startActivity(FormularioPagoActivity.crearIntentNuevo(this, idPedido)));
        binding.cliente.setOnClickListener(v -> {
            if (pedido != null && pedido.getIdClienteP() != null) {
                startActivity(DetalleClienteActivity.crearIntent(this, pedido.getIdClienteP()));
            }
        });
    }

    // Se recarga al volver de editar o de registrar un pago (cambian el saldo y los pagos)
    @Override
    protected void onResume() {
        super.onResume();
        observar(viewModel.consultarPedido(idPedido), binding.cargando, this::mostrarPedido);
    }

    private void mostrarPedido(Pedido pedidoConsultado) {
        pedido = pedidoConsultado;
        binding.codigo.setText(Formato.codigoPedido(pedido.getIdPedido()));
        UtilVista.pintarEstado(binding.estado, pedido.getEstado());
        binding.cliente.setText(pedido.getNombreC());
        binding.registro.setText(getString(R.string.registro_por_fecha,
                Formato.textoOGuion(pedido.getRegistradoPor()), Formato.formatearFecha(pedido.getFechaRegistro())));

        BigDecimal total = pedido.getTotal() == null ? BigDecimal.ZERO : pedido.getTotal();
        BigDecimal saldo = pedido.getSaldo() == null ? BigDecimal.ZERO : pedido.getSaldo();
        binding.total.setText(Formato.formatearPrecio(total));
        binding.pagado.setText(Formato.formatearPrecio(total.subtract(saldo)));
        binding.saldo.setText(Formato.formatearPrecio(saldo));

        adaptadorLineas.mostrarLineas(pedido.getLineas());
        adaptadorPagos.actualizarLista(pedido.getPagos());
        binding.sinPagos.setVisibility(adaptadorPagos.getItemCount() == 0 ? View.VISIBLE : View.GONE);

        // Mismas reglas de la API: no se paga un pedido cancelado ni uno sin saldo
        binding.botonRegistrarPago.setVisibility(pedido.admitePagos() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.accion_editar) {
            startActivity(FormularioPedidoActivity.crearIntent(this, idPedido));
            return true;
        }
        if (item.getItemId() == R.id.accion_eliminar) {
            confirmar(getString(R.string.eliminar_pedido),
                    getString(R.string.confirmar_eliminar, Formato.codigoPedido(idPedido)),
                    getString(R.string.eliminar), this::eliminarPedido);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Un pedido con pagos no se borra (409): la API sugiere cambiar su estado a Cancelado
    private void eliminarPedido() {
        observar(viewModel.eliminarPedido(idPedido), binding.cargando, sinDatos -> {
            Toast.makeText(this, R.string.pedido_eliminado, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
