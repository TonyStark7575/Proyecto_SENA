<?php

require_once __DIR__ . '/ApiController.php';
require_once __DIR__ . '/../../models/Producto.php';
require_once __DIR__ . '/../../helpers/ImagenProducto.php';

class ProductoApiController extends ApiController {

    private $productoModelo;

    public function __construct($conexion) {
        $this->productoModelo = new Producto($conexion);
    }

    // Además del CRUD: api/productos/imagen para subir (POST) o quitar (DELETE) la imagen
    protected function rutas() {
        $rutas = parent::rutas();
        $rutas['imagen'] = ['POST' => 'subirImagen', 'DELETE' => 'quitarImagen'];
        return $rutas;
    }

    public function lista() {
        $productos = $this->productoModelo->obtenerTodos();
        $this->responderJson($productos);
    }

    public function detalle() {
        $producto = $this->buscarProducto();
        if ($producto) {
            $this->responderJson($producto);
        }
    }

    public function crear() {
        $datos = $this->leerJson();

        if ($datos === null) {
            $this->jsonInvalido();
            return;
        }

        [$campos, $errores] = $this->validar($datos);

        if (!empty($errores)) {
            $this->erroresValidacion($errores);
            return;
        }

        $id = $this->productoModelo->crear($campos['nombre_p'], $campos['precio'], $campos['descripcion'], null);
        $producto = $this->productoModelo->obtenerPorId($id);

        $this->responderJson($producto, 201);
    }

    public function actualizar() {
        $producto = $this->buscarProducto();
        if (!$producto) {
            return;
        }

        $datos = $this->leerJson();

        if ($datos === null) {
            $this->jsonInvalido();
            return;
        }

        [$campos, $errores] = $this->validar($datos);

        if (!empty($errores)) {
            $this->erroresValidacion($errores);
            return;
        }

        // JSON no puede llevar archivos: se conserva la imagen que ya tenía (se cambia con api/productos/imagen)
        $this->productoModelo->actualizar($producto['id_pro'], $campos['nombre_p'], $campos['precio'], $campos['descripcion'], $producto['imagen']);

        $this->responderJson($this->productoModelo->obtenerPorId($producto['id_pro']));
    }

    public function eliminar() {
        $producto = $this->buscarProducto();
        if (!$producto) {
            return;
        }

        // Un producto que aparece en algún pedido no se puede borrar:
        // la llave foránea de detalle_pedido lo impide (error 1451 de MySQL)
        try {
            $this->productoModelo->eliminar($producto['id_pro']);
        } catch (PDOException $e) {
            if ($this->esBloqueoPorLlaveForanea($e)) {
                $this->responderJson(['error' => 'No se puede eliminar: el producto está en uno o más pedidos'], 409);
                return;
            }
            throw $e;
        }

        ImagenProducto::borrar($producto['imagen']);

        http_response_code(204);
    }

    // POST api/productos/imagen&id=N con multipart/form-data (campo "imagen")
    public function subirImagen() {
        $producto = $this->buscarProducto();
        if (!$producto) {
            return;
        }

        if (!isset($_FILES['imagen'])) {
            $this->erroresValidacion(['imagen' => 'Selecciona una imagen.']);
            return;
        }

        $subida = ImagenProducto::guardar($_FILES['imagen']);

        if ($subida['error']) {
            $this->erroresValidacion(['imagen' => $subida['error']]);
            return;
        }

        $this->productoModelo->actualizar($producto['id_pro'], $producto['nombre_p'], $producto['precio'], $producto['descripcion'], $subida['nombre']);
        // La imagen anterior ya no la usa nadie: se borra para no dejar huérfanas
        ImagenProducto::borrar($producto['imagen']);

        $this->responderJson($this->productoModelo->obtenerPorId($producto['id_pro']));
    }

    // DELETE api/productos/imagen&id=N: el producto vuelve a mostrar el logo
    public function quitarImagen() {
        $producto = $this->buscarProducto();
        if (!$producto) {
            return;
        }

        $this->productoModelo->actualizar($producto['id_pro'], $producto['nombre_p'], $producto['precio'], $producto['descripcion'], null);
        ImagenProducto::borrar($producto['imagen']);

        $this->responderJson($this->productoModelo->obtenerPorId($producto['id_pro']));
    }

    // Valida el id de la URL y busca el producto. Si algo falla, ya respondió el error y devuelve null
    private function buscarProducto() {
        $id = $this->obtenerId();

        if ($id === false) {
            $this->idInvalido();
            return null;
        }

        $producto = $this->productoModelo->obtenerPorId($id);

        if ($producto === false) {
            $this->noEncontrado('Producto no encontrado');
            return null;
        }

        return $producto;
    }

    // Reglas compartidas por crear() y actualizar()
    private function validar($datos) {
        // Si llega algo que no es texto (por ejemplo un arreglo), se trata como vacío
        $nombre = $this->texto($datos, 'nombre_p');
        $descripcion = $this->texto($datos, 'descripcion');
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

}
