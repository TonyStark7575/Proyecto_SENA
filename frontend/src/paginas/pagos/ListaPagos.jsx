import { useState } from 'react'
import { Link } from 'react-router'
import Alerta from '../../components/Alerta'
import BadgePago from '../../components/BadgePago'
import Buscador from '../../components/Buscador'
import Encabezado from '../../components/Encabezado'
import EstadoCarga from '../../components/EstadoCarga'
import { codigoPago, coincide, formatearPrecio } from '../../formato'
import { useDatosApi } from '../../hooks/useDatosApi'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

function ListaPagos() {
  const { datos: pagos, cargando, error, status } = useDatosApi('pagos')
  const flash = useMensajeFlash()
  const [busqueda, setBusqueda] = useState('')

  const pagosFiltrados = (pagos ?? []).filter((pago) =>
    coincide(busqueda, codigoPago(pago.id_pago), `#${pago.id_pedido}`, pago.nombre_c, pago.metodo)
  )

  let contenido
  if (cargando || error) {
    contenido = <EstadoCarga cargando={cargando} error={error} status={status} />
  } else if (pagosFiltrados.length === 0) {
    contenido = (
      <p className="text-center text-muted">
        {busqueda ? `No hay pagos que coincidan con "${busqueda}".` : 'Todavía no hay pagos registrados.'}
      </p>
    )
  } else {
    contenido = pagosFiltrados.map((pago) => (
      <div className="entity-card" key={pago.id_pago}>
        <div className="entity-card__top">
          <h3 className="entity-card__name">{codigoPago(pago.id_pago)}</h3>
          <span className="entity-card__date">Pedido #{pago.id_pedido}</span>
        </div>
        <p className="entity-card__detail">Cliente: {pago.nombre_c}</p>
        <p className="entity-card__detail">Método: {pago.metodo}</p>
        <p className="entity-card__detail">
          Monto: ${formatearPrecio(pago.monto)} &nbsp;·&nbsp; <BadgePago saldo={pago.saldo} />
        </p>
        <div className="entity-card__footer entity-card__footer--divided">
          <div></div>
          <div className="entity-card__actions">
            <Link to={`/pagos/${pago.id_pago}`} aria-label={`Ver pago ${codigoPago(pago.id_pago)}`}>
              <i className="bi bi-eye order-row__icon"></i>
            </Link>
            <Link to={`/pagos/${pago.id_pago}/editar`} aria-label={`Editar pago ${codigoPago(pago.id_pago)}`}>
              <i className="bi bi-pencil-square order-row__icon"></i>
            </Link>
          </div>
        </div>
      </div>
    ))
  }

  return (
    <>
      <Encabezado titulo="PAGOS">
        <Buscador valor={busqueda} onCambio={setBusqueda} />
      </Encabezado>

      <section className="users-content">
        <Alerta mensaje={flash} />
        {contenido}
      </section>

      <Link to="/pagos/nuevo" className="btn-primary-custom">Registrar pago</Link>
    </>
  )
}

export default ListaPagos
