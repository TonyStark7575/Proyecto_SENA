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

    public function crear() {
        $datos = json_decode(file_get_contents('php://input'), true);

        if (!is_array($datos)) {
            $this->responderJson(['error' => 'El cuerpo de la petición debe ser JSON válido'], 400);
            return;
        }

        // Si llega algo que no es texto (por ejemplo un arreglo), se trata como vacío
        $nombre = is_string($datos['nombre_p'] ?? null) ? trim($datos['nombre_p']) : '';
        $descripcion = is_string($datos['descripcion'] ?? null) ? trim($datos['descripcion']) : '';
        $precio = filter_var($datos['precio'] ?? null, FILTER_VALIDATE_FLOAT);

        $errores = [];

        if ($nombre === '') {
            $errores['nombre_p'] = 'El nombre es obligatorio';
        } elseif (mb_strlen($nombre) > 50) {
            $errores['nombre_p'] = 'El nombre no puede tener más de 50 caracteres';
        }

        // DECIMAL(10,2) admite como máximo 99.999.999,99
        if ($precio === false || $precio <= 0 || $precio > 99999999.99) {
            $errores['precio'] = 'El precio debe ser un número mayor que 0 y menor que 100.000.000';
        }

        if (!empty($errores)) {
            $this->responderJson(['errores' => $errores], 422);
            return;
        }

        $id = $this->productoModelo->crear($nombre, $precio, $descripcion, null);
        $producto = $this->productoModelo->obtenerPorId($id);

        $this->responderJson($producto, 201);
    }

    private function responderJson($datos, $codigo = 200) {
        http_response_code($codigo);
        header('Content-Type: application/json; charset=utf-8');
        echo json_encode($datos, JSON_UNESCAPED_UNICODE);
    }

}