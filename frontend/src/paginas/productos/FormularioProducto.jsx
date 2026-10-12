import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import CampoFormulario from '../../components/CampoFormulario'
import Encabezado from '../../components/Encabezado'
import { URL_IMAGENES } from '../../formato'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

// Sirve para crear (producto = null) y para editar (producto = el que llegó de la API)
function FormularioProducto({ producto }) {
  const navigate = useNavigate()
  const flash = useMensajeFlash()
  const esNuevo = !producto

  const [nombre, setNombre] = useState(producto?.nombre_p ?? '')
  const [precio, setPrecio] = useState(producto?.precio ?? '')
  const [descripcion, setDescripcion] = useState(producto?.descripcion ?? '')
  const [imagenActual, setImagenActual] = useState(producto?.imagen ?? null)
  const [archivo, setArchivo] = useState(null)

  const [errores, setErrores] = useState({})   // { nombre_p: '...', precio: '...', imagen: '...' }
  const [mensaje, setMensaje] = useState(null)
  const [guardando, setGuardando] = useState(false)

  // Vista previa del archivo elegido, antes de subirlo
  const vistaPrevia = useMemo(() => (archivo ? URL.createObjectURL(archivo) : null), [archivo])
  useEffect(() => {
    // Libera la memoria de la vista previa anterior
    return () => {
      if (vistaPrevia) URL.revokeObjectURL(vistaPrevia)
    }
  }, [vistaPrevia])

  const imagenMostrada = vistaPrevia ?? URL_IMAGENES + (imagenActual || 'LOGOTIPO.png')

  async function guardar(e) {
    e.preventDefault()
    setErrores({})
    setMensaje(null)
    setGuardando(true)

    // 1. Los datos van como JSON
    const datos = { nombre_p: nombre, precio, descripcion }
    const respuesta = esNuevo
      ? await pedirApi('productos', { metodo: 'POST', datos })
      : await pedirApi(`productos&id=${producto.id_pro}`, { metodo: 'PUT', datos })

    if (!respuesta.ok) {
      if (respuesta.status === 422) {
        setErrores(respuesta.datos.errores)
      } else {
        setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
      }
      setGuardando(false)
      return
    }

    const id = respuesta.datos.id_pro

    // 2. La imagen, si se eligió una, va aparte como archivo (JSON no puede llevar archivos)
    if (archivo) {
      const formulario = new FormData()
      formulario.append('imagen', archivo)
      const subida = await pedirApi(`productos/imagen&id=${id}`, { metodo: 'POST', formulario })

      if (!subida.ok) {
        const texto = subida.datos?.errores?.imagen ?? mensajeDeError(subida)

        if (esNuevo) {
          // El producto ya existe: se pasa a editarlo para que pueda reintentar con otra imagen
          navigate(`/productos/${id}/editar`, {
            state: { mensaje: { tipo: 'warning', texto: `El producto se creó, pero la imagen no se guardó: ${texto}` } },
          })
          return
        }

        setErrores({ imagen: texto })
        setGuardando(false)
        return
      }
    }

    navigate(`/productos/${id}`, {
      state: { mensaje: { tipo: 'success', texto: esNuevo ? 'Producto creado correctamente.' : 'Cambios guardados.' } },
    })
  }

  async function quitarImagen() {
    if (!window.confirm('¿Quitar la imagen de este producto? Se mostrará el logo.')) return

    const respuesta = await pedirApi(`productos/imagen&id=${producto.id_pro}`, { metodo: 'DELETE' })

    if (respuesta.ok) {
      setImagenActual(null)
      setMensaje({ tipo: 'success', texto: 'Imagen quitada.' })
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo={esNuevo ? 'PRODUCTOS/ NUEVO PRODUCTO' : 'PRODUCTOS/ EDITAR PRODUCTO'} />

      <section className="users-content">
        <Alerta mensaje={flash} />
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <h3 className="form-card__title">Información del producto</h3>

          {/* noValidate: las reglas las aplica la API y sus mensajes se muestran en cada campo */}
          <form className="form-fields" onSubmit={guardar} noValidate>
            <div className="image-upload">
              <img src={imagenMostrada} alt="Imagen del producto" />
            </div>

            <CampoFormulario id="imagen" etiqueta={esNuevo ? 'Imagen del producto' : 'Cambiar imagen (opcional)'} error={errores.imagen}>
              <input
                type="file"
                id="imagen"
                className="form-group-custom__input"
                accept="image/jpeg,image/png,image/webp,image/gif"
                onChange={(e) => setArchivo(e.target.files[0] ?? null)}
              />
            </CampoFormulario>

            {!esNuevo && imagenActual && !archivo && (
              <button type="button" className="btn-primary-custom btn-primary-custom--secondary btn-primary-custom--sm" onClick={quitarImagen}>
                <i className="bi bi-x-circle"></i> Quitar imagen
              </button>
            )}

            <CampoFormulario id="nombre" etiqueta="Nombre del producto" error={errores.nombre_p}>
              <input type="text" id="nombre" className="form-group-custom__input" value={nombre} onChange={(e) => setNombre(e.target.value)} />
            </CampoFormulario>

            <CampoFormulario id="precio" etiqueta="Precio" error={errores.precio}>
              <input type="number" id="precio" className="form-group-custom__input" step="0.01" value={precio} onChange={(e) => setPrecio(e.target.value)} />
            </CampoFormulario>

            <CampoFormulario id="descripcion" etiqueta="Descripción" error={errores.descripcion}>
              <textarea id="descripcion" className="form-group-custom__input" value={descripcion} onChange={(e) => setDescripcion(e.target.value)}></textarea>
            </CampoFormulario>

            <div className="form-actions">
              <button type="submit" className="btn-primary-custom" disabled={guardando}>
                {guardando ? 'Guardando…' : esNuevo ? 'Guardar' : 'Guardar cambios'}
              </button>
              <Link to={esNuevo ? '/productos' : `/productos/${producto.id_pro}`} className="btn-primary-custom btn-primary-custom--secondary">
                Cancelar
              </Link>
            </div>
          </form>
        </div>
      </section>
    </>
  )
}

export default FormularioProducto
