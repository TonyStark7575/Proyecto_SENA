package com.loshilosdemaya.movil.vista.usuarios;

import androidx.lifecycle.ViewModelProvider;

import com.loshilosdemaya.movil.R;
import com.loshilosdemaya.movil.modelo.Usuario;
import com.loshilosdemaya.movil.vista.comun.AdaptadorFiltrable;
import com.loshilosdemaya.movil.vista.comun.ListaBaseActivity;
import com.loshilosdemaya.movil.vista.comun.ListaViewModel;

/** Lista de usuarios del sistema (GET api/usuarios). */
public class ListaUsuariosActivity extends ListaBaseActivity<Usuario> {

    @Override
    protected String getTitulo() {
        return getString(R.string.modulo_usuarios);
    }

    @Override
    protected String getTextoListaVacia() {
        return getString(R.string.sin_usuarios);
    }

    @Override
    protected String getTextoBotonNuevo() {
        return getString(R.string.nuevo_usuario);
    }

    @Override
    protected ListaViewModel<Usuario> crearViewModel() {
        return new ViewModelProvider(this).get(UsuarioViewModel.class);
    }

    @Override
    protected AdaptadorFiltrable<Usuario, ?> crearAdaptador(AdaptadorFiltrable.AlSeleccionar<Usuario> alSeleccionar) {
        return new UsuarioAdapter(alSeleccionar);
    }

    @Override
    protected void abrirDetalle(Usuario usuario) {
        startActivity(DetalleUsuarioActivity.crearIntent(this, usuario.getIdUsuario()));
    }

    @Override
    protected void abrirFormularioNuevo() {
        startActivity(FormularioUsuarioActivity.crearIntent(this, null));
    }
}
