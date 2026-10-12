<?php

// Clase base de los controladores de la API: reúne lo que todos repiten
class ApiController {

    /**
     * Qué método de este controlador atiende cada sub-ruta y cada método HTTP.
     * La sub-ruta '' es la principal (por ejemplo api/productos); otras, como 'imagen',
     * corresponden a api/productos/imagen. Cada controlador puede ampliar o cambiar este mapa.
     */
    protected function rutas() {
        return [
            '' => [
                'GET'    => isset($_GET['id']) ? 'detalle' : 'lista',
                'POST'   => 'crear',
                'PUT'    => 'actualizar',
                'DELETE' => 'eliminar',
            ],
        ];
    }

    // Lo llama public/index.php: busca el método que corresponde y lo ejecuta
    public function atender($subruta, $metodoHttp) {
        $rutas = $this->rutas();

        if (!isset($rutas[$subruta])) {
            $this->responderJson(['error' => 'Endpoint no encontrado'], 404);
            return;
        }

        $accion = $rutas[$subruta][$metodoHttp] ?? null;

        if ($accion === null) {
            $this->responderJson(['error' => 'Método no permitido'], 405);
            return;
        }

        $this->$accion();
    }

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
    protected function obtenerId($parametro = 'id') {
        return filter_var($_GET[$parametro] ?? null, FILTER_VALIDATE_INT);
    }

    // Texto de un campo JSON sin espacios a los lados; si no llegó o no es texto, devuelve ''
    protected function texto($datos, $campo) {
        return is_string($datos[$campo] ?? null) ? trim($datos[$campo]) : '';
    }

    // Respuestas que se repiten en casi todos los métodos
    protected function idInvalido() {
        $this->responderJson(['error' => 'El id debe ser un número entero'], 400);
    }

    protected function jsonInvalido() {
        $this->responderJson(['error' => 'El cuerpo de la petición debe ser JSON válido'], 400);
    }

    protected function noEncontrado($mensaje) {
        $this->responderJson(['error' => $mensaje], 404);
    }

    protected function erroresValidacion($errores) {
        $this->responderJson(['errores' => $errores], 422);
    }

    // Los montos se comparan en centavos enteros para evitar errores de redondeo con decimales
    // (por ejemplo 0.1 + 0.2 no da exactamente 0.3 con decimales de punto flotante)
    protected function centavos($valor) {
        return (int) round(((float) $valor) * 100);
    }

    // true si la excepción es de una llave foránea que impide borrar (error 1451 de MySQL)
    protected function esBloqueoPorLlaveForanea(PDOException $e) {
        return ($e->errorInfo[1] ?? null) === 1451;
    }

    // true si la excepción es de un valor UNIQUE repetido, como un correo (error 1062 de MySQL)
    protected function esValorRepetido(PDOException $e) {
        return ($e->errorInfo[1] ?? null) === 1062;
    }

}
