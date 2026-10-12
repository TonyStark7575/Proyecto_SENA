<?php

require_once __DIR__ . '/ApiController.php';
require_once __DIR__ . '/../../models/Cliente.php';
require_once __DIR__ . '/../../models/Producto.php';
require_once __DIR__ . '/../../models/Pedido.php';
require_once __DIR__ . '/../../models/Pago.php';

class DashboardApiController extends ApiController {

    private $clienteModelo;
    private $productoModelo;
    private $pedidoModelo;
    private $pagoModelo;

    public function __construct($conexion) {
        $this->clienteModelo = new Cliente($conexion);
        $this->productoModelo = new Producto($conexion);
        $this->pedidoModelo = new Pedido($conexion);
        $this->pagoModelo = new Pago($conexion);
    }

    // El dashboard solo se lee: GET api/dashboard
    protected function rutas() {
        return ['' => ['GET' => 'resumen']];
    }

    public function resumen() {
        $ventas = $this->pedidoModelo->ventasDeHoy();
        $cobrado = $this->pagoModelo->cobradoDeHoy();

        $this->responderJson([
            'total_clientes'     => $this->clienteModelo->contar(),
            'total_productos'    => $this->productoModelo->contar(),
            'pedidos_pendientes' => (int) $this->pedidoModelo->contarPorEstado('Pendiente'),
            // Ventas: lo que se vendió hoy (pedidos registrados hoy que no están cancelados)
            'ventas_hoy'         => $ventas['total'],
            'pedidos_hoy'        => (int) $ventas['cantidad'],
            // Cobrado: el dinero que entró hoy (pagos registrados hoy, de cualquier pedido)
            'cobrado_hoy'        => $cobrado['total'],
            'pagos_hoy'          => (int) $cobrado['cantidad'],
            'pedidos_recientes'  => $this->pedidoModelo->obtenerRecientes(5),
        ]);
    }

}
