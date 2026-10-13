package com.loshilosdemaya.movil.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Prepara la foto elegida para subirla: la API acepta máximo 2 MB y una foto de celular
 * suele pesar más. Se reduce a 1280 px por el lado más largo y se guarda como JPG,
 * lo que normalmente deja la imagen por debajo de 500 KB sin que se note la diferencia.
 */
public final class CompresorImagen {

    public static final String TIPO_MIME = "image/jpeg";
    private static final int LADO_MAXIMO = 1280;
    private static final int CALIDAD_JPG = 85;

    private CompresorImagen() {
    }

    public static byte[] comprimir(ContentResolver contenido, Uri imagen) throws IOException {
        Bitmap original = decodificar(contenido, imagen);
        if (original == null) {
            throw new IOException("No se pudo leer la imagen");
        }

        Bitmap reducida = reducir(original);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        reducida.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPG, salida);

        if (reducida != original) {
            reducida.recycle();
        }
        original.recycle();
        return salida.toByteArray();
    }

    // Desde Android 9, ImageDecoder también corrige la rotación de las fotos tomadas con la cámara
    private static Bitmap decodificar(ContentResolver contenido, Uri imagen) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.Source fuente = ImageDecoder.createSource(contenido, imagen);
            return ImageDecoder.decodeBitmap(fuente, (decodificador, info, origen) -> {
                decodificador.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
                int ladoMayor = Math.max(info.getSize().getWidth(), info.getSize().getHeight());
                if (ladoMayor > LADO_MAXIMO) {
                    float escala = (float) LADO_MAXIMO / ladoMayor;
                    decodificador.setTargetSize(
                            Math.round(info.getSize().getWidth() * escala),
                            Math.round(info.getSize().getHeight() * escala));
                }
            });
        }

        try (InputStream entrada = contenido.openInputStream(imagen)) {
            return BitmapFactory.decodeStream(entrada);
        }
    }

    private static Bitmap reducir(Bitmap original) {
        int ladoMayor = Math.max(original.getWidth(), original.getHeight());
        if (ladoMayor <= LADO_MAXIMO) {
            return original;
        }
        float escala = (float) LADO_MAXIMO / ladoMayor;
        return Bitmap.createScaledBitmap(original,
                Math.round(original.getWidth() * escala),
                Math.round(original.getHeight() * escala),
                true);
    }
}
