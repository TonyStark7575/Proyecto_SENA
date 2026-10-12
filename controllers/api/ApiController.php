<?php

// Clase base de los controladores de la API: reúne lo que todos repiten
class ApiController {

    protected function responderJson($datos, $codigo = 200) {
        http_response_code($codigo);
        header('Content-Type: application/json; charset=utf-8');
        echo json_encode($datos, JSON_UNESCAPED_UNICODE);
    }

    // Devuelve el cuerpo JSON como arreglo, o null si no es JSON válido
    // (o si es JSON pero no un objeto/arreglo, por ejemplo un número suelto)
    protected function leerJson() {
        $datos = json_decode(file_get_contents('php://input'), true);
        return is_array($datos) ? $datos : null;
    }

    // Devuelve el id de la URL como entero, o false si falta o no es un número entero
    protected function obtenerId() {
        return filter_var($_GET['id'] ?? null, FILTER_VALIDATE_INT);
    }

}
