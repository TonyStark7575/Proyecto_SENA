import { useState } from 'react'
import { Link } from 'react-router'
import Alerta from '../../components/Alerta'
import Buscador from '../../components/Buscador'
import Encabezado from '../../components/Encabezado'
import EstadoCarga from '../../components/EstadoCarga'
import { coincide, formatearFecha } from '../../formato'
import { useDatosApi } from '../../hooks/useDatosApi'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

function ListaClientes() {
  const { datos: clientes, cargando, error, status } = useDatosApi('clientes')
  const flash = useMensajeFlash()
  const [busqueda, setBusqueda] = useState('')

  const clientesFiltrados = (clientes ?? []).filter((cliente) =>
    coincide(busqueda, cliente.nombre_c, cliente.email_c, cliente.telefono_c)
  )

  let contenido
  if (cargando || error) {
    contenido = <EstadoCarga cargando={cargando} error={error} status={status} />
  } else if (clientesFiltrados.length === 0) {
    contenido = (
      <p className="text-center text-muted">
        {busqueda ? `No hay clientes que coincidan con "${busqueda}".` : 'Todavía no hay clientes registrados.'}
      </p>
    )
  } else {
    contenido = clientesFiltrados.map((cliente) => (
      <div className="entity-card" key={cliente.id_cliente}>
        <div className="entity-card__top">
          <h3 className="entity-card__name">{cliente.nombre_c}</h3>
          <span className="entity-card__date">{formatearFecha(cliente.fecha_registro)}</span>
        </div>
        <p className="entity-card__detail">{cliente.telefono_c}</p>
        <a href={`mailto:${cliente.email_c}`} className="entity-card__email">{cliente.email_c}</a>
        <div className="entity-card__footer entity-card__footer--divided">
          <div></div>
          <div className="entity-card__actions">
            <Link to={`/clientes/${cliente.id_cliente}`} aria-label={`Ver ${cliente.nombre_c}`}>
              <i className="bi bi-eye order-row__icon"></i>
            </Link>
            <Link to={`/clientes/${cliente.id_cliente}/editar`} aria-label={`Editar ${cliente.nombre_c}`}>
              <i className="bi bi-pencil-square order-row__icon"></i>
            </Link>
          </div>
        </div>
      </div>
    ))
  }

  return (
    <>
      <Encabezado titulo="CLIENTES">
        <Buscador valor={busqueda} onCambio={setBusqueda} />
      </Encabezado>

      <section className="users-content users-content--lista">
        <Alerta mensaje={flash} />
        {contenido}
      </section>

      <Link to="/clientes/nuevo" className="btn-primary-custom">Nuevo Cliente</Link>
    </>
  )
}

export default ListaClientes
