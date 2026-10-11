<?php

require_once __DIR__ . '/../models/Producto.php';

class ProductoController {

    private $productoModelo;

    public function __construct($conexion) {
        $this->productoModelo = new Producto($conexion);
    }

    public function lista() {
        $productos = $this->productoModelo->obtenerTodos();

        $mensaje = $_SESSION['mensaje'] ?? null;
        unset($_SESSION['mensaje']);

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
        $id = $_GET['id'] ?? null;
        $producto = $this->productoModelo->obtenerPorId($id);

        if (!$producto) {
            $this->noEncontrado();
            return;
        }

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
            // La imagen anterior ya no la usa nadie: se borra para no dejar huérfanas
            $this->borrarImagen($_POST['imagen_actual'] ?? null);
            $imagen = $nombreArchivo;
        }

        $this->productoModelo->actualizar($id, $nombre, $precio, $descripcion, $imagen);

        header('Location: /ProyectoSENA/public/index.php?ruta=productos/lista');
        exit;
    }

    $id = $_GET['id'] ?? null;
    $producto = $this->productoModelo->obtenerPorId($id);

    if (!$producto) {
        $this->noEncontrado();
        return;
    }

    require __DIR__ . '/../views/productos/editar.php';
}

    public function eliminar() {
        $id = $_POST['id'];
        $producto = $this->productoModelo->obtenerPorId($id);

        // Un producto que aparece en algún pedido no se puede borrar:
        // la llave foránea de detalle_pedido lo impide (error 1451 de MySQL)
        try {
            $this->productoModelo->eliminar($id);
        } catch (PDOException $e) {
            if (($e->errorInfo[1] ?? null) === 1451) {
                $this->volverALista('danger', 'No se puede eliminar el producto porque está en uno o más pedidos.');
            }
            throw $e;
        }

        if ($producto) {
            $this->borrarImagen($producto['imagen']);
        }

        $this->volverALista('success', 'Producto eliminado correctamente.');
    }

    // Guarda un mensaje en la sesión para mostrarlo una sola vez en la lista
    private function volverALista($tipo, $mensaje) {
        $_SESSION['mensaje'] = ['tipo' => $tipo, 'texto' => $mensaje];
        header('Location: /ProyectoSENA/public/index.php?ruta=productos/lista');
        exit;
    }

    // Solo borra imágenes subidas por el formulario (prefijo timestamp_),
    // nunca las imágenes fijas del sistema como LOGOTIPO.png
    private function borrarImagen($nombreArchivo) {
        if (!$nombreArchivo || !preg_match('/^\d+_/', $nombreArchivo)) {
            return;
        }

        $ruta = __DIR__ . '/../public/img/' . basename($nombreArchivo);
        if (is_file($ruta)) {
            unlink($ruta);
        }
    }

    private function noEncontrado() {
        http_response_code(404);
        $mensaje = 'El producto que buscas no existe o fue eliminado.';
        $volver = '/ProyectoSENA/public/index.php?ruta=productos/lista';
        require __DIR__ . '/../views/layouts/no_encontrado.php';
    }

}