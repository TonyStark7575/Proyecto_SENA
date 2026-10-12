<?php

require_once __DIR__ . '/ApiController.php';
require_once __DIR__ . '/../../models/Pedido.php';
require_once __DIR__ . '/../../models/DetallePedido.php';
require_once __DIR__ . '/../../models/Pago.php';
require_once __DIR__ . '/../../models/Cliente.php';
require_once __DIR__ . '/../../models/Producto.php';
require_once __DIR__ . '/../../models/Usuario.php';

class PedidoApiController extends ApiController {

    const ESTADOS = ['Pendiente', 'En proceso', 'Entregado', 'Cancelado'];

    // En estos estados ya no se agregan ni se quitan productos
    const ESTADOS_CERRADOS = ['Entregado', 'Cancelado'];

    const CANTIDAD_MAXIMA = 9999;

    // Máximo de una columna DECIMAL(10,2), en centavos
    const MAXIMO_CENTAVOS = 9999999999;

    private $conexion;
    private $pedidoModelo;
    private $detalleModelo;
    private $pagoModelo;
    private $clienteModelo;
    private $productoModelo;
    private $usuarioModelo;

    public function __construct($conexion) {
        $this->conexion = $conexion;
        $this->pedidoModelo = new Pedido($conexion);
        $this->detalleModelo = new DetallePedido($conexion);
        $this->pagoModelo = new Pago($conexion);
        $this->clienteModelo = new Cliente($conexion);
        $this->productoModelo = new Producto($conexion);
        $this->usuarioModelo = new Usuario($conexion);
    }

    // Además del CRUD: api/pedidos/lineas para agregar (POST, id = pedido) o quitar (DELETE, id = línea) productos
    protected function rutas() {
        $rutas = parent::rutas();
        $rutas['lineas'] = ['POST' => 'agregarLinea', 'DELETE' => 'quitarLinea'];
        return $rutas;
    }

    public function lista() {
        $this->responderJson($this->pedidoModelo->obtenerTodos());
    }

    public function detalle() {
        $pedido = $this->buscarPedido($this->obtenerId());
        if ($pedido) {
            $this->responderJson($this->completo($pedido['id_pedido']));
        }
    }

    // Crea el pedido con todos sus productos de una vez (el "carrito").
    // JSON: { id_cliente, estado, lineas: [ { id_pro, cantidad }, ... ] }
    public function crear() {
        $datos = $this->leerJson();

        if ($datos === null) {
            $this->jsonInvalido();
            return;
        }

        $errores = [];

        $idCliente = $this->validarCliente($datos, $errores);
        $estado = $this->validarEstado($datos, $errores);

        $lineas = [];
        if (!is_array($datos['lineas'] ?? null) || count($datos['lineas']) === 0) {
            $errores['lineas'] = 'Agrega al menos un producto al pedido';
        } else {
            $lineas = $this->validarLineas($datos['lineas'], 0, $errores);
        }

        if (!empty($errores)) {
            $this->erroresValidacion($errores);
            return;
        }

        // Todo o nada: si falla una línea, no queda un pedido a medias
        $this->conexion->beginTransaction();
        try {
            $idPedido = $this->pedidoModelo->crear($idCliente, $_SESSION['id_usuario'], $estado);
            foreach ($lineas as $linea) {
                $this->detalleModelo->agregar($idPedido, $linea['id_pro'], $linea['cantidad'], $linea['precio']);
            }
            $this->conexion->commit();
        } catch (Throwable $e) {
            $this->conexion->rollBack();
            throw $e;
        }

        // Los triggers ya calcularon total y saldo
        $this->responderJson($this->completo($idPedido), 201);
    }

    // Cambia el cliente y el estado (los productos se manejan con api/pedidos/lineas)
    public function actualizar() {
        $pedido = $this->buscarPedido($this->obtenerId());
        if (!$pedido) {
            return;
        }

        $datos = $this->leerJson();

        if ($datos === null) {
            $this->jsonInvalido();
            return;
        }

        $errores = [];
        $idCliente = $this->validarCliente($datos, $errores);
        $estado = $this->validarEstado($datos, $errores);

        if (!empty($errores)) {
            $this->erroresValidacion($errores);
            return;
        }

        $this->pedidoModelo->actualizar($pedido['id_pedido'], $idCliente, $estado);

        $this->responderJson($this->completo($pedido['id_pedido']));
    }

    public function eliminar() {
        $pedido = $this->buscarPedido($this->obtenerId());
        if (!$pedido) {
            return;
        }

        // Un pedido con pagos es parte del historial de dinero: no se borra, se cancela
        if (count($this->pagoModelo->obtenerPorPedido($pedido['id_pedido'])) > 0) {
            $this->responderJson(['error' => 'No se puede eliminar: el pedido tiene pagos registrados. Puedes cambiar su estado a Cancelado.'], 409);
            return;
        }

        // Sus líneas se borran solas (ON DELETE CASCADE)
        $this->pedidoModelo->eliminar($pedido['id_pedido']);

        http_response_code(204);
    }

    // POST api/pedidos/lineas&id=PEDIDO   JSON: { id_pro, cantidad }
    public function agregarLinea() {
        $pedido = $this->buscarPedido($this->obtenerId());
        if (!$pedido || !$this->permiteCambiarLineas($pedido)) {
            return;
        }

        $datos = $this->leerJson();

        if ($datos === null) {
            $this->jsonInvalido();
            return;
        }

        $errores = [];
        $lineas = $this->validarLineas([$datos], $this->centavos($pedido['total']), $errores);

        if (!empty($errores)) {
            // Para una sola línea, los errores se devuelven con el nombre de cada campo
            $this->erroresValidacion($errores['detalle_lineas'][0] ?? $errores);
            return;
        }

        $linea = $lineas[0];
        $this->detalleModelo->agregar($pedido['id_pedido'], $linea['id_pro'], $linea['cantidad'], $linea['precio']);

        $this->responderJson($this->completo($pedido['id_pedido']), 201);
    }

    // DELETE api/pedidos/lineas&id=LINEA. Responde 200 con el pedido actualizado (totales recalculados)
    public function quitarLinea() {
        $idLinea = $this->obtenerId();

        if ($idLinea === false) {
            $this->idInvalido();
            return;
        }

        $linea = $this->detalleModelo->obtenerPorId($idLinea);

        if (!$linea) {
            $this->noEncontrado('Línea de pedido no encontrada');
            return;
        }

        $pedido = $this->pedidoModelo->obtenerPorId($linea['id_pedido_d']);
        if (!$this->permiteCambiarLineas($pedido)) {
            return;
        }

        // Lo ya pagado es total - saldo (lo mantienen los triggers)
        $pagado = $this->centavos($pedido['total']) - $this->centavos($pedido['saldo']);
        $totalNuevo = $this->centavos($pedido['total']) - $this->centavos($linea['subtotal']);

        if ($totalNuevo < $pagado) {
            $this->responderJson(['error' => 'No se puede quitar: el total del pedido quedaría por debajo de lo que ya se pagó.'], 409);
            return;
        }

        $this->detalleModelo->eliminar($idLinea);

        $this->responderJson($this->completo($pedido['id_pedido']));
    }

    // Pedido con sus líneas, sus pagos y quién lo registró
    private function completo($idPedido) {
        $pedido = $this->pedidoModelo->obtenerPorId($idPedido);
        $registradoPor = $this->usuarioModelo->obtenerPorId($pedido['id_usuario_p']);

        $pedido['registrado_por'] = $registradoPor ? $registradoPor['nombre_u'] : null;
        $pedido['lineas'] = $this->detalleModelo->obtenerPorPedido($idPedido);
        $pedido['pagos'] = $this->pagoModelo->obtenerPorPedido($idPedido);

        return $pedido;
    }

    private function buscarPedido($id) {
        if ($id === false) {
            $this->idInvalido();
            return null;
        }

        $pedido = $this->pedidoModelo->obtenerPorId($id);

        if ($pedido === false) {
            $this->noEncontrado('Pedido no encontrado');
            return null;
        }

        return $pedido;
    }

    private function permiteCambiarLineas($pedido) {
        if (in_array($pedido['estado'], self::ESTADOS_CERRADOS)) {
            $this->responderJson(['error' => 'No se pueden cambiar los productos de un pedido ' . $pedido['estado'] . '.'], 409);
            return false;
        }
        return true;
    }

    private function validarCliente($datos, &$errores) {
        $idCliente = filter_var($datos['id_cliente'] ?? null, FILTER_VALIDATE_INT);

        if ($idCliente === false || !$this->clienteModelo->obtenerPorId($idCliente)) {
            $errores['id_cliente'] = 'Selecciona un cliente válido';
        }

        return $idCliente;
    }

    private function validarEstado($datos, &$errores) {
        $estado = $datos['estado'] ?? 'Pendiente';

        if (!in_array($estado, self::ESTADOS, true)) {
            $errores['estado'] = 'El estado debe ser Pendiente, En proceso, Entregado o Cancelado';
        }

        return $estado;
    }

    /**
     * Valida una lista de { id_pro, cantidad }. Si un producto se repite, junta las cantidades.
     * El precio se toma de la BD (nunca del cliente) y queda guardado en la línea,
     * así el pedido conserva el precio del momento aunque el producto cambie después.
     * $totalActual: centavos que el pedido ya suma (0 al crear).
     */
    private function validarLineas($lineasRecibidas, $totalActual, &$errores) {
        $porProducto = [];
        $erroresPorLinea = [];

        foreach (array_values($lineasRecibidas) as $i => $linea) {
            $idPro = filter_var($linea['id_pro'] ?? null, FILTER_VALIDATE_INT);
            $cantidad = filter_var($linea['cantidad'] ?? null, FILTER_VALIDATE_INT);
            $errorLinea = [];

            $producto = $idPro !== false ? $this->productoModelo->obtenerPorId($idPro) : false;
            if (!$producto) {
                $errorLinea['id_pro'] = 'Selecciona un producto válido';
            }

            if ($cantidad === false || $cantidad < 1 || $cantidad > self::CANTIDAD_MAXIMA) {
                $errorLinea['cantidad'] = 'La cantidad debe ser un número entero entre 1 y ' . self::CANTIDAD_MAXIMA;
            }

            if ($errorLinea) {
                $erroresPorLinea[$i] = $errorLinea;
                continue;
            }

            if (isset($porProducto[$idPro])) {
                $porProducto[$idPro]['cantidad'] += $cantidad;
            } else {
                $porProducto[$idPro] = ['id_pro' => $idPro, 'cantidad' => $cantidad, 'precio' => $producto['precio']];
            }
        }

        if ($erroresPorLinea) {
            $errores['lineas'] = 'Revisa los productos del pedido';
            $errores['detalle_lineas'] = $erroresPorLinea;
            return [];
        }

        // Ni cada subtotal ni el total pueden pasar del máximo de DECIMAL(10,2)
        $total = $totalActual;
        foreach ($porProducto as $linea) {
            $subtotal = $linea['cantidad'] * $this->centavos($linea['precio']);
            $total += $subtotal;
            if ($linea['cantidad'] > self::CANTIDAD_MAXIMA || $subtotal > self::MAXIMO_CENTAVOS || $total > self::MAXIMO_CENTAVOS) {
                $errores['lineas'] = 'El total del pedido supera el máximo permitido';
                return [];
            }
        }

        return array_values($porProducto);
    }

    // Los montos se comparan en centavos enteros para evitar errores de redondeo con decimales
    private function centavos($valor) {
        return (int) round(((float) $valor) * 100);
    }

}
