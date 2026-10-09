<?php

require_once __DIR__ . '/../models/Cliente.php';
require_once __DIR__ . '/../models/Usuario.php';

class ClienteController {

    private $clienteModelo;
    private $usuarioModelo;

    public function __construct($conexion) {
        $this->clienteModelo = new Cliente($conexion);
        $this->usuarioModelo = new Usuario($conexion);
    }

    public function lista() {
        $clientes = $this->clienteModelo->obtenerTodos();
        require __DIR__ . '/../views/clientes/lista.php';
    }

    public function crear() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $nombre = $_POST['nombre'];
            $email = $_POST['email'];
            $telefono = $_POST['telefono'];
            // El cliente queda registrado por el usuario que inició sesión
            $idUsuario = $_SESSION['id_usuario'];

            $this->clienteModelo->crear($nombre, $email, $telefono, $idUsuario);

            header('Location: /ProyectoSENA/public/index.php?ruta=clientes/lista');
            exit;
        }

        require __DIR__ . '/../views/clientes/crear.php';
    }

    public function detalle() {
        $id = $_GET['id'];
        $cliente = $this->clienteModelo->obtenerPorId($id);
        $usuarioRegistro = $this->usuarioModelo->obtenerPorId($cliente['id_usuario_c']);

        require __DIR__ . '/../views/clientes/detalle.php';
    }

    public function editar() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $id = $_POST['id'];
            $nombre = $_POST['nombre'];
            $email = $_POST['email'];
            $telefono = $_POST['telefono'];
            // "Registrado por" no cambia al editar: se conserva el usuario original
            $cliente = $this->clienteModelo->obtenerPorId($id);
            $this->clienteModelo->actualizar($id, $nombre, $email, $telefono, $cliente['id_usuario_c']);

            header('Location: /ProyectoSENA/public/index.php?ruta=clientes/lista');
            exit;
        }

        $id = $_GET['id'];
        $cliente = $this->clienteModelo->obtenerPorId($id);

        require __DIR__ . '/../views/clientes/editar.php';
    }

    public function eliminar() {
        $id = $_POST['id'];
        $this->clienteModelo->eliminar($id);
        header('Location: /ProyectoSENA/public/index.php?ruta=clientes/lista');
        exit;
    }

}