import { useState } from 'react'
import { Link } from 'react-router'
import Alerta from '../../components/Alerta'
import BadgeEstado from '../../components/BadgeEstado'
import Buscador from '../../components/Buscador'
import Encabezado from '../../components/Encabezado'
import EstadoCarga from '../../components/EstadoCarga'
import { coincide } from '../../formato'
import { useDatosApi } from '../../hooks/useDatosApi'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

// Los tres filtros del diseño: el texto del botón y el estado que filtra
const FILTROS = [
  { estado: 'En proceso', texto: 'En proceso', clase: 'filter-pill--en-proceso' },
  { estado: 'Pendiente', texto: 'Pendientes', clase: 'filter-pill--pendiente' },
  { estado: 'Entregado', texto: 'Entregados', clase: 'filter-pill--entregado' },
]

function ListaPedidos() {
  const { datos: pedidos, cargando, error, status } = useDatosApi('pedidos')
  const flash = useMensajeFlash()
  const [busqueda, setBusqueda] = useState('')
  const [filtro, setFiltro] = useState(null) // null = todos los estados

  // Clic en el filtro activo lo quita; clic en otro lo cambia
  function elegirFiltro(estado) {
    setFiltro(filtro === estado ? null : estado)
  }

  const pedidosFiltrados = (pedidos ?? []).filter(
    (pedido) =>
      (filtro === null || pedido.estado === filtro) &&
      coincide(busqueda, `#${pedido.id_pedido}`, pedido.nombre_c, pedido.telefono_c)
  )

  let contenido
  if (cargando || error) {
    contenido = <EstadoCarga cargando={cargando} error={error} status={status} />
  } else if (pedidosFiltrados.length === 0) {
    contenido = (
      <p className="text-center text-muted">
        {busqueda || filtro ? 'No hay pedidos con ese filtro.' : 'Todavía no hay pedidos registrados.'}
      </p>
    )
  } else {
    contenido = pedidosFiltrados.map((pedido) => (
      <div className="entity-card" key={pedido.id_pedido}>
        <div className="entity-card__top">
          <p className="entity-card__detail">ID: {pedido.id_pedido}</p>
          <BadgeEstado estado={pedido.estado} />
        </div>
        <p className="entity-card__detail">Cliente: {pedido.nombre_c}</p>
        <p className="entity-card__detail">Tel: {pedido.telefono_c}</p>
        <div className="entity-card__footer">
          <div></div>
          <div className="entity-card__actions">
            <Link to={`/pedidos/${pedido.id_pedido}`} aria-label={`Ver pedido ${pedido.id_pedido}`}>
              <i className="bi bi-eye order-row__icon"></i>
            </Link>
            <Link to={`/pedidos/${pedido.id_pedido}/editar`} aria-label={`Editar pedido ${pedido.id_pedido}`}>
              <i className="bi bi-pencil-square order-row__icon"></i>
            </Link>
          </div>
        </div>
      </div>
    ))
  }

  return (
    <>
      <Encabezado titulo="PEDIDOS">
        <Buscador valor={busqueda} onCambio={setBusqueda} />
      </Encabezado>

      <section className="users-content">
        <Alerta mensaje={flash} />

        <div className={'filter-pills' + (filtro ? ' filter-pills--con-filtro' : '')}>
          {FILTROS.map((f) => (
            <button
              key={f.estado}
              type="button"
              className={`filter-pill ${f.clase}` + (filtro === f.estado ? ' filter-pill--activo' : '')}
              aria-pressed={filtro === f.estado}
              onClick={() => elegirFiltro(f.estado)}
            >
              {f.texto}
            </button>
          ))}
        </div>

        {contenido}
      </section>

      <Link to="/pedidos/nuevo" className="btn-primary-custom">Nuevo Pedido</Link>
    </>
  )
}

export default ListaPedidos
