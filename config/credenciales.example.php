<?php

/**
 * PLANTILLA de credenciales de la base de datos.
 *
 * 1. Copia este archivo en esta misma carpeta con el nombre  credenciales.php
 * 2. Cambia los valores por los de tu MySQL.
 *
 * credenciales.php está en .gitignore: cada quien tiene el suyo y nunca se sube a Git.
 * Este archivo de ejemplo sí se sube, pero NUNCA debe tener una contraseña real.
 */

    define('DB_HOST', '127.0.0.1');
    define('DB_NAME', 'los_hilos_maya');
    define('DB_USER', 'root');
    define('DB_PASS', '');   // En XAMPP recién instalado, root no tiene contraseña
