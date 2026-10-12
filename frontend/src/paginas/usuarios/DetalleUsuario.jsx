import { useState } from 'react'
import { Link, useNavigate, useOutletContext, useParams } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import Encabezado from '../../components/Encabezado'
import PaginaConDatos from '../../components/PaginaConDatos'
import { formatearFecha } from '../../formato'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

function DetalleUsuario() {
  const { id } = useParams()

  return (
    <PaginaConDatos ruta={`usuarios&id=${id}`} titulo="USUARIOS / DETALLES" textoNoEncontrado="El usuario que buscas no existe o fue eliminado.">
      {(usuario) => <PerfilUsuario key={usuario.id_usuario} usuarioInicial={usuario} />}
    </PaginaConDatos>
  )
}

function PerfilUsuario({ usuarioInicial }) {
  const navigate = useNavigate()
  const flash = useMensajeFlash()
  const { usuario: sesion } = useOutletContext()

  // Al desactivar o reactivar, la API devuelve el usuario actualizado y se guarda aquí
  const [usuario, setUsuario] = useState(usuarioInicial)
  const [mensaje, setMensaje] = useState(null)

  const esYo = Number(usuario.id_usuario) === Number(sesion.id_usuario)
  const tieneRegistros = usuario.registros.clientes > 0 || usuario.registros.pedidos > 0

  async function eliminar() {
    const aviso = tieneRegistros
      ? 'Este usuario tiene clientes o pedidos registrados: no se puede eliminar, se desactivará. ¿Continuar?'
      : '¿Seguro que quieres eliminar este usuario?'
    if (!window.confirm(aviso)) return

    const respuesta = await pedirApi(`usuarios&id=${usuario.id_usuario}`, { metodo: 'DELETE' })

    if (respuesta.status === 204) {
      navigate('/usuarios', { state: { mensaje: { tipo: 'success', texto: `Usuario "${usuario.nombre_u}" eliminado.` } } })
    } else if (respuesta.ok) {
      // 200 con resultado "desactivado": tenía registros
      setUsuario(respuesta.datos.usuario)
      setMensaje({ tipo: 'warning', texto: respuesta.datos.mensaje })
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  async function reactivar() {
    const respuesta = await pedirApi(`usuarios/activo&id=${usuario.id_usuario}`, { metodo: 'PUT', datos: { activo: true } })

    if (respuesta.ok) {
      setUsuario(respuesta.datos)
      setMensaje({ tipo: 'success', texto: `Usuario "${usuario.nombre_u}" reactivado.` })
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo="USUARIOS / DETALLES" />

      <section className="users-content">
        <Alerta mensaje={flash} />
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <div className="detail-header text-center">
            <h3 className="form-card__title">Perfil del Usuario</h3>
          </div>

          <div className="profile-avatar">
            <i className="bi bi-person-fill"></i>
          </div>

          <div className="profile-data">
            <p className="profile-data__row"><strong>Nombre:</strong> {usuario.nombre_u}</p>
            <p className="profile-data__row"><strong>Correo:</strong> {usuario.email_u}</p>
            <p className="profile-data__row"><strong>Teléfono:</strong> {usuario.telefono_u}</p>
            <p className="profile-data__row"><strong>Rol:</strong> {usuario.rol === 'ADMINISTRADORA' ? 'Administradora' : 'Repartidor'}</p>
            <p className="profile-data__row"><strong>Fecha de registro:</strong> {formatearFecha(usuario.fecha_registro)}</p>
            <p className="profile-data__row"><strong>Estado:</strong> {usuario.activo ? 'Activo' : 'Inactivo (no puede iniciar sesión)'}</p>
            <p className="profile-data__row">
              <strong>Registros:</strong> {usuario.registros.clientes} cliente(s) y {usuario.registros.pedidos} pedido(s)
            </p>
          </div>

          <div className="detail-actions-row">
            <Link to={`/usuarios/${usuario.id_usuario}/editar`} className="btn-primary-custom" aria-label="Editar usuario">
              <i className="bi bi-pencil-square"></i>
            </Link>

            {/* Igual que en PHP: inactivo → reactivar; activo y no soy yo → eliminar */}
            {!usuario.activo && (
              <button type="button" className="btn-primary-custom" title="Reactivar usuario" aria-label="Reactivar usuario" onClick={reactivar}>
                <i className="bi bi-person-check"></i>
              </button>
            )}
            {usuario.activo && !esYo && (
              <button type="button" className="btn-primary-custom" title="Eliminar usuario" aria-label="Eliminar usuario" onClick={eliminar}>
                <i className="bi bi-trash"></i>
              </button>
            )}
          </div>
        </div>

        <div className="form-actions form-actions--center">
          <Link to="/usuarios" className="btn-primary-custom btn-primary-custom--secondary">Volver</Link>
        </div>
      </section>
    </>
  )
}

export default DetalleUsuario
