package com.loshilosdemaya.movil.vista.usuarios;

import androidx.lifecycle.LiveData;

import com.loshilosdemaya.movil.modelo.ResultadoEliminacion;
import com.loshilosdemaya.movil.modelo.Usuario;
import com.loshilosdemaya.movil.repositorio.Resultado;
import com.loshilosdemaya.movil.repositorio.UsuarioRepositorio;
import com.loshilosdemaya.movil.vista.comun.ListaViewModel;

import java.util.List;

/** ViewModel del módulo de usuarios (personas que usan el sistema). */
public class UsuarioViewModel extends ListaViewModel<Usuario> {

    private final UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();

    @Override
    protected LiveData<Resultado<List<Usuario>>> consultarLista() {
        return usuarioRepositorio.consultarUsuarios();
    }

    public LiveData<Resultado<Usuario>> consultarUsuario(int idUsuario) {
        return usuarioRepositorio.consultarUsuario(idUsuario);
    }

    public LiveData<Resultado<Usuario>> registrarUsuario(String nombre, String correo, String telefono, String password, String rol) {
        return usuarioRepositorio.registrarUsuario(new Usuario(nombre, correo, telefono, password, rol));
    }

    // password vacío = conservar la contraseña actual
    public LiveData<Resultado<Usuario>> actualizarUsuario(int idUsuario, String nombre, String correo, String telefono, String password, String rol) {
        return usuarioRepositorio.actualizarUsuario(idUsuario, new Usuario(nombre, correo, telefono, password, rol));
    }

    public LiveData<Resultado<ResultadoEliminacion>> eliminarUsuario(int idUsuario) {
        return usuarioRepositorio.eliminarUsuario(idUsuario);
    }

    public LiveData<Resultado<Usuario>> cambiarActivo(int idUsuario, boolean activo) {
        return usuarioRepositorio.cambiarActivo(idUsuario, activo);
    }
}
