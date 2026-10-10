<?php

require_once __DIR__ . '/../../models/Producto.php';

class ProductoApiController {

    private $productoModelo;

    public function __construct($conexion) {
        $this->productoModelo = new Producto($conexion);
    }

    public function lista() {
        $productos = $this->productoModelo->obtenerTodos();

        header('Content-Type: application/json; charset=utf-8');
        echo json_encode($productos, JSON_UNESCAPED_UNICODE);
    }

}