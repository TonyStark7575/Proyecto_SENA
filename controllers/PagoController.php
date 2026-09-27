<?php

require_once __DIR__ . '/../models/Pago.php';
require_once __DIR__ . '/../models/Pedido.php';

class PagoController {

    private $pagoModelo;
    private $pedidoModelo;

    public function __construct($conexion) {
        $this->pagoModelo = new Pago($conexion);
        $this->pedidoModelo = new Pedido($conexion);
    }

    public function lista() {
        $pagos = $this->pagoModelo->obtenerTodos();
        require __DIR__ . '/../views/pagos/lista.php';
    }

    public function crear() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $idPedido = $_POST['id_pedido'];
            $monto = $_POST['monto'];
            $metodo = $_POST['metodo'];

            $this->pagoModelo->crear($idPedido, $monto, $metodo);

            header('Location: /ProyectoSENA/public/index.php?ruta=pedidos/detalle&id=' . $idPedido);
            exit;
        }

        $pedidos = $this->pedidoModelo->obtenerTodos();
        require __DIR__ . '/../views/pagos/crear.php';
    }

    public function detalle() {
        $id = $_GET['id'];
        $pago = $this->pagoModelo->obtenerPorId($id);
        require __DIR__ . '/../views/pagos/detalle.php';
    }

    public function editar() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $id = $_POST['id'];
            $monto = $_POST['monto'];
            $metodo = $_POST['metodo'];

            $this->pagoModelo->actualizar($id, $monto, $metodo);

            header('Location: /ProyectoSENA/public/index.php?ruta=pagos/detalle&id=' . $id);
            exit;
        }

        $id = $_GET['id'];
        $pago = $this->pagoModelo->obtenerPorId($id);
        require __DIR__ . '/../views/pagos/editar.php';
    }

    public function eliminar() {
        $id = $_POST['id'];
        $this->pagoModelo->eliminar($id);
        header('Location: /ProyectoSENA/public/index.php?ruta=pagos/lista');
        exit;
    }

}