<?php

require_once __DIR__ . '/ApiController.php';
require_once __DIR__ . '/../../models/Producto.php';

class ProductoApiController extends ApiController {

    private $productoModelo;

    public function __construct($conexion) {
        $this->productoModelo = new Producto($conexion);
    }

    public function lista() {
        $productos = $this->productoModelo->obtenerTodos();
        $this->responderJson($productos);
    }

    public function detalle() {
        $id = $this->obtenerId();

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
        $datos = $this->leerJson();

        if ($datos === null) {
            $this->responderJson(['error' => 'El cuerpo de la petición debe ser JSON válido'], 400);
            return;
        }

        [$campos, $errores] = $this->validar($datos);

        if (!empty($errores)) {
            $this->responderJson(['errores' => $errores], 422);
            return;
        }

        $id = $this->productoModelo->crear($campos['nombre_p'], $campos['precio'], $campos['descripcion'], null);
        $producto = $this->productoModelo->obtenerPorId($id);

        $this->responderJson($producto, 201);
    }

    public function actualizar() {
        $id = $this->obtenerId();

        if ($id === false) {
            $this->responderJson(['error' => 'El id debe ser un número entero'], 400);
            return;
        }

        $producto = $this->productoModelo->obtenerPorId($id);

        if ($producto === false) {
            $this->responderJson(['error' => 'Producto no encontrado'], 404);
            return;
        }

        $datos = $this->leerJson();

        if ($datos === null) {
            $this->responderJson(['error' => 'El cuerpo de la petición debe ser JSON válido'], 400);
            return;
        }

        [$campos, $errores] = $this->validar($datos);

        if (!empty($errores)) {
            $this->responderJson(['errores' => $errores], 422);
            return;
        }

        // JSON no puede llevar archivos: se conserva la imagen que ya tenía
        $this->productoModelo->actualizar($id, $campos['nombre_p'], $campos['precio'], $campos['descripcion'], $producto['imagen']);
        $producto = $this->productoModelo->obtenerPorId($id);

        $this->responderJson($producto);
    }

    public function eliminar() {
        $id = $this->obtenerId();

        if ($id === false) {
            $this->responderJson(['error' => 'El id debe ser un número entero'], 400);
            return;
        }

        $producto = $this->productoModelo->obtenerPorId($id);

        if ($producto === false) {
            $this->responderJson(['error' => 'Producto no encontrado'], 404);
            return;
        }

        // Un producto que aparece en algún pedido no se puede borrar:
        // la llave foránea de detalle_pedido lo impide (error 1451 de MySQL)
        try {
            $this->productoModelo->eliminar($id);
        } catch (PDOException $e) {
            if (($e->errorInfo[1] ?? null) === 1451) {
                $this->responderJson(['error' => 'No se puede eliminar: el producto está en uno o más pedidos'], 409);
                return;
            }
            throw $e;
        }

        $this->borrarImagen($producto['imagen']);

        http_response_code(204);
    }

    // Reglas compartidas por crear() y actualizar()
    private function validar($datos) {
        // Si llega algo que no es texto (por ejemplo un arreglo), se trata como vacío
        $nombre = is_string($datos['nombre_p'] ?? null) ? trim($datos['nombre_p']) : '';
        $descripcion = is_string($datos['descripcion'] ?? null) ? trim($datos['descripcion']) : '';
        $precio = filter_var($datos['precio'] ?? null, FILTER_VALIDATE_FLOAT);

        // Se redondea como lo guarda DECIMAL(10,2), para validar el valor que de verdad quedará en la BD
        // (0.001 se guardaría como 0.00 y 99999999.995 como 100000000.00)
        if ($precio !== false) {
            $precio = round($precio, 2);
        }

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

        $campos = ['nombre_p' => $nombre, 'precio' => $precio, 'descripcion' => $descripcion];

        return [$campos, $errores];
    }

    // Igual que en ProductoController: solo borra imágenes subidas (prefijo timestamp_),
    // nunca las imágenes fijas del sistema como LOGOTIPO.png
    private function borrarImagen($nombreArchivo) {
        if (!$nombreArchivo || !preg_match('/^\d+_/', $nombreArchivo)) {
            return;
        }

        $ruta = __DIR__ . '/../../public/img/' . basename($nombreArchivo);
        if (is_file($ruta)) {
            unlink($ruta);
        }
    }

}
