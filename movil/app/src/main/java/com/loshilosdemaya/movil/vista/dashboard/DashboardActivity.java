package com.loshilosdemaya.movil.vista.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.databinding.ActivityDashboardBinding;
import com.loshilosdemaya.movil.databinding.ItemIndicadorBinding;
import com.loshilosdemaya.movil.databinding.ItemModuloBinding;
import com.loshilosdemaya.movil.modelo.ResumenDashboard;
import com.loshilosdemaya.movil.modelo.Sesion;
import com.loshilosdemaya.movil.util.Formato;
import com.loshilosdemaya.movil.util.SesionActual;
import com.loshilosdemaya.movil.vista.clientes.ListaClientesActivity;
import com.loshilosdemaya.movil.vista.comun.BaseActivity;
import com.loshilosdemaya.movil.vista.login.LoginActivity;
import com.loshilosdemaya.movil.vista.pagos.ListaPagosActivity;
import com.loshilosdemaya.movil.vista.pedidos.DetallePedidoActivity;
import com.loshilosdemaya.movil.vista.pedidos.ListaPedidosActivity;
import com.loshilosdemaya.movil.vista.pedidos.PedidoAdapter;
import com.loshilosdemaya.movil.vista.productos.ListaProductosActivity;
import com.loshilosdemaya.movil.vista.usuarios.ListaUsuariosActivity;

/**
 * Pantalla principal después de iniciar sesión: indicadores del día (api/dashboard),
 * accesos a los cinco módulos y los últimos pedidos.
 */
public class DashboardActivity extends BaseActivity {

    private ActivityDashboardBinding binding;
    private DashboardViewModel viewModel;
    private PedidoAdapter adaptadorRecientes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarToolbar(binding.toolbar, getString(R.string.app_name), false);
        Sesion sesion = SesionActual.obtener();
        if (sesion != null) {
            binding.toolbar.setSubtitle(getString(R.string.saludo, sesion.getNombre(), sesion.getRol()));
        }

        configurarModulo(binding.moduloProductos, R.drawable.ic_productos, R.string.modulo_productos, ListaProductosActivity.class);
        configurarModulo(binding.moduloClientes, R.drawable.ic_clientes, R.string.modulo_clientes, ListaClientesActivity.class);
        configurarModulo(binding.moduloPedidos, R.drawable.ic_pedidos, R.string.modulo_pedidos, ListaPedidosActivity.class);
        configurarModulo(binding.moduloPagos, R.drawable.ic_pagos, R.string.modulo_pagos, ListaPagosActivity.class);
        configurarModulo(binding.moduloUsuarios, R.drawable.ic_usuarios, R.string.modulo_usuarios, ListaUsuariosActivity.class);

        adaptadorRecientes = new PedidoAdapter(pedido ->
                startActivity(DetallePedidoActivity.crearIntent(this, pedido.getIdPedido())));
        binding.listaRecientes.setLayoutManager(new LinearLayoutManager(this));
        binding.listaRecientes.setAdapter(adaptadorRecientes);

        binding.recargar.setColorSchemeResources(R.color.primario);
        binding.recargar.setOnRefreshListener(viewModel()::recargarResumen);

        viewModel().getResumen().observe(this, resultado -> {
            binding.recargar.setRefreshing(resultado.estaCargando());
            if (resultado.esExito()) {
                mostrarResumen(resultado.getDatos());
            } else if (resultado.esError()) {
                manejarError(resultado);
            }
        });
    }

    // Los indicadores cambian cada vez que se registra algo: se recargan al volver a esta pantalla
    @Override
    protected void onResume() {
        super.onResume();
        viewModel().recargarResumen();
    }

    private DashboardViewModel viewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        }
        return viewModel;
    }

    private void mostrarResumen(ResumenDashboard resumen) {
        llenarIndicador(binding.indicadorVentas, R.string.ventas_hoy,
                Formato.formatearPrecio(resumen.getVentasHoy()),
                getResources().getQuantityString(R.plurals.cantidad_pedidos, resumen.getPedidosHoy(), resumen.getPedidosHoy()));
        llenarIndicador(binding.indicadorCobrado, R.string.cobrado_hoy,
                Formato.formatearPrecio(resumen.getCobradoHoy()),
                getResources().getQuantityString(R.plurals.cantidad_pagos, resumen.getPagosHoy(), resumen.getPagosHoy()));
        llenarIndicador(binding.indicadorPendientes, R.string.pedidos_pendientes,
                String.valueOf(resumen.getPedidosPendientes()), getString(R.string.por_entregar));
        llenarIndicador(binding.indicadorClientes, R.string.modulo_clientes,
                String.valueOf(resumen.getTotalClientes()), getString(R.string.registrados));
        llenarIndicador(binding.indicadorProductos, R.string.modulo_productos,
                String.valueOf(resumen.getTotalProductos()), getString(R.string.en_catalogo));

        adaptadorRecientes.actualizarLista(resumen.getPedidosRecientes());
        binding.sinRecientes.setVisibility(adaptadorRecientes.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void llenarIndicador(ItemIndicadorBinding indicador, int titulo, String valor, String detalle) {
        indicador.titulo.setText(titulo);
        indicador.valor.setText(valor);
        indicador.detalle.setText(detalle);
    }

    private void configurarModulo(ItemModuloBinding modulo, @DrawableRes int icono, int nombre, Class<?> pantalla) {
        modulo.icono.setImageResource(icono);
        modulo.nombre.setText(nombre);
        modulo.getRoot().setOnClickListener(v -> startActivity(new Intent(this, pantalla)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.accion_cerrar_sesion) {
            confirmar(getString(R.string.cerrar_sesion), getString(R.string.confirmar_cerrar_sesion),
                    getString(R.string.cerrar_sesion), this::cerrarSesion);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion() {
        viewModel().cerrarSesion().observe(this, resultado -> {
            if (!resultado.estaCargando()) {
                SesionActual.limpiar();
                Intent intencion = new Intent(this, LoginActivity.class);
                intencion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intencion);
                finish();
            }
        });
    }
}
