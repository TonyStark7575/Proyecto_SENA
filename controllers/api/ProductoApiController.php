<?php

require_once __DIR__ . '/../../models/Producto.php';

class ProductoApiController {

    private $productoModelo;

    public function __construct($conexion) {
        $this->productoModelo = new Producto($conexion);
    }

    public function lista() {
        $productos = $this->productoModelo->obtenerTodos();
        $this->responderJson($productos);
    }

    public function detalle() {
        $id = filter_var($_GET['id'], FILTER_VALIDATE_INT);

        if ($id === false) {
            $this->responderJson(['error' => 'El id debe ser un número entero'], 400);
            return;
        }

        $producto = $this->productoModelo->obtenerPorId($id);

        if ($producto === false) {
            $this->responderJson(['error' => 'Producto no encontrado'], 404);
            return;
        }

        $this->responderJson($producto);
    }

    private function responderJson($datos, $codigo = 200) {
        http_response_code($codigo);
        header('Content-Type: application/json; charset=utf-8');
        echo json_encode($datos, JSON_UNESCAPED_UNICODE);
    }

}