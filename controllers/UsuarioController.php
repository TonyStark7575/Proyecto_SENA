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
        $nombre = $_POST['nombre'];
        $email = $_POST['email'];
        $telefono = $_POST['telefono'];
        $password = $_POST['password'];
        $confirmarPassword = $_POST['confirmar-password'];
        $rol = $_POST['rol'];

        if ($password !== $confirmarPassword) {
            $error = "Las contraseñas no coinciden.";
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
        $nombre = $_POST['nombre'];
        $email = $_POST['email'];
        $telefono = $_POST['telefono'];
        $rol = $_POST['rol'];
        $password = $_POST['password'];
        $confirmarPassword = $_POST['confirmar-password'];

        if ($password !== $confirmarPassword) {
            $error = "Las contraseñas no coinciden.";
            $usuario = $this->usuarioModelo->obtenerPorId($id);
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

}