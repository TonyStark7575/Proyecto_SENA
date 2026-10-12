import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import CampoFormulario from '../../components/CampoFormulario'
import Encabezado from '../../components/Encabezado'
import { METODOS_PAGO, formatearPrecio } from '../../formato'
import { useDatosApi } from '../../hooks/useDatosApi'

function NuevoPago() {
  const navigate = useNavigate()
  // Si se llega desde el detalle de un pedido (/pagos/nuevo?pedido=7), ese pedido viene elegido
  const [parametros] = useSearchParams()
  const { datos: pedidos } = useDatosApi('pedidos')

  const [idPedido, setIdPedido] = useState(parametros.get('pedido') ?? '')
  const [monto, setMonto] = useState('')
  const [metodo, setMetodo] = useState('Efectivo')

  const [errores, setErrores] = useState({})
  const [mensaje, setMensaje] = useState(null)
  const [guardando, setGuardando] = useState(false)

  // Solo se ofrecen pedidos que pueden recibir pagos: no cancelados y con saldo pendiente
  const pedidosConSaldo = (pedidos ?? []).filter((p) => p.estado !== 'Cancelado' && Number(p.saldo) > 0)
  const pedidoElegido = pedidosConSaldo.find((p) => String(p.id_pedido) === idPedido)

  async function guardar(e) {
    e.preventDefault()
    setErrores({})
    setMensaje(null)
    setGuardando(true)

    const respuesta = await pedirApi('pagos', { metodo: 'POST', datos: { id_pedido: idPedido, monto, metodo } })

    setGuardando(false)

    if (respuesta.ok) {
      // Igual que en PHP: después de pagar se vuelve al pedido, que ya muestra el saldo nuevo
      navigate(`/pedidos/${respuesta.datos.id_pedido}`, {
        state: { mensaje: { tipo: 'success', texto: `Pago de $${formatearPrecio(respuesta.datos.monto)} registrado.` } },
      })
    } else if (respuesta.status === 422) {
      setErrores(respuesta.datos.errores)
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo="PAGOS/ REGISTRAR NUEVO" />

      <section className="users-content">
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <h3 className="form-card__title">Registrar Nuevo Pago</h3>

          <form className="form-fields" onSubmit={guardar} noValidate>
            <CampoFormulario id="id_pedido" etiqueta="Pedido" error={errores.id_pedido}>
              <select id="id_pedido" className="form-group-custom__input" value={idPedido} onChange={(e) => setIdPedido(e.target.value)}>
                <option value="" disabled>Selecciona un pedido</option>
                {pedidosConSaldo.map((p) => (
                  <option key={p.id_pedido} value={p.id_pedido}>
                    #{p.id_pedido} - {p.nombre_c} (Saldo: ${formatearPrecio(p.saldo)})
                  </option>
                ))}
              </select>
            </CampoFormulario>

            <div className="form-row">
              <CampoFormulario id="monto" etiqueta="Monto" error={errores.monto}>
                <input type="number" id="monto" className="form-group-custom__input" placeholder="0" step="0.01" value={monto} onChange={(e) => setMonto(e.target.value)} />
              </CampoFormulario>
              <CampoFormulario id="metodo" etiqueta="Método" error={errores.metodo}>
                <select id="metodo" className="form-group-custom__input" value={metodo} onChange={(e) => setMetodo(e.target.value)}>
                  {METODOS_PAGO.map((m) => (
                    <option key={m} value={m}>{m}</option>
                  ))}
                </select>
              </CampoFormulario>
            </div>

            {pedidoElegido && (
              <button type="button" className="btn-primary-custom btn-primary-custom--secondary btn-primary-custom--sm" onClick={() => setMonto(pedidoElegido.saldo)}>
                Pagar el saldo completo (${formatearPrecio(pedidoElegido.saldo)})
              </button>
            )}

            <div className="form-actions">
              <button type="submit" className="btn-primary-custom" disabled={guardando}>
                {guardando ? 'Guardando…' : 'Guardar pago'}
              </button>
              <Link to={idPedido ? `/pedidos/${idPedido}` : '/pagos'} className="btn-primary-custom btn-primary-custom--secondary">Cancelar</Link>
            </div>
          </form>
        </div>
      </section>
    </>
  )
}

export default NuevoPago
