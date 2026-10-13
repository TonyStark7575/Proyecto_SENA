package com.loshilosdemaya.movil.vista.clientes;

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
import com.loshilosdemaya.movil.databinding.ActivityDetalleClienteBinding;
import com.loshilosdemaya.movil.modelo.Cliente;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;
import com.loshilosdemaya.movil.vista.pagos.DetallePagoActivity;
import com.loshilosdemaya.movil.vista.pagos.PagoAdapter;
import com.loshilosdemaya.movil.vista.pedidos.DetallePedidoActivity;
import com.loshilosdemaya.movil.vista.pedidos.PedidoAdapter;

/** Detalle de un cliente con su historial de pedidos y pagos (GET api/clientes&id=N). */
public class DetalleClienteActivity extends BaseActivity {

    private static final String EXTRA_ID_CLIENTE = "id_cliente";

    private ActivityDetalleClienteBinding binding;
    private ClienteViewModel viewModel;
    private PedidoAdapter adaptadorPedidos;
    private PagoAdapter adaptadorPagos;
    private int idCliente;
    private Cliente cliente;

    public static Intent crearIntent(Context contexto, int idCliente) {
        return new Intent(contexto, DetalleClienteActivity.class).putExtra(EXTRA_ID_CLIENTE, idCliente);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalleClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idCliente = getIntent().getIntExtra(EXTRA_ID_CLIENTE, 0);
        configurarToolbar(binding.toolbar, getString(R.string.detalle_cliente), true);
        viewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        // Los adaptadores de pedidos y pagos se reutilizan de sus propios módulos
        adaptadorPedidos = new PedidoAdapter(pedido ->
                startActivity(DetallePedidoActivity.crearIntent(this, pedido.getIdPedido())));
        binding.listaPedidos.setLayoutManager(new LinearLayoutManager(this));
        binding.listaPedidos.setAdapter(adaptadorPedidos);

        adaptadorPagos = new PagoAdapter(pago ->
                startActivity(DetallePagoActivity.crearIntent(this, pago.getIdPago())));
        binding.listaPagos.setLayoutManager(new LinearLayoutManager(this));
        binding.listaPagos.setAdapter(adaptadorPagos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        observar(viewModel.consultarCliente(idCliente), binding.cargando, this::mostrarCliente);
    }

    private void mostrarCliente(Cliente clienteConsultado) {
        cliente = clienteConsultado;
        binding.nombre.setText(cliente.getNombreC());
        binding.correo.setText(cliente.getEmailC());
        binding.telefono.setText(Formato.textoOGuion(cliente.getTelefonoC()));
        binding.registro.setText(getString(R.string.registro_por_fecha,
                Formato.textoOGuion(cliente.getRegistradoPor()), Formato.formatearFecha(cliente.getFechaRegistro())));

        adaptadorPedidos.actualizarLista(cliente.getPedidos());
        binding.sinPedidos.setVisibility(adaptadorPedidos.getItemCount() == 0 ? View.VISIBLE : View.GONE);

        adaptadorPagos.actualizarLista(cliente.getPagos());
        binding.sinPagos.setVisibility(adaptadorPagos.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.accion_editar) {
            startActivity(FormularioClienteActivity.crearIntent(this, idCliente));
            return true;
        }
        if (item.getItemId() == R.id.accion_eliminar) {
            String nombre = cliente != null ? cliente.getNombreC() : "";
            confirmar(getString(R.string.eliminar_cliente), getString(R.string.confirmar_eliminar, nombre),
                    getString(R.string.eliminar), this::eliminarCliente);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Un cliente con pedidos no se puede borrar: la API responde 409 con el motivo
    private void eliminarCliente() {
        observar(viewModel.eliminarCliente(idCliente), binding.cargando, sinDatos -> {
            Toast.makeText(this, R.string.cliente_eliminado, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
