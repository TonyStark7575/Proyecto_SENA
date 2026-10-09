<?php

require_once __DIR__ . '/../models/Usuario.php';

class UsuarioController {

    private $usuarioModelo;

    public function __construct($conexion) {
        $this->usuarioModelo = new Usuario($conexion);
    }

    public function lista() {
        $usuarios = $this->usuarioModelo->obtenerTodos();

        $mensaje = $_SESSION['mensaje'] ?? null;
        unset($_SESSION['mensaje']);

        require __DIR__ . '/../views/usuarios/lista.php';
    }

    public function crear() {
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $nombre = trim($_POST['nombre']);
        $email = trim($_POST['email']);
        $telefono = trim($_POST['telefono']);
        $password = $_POST['password'];
        $confirmarPassword = $_POST['confirmar-password'];
        $rol = $_POST['rol'];

        $error = $this->validar($nombre, $email);

        if (!$error && $password === '') {
            $error = "La contraseña es obligatoria.";
        }

        if (!$error && $password !== $confirmarPassword) {
            $error = "Las contraseñas no coinciden.";
        }

        if ($error) {
            require __DIR__ . '/../views/usuarios/crear.php';
            return;
        }

        $this->usuarioModelo->crear($nombre, $email, $telefono, $password, $rol);

        header('Location: /ProyectoSENA/public/index.php?ruta=usuarios/lista');
        exit;
    }

    require __DIR__ . '/../views/usuarios/crear.php';
}

    public function detalle() {
        $id = $_GET['id'] ?? null;
        $usuario = $this->usuarioModelo->obtenerPorId($id);

        if (!$usuario) {
            $this->noEncontrado();
            return;
        }

        require __DIR__ . '/../views/usuarios/detalle.php';
    }

   public function editar() {
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $id = $_POST['id'];
        $nombre = trim($_POST['nombre']);
        $email = trim($_POST['email']);
        $telefono = trim($_POST['telefono']);
        $rol = $_POST['rol'];
        $password = $_POST['password'];
        $confirmarPassword = $_POST['confirmar-password'];

        $usuario = $this->usuarioModelo->obtenerPorId($id);
        if (!$usuario) {
            $this->noEncontrado();
            return;
        }

        $error = $this->validar($nombre, $email);

        if (!$error && $password !== $confirmarPassword) {
            $error = "Las contraseñas no coinciden.";
        }

        if ($error) {
            // Se devuelve el formulario con lo que el usuario escribió
            $usuario['nombre_u'] = $nombre;
            $usuario['email_u'] = $email;
            $usuario['telefono_u'] = $telefono;
            require __DIR__ . '/../views/usuarios/editar.php';
            return;
        }

        $this->usuarioModelo->actualizar($id, $nombre, $email, $telefono, $rol, $password);

        header('Location: /ProyectoSENA/public/index.php?ruta=usuarios/lista');
        exit;
    }

    $id = $_GET['id'] ?? null;
    $usuario = $this->usuarioModelo->obtenerPorId($id);

    if (!$usuario) {
        $this->noEncontrado();
        return;
    }

    require __DIR__ . '/../views/usuarios/editar.php';
}

    // Si el usuario no tiene registros asociados se borra de verdad.
    // Si registró clientes o pedidos, la BD no deja borrarlo (RESTRICT),
    // así que se desactiva para conservar el historial.
    public function eliminar() {
        $id = $_POST['id'];
        $usuario = $this->usuarioModelo->obtenerPorId($id);

        if (!$usuario) {
            $this->noEncontrado();
            return;
        }

        if ($usuario['id_usuario'] == $_SESSION['id_usuario']) {
            $this->volverALista('danger', 'No puedes eliminar ni desactivar tu propio usuario mientras tienes la sesión iniciada.');
        }

        $registros = $this->usuarioModelo->contarRegistros($id);
        $tieneRegistros = $registros['clientes'] > 0 || $registros['pedidos'] > 0;

        if (!$tieneRegistros) {
            try {
                $this->usuarioModelo->eliminar($id);
                $this->volverALista('success', 'Usuario "' . $usuario['nombre_u'] . '" eliminado.');
            } catch (PDOException $e) {
                // Otra llave foránea lo bloqueó: se trata igual que si tuviera registros
            }
        }

        $this->usuarioModelo->cambiarActivo($id, 0);
        $this->volverALista('warning',
            'El usuario "' . $usuario['nombre_u'] . '" tiene ' . $registros['clientes'] . ' cliente(s) y '
            . $registros['pedidos'] . ' pedido(s) registrados, por eso no se puede eliminar. '
            . 'Se desactivó: ya no podrá iniciar sesión, pero su historial se conserva.');
    }

    public function activar() {
        $id = $_POST['id'];
        $usuario = $this->usuarioModelo->obtenerPorId($id);

        if (!$usuario) {
            $this->noEncontrado();
            return;
        }

        $this->usuarioModelo->cambiarActivo($id, 1);
        $this->volverALista('success', 'Usuario "' . $usuario['nombre_u'] . '" reactivado.');
    }

    // Guarda un mensaje en la sesión para mostrarlo una sola vez en la lista
    private function volverALista($tipo, $mensaje) {
        $_SESSION['mensaje'] = ['tipo' => $tipo, 'texto' => $mensaje];
        header('Location: /ProyectoSENA/public/index.php?ruta=usuarios/lista');
        exit;
    }

    private function validar($nombre, $email) {
        if ($nombre === '') {
            return 'El nombre es obligatorio.';
        }
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            return 'El correo no tiene un formato válido (ejemplo: nombre@correo.com).';
        }
        return null;
    }

    private function noEncontrado() {
        http_response_code(404);
        $mensaje = 'El usuario que buscas no existe o fue eliminado.';
        $volver = '/ProyectoSENA/public/index.php?ruta=usuarios/lista';
        require __DIR__ . '/../views/layouts/no_encontrado.php';
    }

}
