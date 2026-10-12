<?php

require_once __DIR__ . '/ApiController.php';
require_once __DIR__ . '/../../models/Cliente.php';
require_once __DIR__ . '/../../models/Usuario.php';
require_once __DIR__ . '/../../models/Pedido.php';
require_once __DIR__ . '/../../models/Pago.php';

class ClienteApiController extends ApiController {

    private $clienteModelo;
    private $usuarioModelo;
    private $pedidoModelo;
    private $pagoModelo;

    public function __construct($conexion) {
        $this->clienteModelo = new Cliente($conexion);
        $this->usuarioModelo = new Usuario($conexion);
        $this->pedidoModelo = new Pedido($conexion);
        $this->pagoModelo = new Pago($conexion);
    }

    public function lista() {
        $this->responderJson($this->clienteModelo->obtenerTodos());
    }

    // El detalle incluye quién lo registró y su historial de pedidos y pagos
    public function detalle() {
        $cliente = $this->buscarCliente();
        if (!$cliente) {
            return;
        }

        $this->responderJson($this->conHistorial($cliente));
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

        // El cliente queda registrado por el usuario que inició sesión
        try {
            $id = $this->clienteModelo->crear($campos['nombre_c'], $campos['email_c'], $campos['telefono_c'], $_SESSION['id_usuario']);
        } catch (PDOException $e) {
            if ($this->esValorRepetido($e)) {
                $this->erroresValidacion(['email_c' => 'Ya existe un cliente con ese correo']);
                return;
            }
            throw $e;
        }

        $this->responderJson($this->conHistorial($this->clienteModelo->obtenerPorId($id)), 201);
    }

    public function actualizar() {
        $cliente = $this->buscarCliente();
        if (!$cliente) {
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

        // Quien lo registró no cambia al editar
        try {
            $this->clienteModelo->actualizar($cliente['id_cliente'], $campos['nombre_c'], $campos['email_c'], $campos['telefono_c'], $cliente['id_usuario_c']);
        } catch (PDOException $e) {
            if ($this->esValorRepetido($e)) {
                $this->erroresValidacion(['email_c' => 'Ya existe un cliente con ese correo']);
                return;
            }
            throw $e;
        }

        $this->responderJson($this->conHistorial($this->clienteModelo->obtenerPorId($cliente['id_cliente'])));
    }

    public function eliminar() {
        $cliente = $this->buscarCliente();
        if (!$cliente) {
            return;
        }

        // Un cliente con pedidos no se puede borrar: la llave foránea de pedido lo impide
        try {
            $this->clienteModelo->eliminar($cliente['id_cliente']);
        } catch (PDOException $e) {
            if ($this->esBloqueoPorLlaveForanea($e)) {
                $this->responderJson(['error' => 'No se puede eliminar: el cliente tiene pedidos registrados'], 409);
                return;
            }
            throw $e;
        }

        http_response_code(204);
    }

    private function buscarCliente() {
        $id = $this->obtenerId();

        if ($id === false) {
            $this->idInvalido();
            return null;
        }

        $cliente = $this->clienteModelo->obtenerPorId($id);

        if ($cliente === false) {
            $this->noEncontrado('Cliente no encontrado');
            return null;
        }

        return $cliente;
    }

    private function conHistorial($cliente) {
        $registradoPor = $this->usuarioModelo->obtenerPorId($cliente['id_usuario_c']);

        $cliente['registrado_por'] = $registradoPor ? $registradoPor['nombre_u'] : null;
        $cliente['pedidos'] = $this->pedidoModelo->obtenerPorCliente($cliente['id_cliente']);
        $cliente['pagos'] = $this->pagoModelo->obtenerPorCliente($cliente['id_cliente']);

        return $cliente;
    }

    // Nombre obligatorio, correo con formato válido y largos que caben en las columnas
    private function validar($datos) {
        $nombre = $this->texto($datos, 'nombre_c');
        $email = $this->texto($datos, 'email_c');
        $telefono = $this->texto($datos, 'telefono_c');

        $errores = [];

        if ($nombre === '') {
            $errores['nombre_c'] = 'El nombre es obligatorio';
        } elseif (mb_strlen($nombre) > 50) {
            $errores['nombre_c'] = 'El nombre no puede tener más de 50 caracteres';
        }

        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            $errores['email_c'] = 'El correo no tiene un formato válido (ejemplo: nombre@correo.com)';
        } elseif (mb_strlen($email) > 255) {
            $errores['email_c'] = 'El correo no puede tener más de 255 caracteres';
        }

        if (mb_strlen($telefono) > 50) {
            $errores['telefono_c'] = 'El teléfono no puede tener más de 50 caracteres';
        }

        $campos = ['nombre_c' => $nombre, 'email_c' => $email, 'telefono_c' => $telefono];

        return [$campos, $errores];
    }

}
