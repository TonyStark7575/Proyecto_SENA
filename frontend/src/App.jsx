import { useEffect, useState } from 'react'
import TarjetaProducto from './components/TarjetaProducto'

const URL_API = '/ProyectoSENA/public/index.php?ruta=api/productos'

function App() {
  const [productos, setProductos] = useState([])   // llegan de la API
  const [cargando, setCargando] = useState(true)   // ¿todavía esperando?
  const [error, setError] = useState(null)         // mensaje si algo falla
  const [busqueda, setBusqueda] = useState('')

  // Se ejecuta UNA vez, después del primer dibujo ([] = no depende de nada)
  useEffect(() => {
    async function cargarProductos() {
      try {
        const respuesta = await fetch(URL_API)

        if (respuesta.status === 401) {
          setError('No hay sesión iniciada. Inicia sesión en la aplicación y recarga esta página.')
          return
        }
        if (!respuesta.ok) {
          setError(`Error del servidor (${respuesta.status}).`)
          return
        }

        const datos = await respuesta.json()
        setProductos(datos)
      } catch {
        // Sin conexión, o la respuesta no era JSON (el famoso "Unexpected token '<'")
        setError('No se pudo conectar con el servidor o la respuesta no es válida.')
      } finally {
        // Se ejecuta siempre: con éxito, con error y también después de un return
        setCargando(false)
      }
    }

    cargarProductos()
  }, [])

  // Dato derivado, igual que antes
  const texto = busqueda.trim().toLowerCase()
  const productosFiltrados = productos.filter((producto) =>
    producto.nombre_p.toLowerCase().includes(texto)
  )

  // Qué mostrar según el estado: cargando, error o la lista
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
          {/* Menú y logout se conectan más adelante */}
          <button className="app-header__menu" type="button" aria-label="Abrir menú">
            <i className="bi bi-list"></i>
          </button>
          <button className="app-header__logout" type="button" aria-label="Cerrar sesión">
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

export default App