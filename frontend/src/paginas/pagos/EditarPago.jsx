import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import CampoFormulario from '../../components/CampoFormulario'
import Encabezado from '../../components/Encabezado'
import PaginaConDatos from '../../components/PaginaConDatos'
import { METODOS_PAGO, codigoPago } from '../../formato'

function EditarPago() {
  const { id } = useParams()

  return (
    <PaginaConDatos ruta={`pagos&id=${id}`} titulo={`PAGOS/ EDITAR PAGO #${codigoPago(id)}`} textoNoEncontrado="El pago que buscas no existe o fue eliminado.">
      {(pago) => <FormularioEditarPago key={pago.id_pago} pago={pago} />}
    </PaginaConDatos>
  )
}

// Se cambian el monto y el método, no el pedido
function FormularioEditarPago({ pago }) {
  const navigate = useNavigate()
  const codigo = codigoPago(pago.id_pago)

  const [monto, setMonto] = useState(pago.monto)
  const [metodo, setMetodo] = useState(pago.metodo)
  const [errores, setErrores] = useState({})
  const [mensaje, setMensaje] = useState(null)
  const [guardando, setGuardando] = useState(false)

  async function guardar(e) {
    e.preventDefault()
    setErrores({})
    setMensaje(null)
    setGuardando(true)

    const respuesta = await pedirApi(`pagos&id=${pago.id_pago}`, { metodo: 'PUT', datos: { monto, metodo } })

    setGuardando(false)

    if (respuesta.ok) {
      navigate(`/pagos/${pago.id_pago}`, { state: { mensaje: { tipo: 'success', texto: 'Cambios guardados. El saldo del pedido se recalculó.' } } })
    } else if (respuesta.status === 422) {
      setErrores(respuesta.datos.errores)
    } else {
      // 409: el pedido está Cancelado
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo={`PAGOS/ EDITAR PAGO #${codigo}`} />

      <section className="users-content">
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <h3 className="form-card__title">Editar Pago #{codigo}</h3>

          <div className="profile-data profile-data--left">
            <p className="profile-data__row">Pedido: #{pago.id_pedido}</p>
            <p className="profile-data__row">Cliente: {pago.nombre_c}</p>
          </div>

          <form className="form-fields" onSubmit={guardar} noValidate>
            <div className="form-row">
              <CampoFormulario id="monto" etiqueta="Monto" error={errores.monto}>
                <input type="number" id="monto" className="form-group-custom__input" step="0.01" value={monto} onChange={(e) => setMonto(e.target.value)} />
              </CampoFormulario>
              <CampoFormulario id="metodo" etiqueta="Método" error={errores.metodo}>
                <select id="metodo" className="form-group-custom__input" value={metodo} onChange={(e) => setMetodo(e.target.value)}>
                  {METODOS_PAGO.map((m) => (
                    <option key={m} value={m}>{m}</option>
                  ))}
                </select>
              </CampoFormulario>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn-primary-custom" disabled={guardando}>
                {guardando ? 'Guardando…' : 'Guardar cambios'}
              </button>
              <Link to={`/pagos/${pago.id_pago}`} className="btn-primary-custom btn-primary-custom--secondary">Cancelar</Link>
            </div>
          </form>
        </div>
      </section>
    </>
  )
}

export default EditarPago
