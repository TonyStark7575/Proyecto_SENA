<?php

require_once __DIR__ . '/../models/Pedido.php';
require_once __DIR__ . '/../models/DetallePedido.php';
require_once __DIR__ . '/../models/Cliente.php';
require_once __DIR__ . '/../models/Producto.php';
require_once __DIR__ . '/../models/Usuario.php';

class PedidoController {

    private $pedidoModelo;
    private $detalleModelo;
    private $clienteModelo;
    private $productoModelo;
    private $usuarioModelo;

    public function __construct($conexion) {
        $this->pedidoModelo = new Pedido($conexion);
        $this->detalleModelo = new DetallePedido($conexion);
        $this->clienteModelo = new Cliente($conexion);
        $this->productoModelo = new Producto($conexion);
        $this->usuarioModelo = new Usuario($conexion);
    }

    public function lista() {
        $pedidos = $this->pedidoModelo->obtenerTodos();
        require __DIR__ . '/../views/pedidos/lista.php';
    }

      public function crear() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $idCliente = $_POST['id_cliente'];
            $idUsuario = $_POST['id_usuario'];
            $estado = $_POST['estado'];
            $idProducto = $_POST['id_producto'];
            $cantidad = $_POST['cantidad'];

            $idPedidoNuevo = $this->pedidoModelo->crear($idCliente, $idUsuario, $estado);

            $producto = $this->productoModelo->obtenerPorId($idProducto);
            $this->detalleModelo->agregar($idPedidoNuevo, $idProducto, $cantidad, $producto['precio']);

            header('Location: /ProyectoSENA/public/index.php?ruta=pedidos/detalle&id=' . $idPedidoNuevo);
            exit;
        }

        $clientes = $this->clienteModelo->obtenerTodos();
        $productos = $this->productoModelo->obtenerTodos();
        $usuarios = $this->usuarioModelo->obtenerTodos();
        require __DIR__ . '/../views/pedidos/crear.php';
    }

    public function detalle() {
        $id = $_GET['id'];
        $pedido = $this->pedidoModelo->obtenerPorId($id);
        $lineas = $this->detalleModelo->obtenerPorPedido($id);
        $productos = $this->productoModelo->obtenerTodos();

        require __DIR__ . '/../views/pedidos/detalle.php';
    }

    public function agregarProducto() {
        $idPedido = $_POST['id_pedido'];
        $idProducto = $_POST['id_producto'];
        $cantidad = $_POST['cantidad'];

        $producto = $this->productoModelo->obtenerPorId($idProducto);
        $this->detalleModelo->agregar($idPedido, $idProducto, $cantidad, $producto['precio']);

        header('Location: /ProyectoSENA/public/index.php?ruta=pedidos/detalle&id=' . $idPedido);
        exit;
    }

    public function eliminarLinea() {
        $idDetalle = $_POST['id_detalle'];
        $idPedido = $_POST['id_pedido'];

        $this->detalleModelo->eliminar($idDetalle);

        header('Location: /ProyectoSENA/public/index.php?ruta=pedidos/detalle&id=' . $idPedido);
        exit;
    }

    public function editar() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $id = $_POST['id'];
            $idCliente = $_POST['id_cliente'];
            $estado = $_POST['estado'];

            $this->pedidoModelo->actualizar($id, $idCliente, $estado);

            header('Location: /ProyectoSENA/public/index.php?ruta=pedidos/detalle&id=' . $id);
            exit;
        }

        $id = $_GET['id'];
        $pedido = $this->pedidoModelo->obtenerPorId($id);
        $clientes = $this->clienteModelo->obtenerTodos();

        require __DIR__ . '/../views/pedidos/editar.php';
    }

    public function eliminar() {
        $id = $_POST['id'];
        $this->pedidoModelo->eliminar($id);
        header('Location: /ProyectoSENA/public/index.php?ruta=pedidos/lista');
        exit;
    }

}