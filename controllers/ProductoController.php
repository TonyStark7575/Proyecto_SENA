<?php

require_once __DIR__ . '/../models/Producto.php';

class ProductoController {

    private $productoModelo;

    public function __construct($conexion) {
        $this->productoModelo = new Producto($conexion);
    }

    public function lista() {
    $productos = $this->productoModelo->obtenerTodos();
    
    require __DIR__ . '/../views/productos/lista.php';
}

}