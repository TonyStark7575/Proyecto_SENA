import { useState } from 'react'
import { Link } from 'react-router'
import Alerta from '../../components/Alerta'
import Buscador from '../../components/Buscador'
import { coincide } from '../../formato'
import Encabezado from '../../components/Encabezado'
import EstadoCarga from '../../components/EstadoCarga'
import TarjetaProducto from '../../components/TarjetaProducto'
import { useDatosApi } from '../../hooks/useDatosApi'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

function ListaProductos() {
  const { datos: productos, cargando, error, status } = useDatosApi('productos')
  const flash = useMensajeFlash()
  const [busqueda, setBusqueda] = useState('')

  // Dato derivado: se recalcula en cada redibujo, no necesita su propio estado
  const productosFiltrados = (productos ?? []).filter((producto) => coincide(busqueda, producto.nombre_p))

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
        <Buscador valor={busqueda} onCambio={setBusqueda} />
      </Encabezado>

      <Link to="/productos/nuevo" className="btn-primary-custom btn-primary-custom--pill">Nuevo Producto</Link>

      <section className="users-content">
        <Alerta mensaje={flash} />
        {contenido}
      </section>
    </>
  )
}

export default ListaProductos
