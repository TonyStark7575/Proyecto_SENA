<?php

require_once __DIR__ . '/../models/Producto.php';

class ProductoController {

    private $productoModelo;

    public function __construct($conexion) {
        $this->productoModelo = new Producto($conexion);
    }

    public function lista() {
        $productos = $this->productoModelo->obtenerTodos();
        
        foreach ($productos as $producto) {
            echo $producto['nombre_p'] . " - $" . $producto['precio'] . "<br>";
        }
    }

}