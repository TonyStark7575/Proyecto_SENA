<?php

require_once __DIR__ . '/../models/Producto.php';
require_once __DIR__ . '/../helpers/ImagenProducto.php';

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

            // Si se eligió un archivo, se valida que sea una imagen real antes de guardarlo
            if (($_FILES['imagen']['error'] ?? UPLOAD_ERR_NO_FILE) !== UPLOAD_ERR_NO_FILE) {
                $subida = ImagenProducto::guardar($_FILES['imagen']);
                if ($subida['error']) {
                    $this->volverALista('danger', 'No se creó el producto: ' . $subida['error']);
                }
                $imagen = $subida['nombre'];
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
        // La imagen actual se lee de la BD, no del campo oculto del formulario (el navegador podría alterarlo)
        $productoActual = $this->productoModelo->obtenerPorId($id);
        if (!$productoActual) {
            $this->noEncontrado();
            return;
        }
        $imagen = $productoActual['imagen'];

        if (($_FILES['imagen']['error'] ?? UPLOAD_ERR_NO_FILE) !== UPLOAD_ERR_NO_FILE) {
            $subida = ImagenProducto::guardar($_FILES['imagen']);
            if ($subida['error']) {
                $this->volverALista('danger', 'No se guardaron los cambios: ' . $subida['error']);
            }
            // La imagen anterior ya no la usa nadie: se borra para no dejar huérfanas
            ImagenProducto::borrar($imagen);
            $imagen = $subida['nombre'];
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
            ImagenProducto::borrar($producto['imagen']);
        }

        $this->volverALista('success', 'Producto eliminado correctamente.');
    }

    // Guarda un mensaje en la sesión para mostrarlo una sola vez en la lista
    private function volverALista($tipo, $mensaje) {
        $_SESSION['mensaje'] = ['tipo' => $tipo, 'texto' => $mensaje];
        header('Location: /ProyectoSENA/public/index.php?ruta=productos/lista');
        exit;
    }

    private function noEncontrado() {
        http_response_code(404);
        $mensaje = 'El producto que buscas no existe o fue eliminado.';
        $volver = '/ProyectoSENA/public/index.php?ruta=productos/lista';
        require __DIR__ . '/../views/layouts/no_encontrado.php';
    }

}