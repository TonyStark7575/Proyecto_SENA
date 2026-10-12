import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import Encabezado from '../../components/Encabezado'
import EstadoCarga from '../../components/EstadoCarga'
import { URL_IMAGENES, formatearFecha, formatearPrecio } from '../../formato'
import { useDatosApi } from '../../hooks/useDatosApi'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

function DetalleProducto() {
  // useParams lee la parte variable de la URL: en /productos/7, id = "7"
  const { id } = useParams()
  const navigate = useNavigate()
  const flash = useMensajeFlash()
  const { datos: producto, cargando, error, status } = useDatosApi(`productos&id=${id}`)
  const [mensaje, setMensaje] = useState(null)
  const [eliminando, setEliminando] = useState(false)

  async function eliminar() {
    if (!window.confirm('¿Seguro que quieres eliminar este producto?')) return

    setEliminando(true)
    const respuesta = await pedirApi(`productos&id=${id}`, { metodo: 'DELETE' })
    setEliminando(false)

    if (respuesta.ok) {
      navigate('/productos', { state: { mensaje: { tipo: 'success', texto: 'Producto eliminado correctamente.' } } })
    } else {
      // 409: el producto está en algún pedido
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  if (!producto) {
    return (
      <>
        <Encabezado titulo="DETALLES" />
        <section className="users-content">
          <EstadoCarga cargando={cargando} error={error} status={status} textoNoEncontrado="El producto que buscas no existe o fue eliminado." />
        </section>
      </>
    )
  }

  const imagen = URL_IMAGENES + (producto.imagen || 'LOGOTIPO.png')

  return (
    <>
      <Encabezado titulo={`DETALLES / ${producto.nombre_p}`} />

      <section className="users-content">
        <Alerta mensaje={flash} />
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <div className="image-upload" style={{ width: 160, height: 160 }}>
            <img src={imagen} alt={producto.nombre_p} />
          </div>

          <div className="profile-data">
            <p className="profile-data__row"><strong>{producto.nombre_p}</strong></p>
            <p className="profile-data__row">Precio: $ {formatearPrecio(producto.precio)}</p>
            <p className="profile-data__row"><strong>Descripción</strong></p>
            <p className="profile-data__row">{producto.descripcion}</p>
            <p className="profile-data__row">Fecha Registro {formatearFecha(producto.fecha_registro)}</p>
          </div>

          <div className="detail-actions-row">
            <Link to={`/productos/${id}/editar`} className="btn-primary-custom" aria-label="Editar producto">
              <i className="bi bi-pencil-square"></i>
            </Link>
            <button type="button" className="btn-primary-custom" onClick={eliminar} disabled={eliminando} aria-label="Eliminar producto">
              <i className="bi bi-trash"></i>
            </button>
          </div>
        </div>

        <div className="form-actions form-actions--center">
          <Link to="/productos" className="btn-primary-custom btn-primary-custom--secondary">Volver</Link>
        </div>
      </section>
    </>
  )
}

export default DetalleProducto
