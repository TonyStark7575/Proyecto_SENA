<?php

require_once __DIR__ . '/ApiController.php';
require_once __DIR__ . '/../../models/Usuario.php';

class AuthApiController extends ApiController {

    private $usuarioModelo;

    public function __construct($conexion) {
        $this->usuarioModelo = new Usuario($conexion);
    }

    public function login() {
        $datos = $this->leerJson();

        if ($datos === null) {
            $this->responderJson(['error' => 'El cuerpo de la petición debe ser JSON válido'], 400);
            return;
        }

        // Si llega algo que no es texto (por ejemplo un arreglo), se trata como vacío
        $email = is_string($datos['email'] ?? null) ? trim($datos['email']) : '';
        $password = is_string($datos['password'] ?? null) ? $datos['password'] : '';

        $errores = [];

        if ($email === '') {
            $errores['email'] = 'El correo es obligatorio';
        }

        if ($password === '') {
            $errores['password'] = 'La contraseña es obligatoria';
        }

        if (!empty($errores)) {
            $this->responderJson(['errores' => $errores], 422);
            return;
        }

        $usuario = $this->usuarioModelo->obtenerPorEmail($email);

        if (!$usuario || !password_verify($password, $usuario['password'])) {
            $this->responderJson(['error' => 'Credenciales incorrectas'], 401);
            return;
        }

        // Misma regla que LoginController: solo se avisa del bloqueo si la contraseña es correcta
        if (!$usuario['activo']) {
            $this->responderJson(['error' => 'Este usuario está desactivado. Habla con la administradora.'], 403);
            return;
        }

        session_regenerate_id(true);

        $_SESSION['id_usuario'] = $usuario['id_usuario'];
        $_SESSION['nombre'] = $usuario['nombre_u'];
        $_SESSION['rol'] = $usuario['rol'];

        $this->responderSesion();
    }

    public function logout() {
        $_SESSION = [];
        session_destroy();

        http_response_code(204);
    }

    public function sesion() {
        $this->responderSesion();
    }

    // Nunca incluye el hash de la contraseña: solo lo que React necesita
    private function responderSesion() {
        $this->responderJson([
            'id_usuario' => $_SESSION['id_usuario'],
            'nombre' => $_SESSION['nombre'],
            'rol' => $_SESSION['rol'],
        ]);
    }

}
