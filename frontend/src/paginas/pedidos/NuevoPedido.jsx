import { useState } from 'react'
import { Link, useNavigate, useOutletContext } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import CampoFormulario from '../../components/CampoFormulario'
import Encabezado from '../../components/Encabezado'
import { ESTADOS_PEDIDO, formatearPrecio } from '../../formato'
import { useDatosApi } from '../../hooks/useDatosApi'

// Crear un pedido con varios productos de una vez: se arman en un "carrito"
// y se envían todos juntos a la API (que los guarda en una sola transacción)
function NuevoPedido() {
  const navigate = useNavigate()
  const { usuario } = useOutletContext()
  const { datos: clientes } = useDatosApi('clientes')
  const { datos: productos } = useDatosApi('productos')

  const [idCliente, setIdCliente] = useState('')
  const [estado, setEstado] = useState('Pendiente')

  // Cada elemento: { id_pro, nombre_p, precio, cantidad }
  const [carrito, setCarrito] = useState([])
  const [productoElegido, setProductoElegido] = useState('')
  const [cantidadElegida, setCantidadElegida] = useState('1')
  const [errorCarrito, setErrorCarrito] = useState(null)

  const [errores, setErrores] = useState({})
  const [mensaje, setMensaje] = useState(null)
  const [guardando, setGuardando] = useState(false)

  // Dato derivado: el total se calcula del carrito en cada redibujo
  const total = carrito.reduce((suma, item) => suma + Number(item.precio) * item.cantidad, 0)

  function agregarAlCarrito() {
    const producto = (productos ?? []).find((p) => String(p.id_pro) === productoElegido)
    const cantidad = Number(cantidadElegida)

    if (!producto) {
      setErrorCarrito('Selecciona un producto.')
      return
    }
    if (!Number.isInteger(cantidad) || cantidad < 1) {
      setErrorCarrito('La cantidad debe ser un número entero mayor que 0.')
      return
    }

    setErrorCarrito(null)
    setErrores({ ...errores, lineas: undefined, detalle_lineas: undefined })

    // Si el producto ya está en el carrito, se suma la cantidad en vez de repetirlo
    const yaEsta = carrito.some((item) => item.id_pro === producto.id_pro)
    if (yaEsta) {
      setCarrito(carrito.map((item) => (item.id_pro === producto.id_pro ? { ...item, cantidad: item.cantidad + cantidad } : item)))
    } else {
      setCarrito([...carrito, { id_pro: producto.id_pro, nombre_p: producto.nombre_p, precio: producto.precio, cantidad }])
    }

    setProductoElegido('')
    setCantidadElegida('1')
  }

  function quitarDelCarrito(idPro) {
    setCarrito(carrito.filter((item) => item.id_pro !== idPro))
  }

  async function crearPedido(e) {
    e.preventDefault()
    setErrores({})
    setMensaje(null)
    setGuardando(true)

    const respuesta = await pedirApi('pedidos', {
      metodo: 'POST',
      datos: {
        id_cliente: idCliente,
        estado,
        lineas: carrito.map((item) => ({ id_pro: item.id_pro, cantidad: item.cantidad })),
      },
    })

    setGuardando(false)

    if (respuesta.ok) {
      navigate(`/pedidos/${respuesta.datos.id_pedido}`, {
        state: { mensaje: { tipo: 'success', texto: `Pedido #${respuesta.datos.id_pedido} creado.` } },
      })
    } else if (respuesta.status === 422) {
      setErrores(respuesta.datos.errores)
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo="PEDIDOS/ CREAR NUEVO" />

      <section className="users-content">
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <h3 className="form-card__title">Crear Nuevo Pedido</h3>

          <form className="form-fields" onSubmit={crearPedido} noValidate>
            <CampoFormulario id="id_cliente" etiqueta="Cliente" error={errores.id_cliente}>
              <select id="id_cliente" className="form-group-custom__input" value={idCliente} onChange={(e) => setIdCliente(e.target.value)}>
                <option value="" disabled>Selecciona un cliente</option>
                {(clientes ?? []).map((cliente) => (
                  <option key={cliente.id_cliente} value={cliente.id_cliente}>{cliente.nombre_c}</option>
                ))}
              </select>
            </CampoFormulario>

            <CampoFormulario id="registrado-por" etiqueta="Registrado por">
              <input type="text" id="registrado-por" className="form-group-custom__input" value={usuario.nombre} disabled />
            </CampoFormulario>

            <div className="subsection">
              <h4 className="subsection__title">Productos del pedido</h4>

              <div className="form-row">
                <CampoFormulario id="id_producto" etiqueta="Producto">
                  <select id="id_producto" className="form-group-custom__input" value={productoElegido} onChange={(e) => setProductoElegido(e.target.value)}>
                    <option value="" disabled>Selecciona un producto</option>
                    {(productos ?? []).map((producto) => (
                      <option key={producto.id_pro} value={producto.id_pro}>
                        {producto.nombre_p} — ${formatearPrecio(producto.precio)}
                      </option>
                    ))}
                  </select>
                </CampoFormulario>
                <CampoFormulario id="cantidad" etiqueta="Cantidad">
                  <input type="number" id="cantidad" className="form-group-custom__input" min="1" value={cantidadElegida} onChange={(e) => setCantidadElegida(e.target.value)} />
                </CampoFormulario>
              </div>

              {errorCarrito && <p className="text-danger small" role="alert">{errorCarrito}</p>}

              <button type="button" className="btn-primary-custom btn-primary-custom--secondary btn-primary-custom--sm" onClick={agregarAlCarrito}>
                + Agregar al pedido
              </button>

              {carrito.length === 0 && <p className="carrito__vacio">Todavía no agregaste productos.</p>}

              {carrito.map((item, i) => (
                <div key={item.id_pro}>
                  <div className="order-row">
                    <div>
                      <p className="order-row__id"><strong>{item.nombre_p}</strong></p>
                      <p className="order-row__code">Cantidad: {item.cantidad} · ${formatearPrecio(item.precio)} c/u</p>
                    </div>
                    <div className="order-row__actions">
                      <span className="order-row__code"><strong>${formatearPrecio(Number(item.precio) * item.cantidad)}</strong></span>
                      <button type="button" className="boton-icono" onClick={() => quitarDelCarrito(item.id_pro)} aria-label={`Quitar ${item.nombre_p}`}>
                        <i className="bi bi-trash order-row__icon order-row__icon--delete"></i>
                      </button>
                    </div>
                  </div>
                  {/* Errores que la API devolvió para esta línea concreta */}
                  {errores.detalle_lineas?.[i] && (
                    <p className="text-danger small mb-1" role="alert">{Object.values(errores.detalle_lineas[i]).join(' ')}</p>
                  )}
                </div>
              ))}

              {carrito.length > 0 && (
                <div className="carrito__total">
                  <span>Total</span>
                  <span>${formatearPrecio(total)}</span>
                </div>
              )}

              {errores.lineas && <p className="text-danger small mt-1" role="alert">{errores.lineas}</p>}
            </div>

            <CampoFormulario id="estado" etiqueta="Estado inicial" error={errores.estado}>
              <select id="estado" className="form-group-custom__input" value={estado} onChange={(e) => setEstado(e.target.value)}>
                {ESTADOS_PEDIDO.map((opcion) => (
                  <option key={opcion} value={opcion}>{opcion}</option>
                ))}
              </select>
            </CampoFormulario>

            <div className="form-actions">
              <button type="submit" className="btn-primary-custom" disabled={guardando}>
                {guardando ? 'Creando…' : 'Crear pedido'}
              </button>
              <Link to="/pedidos" className="btn-primary-custom btn-primary-custom--secondary">Cancelar</Link>
            </div>
          </form>
        </div>
      </section>
    </>
  )
}

export default NuevoPedido
