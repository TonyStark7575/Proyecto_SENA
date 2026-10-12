<?php
/**
 * Punto de entrada del servidor: atiende solo la API (index.php?ruta=api/...).
 * La interfaz es la app React compilada en public/app/.
 */

// ===== RUTA =====
$ruta = $_GET['ruta'] ?? '';

$partes = explode('/', $ruta);
$recurso = $partes[0];
$accion = $partes[1] ?? '';

// Cualquier ruta que no sea de la API (por ejemplo un enlace viejo como ?ruta=clientes/lista)
// lleva a la pantalla equivalente de la app React
if ($recurso !== 'api') {
    $modulosApp = ['productos', 'clientes', 'pedidos', 'pagos', 'usuarios'];
    header('Location: app/' . (in_array($recurso, $modulosApp) ? $recurso : ''));
    exit;
}

// ===== MANEJO DE ERRORES =====
// Toda excepción no atrapada termina aquí, incluida la de conexión a la BD.
// El detalle real va al log de Apache; al cliente solo le llega un mensaje genérico.
set_exception_handler(function ($e) {
    // Mensaje, archivo y línea, sin la traza: la traza puede incluir argumentos como la contraseña de la BD
    error_log(get_class($e) . ': ' . $e->getMessage() . ' en ' . $e->getFile() . ':' . $e->getLine());

    // Descarta lo que se hubiera alcanzado a generar antes del error
    while (ob_get_level() > 0) {
        ob_end_clean();
    }

    http_response_code(500);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(['error' => 'Error interno del servidor']);
});

// ===== CONTENT-TYPE =====
// Va antes de la sesión y de la guardia para cubrir también api/login (ruta pública).
// Un formulario HTML de otra página no puede enviar application/json, así que esto frena el CSRF.
// Sin cuerpo (por ejemplo api/logout) no se exige.
// Excepción: las rutas que reciben archivos aceptan multipart/form-data, pero solo con la cabecera
// X-Requested-With, que un formulario de otra página tampoco puede enviar.
$rutasConArchivos = ['api/productos/imagen'];

if (in_array($_SERVER['REQUEST_METHOD'], ['POST', 'PUT'])) {
    // Con multipart, PHP no deja el cuerpo en php://input: por eso también se mira el Content-Length
    $hayCuerpo = (int) ($_SERVER['CONTENT_LENGTH'] ?? 0) > 0 || file_get_contents('php://input') !== '';
    $tipoCuerpo = strtolower(trim($_SERVER['CONTENT_TYPE'] ?? ''));

    $esJson = str_starts_with($tipoCuerpo, 'application/json');
    $esArchivoPermitido = in_array($ruta, $rutasConArchivos)
        && str_starts_with($tipoCuerpo, 'multipart/form-data')
        && isset($_SERVER['HTTP_X_REQUESTED_WITH']);

    if ($hayCuerpo && !$esJson && !$esArchivoPermitido) {
        http_response_code(415);
        header('Content-Type: application/json; charset=utf-8');
        echo json_encode(['error' => 'El cuerpo debe enviarse como application/json'], JSON_UNESCAPED_SLASHES);
        exit;
    }
}

session_start();
require_once __DIR__ . '/../config/conexion.php';

// ===== GUARDIA DE AUTENTICACIÓN =====
// Solo api/login (la ruta completa) se puede usar sin sesión
$esRutaPublica = ($ruta === 'api/login');
$haySesion = isset($_SESSION['id_usuario']);

// La sesión puede haber quedado abierta después de que el usuario fue desactivado o eliminado:
// en cada petición protegida se vuelve a consultar en la BD
if ($haySesion && !$esRutaPublica) {
    require_once __DIR__ . '/../models/Usuario.php';
    $usuarioModelo = new Usuario($conexion);
    $usuarioSesion = $usuarioModelo->obtenerPorId($_SESSION['id_usuario']);

    if (!$usuarioSesion || !$usuarioSesion['activo']) {
        $_SESSION = [];
        session_destroy();
        $haySesion = false;
    } else {
        // Por si la administradora cambió el nombre o el rol mientras la sesión estaba abierta
        $_SESSION['nombre'] = $usuarioSesion['nombre_u'];
        $_SESSION['rol'] = $usuarioSesion['rol'];
    }
}

if (!$haySesion && !$esRutaPublica) {
    http_response_code(401);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(['error' => 'No autenticado']);
    exit;
}

// ===== RUTAS DE LA API =====
// Recurso => controlador que lo atiende. Cada controlador decide, según la
// sub-ruta (api/productos/imagen) y el método HTTP, cuál de sus métodos ejecutar
$recursosApi = [
    'productos' => 'ProductoApiController',
    'clientes'  => 'ClienteApiController',
    'pedidos'   => 'PedidoApiController',
    'pagos'     => 'PagoApiController',
    'usuarios'  => 'UsuarioApiController',
    'dashboard' => 'DashboardApiController',
];

if (isset($recursosApi[$accion])) {
    $clase = $recursosApi[$accion];
    require_once __DIR__ . '/../controllers/api/' . $clase . '.php';
    $api = new $clase($conexion);
    $api->atender($partes[2] ?? '', $_SERVER['REQUEST_METHOD']);
} elseif (in_array($accion, ['login', 'logout', 'sesion'])) {
    require_once __DIR__ . '/../controllers/api/AuthApiController.php';
    $api = new AuthApiController($conexion);
    $metodo = $_SERVER['REQUEST_METHOD'];

    if ($accion === 'login' && $metodo === 'POST') {
        $api->login();
    } elseif ($accion === 'logout' && $metodo === 'POST') {
        $api->logout();
    } elseif ($accion === 'sesion' && $metodo === 'GET') {
        $api->sesion();
    } else {
        http_response_code(405);
        header('Content-Type: application/json; charset=utf-8');
        echo json_encode(['error' => 'Método no permitido'], JSON_UNESCAPED_UNICODE);
    }
} else {
    http_response_code(404);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(['error' => 'Endpoint no encontrado']);
}
