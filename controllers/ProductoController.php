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
            $imagen = null;

            if ($_FILES['imagen']['error'] === UPLOAD_ERR_OK) {
                $nombreArchivo = time() . '_' . $_FILES['imagen']['name'];
                $rutaDestino = __DIR__ . '/../public/img/' . $nombreArchivo;
                move_uploaded_file($_FILES['imagen']['tmp_name'], $rutaDestino);
                $imagen = $nombreArchivo;
            }

            $this->productoModelo->crear($nombre, $precio, $descripcion, $imagen);

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

public function editar() {
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $id = $_POST['id'];
        $nombre = $_POST['nombre'];
        $precio = $_POST['precio'];
        $descripcion = $_POST['descripcion'];
        $imagen = $_POST['imagen_actual'] ?? null;

        if ($_FILES['imagen']['error'] === UPLOAD_ERR_OK) {
            $nombreArchivo = time() . '_' . $_FILES['imagen']['name'];
            $rutaDestino = __DIR__ . '/../public/img/' . $nombreArchivo;
            move_uploaded_file($_FILES['imagen']['tmp_name'], $rutaDestino);
            $imagen = $nombreArchivo;
        }

        $this->productoModelo->actualizar($id, $nombre, $precio, $descripcion, $imagen);

        header('Location: /ProyectoSENA/public/index.php?ruta=productos/lista');
        exit;
    }

    $id = $_GET['id'];
    $producto = $this->productoModelo->obtenerPorId($id);

    require __DIR__ . '/../views/productos/editar.php';
}

    public function eliminar() {
        $id = $_POST['id'];
        $this->productoModelo->eliminar($id);
        header('Location: /ProyectoSENA/public/index.php?ruta=productos/lista');
        exit;
    }

}