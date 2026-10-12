<?php

require_once __DIR__ . '/ApiController.php';
require_once __DIR__ . '/../../models/Usuario.php';

class UsuarioApiController extends ApiController {

    const ROLES = ['ADMINISTRADORA', 'REPARTIDOR'];

    const PASSWORD_MINIMO = 8;

    // password_hash (bcrypt) solo usa los primeros 72 bytes: más largo daría una falsa sensación de seguridad
    const PASSWORD_MAXIMO = 72;

    private $usuarioModelo;

    public function __construct($conexion) {
        $this->usuarioModelo = new Usuario($conexion);
    }

    // Además del CRUD: api/usuarios/activo para activar o desactivar (PUT, JSON { activo: true|false })
    protected function rutas() {
        $rutas = parent::rutas();
        $rutas['activo'] = ['PUT' => 'cambiarActivo'];
        return $rutas;
    }

    public function lista() {
        $usuarios = array_map([$this, 'sinPassword'], $this->usuarioModelo->obtenerTodos());
        $this->responderJson($usuarios);
    }

    public function detalle() {
        $usuario = $this->buscarUsuario();
        if ($usuario) {
            $this->responderJson($this->publico($usuario));
        }
    }

    // JSON: { nombre_u, email_u, telefono_u, password, rol }
    public function crear() {
        $datos = $this->leerJson();

        if ($datos === null) {
            $this->jsonInvalido();
            return;
        }

        [$campos, $errores] = $this->validar($datos, true);

        if (!empty($errores)) {
            $this->erroresValidacion($errores);
            return;
        }

        try {
            $id = $this->usuarioModelo->crear($campos['nombre_u'], $campos['email_u'], $campos['telefono_u'], $campos['password'], $campos['rol']);
        } catch (PDOException $e) {
            if ($this->esValorRepetido($e)) {
                $this->erroresValidacion(['email_u' => 'Ya existe un usuario con ese correo']);
                return;
            }
            throw $e;
        }

        $this->responderJson($this->publico($this->usuarioModelo->obtenerPorId($id)), 201);
    }

    // La contraseña es opcional: si llega vacía, se conserva la actual
    public function actualizar() {
        $usuario = $this->buscarUsuario();
        if (!$usuario) {
            return;
        }

        $datos = $this->leerJson();

        if ($datos === null) {
            $this->jsonInvalido();
            return;
        }

        [$campos, $errores] = $this->validar($datos, false);

        // Si la administradora se quitara su propio rol, podría quedar sin nadie que administre
        if ($this->esUsuarioDeLaSesion($usuario) && $campos['rol'] !== $usuario['rol']) {
            $errores['rol'] = 'No puedes cambiar tu propio rol';
        }

        if (!empty($errores)) {
            $this->erroresValidacion($errores);
            return;
        }

        try {
            $this->usuarioModelo->actualizar($usuario['id_usuario'], $campos['nombre_u'], $campos['email_u'], $campos['telefono_u'], $campos['rol'], $campos['password'] ?: null);
        } catch (PDOException $e) {
            if ($this->esValorRepetido($e)) {
                $this->erroresValidacion(['email_u' => 'Ya existe un usuario con ese correo']);
                return;
            }
            throw $e;
        }

        $this->responderJson($this->publico($this->usuarioModelo->obtenerPorId($usuario['id_usuario'])));
    }

    /**
     * Misma regla que UsuarioController::eliminar():
     * - sin clientes ni pedidos registrados → se borra de verdad (204)
     * - con registros → se desactiva para conservar el historial (200, resultado "desactivado")
     */
    public function eliminar() {
        $usuario = $this->buscarUsuario();
        if (!$usuario) {
            return;
        }

        if ($this->esUsuarioDeLaSesion($usuario)) {
            $this->responderJson(['error' => 'No puedes eliminar ni desactivar tu propio usuario mientras tienes la sesión iniciada.'], 409);
            return;
        }

        $registros = $this->usuarioModelo->contarRegistros($usuario['id_usuario']);

        if ($registros['clientes'] == 0 && $registros['pedidos'] == 0) {
            try {
                $this->usuarioModelo->eliminar($usuario['id_usuario']);
                http_response_code(204);
                return;
            } catch (PDOException $e) {
                // Otra llave foránea lo bloqueó: se trata igual que si tuviera registros
                if (!$this->esBloqueoPorLlaveForanea($e)) {
                    throw $e;
                }
            }
        }

        $this->usuarioModelo->cambiarActivo($usuario['id_usuario'], 0);

        $this->responderJson([
            'resultado' => 'desactivado',
            'mensaje' => 'El usuario "' . $usuario['nombre_u'] . '" tiene ' . $registros['clientes'] . ' cliente(s) y '
                . $registros['pedidos'] . ' pedido(s) registrados, por eso no se puede eliminar. '
                . 'Se desactivó: ya no podrá iniciar sesión, pero su historial se conserva.',
            'usuario' => $this->publico($this->usuarioModelo->obtenerPorId($usuario['id_usuario'])),
        ]);
    }

    // PUT api/usuarios/activo&id=N   JSON: { activo: true|false }
    public function cambiarActivo() {
        $usuario = $this->buscarUsuario();
        if (!$usuario) {
            return;
        }

        $datos = $this->leerJson();

        if ($datos === null) {
            $this->jsonInvalido();
            return;
        }

        if (!is_bool($datos['activo'] ?? null)) {
            $this->erroresValidacion(['activo' => 'El campo activo debe ser true o false']);
            return;
        }

        if (!$datos['activo'] && $this->esUsuarioDeLaSesion($usuario)) {
            $this->responderJson(['error' => 'No puedes desactivar tu propio usuario mientras tienes la sesión iniciada.'], 409);
            return;
        }

        $this->usuarioModelo->cambiarActivo($usuario['id_usuario'], $datos['activo'] ? 1 : 0);

        $this->responderJson($this->publico($this->usuarioModelo->obtenerPorId($usuario['id_usuario'])));
    }

    private function buscarUsuario() {
        $id = $this->obtenerId();

        if ($id === false) {
            $this->idInvalido();
            return null;
        }

        $usuario = $this->usuarioModelo->obtenerPorId($id);

        if ($usuario === false) {
            $this->noEncontrado('Usuario no encontrado');
            return null;
        }

        return $usuario;
    }

    private function esUsuarioDeLaSesion($usuario) {
        return (int) $usuario['id_usuario'] === (int) $_SESSION['id_usuario'];
    }

    // El hash de la contraseña nunca sale de la API
    private function sinPassword($usuario) {
        unset($usuario['password']);
        return $usuario;
    }

    // Datos del detalle: sin contraseña, y con cuántos clientes y pedidos registró
    // (React lo usa para avisar si al "eliminar" en realidad se va a desactivar)
    private function publico($usuario) {
        $usuario = $this->sinPassword($usuario);
        $registros = $this->usuarioModelo->contarRegistros($usuario['id_usuario']);
        $usuario['registros'] = ['clientes' => (int) $registros['clientes'], 'pedidos' => (int) $registros['pedidos']];
        return $usuario;
    }

    private function validar($datos, $esNuevo) {
        $nombre = $this->texto($datos, 'nombre_u');
        $email = $this->texto($datos, 'email_u');
        $telefono = $this->texto($datos, 'telefono_u');
        // La contraseña no se recorta: los espacios pueden ser parte de ella
        $password = is_string($datos['password'] ?? null) ? $datos['password'] : '';
        $rol = $datos['rol'] ?? null;

        $errores = [];

        if ($nombre === '') {
            $errores['nombre_u'] = 'El nombre es obligatorio';
        } elseif (mb_strlen($nombre) > 50) {
            $errores['nombre_u'] = 'El nombre no puede tener más de 50 caracteres';
        }

        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            $errores['email_u'] = 'El correo no tiene un formato válido (ejemplo: nombre@correo.com)';
        } elseif (mb_strlen($email) > 255) {
            $errores['email_u'] = 'El correo no puede tener más de 255 caracteres';
        }

        if (mb_strlen($telefono) > 50) {
            $errores['telefono_u'] = 'El teléfono no puede tener más de 50 caracteres';
        }

        if ($password === '') {
            if ($esNuevo) {
                $errores['password'] = 'La contraseña es obligatoria';
            }
        } elseif (mb_strlen($password) < self::PASSWORD_MINIMO) {
            $errores['password'] = 'La contraseña debe tener al menos ' . self::PASSWORD_MINIMO . ' caracteres';
        } elseif (strlen($password) > self::PASSWORD_MAXIMO) {
            $errores['password'] = 'La contraseña no puede tener más de ' . self::PASSWORD_MAXIMO . ' caracteres';
        }

        if (!in_array($rol, self::ROLES, true)) {
            $errores['rol'] = 'Selecciona un rol válido';
        }

        $campos = ['nombre_u' => $nombre, 'email_u' => $email, 'telefono_u' => $telefono, 'password' => $password, 'rol' => $rol];

        return [$campos, $errores];
    }

}
