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

    public function crear() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $nombre = $_POST['nombre'];
            $precio = $_POST['precio'];
            $descripcion = $_POST['descripcion'];

            $this->productoModelo->crear($nombre, $precio, $descripcion);

            header('Location: /ProyectoSENA/public/index.php?ruta=productos/lista');
            exit;
        }

        require __DIR__ . '/../views/productos/crear.php';
    }

    public function detalle() {
        $id = $_GET['id'];
        $producto = $this->productoModelo->obtenerPorId($id);
        
        require __DIR__ . '/../views/productos/detalle.php';
    }

}