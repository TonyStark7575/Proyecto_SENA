import { formatearPrecio } from '../formato'

// Las imágenes llegan por el proxy de Vite, igual que la API
export const URL_IMAGENES = '/ProyectoSENA/public/img/'

function TarjetaProducto({ producto }) {
  // Igual que el if/else de lista.php: si no tiene imagen, se usa el logo
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
        {/* Los enlaces se conectan en la Etapa 5, con la navegación */}
        <a href="#">
          <i className="bi bi-eye order-row__icon"></i>
        </a>
        <a href="#">
          <i className="bi bi-pencil-square order-row__icon"></i>
        </a>
      </div>
    </div>
  )
}

export default TarjetaProducto