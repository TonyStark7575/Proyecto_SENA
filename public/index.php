<?php

// ===== RUTA =====
// Se calcula primero porque el manejador de errores necesita saber si la petición es de la API
$ruta = $_GET['ruta'] ?? 'inicio';

$partes = explode('/', $ruta);
$recurso = $partes[0];
$accion = $partes[1] ?? 'index';
$esApi = ($recurso === 'api');

// ===== MANEJO DE ERRORES =====
// Toda excepción no atrapada termina aquí, incluida la de conexión a la BD.
// El detalle real va al log de Apache; al usuario solo le llega un mensaje genérico.
set_exception_handler(function ($e) use ($esApi) {
    // Mensaje, archivo y línea, sin la traza: la traza puede incluir argumentos como la contraseña de la BD
    error_log(get_class($e) . ': ' . $e->getMessage() . ' en ' . $e->getFile() . ':' . $e->getLine());

    // Descarta lo que se hubiera alcanzado a generar (por ejemplo, media vista HTML)
    while (ob_get_level() > 0) {
        ob_end_clean();
    }

    http_response_code(500);

    if ($esApi) {
        header('Content-Type: application/json; charset=utf-8');
        echo json_encode(['error' => 'Error interno del servidor']);
    } else {
        header('Content-Type: text/html; charset=utf-8');
        echo '<!doctype html><html lang="es"><meta charset="utf-8"><title>Error</title>'
            . '<p>Ocurrió un error inesperado. Intenta de nuevo en unos minutos.</p></html>';
    }
});

// ===== CONTENT-TYPE DE LA API =====
// Va antes de la sesión y de la guardia para cubrir también api/login (ruta pública).
// Un formulario HTML de otra página no puede enviar application/json, así que esto frena el CSRF.
// Sin cuerpo (por ejemplo api/logout) no se exige.
// Excepción: las rutas que reciben archivos aceptan multipart/form-data, pero solo con la cabecera
// X-Requested-With, que un formulario de otra página tampoco puede enviar.
$rutasConArchivos = ['api/productos/imagen'];

if ($esApi && in_array($_SERVER['REQUEST_METHOD'], ['POST', 'PUT'])) {
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
$rutasPublicas = ['login'];
// De la API solo es pública la ruta completa api/login; el resto de api exige sesión
$rutasApiPublicas = ['api/login'];
$esRutaPublica = in_array($recurso, $rutasPublicas) || in_array($ruta, $rutasApiPublicas);
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

    // Peticiones a la API: responder JSON, nunca redirigir
    if ($recurso === 'api') {
        http_response_code(401);
        header('Content-Type: application/json; charset=utf-8');
        echo json_encode(['error' => 'No autenticado']);
        exit;
    }

    // Pantallas HTML: redirigir al login, como hasta ahora
    header('Location: /ProyectoSENA/public/index.php?ruta=login');
    exit;
}

if ($haySesion && $recurso === 'login') {
    header('Location: /ProyectoSENA/public/index.php?ruta=inicio');
    exit;
}

switch ($recurso) {

    case 'login':
        require_once __DIR__ . '/../controllers/LoginController.php';
        $controlador = new LoginController($conexion);
        $controlador->index();
        break;

    case 'logout':
        require_once __DIR__ . '/../controllers/LoginController.php';
        $controlador = new LoginController($conexion);
        $controlador->logout();
        break;

    case 'productos':
        require_once __DIR__ . '/../controllers/ProductoController.php';
        $controlador = new ProductoController($conexion);

        if ($accion === 'lista') {
            $controlador->lista();
        } elseif ($accion === 'crear') {
            $controlador->crear();
        } elseif ($accion === 'detalle') {
            $controlador->detalle();
        } elseif ($accion === 'editar') {
            $controlador->editar();
        } elseif ($accion === 'eliminar') {
            $controlador->eliminar();
        } else {
            echo "Acción no reconocida para productos: " . $accion;
        }
        break;

    case 'usuarios':
        require_once __DIR__ . '/../controllers/UsuarioController.php';
        $controlador = new UsuarioController($conexion);

        if ($accion === 'lista') {
            $controlador->lista();
        } elseif ($accion === 'crear') {
            $controlador->crear();
        } elseif ($accion === 'detalle') {
            $controlador->detalle();
        } elseif ($accion === 'editar') {
            $controlador->editar();
        } elseif ($accion === 'eliminar') {
            $controlador->eliminar();
        } elseif ($accion === 'activar') {
            $controlador->activar();
        } else {
            echo "Acción no reconocida para usuarios: " . $accion;
        }
        break;

    case 'clientes':
        require_once __DIR__ . '/../controllers/ClienteController.php';
        $controlador = new ClienteController($conexion);

        if ($accion === 'lista') {
            $controlador->lista();
        } elseif ($accion === 'crear') {
            $controlador->crear();
        } elseif ($accion === 'detalle') {
            $controlador->detalle();
        } elseif ($accion === 'editar') {
            $controlador->editar();
        } elseif ($accion === 'eliminar') {
            $controlador->eliminar();
        } else {
            echo "Acción no reconocida para clientes: " . $accion;
        }
        break;

    case 'pedidos':
        require_once __DIR__ . '/../controllers/PedidoController.php';
        $controlador = new PedidoController($conexion);

        if ($accion === 'lista') {
            $controlador->lista();
        } elseif ($accion === 'crear') {
            $controlador->crear();
        } elseif ($accion === 'detalle') {
            $controlador->detalle();
        } elseif ($accion === 'editar') {
            $controlador->editar();
        } elseif ($accion === 'eliminar') {
            $controlador->eliminar();
        } elseif ($accion === 'agregarProducto') {
            $controlador->agregarProducto();
        } elseif ($accion === 'eliminarLinea') {
            $controlador->eliminarLinea();
        } else {
            echo "Acción no reconocida para pedidos: " . $accion;
        }
        break;

    case 'pagos':
        require_once __DIR__ . '/../controllers/PagoController.php';
        $controlador = new PagoController($conexion);

        if ($accion === 'lista') {
            $controlador->lista();
        } elseif ($accion === 'crear') {
            $controlador->crear();
        } elseif ($accion === 'detalle') {
            $controlador->detalle();
        } elseif ($accion === 'editar') {
            $controlador->editar();
        } elseif ($accion === 'eliminar') {
            $controlador->eliminar();
        } else {
            echo "Acción no reconocida para pagos: " . $accion;
        }
        break;

            case 'api':
        // Recurso de la API => controlador que lo atiende. Cada controlador decide, según la
        // sub-ruta (api/productos/imagen) y el método HTTP, cuál de sus métodos ejecutar
        $recursosApi = [
            'productos' => 'ProductoApiController',
            'clientes'  => 'ClienteApiController',
            'pedidos'   => 'PedidoApiController',
            'pagos'     => 'PagoApiController',
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
        break;

    case 'inicio':
        require_once __DIR__ . '/../controllers/DashboardController.php';
        $controlador = new DashboardController($conexion);
        $controlador->index();
        break;

    default:
        echo "Ruta no reconocida: " . $recurso;
        break;
}
