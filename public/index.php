<?php
require_once __DIR__ . '/../config/conexion.php';

$ruta = $_GET['ruta'] ?? 'inicio';

$partes = explode('/', $ruta);
$recurso = $partes[0];
$accion = $partes[1] ?? 'index';

switch ($recurso) {

case 'login':
    require __DIR__ . '/../views/login.php';
    break;
    
    case 'productos':
        require_once __DIR__ . '/../controllers/ProductoController.php';
        $controlador = new ProductoController($conexion);
        
        if ($accion === 'lista') {
            $controlador->lista();
        } 
        elseif ($accion === 'crear') {
            $controlador->crear();
        }
        elseif ($accion === 'detalle') {
            $controlador->detalle();
        } 
        elseif ($accion === 'editar') {
            $controlador->editar();
        }
        elseif ($accion === 'eliminar') {
            $controlador->eliminar();
        }
        else {
            echo "Acción no reconocida para productos: " . $accion;
        }
        break;

    case 'usuarios':
        require_once __DIR__ . '/../controllers/UsuarioController.php';
        $controlador = new UsuarioController($conexion);

        if ($accion === 'lista') {
            $controlador->lista();
        }
        elseif ($accion === 'crear') {
            $controlador->crear();
        }
        elseif ($accion === 'detalle') {
            $controlador->detalle();
        }
        elseif ($accion === 'editar') {
            $controlador->editar();
        }
        elseif ($accion === 'eliminar') {
            $controlador->eliminar();
        }
        else {
            echo "Acción no reconocida para usuarios: " . $accion;
        }
        break;

    case 'clientes':
        require_once __DIR__ . '/../controllers/ClienteController.php';
        $controlador = new ClienteController($conexion);

        if ($accion === 'lista') {
            $controlador->lista();
        }
        elseif ($accion === 'crear') {
            $controlador->crear();
        }
        elseif ($accion === 'detalle') {
            $controlador->detalle();
        }
        elseif ($accion === 'editar') {
            $controlador->editar();
        }
        elseif ($accion === 'eliminar') {
            $controlador->eliminar();
        }
        else {
            echo "Acción no reconocida para clientes: " . $accion;
        }
        break;

    case 'pedidos':
    require_once __DIR__ . '/../controllers/PedidoController.php';
    $controlador = new PedidoController($conexion);

    if ($accion === 'lista') {
        $controlador->lista();
    }
    elseif ($accion === 'crear') {
        $controlador->crear();
    }
    elseif ($accion === 'detalle') {
        $controlador->detalle();
    }
    elseif ($accion === 'editar') {
        $controlador->editar();
    }
    elseif ($accion === 'eliminar') {
        $controlador->eliminar();
    }
    elseif ($accion === 'agregarProducto') {
        $controlador->agregarProducto();
    }
    elseif ($accion === 'eliminarLinea') {
        $controlador->eliminarLinea();
    }
    else {
        echo "Acción no reconocida para pedidos: " . $accion;
    }
    break;

    case 'pagos':
    require_once __DIR__ . '/../controllers/PagoController.php';
    $controlador = new PagoController($conexion);

    if ($accion === 'lista') {
        $controlador->lista();
    }
    elseif ($accion === 'crear') {
        $controlador->crear();
    }
    elseif ($accion === 'detalle') {
        $controlador->detalle();
    }
    elseif ($accion === 'editar') {
        $controlador->editar();
    }
    elseif ($accion === 'eliminar') {
        $controlador->eliminar();
    }
    else {
        echo "Acción no reconocida para pagos: " . $accion;
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