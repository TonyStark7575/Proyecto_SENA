package com.loshilosdemaya.movil.repositorio;

import androidx.lifecycle.LiveData;

import com.loshilosdemaya.movil.modelo.Producto;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/** Operaciones del módulo de productos contra la API. */
public class ProductoRepositorio extends RepositorioBase {

    public LiveData<Resultado<List<Producto>>> consultarProductos() {
        return ejecutar(api().consultarProductos());
    }

    public LiveData<Resultado<Producto>> consultarProducto(int idProducto) {
        return ejecutar(api().consultarProducto(idProducto));
    }

    public LiveData<Resultado<Producto>> registrarProducto(Producto producto) {
        return ejecutar(api().registrarProducto(producto));
    }

    public LiveData<Resultado<Producto>> actualizarProducto(int idProducto, Producto producto) {
        return ejecutar(api().actualizarProducto(idProducto, producto));
    }

    public LiveData<Resultado<Void>> eliminarProducto(int idProducto) {
        return ejecutar(api().eliminarProducto(idProducto));
    }

    /**
     * Sube la imagen como multipart/form-data en el campo "imagen" (el mismo que espera
     * ImagenProducto.php). La API valida el tipo real del archivo y el tamaño.
     */
    public LiveData<Resultado<Producto>> subirImagen(int idProducto, byte[] contenido, String tipoMime, String nombreArchivo) {
        RequestBody cuerpo = RequestBody.create(contenido, MediaType.parse(tipoMime));
        MultipartBody.Part parte = MultipartBody.Part.createFormData("imagen", nombreArchivo, cuerpo);
        return ejecutar(api().subirImagenProducto(idProducto, parte));
    }

    public LiveData<Resultado<Producto>> quitarImagen(int idProducto) {
        return ejecutar(api().quitarImagenProducto(idProducto));
    }
}
