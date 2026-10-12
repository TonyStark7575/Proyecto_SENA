// Las imágenes llegan por el proxy de Vite, igual que la API
const URL_IMAGENES = '/ProyectoSENA/public/img/'

function TarjetaProducto({ producto }) {
  // Igual que el if/else de lista.php: si no tiene imagen, se usa el logo
  const imagen = producto.imagen
    ? URL_IMAGENES + producto.imagen
    : URL_IMAGENES + 'Logo-no-texto.jpg'

  // Equivale a number_format($precio, 0, ',', '.') → "45.000"
  // Number() porque la API entrega el precio como texto ("45000.00")
  const precio = Number(producto.precio).toLocaleString('es-CO', {
    maximumFractionDigits: 0,
  })

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