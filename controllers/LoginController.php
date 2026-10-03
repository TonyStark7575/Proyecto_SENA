<?php

require_once __DIR__ . '/../models/Usuario.php';

class LoginController {

    private $usuarioModelo;

    public function __construct($conexion) {
        $this->usuarioModelo = new Usuario($conexion);
    }

    public function index() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $email = trim($_POST['email']);
            $password = $_POST['password'];

            $usuario = $this->usuarioModelo->obtenerPorEmail($email);

            if ($usuario && password_verify($password, $usuario['password'])) {
                session_regenerate_id(true);

                $_SESSION['id_usuario'] = $usuario['id_usuario'];
                $_SESSION['nombre'] = $usuario['nombre_u'];
                $_SESSION['rol'] = $usuario['rol'];

                header('Location: /ProyectoSENA/public/index.php?ruta=inicio');
                exit;
            }

            $error = 'Credenciales incorrectas';
        }

        require __DIR__ . '/../views/login.php';
    }

}