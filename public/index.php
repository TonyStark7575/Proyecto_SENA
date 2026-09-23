<?php
require_once __DIR__ . '/../config/conexion.php';

$ruta = $_GET['ruta'] ?? 'inicio';

$partes = explode('/', $ruta);
$recurso = $partes[0];
$accion = $partes[1] ?? 'index';

switch ($recurso) {
    case 'productos':
        require_once __DIR__ . '/../controllers/ProductoController.php';
        $controlador = new ProductoController($conexion);
        
        if ($accion === 'lista') {
            $controlador->lista();
        } else {
            echo "Acción no reconocida para productos: " . $accion;
        }
        break;

    case 'inicio':
        echo "Controlador elegido: DashboardController, método: " . $accion;
        break;

    default:
        echo "Ruta no reconocida: " . $recurso;
        break;
}