<?php
/**
 * Conexión centralizada a la base de datos (PDO).
 *
 * Este archivo lee las credenciales desde config/credenciales.php
 * (que NO está en el repositorio, cada quien tiene el suyo)
 * y devuelve un objeto PDO listo para usar en los Modelos.
 */
    require_once __DIR__ . '/credenciales.php';  /** Con esta linea ya se tiene acceso a las 4 constantes*/

    // Si la conexión falla, PDO lanza una PDOException. No se atrapa aquí:
    // la recibe el manejador de errores de public/index.php, que la registra en el log
    // y responde un mensaje genérico (sin mostrar detalles de la conexión al usuario)
    $dsn = 'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=utf8mb4';

    $conexion = new PDO ($dsn, DB_USER, DB_PASS, [
        PDO::ATTR_ERRMODE => PDO:: ERRMODE_EXCEPTION,       /**array asociativo de opciones de configuración. */
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    ]);
