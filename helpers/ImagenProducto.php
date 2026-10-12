<?php

/**
 * Guardado y borrado de imágenes de productos en public/img/.
 * La usa la API de productos (subir, reemplazar y quitar la imagen).
 */
class ImagenProducto {

    // Tipo real del archivo => extensión con la que se guarda
    const TIPOS_PERMITIDOS = [
        'image/jpeg' => 'jpg',
        'image/png'  => 'png',
        'image/webp' => 'webp',
        'image/gif'  => 'gif',
    ];

    const TAMANO_MAXIMO = 2 * 1024 * 1024; // 2 MB

    const CARPETA = __DIR__ . '/../public/img/';

    /**
     * Valida y guarda un archivo de $_FILES.
     * Devuelve ['nombre' => 'archivo.jpg', 'error' => null] o ['nombre' => null, 'error' => 'mensaje'].
     */
    public static function guardar($archivo) {
        $error = $archivo['error'] ?? UPLOAD_ERR_NO_FILE;

        if ($error === UPLOAD_ERR_INI_SIZE || $error === UPLOAD_ERR_FORM_SIZE
            || ($error === UPLOAD_ERR_OK && $archivo['size'] > self::TAMANO_MAXIMO)) {
            return ['nombre' => null, 'error' => 'La imagen no puede pesar más de 2 MB.'];
        }

        if ($error !== UPLOAD_ERR_OK) {
            return ['nombre' => null, 'error' => 'No se recibió la imagen. Intenta de nuevo.'];
        }

        // El tipo se averigua leyendo el contenido del archivo, no su nombre ni lo que diga el navegador:
        // así un "foto.php" o un "foto.jpg" que en realidad es código nunca se acepta
        $tipo = (new finfo(FILEINFO_MIME_TYPE))->file($archivo['tmp_name']);

        if (!isset(self::TIPOS_PERMITIDOS[$tipo]) || getimagesize($archivo['tmp_name']) === false) {
            return ['nombre' => null, 'error' => 'La imagen debe ser JPG, PNG, WEBP o GIF.'];
        }

        // El nombre lo pone el servidor (nunca el del usuario): timestamp + parte aleatoria + extensión segura
        $nombre = time() . '_' . bin2hex(random_bytes(4)) . '.' . self::TIPOS_PERMITIDOS[$tipo];

        if (!move_uploaded_file($archivo['tmp_name'], self::CARPETA . $nombre)) {
            return ['nombre' => null, 'error' => 'No se pudo guardar la imagen.'];
        }

        return ['nombre' => $nombre, 'error' => null];
    }

    // Solo borra imágenes subidas (prefijo timestamp_), nunca las fijas del sistema como LOGOTIPO.png
    public static function borrar($nombreArchivo) {
        if (!$nombreArchivo || !preg_match('/^\d+_/', $nombreArchivo)) {
            return;
        }

        $ruta = self::CARPETA . basename($nombreArchivo);
        if (is_file($ruta)) {
            unlink($ruta);
        }
    }

}
