import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import BadgeEstado from '../../components/BadgeEstado'
import CampoFormulario from '../../components/CampoFormulario'
import Encabezado from '../../components/Encabezado'
import PaginaConDatos from '../../components/PaginaConDatos'
import { codigoPago, formatearFecha, formatearPrecio } from '../../formato'
import { useDatosApi } from '../../hooks/useDatosApi'
import { useMensajeFlash } from '../../hooks/useMensajeFlash'

// En estos estados la API no permite agregar ni quitar productos
const ESTADOS_CERRADOS = ['Entregado', 'Cancelado']

function DetallePedido() {
  const { id } = useParams()

  return (
    <PaginaConDatos ruta={`pedidos&id=${id}`} titulo={`PEDIDOS/ DETALLE #${id}`} textoNoEncontrado="El pedido que buscas no existe o fue eliminado.">
      {(pedido) => <ContenidoPedido key={pedido.id_pedido} pedidoInicial={pedido} />}
    </PaginaConDatos>
  )
}

function ContenidoPedido({ pedidoInicial }) {
  const navigate = useNavigate()
  const flash = useMensajeFlash()
  const { datos: productos } = useDatosApi('productos')

  // Cada cambio de productos devuelve el pedido ya recalculado: se guarda aquí sin volver a pedirlo
  const [pedido, setPedido] = useState(pedidoInicial)
  const [mensaje, setMensaje] = useState(null)

  const [idProducto, setIdProducto] = useState('')
  const [cantidad, setCantidad] = useState('1')
  const [errores, setErrores] = useState({})

  const cerrado = ESTADOS_CERRADOS.includes(pedido.estado)
  const puedeRecibirPagos = pedido.estado !== 'Cancelado' && Number(pedido.saldo) > 0

  async function agregarProducto(e) {
    e.preventDefault()
    setErrores({})
    setMensaje(null)

    const respuesta = await pedirApi(`pedidos/lineas&id=${pedido.id_pedido}`, {
      metodo: 'POST',
      datos: { id_pro: idProducto, cantidad },
    })

    if (respuesta.ok) {
      setPedido(respuesta.datos)
      setIdProducto('')
      setCantidad('1')
    } else if (respuesta.status === 422) {
      setErrores(respuesta.datos.errores)
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  async function quitarLinea(linea) {
    if (!window.confirm(`¿Quitar "${linea.nombre_p}" del pedido?`)) return

    const respuesta = await pedirApi(`pedidos/lineas&id=${linea.id_detalle}`, { metodo: 'DELETE' })

    if (respuesta.ok) {
      setPedido(respuesta.datos)
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  async function eliminarPedido() {
    if (!window.confirm('¿Seguro que quieres eliminar este pedido? También se quitarán sus productos.')) return

    const respuesta = await pedirApi(`pedidos&id=${pedido.id_pedido}`, { metodo: 'DELETE' })

    if (respuesta.ok) {
      navigate('/pedidos', { state: { mensaje: { tipo: 'success', texto: `Pedido #${pedido.id_pedido} eliminado.` } } })
    } else {
      // 409: tiene pagos (se sugiere cancelarlo)
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo={`PEDIDOS/ DETALLE #${pedido.id_pedido}`} />

      <section className="users-content">
        <Alerta mensaje={flash} />
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <h3 className="form-card__title">Detalle del Pedido #{pedido.id_pedido}</h3>

          <div className="profile-data profile-data--left">
            <p className="profile-data__row">ID del pedido: #{pedido.id_pedido}</p>
            <p className="profile-data__row">Fecha: {formatearFecha(pedido.fecha_registro)}</p>
            <p className="profile-data__row">
              Cliente: <Link to={`/clientes/${pedido.id_cliente_p}`}>{pedido.nombre_c}</Link>
            </p>
            <p className="profile-data__row">Registrado por: {pedido.registrado_por}</p>
            <p className="profile-data__row">
              Estado: <BadgeEstado estado={pedido.estado} />
            </p>
          </div>

          <div className="subsection">
            <h4 className="subsection__title">Detalle de productos</h4>

            {pedido.lineas.length === 0 && <p className="profile-data__row">Este pedido no tiene productos.</p>}

            {pedido.lineas.map((linea) => (
              <div className="order-row" key={linea.id_detalle}>
                <div>
                  <p className="order-row__id"><strong>{linea.nombre_p}</strong></p>
                  <p className="order-row__code">
                    Cantidad: {linea.cantidad} · ${formatearPrecio(linea.precio)} c/u
                  </p>
                </div>
                <div className="order-row__actions">
                  <span className="order-row__code"><strong>${formatearPrecio(linea.subtotal)}</strong></span>
                  {!cerrado && (
                    <button type="button" className="boton-icono" onClick={() => quitarLinea(linea)} aria-label={`Quitar ${linea.nombre_p}`}>
                      <i className="bi bi-trash order-row__icon order-row__icon--delete"></i>
                    </button>
                  )}
                </div>
              </div>
            ))}

            <div className="order-row">
              <span className="order-row__id"><strong>Total</strong></span>
              <span className="order-row__code"><strong>${formatearPrecio(pedido.total)}</strong></span>
            </div>
            <div className="order-row">
              <span className="order-row__id">Saldo pendiente</span>
              <span className="order-row__code">${formatearPrecio(pedido.saldo)}</span>
            </div>
          </div>

          {cerrado ? (
            <p className="profile-data__row text-muted">
              Este pedido está {pedido.estado}: ya no se le pueden agregar ni quitar productos.
            </p>
          ) : (
            <div className="subsection">
              <h4 className="subsection__title">Agregar producto a este pedido</h4>
              <form onSubmit={agregarProducto} noValidate>
                <div className="form-row">
                  <CampoFormulario id="id_producto" etiqueta="Producto" error={errores.id_pro}>
                    <select id="id_producto" className="form-group-custom__input" value={idProducto} onChange={(e) => setIdProducto(e.target.value)}>
                      <option value="" disabled>Selecciona un producto</option>
                      {(productos ?? []).map((producto) => (
                        <option key={producto.id_pro} value={producto.id_pro}>
                          {producto.nombre_p} — ${formatearPrecio(producto.precio)}
                        </option>
                      ))}
                    </select>
                  </CampoFormulario>
                  <CampoFormulario id="cantidad" etiqueta="Cantidad" error={errores.cantidad}>
                    <input type="number" id="cantidad" className="form-group-custom__input" min="1" value={cantidad} onChange={(e) => setCantidad(e.target.value)} />
                  </CampoFormulario>
                </div>
                {errores.lineas && <p className="text-danger small" role="alert">{errores.lineas}</p>}
                <button type="submit" className="btn-primary-custom btn-primary-custom--secondary btn-primary-custom--sm">
                  + Agregar producto
                </button>
              </form>
            </div>
          )}

          <div className="subsection">
            <h4 className="subsection__title">Pagos registrados para este pedido</h4>
            {pedido.pagos.length === 0 && <p className="profile-data__row">Este pedido todavía no tiene pagos.</p>}
            {pedido.pagos.map((pago) => (
              <div className="order-row" key={pago.id_pago}>
                <span className="order-row__id">
                  {codigoPago(pago.id_pago)} · {formatearFecha(pago.fecha_p)} · {pago.metodo}
                </span>
                <div className="order-row__actions">
                  <span className="order-row__code">${formatearPrecio(pago.monto)}</span>
                  <Link to={`/pagos/${pago.id_pago}`} aria-label={`Ver pago ${codigoPago(pago.id_pago)}`}>
                    <i className="bi bi-eye-fill order-row__icon"></i>
                  </Link>
                </div>
              </div>
            ))}
          </div>

          <div className="form-actions form-actions--end">
            <Link to={`/pedidos/${pedido.id_pedido}/editar`} className="btn-primary-custom">Editar</Link>
            {puedeRecibirPagos && (
              <Link to={`/pagos/nuevo?pedido=${pedido.id_pedido}`} className="btn-primary-custom btn-primary-custom--secondary">
                Registrar pago
              </Link>
            )}
            <button type="button" className="btn-primary-custom btn-primary-custom--secondary" onClick={eliminarPedido}>
              Eliminar
            </button>
            <Link to="/pedidos" className="btn-primary-custom btn-primary-custom--secondary">Volver</Link>
          </div>
        </div>
      </section>
    </>
  )
}

export default DetallePedido
