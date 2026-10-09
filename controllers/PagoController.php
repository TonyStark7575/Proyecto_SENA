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
        // Si se llega desde el Detalle de un Pedido, ese pedido aparece pre-seleccionado
        $idPedidoSeleccionado = $_GET['id_pedido'] ?? null;
        require __DIR__ . '/../views/pagos/crear.php';
    }

    public function detalle() {
        $id = $_GET['id'] ?? null;
        $pago = $this->pagoModelo->obtenerPorId($id);

        if (!$pago) {
            $this->noEncontrado();
            return;
        }

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

        $id = $_GET['id'] ?? null;
        $pago = $this->pagoModelo->obtenerPorId($id);

        if (!$pago) {
            $this->noEncontrado();
            return;
        }

        require __DIR__ . '/../views/pagos/editar.php';
    }

    public function eliminar() {
        $id = $_POST['id'];
        $this->pagoModelo->eliminar($id);
        header('Location: /ProyectoSENA/public/index.php?ruta=pagos/lista');
        exit;
    }

    private function noEncontrado() {
        http_response_code(404);
        $mensaje = 'El pago que buscas no existe o fue eliminado.';
        $volver = '/ProyectoSENA/public/index.php?ruta=pagos/lista';
        require __DIR__ . '/../views/layouts/no_encontrado.php';
    }

}