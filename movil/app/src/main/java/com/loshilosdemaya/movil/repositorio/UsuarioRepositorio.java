package com.loshilosdemaya.movil.repositorio;

import androidx.lifecycle.LiveData;

import com.loshilosdemaya.movil.modelo.ResultadoEliminacion;
import com.loshilosdemaya.movil.modelo.Usuario;

import java.util.Collections;
import java.util.List;

/** Operaciones del módulo de usuarios contra la API. */
public class UsuarioRepositorio extends RepositorioBase {

    public LiveData<Resultado<List<Usuario>>> consultarUsuarios() {
        return ejecutar(api().consultarUsuarios());
    }

    public LiveData<Resultado<Usuario>> consultarUsuario(int idUsuario) {
        return ejecutar(api().consultarUsuario(idUsuario));
    }

    public LiveData<Resultado<Usuario>> registrarUsuario(Usuario usuario) {
        return ejecutar(api().registrarUsuario(usuario));
    }

    public LiveData<Resultado<Usuario>> actualizarUsuario(int idUsuario, Usuario usuario) {
        return ejecutar(api().actualizarUsuario(idUsuario, usuario));
    }

    // Si tiene registros, la API lo desactiva en vez de borrarlo (ver ResultadoEliminacion)
    public LiveData<Resultado<ResultadoEliminacion>> eliminarUsuario(int idUsuario) {
        return ejecutar(api().eliminarUsuario(idUsuario));
    }

    public LiveData<Resultado<Usuario>> cambiarActivo(int idUsuario, boolean activo) {
        return ejecutar(api().cambiarActivoUsuario(idUsuario, Collections.singletonMap("activo", activo)));
    }
}
