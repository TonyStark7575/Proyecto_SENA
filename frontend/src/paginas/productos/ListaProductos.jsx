import { useState } from 'react'
import Encabezado from '../../components/Encabezado'
import EstadoCarga from '../../components/EstadoCarga'
import TarjetaProducto from '../../components/TarjetaProducto'
import { useDatosApi } from '../../hooks/useDatosApi'

function ListaProductos() {
  const { datos: productos, cargando, error, status } = useDatosApi('productos')
  const [busqueda, setBusqueda] = useState('')

  // Dato derivado: se recalcula en cada redibujo, no necesita su propio estado
  const texto = busqueda.trim().toLowerCase()
  const productosFiltrados = (productos ?? []).filter((producto) =>
    producto.nombre_p.toLowerCase().includes(texto)
  )

  let contenido
  if (cargando || error) {
    contenido = <EstadoCarga cargando={cargando} error={error} status={status} />
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
    <>
      <Encabezado titulo="PRODUCTOS">
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
      </Encabezado>

      <section className="users-content">{contenido}</section>
    </>
  )
}

export default ListaProductos
