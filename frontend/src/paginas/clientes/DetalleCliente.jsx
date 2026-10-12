import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import BadgeEstado from '../../components/BadgeEstado'
import Encabezado from '../../components/Encabezado'
import PaginaConDatos from '../../components/PaginaConDatos'
import { codigoPago, formatearFecha, formatearPrecio } from '../../formato'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

function DetalleCliente() {
  const { id } = useParams()

  return (
    <PaginaConDatos ruta={`clientes&id=${id}`} titulo="CLIENTE / DETALLES" textoNoEncontrado="El cliente que buscas no existe o fue eliminado.">
      {(cliente) => <ContenidoCliente cliente={cliente} />}
    </PaginaConDatos>
  )
}

function ContenidoCliente({ cliente }) {
  const navigate = useNavigate()
  const flash = useMensajeFlash()
  const [mensaje, setMensaje] = useState(null)

  async function eliminar() {
    if (!window.confirm('¿Seguro que quieres eliminar este cliente?')) return

    const respuesta = await pedirApi(`clientes&id=${cliente.id_cliente}`, { metodo: 'DELETE' })

    if (respuesta.ok) {
      navigate('/clientes', { state: { mensaje: { tipo: 'success', texto: `Cliente "${cliente.nombre_c}" eliminado.` } } })
    } else {
      // 409: tiene pedidos registrados
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo="CLIENTE / DETALLES" />

      <section className="users-content">
        <Alerta mensaje={flash} />
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <div className="detail-header">
            <h3 className="form-card__title">Información del Cliente</h3>
            <Link to={`/clientes/${cliente.id_cliente}/editar`} aria-label="Editar cliente">
              <i className="bi bi-pencil-square detail-header__edit-icon"></i>
            </Link>
          </div>

          <div className="profile-data profile-data--left">
            <p className="profile-data__row">Correo: {cliente.email_c}</p>
            <p className="profile-data__row">Nombre: {cliente.nombre_c}</p>
            <p className="profile-data__row">Telefono: {cliente.telefono_c}</p>
            <p className="profile-data__row">Fecha registro: {formatearFecha(cliente.fecha_registro)}</p>
            <p className="profile-data__row">Registrado por: {cliente.registrado_por}</p>
          </div>

          <button type="button" className="btn-primary-custom btn-primary-custom--secondary" onClick={eliminar}>
            <i className="bi bi-trash"></i> Eliminar cliente
          </button>
        </div>

        <div className="form-card">
          <h3 className="form-card__title">Historial de Pedidos</h3>
          {cliente.pedidos.length === 0 && <p className="profile-data__row">Este cliente todavía no tiene pedidos.</p>}
          {cliente.pedidos.map((pedido) => (
            <div className="order-row" key={pedido.id_pedido}>
              <div className="order-row__info">
                <span className="order-row__id">#{pedido.id_pedido} · {formatearFecha(pedido.fecha_registro)}</span>
              </div>
              <BadgeEstado estado={pedido.estado} />
              <span className="order-row__code">${formatearPrecio(pedido.total)}</span>
              <div className="order-row__actions">
                <Link to={`/pedidos/${pedido.id_pedido}`} aria-label={`Ver pedido ${pedido.id_pedido}`}>
                  <i className="bi bi-eye-fill order-row__icon"></i>
                </Link>
              </div>
            </div>
          ))}
        </div>

        <div className="form-card">
          <h3 className="form-card__title">Historial de Pagos</h3>
          {cliente.pagos.length === 0 && <p className="profile-data__row">Este cliente todavía no tiene pagos.</p>}
          {cliente.pagos.map((pago) => (
            <div className="order-row" key={pago.id_pago}>
              <div className="order-row__info">
                <span className="order-row__id">{codigoPago(pago.id_pago)} · Pedido #{pago.id_pedido_p}</span>
              </div>
              <span className="order-row__code">{pago.metodo} · ${formatearPrecio(pago.monto)}</span>
              <div className="order-row__actions">
                <Link to={`/pagos/${pago.id_pago}`} aria-label={`Ver pago ${codigoPago(pago.id_pago)}`}>
                  <i className="bi bi-eye-fill order-row__icon"></i>
                </Link>
              </div>
            </div>
          ))}
        </div>
      </section>

      <div className="form-actions form-actions--center">
        <Link to="/clientes" className="btn-primary-custom btn-primary-custom--secondary">Volver</Link>
      </div>
    </>
  )
}

export default DetalleCliente
