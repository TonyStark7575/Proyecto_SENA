import { useState } from 'react'
import TarjetaProducto from './components/TarjetaProducto'

// Datos fijos con la MISMA forma que devuelve tu API (GET api/productos)
const productos = [
  { id_pro: 1, nombre_p: 'Gorro tejido', precio: '45000.00', imagen: null },
  { id_pro: 2, nombre_p: 'Bufanda de lana', precio: '60000.00', imagen: '' },
  { id_pro: 3, nombre_p: 'Amigurumi conejo', precio: '35000.50', imagen: null },
  { id_pro: 4, nombre_p: 'Gorro de lana para bebé', precio: '38000.00', imagen: null },
  { id_pro: 5, nombre_p: 'Mantita tejida', precio: '90000.00', imagen: null },
]

function App() {
  // Estado: lo que el usuario escribe en el buscador
  const [busqueda, setBusqueda] = useState('')

  // Dato derivado: se recalcula en cada redibujo, no necesita su propio estado
  const texto = busqueda.trim().toLowerCase()
  const productosFiltrados = productos.filter((producto) =>
    producto.nombre_p.toLowerCase().includes(texto)
  )

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

      <section className="users-content">
        {productosFiltrados.length === 0 && (
          <p className="text-center text-muted">
            No hay productos que coincidan con "{busqueda}".
          </p>
        )}

        <div className="products-grid">
          {productosFiltrados.map((producto) => (
            <TarjetaProducto key={producto.id_pro} producto={producto} />
          ))}
        </div>
      </section>
    </main>
  )
}

export default App
