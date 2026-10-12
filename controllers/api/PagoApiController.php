<?php

require_once __DIR__ . '/ApiController.php';
require_once __DIR__ . '/../../models/Pago.php';
require_once __DIR__ . '/../../models/Pedido.php';

class PagoApiController extends ApiController {

    const METODOS = ['Efectivo', 'Transferencia'];

    private $pagoModelo;
    private $pedidoModelo;

    public function __construct($conexion) {
        $this->pagoModelo = new Pago($conexion);
        $this->pedidoModelo = new Pedido($conexion);
    }

    public function lista() {
        $this->responderJson($this->pagoModelo->obtenerTodos());
    }

    public function detalle() {
        $pago = $this->buscarPago();
        if ($pago) {
            $this->responderJson($pago);
        }
    }

    // JSON: { id_pedido, monto, metodo }. El saldo del pedido lo recalcula el trigger de pago
    public function crear() {
        $datos = $this->leerJson();

        if ($datos === null) {
            $this->jsonInvalido();
            return;
        }

        $errores = [];

        $idPedido = filter_var($datos['id_pedido'] ?? null, FILTER_VALIDATE_INT);
        $pedido = $idPedido !== false ? $this->pedidoModelo->obtenerPorId($idPedido) : false;

        if (!$pedido) {
            $errores['id_pedido'] = 'Selecciona un pedido válido';
        } elseif ($pedido['estado'] === 'Cancelado') {
            $this->pedidoCancelado();
            return;
        }

        // Al crear, el monto no puede pasar de lo que falta por pagar
        $maximo = $pedido ? $this->centavos($pedido['saldo']) : null;
        [$monto, $metodo] = $this->validarMontoYMetodo($datos, $maximo, $errores);

        if (!empty($errores)) {
            $this->erroresValidacion($errores);
            return;
        }

        $id = $this->pagoModelo->crear($pedido['id_pedido'], $monto, $metodo);

        $this->responderJson($this->pagoModelo->obtenerPorId($id), 201);
    }

    // JSON: { monto, metodo }. El pedido de un pago no se cambia (igual que en PHP)
    public function actualizar() {
        $pago = $this->buscarPago();
        if (!$pago) {
            return;
        }

        $pedido = $this->pedidoModelo->obtenerPorId($pago['id_pedido_p']);
        if ($pedido['estado'] === 'Cancelado') {
            $this->pedidoCancelado();
            return;
        }

        $datos = $this->leerJson();

        if ($datos === null) {
            $this->jsonInvalido();
            return;
        }

        // Al editar, este pago puede llegar hasta lo que falta por pagar más lo que ya valía
        $maximo = $this->centavos($pedido['saldo']) + $this->centavos($pago['monto']);

        $errores = [];
        [$monto, $metodo] = $this->validarMontoYMetodo($datos, $maximo, $errores);

        if (!empty($errores)) {
            $this->erroresValidacion($errores);
            return;
        }

        $this->pagoModelo->actualizar($pago['id_pago'], $monto, $metodo);

        $this->responderJson($this->pagoModelo->obtenerPorId($pago['id_pago']));
    }

    public function eliminar() {
        $pago = $this->buscarPago();
        if (!$pago) {
            return;
        }

        $this->pagoModelo->eliminar($pago['id_pago']);

        http_response_code(204);
    }

    private function buscarPago() {
        $id = $this->obtenerId();

        if ($id === false) {
            $this->idInvalido();
            return null;
        }

        $pago = $this->pagoModelo->obtenerPorId($id);

        if ($pago === false) {
            $this->noEncontrado('Pago no encontrado');
            return null;
        }

        return $pago;
    }

    private function pedidoCancelado() {
        $this->responderJson(['error' => 'El pedido está Cancelado: no admite pagos.'], 409);
    }

    // $maximo en centavos (null si no hay pedido válido: entonces no se compara)
    private function validarMontoYMetodo($datos, $maximo, &$errores) {
        $monto = filter_var($datos['monto'] ?? null, FILTER_VALIDATE_FLOAT);
        if ($monto !== false) {
            // Igual que el precio: se valida el valor que de verdad guardará DECIMAL(10,2)
            $monto = round($monto, 2);
        }

        if ($monto === false || $monto <= 0) {
            $errores['monto'] = 'El monto debe ser un número mayor que 0';
        } elseif ($maximo !== null && $this->centavos($monto) > $maximo) {
            $errores['monto'] = $maximo === 0
                ? 'Este pedido no tiene saldo pendiente'
                : 'El monto máximo para este pago es $' . number_format($maximo / 100, 2, ',', '.') . ' (lo que falta por pagar del pedido)';
        }

        $metodo = $datos['metodo'] ?? null;
        if (!in_array($metodo, self::METODOS, true)) {
            $errores['metodo'] = 'El método debe ser Efectivo o Transferencia';
        }

        return [$monto, $metodo];
    }

}
