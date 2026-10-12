import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import BadgePago from '../../components/BadgePago'
import Encabezado from '../../components/Encabezado'
import PaginaConDatos from '../../components/PaginaConDatos'
import { codigoPago, formatearFecha, formatearPrecio } from '../../formato'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

function DetallePago() {
  const { id } = useParams()

  return (
    <PaginaConDatos ruta={`pagos&id=${id}`} titulo={`PAGOS/ DETALLE #${codigoPago(id)}`} textoNoEncontrado="El pago que buscas no existe o fue eliminado.">
      {(pago) => <ContenidoPago pago={pago} />}
    </PaginaConDatos>
  )
}

function ContenidoPago({ pago }) {
  const navigate = useNavigate()
  const flash = useMensajeFlash()
  const [mensaje, setMensaje] = useState(null)
  const codigo = codigoPago(pago.id_pago)

  async function eliminar() {
    if (!window.confirm('¿Eliminar este pago? El saldo del pedido se recalculará.')) return

    const respuesta = await pedirApi(`pagos&id=${pago.id_pago}`, { metodo: 'DELETE' })

    if (respuesta.ok) {
      navigate('/pagos', { state: { mensaje: { tipo: 'success', texto: `Pago #${codigo} eliminado. El saldo del pedido #${pago.id_pedido} se recalculó.` } } })
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo={`PAGOS/ DETALLE #${codigo}`} />

      <section className="users-content">
        <Alerta mensaje={flash} />
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <h3 className="form-card__title">Detalle del Pago #{codigo}</h3>

          <div className="profile-data profile-data--left">
            <p className="profile-data__row">ID de pago: #{codigo}</p>
            <p className="profile-data__row">Pedido asociado: #{pago.id_pedido}</p>
          </div>

          <Link to={`/pedidos/${pago.id_pedido}`} className="btn-primary-custom btn-primary-custom--secondary btn-primary-custom--sm">
            <i className="bi bi-eye"></i> Ver pedido completo
          </Link>

          <div className="profile-data profile-data--left" style={{ marginTop: 16 }}>
            <p className="profile-data__row">Cliente: {pago.nombre_c}</p>
            <p className="profile-data__row">Monto: ${formatearPrecio(pago.monto)}</p>
            <p className="profile-data__row">Método: {pago.metodo}</p>
            <p className="profile-data__row">Fecha: {formatearFecha(pago.fecha_p)}</p>
            <p className="profile-data__row">
              Estado del pedido: <BadgePago saldo={pago.saldo} />
            </p>
          </div>

          <div className="form-actions form-actions--end">
            <Link to={`/pagos/${pago.id_pago}/editar`} className="btn-primary-custom">Editar</Link>
            <button type="button" className="btn-primary-custom btn-primary-custom--secondary" onClick={eliminar}>Eliminar</button>
            <Link to="/pagos" className="btn-primary-custom btn-primary-custom--secondary">Volver</Link>
          </div>
        </div>
      </section>
    </>
  )
}

export default DetallePago
