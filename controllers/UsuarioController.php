<?php

require_once __DIR__ . '/../models/Usuario.php';

class UsuarioController {

    private $usuarioModelo;

    public function __construct($conexion) {
        $this->usuarioModelo = new Usuario($conexion);
    }

    public function lista() {
        $usuarios = $this->usuarioModelo->obtenerTodos();
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
        $id = $_GET['id'];
        $usuario = $this->usuarioModelo->obtenerPorId($id);
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

    $id = $_GET['id'];
    $usuario = $this->usuarioModelo->obtenerPorId($id);
    require __DIR__ . '/../views/usuarios/editar.php';
}

    public function eliminar() {
        $id = $_POST['id'];
        $this->usuarioModelo->eliminar($id);
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

}
