<?php

$ruta = $_GET['ruta'] ?? 'inicio';  /** Si no hay ruta, por defecto es inicio */    
echo "La ruta solicitada es: " . $ruta;
?>


<?php
$ruta = $_GET['ruta'] ?? 'inicio';

$partes = explode('/', $ruta);
$recurso = $partes[0];
$accion = $partes[1] ?? 'index';

switch ($recurso) {
    case 'clientes':
        echo "Controlador elegido: ClienteController, método: " . $accion;
        break;

    case 'productos':
        echo "Controlador elegido: ProductoController, método: " . $accion;
        break;

    case 'inicio':
        echo "Controlador elegido: DashboardController, método: " . $accion;
        break;

    default:
        echo "Ruta no reconocida: " . $recurso;
        break;
}
 