import { Link } from 'react-router'
import { URL_IMAGENES, formatearPrecio } from '../formato'

function TarjetaProducto({ producto }) {
  // Si el producto no tiene imagen, se usa el logo
  const imagen = producto.imagen
    ? URL_IMAGENES + producto.imagen
    : URL_IMAGENES + 'Logo-no-texto.jpg'

  const precio = formatearPrecio(producto.precio)

  return (
    <div className="product-card">
      <div className="product-card__image">
        <img src={imagen} alt={producto.nombre_p} />
      </div>
      <p className="product-card__name">{producto.nombre_p}</p>
      <p className="product-card__price">$ {precio}</p>
      <div className="product-card__actions">
        <Link to={`/productos/${producto.id_pro}`} aria-label={`Ver ${producto.nombre_p}`}>
          <i className="bi bi-eye order-row__icon"></i>
        </Link>
        <Link to={`/productos/${producto.id_pro}/editar`} aria-label={`Editar ${producto.nombre_p}`}>
          <i className="bi bi-pencil-square order-row__icon"></i>
        </Link>
      </div>
    </div>
  )
}

export default TarjetaProducto