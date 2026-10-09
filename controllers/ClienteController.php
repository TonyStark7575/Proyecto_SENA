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
            $nombre = trim($_POST['nombre']);
            $email = trim($_POST['email']);
            $telefono = trim($_POST['telefono']);
            // El cliente queda registrado por el usuario que inició sesión
            $idUsuario = $_SESSION['id_usuario'];

            $error = $this->validar($nombre, $email);
            if ($error) {
                require __DIR__ . '/../views/clientes/crear.php';
                return;
            }

            $this->clienteModelo->crear($nombre, $email, $telefono, $idUsuario);

            header('Location: /ProyectoSENA/public/index.php?ruta=clientes/lista');
            exit;
        }

        require __DIR__ . '/../views/clientes/crear.php';
    }

    public function detalle() {
        $id = $_GET['id'] ?? null;
        $cliente = $this->clienteModelo->obtenerPorId($id);

        if (!$cliente) {
            $this->noEncontrado();
            return;
        }

        $usuarioRegistro = $this->usuarioModelo->obtenerPorId($cliente['id_usuario_c']);

        require __DIR__ . '/../views/clientes/detalle.php';
    }

    public function editar() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $id = $_POST['id'];
            $nombre = trim($_POST['nombre']);
            $email = trim($_POST['email']);
            $telefono = trim($_POST['telefono']);

            $cliente = $this->clienteModelo->obtenerPorId($id);
            if (!$cliente) {
                $this->noEncontrado();
                return;
            }

            $error = $this->validar($nombre, $email);
            if ($error) {
                // Se devuelve el formulario con lo que el usuario escribió
                $cliente['nombre_c'] = $nombre;
                $cliente['email_c'] = $email;
                $cliente['telefono_c'] = $telefono;
                require __DIR__ . '/../views/clientes/editar.php';
                return;
            }

            // "Registrado por" no cambia al editar: se conserva el usuario original
            $this->clienteModelo->actualizar($id, $nombre, $email, $telefono, $cliente['id_usuario_c']);

            header('Location: /ProyectoSENA/public/index.php?ruta=clientes/lista');
            exit;
        }

        $id = $_GET['id'] ?? null;
        $cliente = $this->clienteModelo->obtenerPorId($id);

        if (!$cliente) {
            $this->noEncontrado();
            return;
        }

        require __DIR__ . '/../views/clientes/editar.php';
    }

    public function eliminar() {
        $id = $_POST['id'];
        $this->clienteModelo->eliminar($id);
        header('Location: /ProyectoSENA/public/index.php?ruta=clientes/lista');
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
        $mensaje = 'El cliente que buscas no existe o fue eliminado.';
        $volver = '/ProyectoSENA/public/index.php?ruta=clientes/lista';
        require __DIR__ . '/../views/layouts/no_encontrado.php';
    }

}
