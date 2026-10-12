import { useEffect, useState } from 'react'
import TarjetaProducto from './TarjetaProducto'

const URL_API = '/ProyectoSENA/public/index.php?ruta=api/productos'

function ListaProductos({ onLogout, onSesionExpirada }) {
  const [productos, setProductos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [busqueda, setBusqueda] = useState('')

  useEffect(() => {
    async function cargarProductos() {
      try {
        const respuesta = await fetch(URL_API)

        if (respuesta.status === 401) {
          onSesionExpirada() // la sesión se cerró: App vuelve a mostrar el login
          return
        }
        if (!respuesta.ok) {
          setError(`Error del servidor (${respuesta.status}).`)
          return
        }

        const datos = await respuesta.json()
        setProductos(datos)
      } catch {
        setError('No se pudo conectar con el servidor o la respuesta no es válida.')
      } finally {
        setCargando(false)
      }
    }

    cargarProductos()
  }, [onSesionExpirada])

  const texto = busqueda.trim().toLowerCase()
  const productosFiltrados = productos.filter((producto) =>
    producto.nombre_p.toLowerCase().includes(texto)
  )

  let contenido
  if (cargando) {
    contenido = <p className="text-center text-muted">Cargando productos…</p>
  } else if (error) {
    contenido = <div className="alert alert-danger">{error}</div>
  } else if (productosFiltrados.length === 0) {
    contenido = (
      <p className="text-center text-muted">
        {busqueda
          ? `No hay productos que coincidan con "${busqueda}".`
          : 'Todavía no hay productos registrados.'}
      </p>
    )
  } else {
    contenido = (
      <div className="products-grid">
        {productosFiltrados.map((producto) => (
          <TarjetaProducto key={producto.id_pro} producto={producto} />
        ))}
      </div>
    )
  }

  return (
    <main className="users-page">
      <header className="app-header">
        <div className="app-header__top">
          {/* El menú se conecta en la Etapa 5 */}
          <button className="app-header__menu" type="button" aria-label="Abrir menú">
            <i className="bi bi-list"></i>
          </button>
          <button
            className="app-header__logout"
            type="button"
            aria-label="Cerrar sesión"
            onClick={onLogout}
          >
            <i className="bi bi-box-arrow-right"></i>
          </button>
        </div>

        <div className="users-header-bar">
          <span className="users-header-bar__title">PRODUCTOS</span>
          <div className="search-bar search-bar--compact">
            <i className="bi bi-search search-bar__icon"></i>
            <input
              type="search"
              className="search-bar__input"
              placeholder="Buscar..."
              aria-label="Buscar"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </div>
        </div>
      </header>

      <section className="users-content">{contenido}</section>
    </main>
  )
}

export default ListaProductos