package com.loshilosdemaya.movil.vista.productos;

import androidx.lifecycle.LiveData;

import com.loshilosdemaya.movil.modelo.Producto;
import com.loshilosdemaya.movil.repositorio.ProductoRepositorio;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.vista.comun.ListaViewModel;

import java.math.BigDecimal;
import java.util.List;

/** ViewModel del módulo de productos: lo usan la lista, el detalle y el formulario. */
public class ProductoViewModel extends ListaViewModel<Producto> {

    private final ProductoRepositorio productoRepositorio = new ProductoRepositorio();

    @Override
    protected LiveData<Resultado<List<Producto>>> consultarLista() {
        return productoRepositorio.consultarProductos();
    }

    public LiveData<Resultado<Producto>> consultarProducto(int idProducto) {
        return productoRepositorio.consultarProducto(idProducto);
    }

    public LiveData<Resultado<Producto>> registrarProducto(String nombre, BigDecimal precio, String descripcion) {
        return productoRepositorio.registrarProducto(new Producto(nombre, precio, descripcion));
    }

    public LiveData<Resultado<Producto>> actualizarProducto(int idProducto, String nombre, BigDecimal precio, String descripcion) {
        return productoRepositorio.actualizarProducto(idProducto, new Producto(nombre, precio, descripcion));
    }

    public LiveData<Resultado<Void>> eliminarProducto(int idProducto) {
        return productoRepositorio.eliminarProducto(idProducto);
    }

    public LiveData<Resultado<Producto>> subirImagen(int idProducto, byte[] contenido, String tipoMime) {
        return productoRepositorio.subirImagen(idProducto, contenido, tipoMime, "producto.jpg");
    }

    public LiveData<Resultado<Producto>> quitarImagen(int idProducto) {
        return productoRepositorio.quitarImagen(idProducto);
    }
}
