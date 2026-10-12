import { useState } from 'react'
import { Link, useOutletContext } from 'react-router'
import BadgeEstado from '../components/BadgeEstado'
import Encabezado from '../components/Encabezado'
import EstadoCarga from '../components/EstadoCarga'
import { coincide, formatearPrecio } from '../formato'
import { useDatosApi } from '../hooks/useDatosApi'

function Dashboard() {
  const { usuario } = useOutletContext()
  const { datos: resumen, cargando, error, status } = useDatosApi('dashboard')
  const [busqueda, setBusqueda] = useState('')

  // El buscador del dashboard filtra los pedidos recientes (por número o cliente)
  const recientes = (resumen?.pedidos_recientes ?? []).filter((pedido) =>
    coincide(busqueda, `#${pedido.id_pedido}`, String(pedido.id_pedido), pedido.nombre_c)
  )

  return (
    <>
      <Encabezado tituloApp="Los Hilos de Maya">
        {/* Mismo HTML que dashboard.php: el buscador grande, sin la clase --compact */}
        <div className="search-bar">
          <i className="bi bi-search search-bar__icon"></i>
          <input
            type="search"
            className="search-bar__input"
            placeholder="Buscar pedido..."
            aria-label="Buscar pedido"
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>
      </Encabezado>

      <section className="dashboard-content">
        <h2 className="dashboard-greeting">Bienvenida, {usuario.nombre}</h2>

        {!resumen ? (
          <EstadoCarga cargando={cargando} error={error} status={status} />
        ) : (
          <>
            <div className="stat-row">
              <Link to="/clientes" className="stat-card text-decoration-none">
                <p className="stat-card__label">Total Clientes</p>
                <p className="stat-card__value">{resumen.total_clientes}</p>
              </Link>
              <div className="stat-card" title={`${resumen.pedidos_hoy} pedido(s) registrados hoy, sin contar cancelados`}>
                <p className="stat-card__label">Ventas Hoy</p>
                <p className="stat-card__value">${formatearPrecio(resumen.ventas_hoy)}</p>
              </div>
              <Link to="/productos" className="stat-card text-decoration-none">
                <p className="stat-card__label">Total Productos</p>
                <p className="stat-card__value">{resumen.total_productos}</p>
              </Link>
            </div>

            <div className="stat-row">
              <Link to="/pedidos" className="stat-card text-decoration-none">
                <p className="stat-card__label">Pedidos Pendientes</p>
                <p className="stat-card__value">{resumen.pedidos_pendientes}</p>
              </Link>
            </div>

            <div className="section-card">
              <h3 className="section-card__title">Pedidos Recientes</h3>

              {recientes.length === 0 && (
                <p className="text-muted small mb-0">
                  {busqueda ? `No hay pedidos recientes que coincidan con "${busqueda}".` : 'Todavía no hay pedidos.'}
                </p>
              )}

              {recientes.map((pedido) => (
                <div className="order-row" key={pedido.id_pedido}>
                  <div className="order-row__info">
                    <span className="order-row__id">ID: {pedido.id_pedido} | {pedido.nombre_c}</span>
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
          </>
        )}
      </section>
    </>
  )
}

export default Dashboard
