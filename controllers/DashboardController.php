<?php

require_once __DIR__ . '/../models/Cliente.php';
require_once __DIR__ . '/../models/Producto.php';
require_once __DIR__ . '/../models/Pedido.php';

class DashboardController {

    private $clienteModelo;
    private $productoModelo;
    private $pedidoModelo;

    public function __construct($conexion) {
        $this->clienteModelo = new Cliente($conexion);
        $this->productoModelo = new Producto($conexion);
        $this->pedidoModelo = new Pedido($conexion);
    }

    public function index() {
        $totalClientes = count($this->clienteModelo->obtenerTodos());
        $totalProductos = count($this->productoModelo->obtenerTodos());
        $pedidosPendientes = $this->pedidoModelo->contarPorEstado('Pendiente');

        $todosLosPedidos = $this->pedidoModelo->obtenerTodos();
        $pedidosRecientes = array_slice($todosLosPedidos, 0, 5);

        require __DIR__ . '/../views/dashboard.php';
    }

}
