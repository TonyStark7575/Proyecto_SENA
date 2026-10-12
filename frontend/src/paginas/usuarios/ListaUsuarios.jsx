import { useState } from 'react'
import { Link } from 'react-router'
import Alerta from '../../components/Alerta'
import Buscador from '../../components/Buscador'
import Encabezado from '../../components/Encabezado'
import EstadoCarga from '../../components/EstadoCarga'
import { coincide, formatearFecha } from '../../formato'
import { useDatosApi } from '../../hooks/useDatosApi'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

function ListaUsuarios() {
  const { datos: usuarios, cargando, error, status } = useDatosApi('usuarios')
  const flash = useMensajeFlash()
  const [busqueda, setBusqueda] = useState('')

  const usuariosFiltrados = (usuarios ?? []).filter((usuario) =>
    coincide(busqueda, usuario.nombre_u, usuario.email_u, usuario.telefono_u)
  )

  let contenido
  if (cargando || error) {
    contenido = <EstadoCarga cargando={cargando} error={error} status={status} />
  } else if (usuariosFiltrados.length === 0) {
    contenido = <p className="text-center text-muted">No hay usuarios que coincidan con "{busqueda}".</p>
  } else {
    contenido = usuariosFiltrados.map((usuario) => (
      <div className={'entity-card' + (usuario.activo ? '' : ' entity-card--inactivo')} key={usuario.id_usuario}>
        <div className="entity-card__top">
          <div>
            <h3 className="entity-card__name">{usuario.nombre_u}</h3>
            <a href={`mailto:${usuario.email_u}`} className="entity-card__email">{usuario.email_u}</a>
          </div>
          <span className="entity-card__date">{formatearFecha(usuario.fecha_registro)}</span>
        </div>
        <div className="entity-card__footer">
          {usuario.rol === 'ADMINISTRADORA'
            ? <span className="badge-status badge-status--admin">Administradora</span>
            : <span className="badge-status badge-status--repartidor">Repartidor</span>}
          {!usuario.activo && <span className="badge-status badge-status--inactivo">Inactivo</span>}
          <div className="entity-card__actions">
            <Link to={`/usuarios/${usuario.id_usuario}`} aria-label={`Ver ${usuario.nombre_u}`}>
              <i className="bi bi-eye order-row__icon"></i>
            </Link>
            <Link to={`/usuarios/${usuario.id_usuario}/editar`} aria-label={`Editar ${usuario.nombre_u}`}>
              <i className="bi bi-pencil-square order-row__icon"></i>
            </Link>
          </div>
        </div>
      </div>
    ))
  }

  return (
    <>
      <Encabezado titulo="USUARIOS">
        <Buscador valor={busqueda} onCambio={setBusqueda} />
      </Encabezado>

      <section className="users-content users-content--lista">
        <Alerta mensaje={flash} />
        {contenido}
      </section>

      <Link to="/usuarios/nuevo" className="btn-primary-custom">Nuevo Usuario</Link>
    </>
  )
}

export default ListaUsuarios
