import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import CampoFormulario from '../../components/CampoFormulario'
import Encabezado from '../../components/Encabezado'
import PaginaConDatos from '../../components/PaginaConDatos'
import { ESTADOS_PEDIDO } from '../../formato'
import { useDatosApi } from '../../hooks/useDatosApi'

function EditarPedido() {
  const { id } = useParams()

  return (
    <PaginaConDatos ruta={`pedidos&id=${id}`} titulo={`PEDIDOS/ EDITAR PEDIDO #${id}`} textoNoEncontrado="El pedido que buscas no existe o fue eliminado.">
      {(pedido) => <FormularioEditarPedido key={pedido.id_pedido} pedido={pedido} />}
    </PaginaConDatos>
  )
}

// Aquí solo se cambian el cliente y el estado; los productos se manejan en el detalle
function FormularioEditarPedido({ pedido }) {
  const navigate = useNavigate()
  const { datos: clientes } = useDatosApi('clientes')

  const [idCliente, setIdCliente] = useState(String(pedido.id_cliente_p))
  const [estado, setEstado] = useState(pedido.estado)
  const [errores, setErrores] = useState({})
  const [mensaje, setMensaje] = useState(null)
  const [guardando, setGuardando] = useState(false)

  async function guardar(e) {
    e.preventDefault()
    setErrores({})
    setMensaje(null)
    setGuardando(true)

    const respuesta = await pedirApi(`pedidos&id=${pedido.id_pedido}`, {
      metodo: 'PUT',
      datos: { id_cliente: idCliente, estado },
    })

    setGuardando(false)

    if (respuesta.ok) {
      navigate(`/pedidos/${pedido.id_pedido}`, { state: { mensaje: { tipo: 'success', texto: 'Cambios guardados.' } } })
    } else if (respuesta.status === 422) {
      setErrores(respuesta.datos.errores)
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo={`PEDIDOS/ EDITAR PEDIDO #${pedido.id_pedido}`} />

      <section className="users-content">
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <h3 className="form-card__title">Editar Pedido #{pedido.id_pedido}</h3>

          <form className="form-fields" onSubmit={guardar} noValidate>
            <CampoFormulario id="id_cliente" etiqueta="Cliente" error={errores.id_cliente}>
              <select id="id_cliente" className="form-group-custom__input" value={idCliente} onChange={(e) => setIdCliente(e.target.value)}>
                {(clientes ?? [{ id_cliente: pedido.id_cliente_p, nombre_c: pedido.nombre_c }]).map((cliente) => (
                  <option key={cliente.id_cliente} value={cliente.id_cliente}>{cliente.nombre_c}</option>
                ))}
              </select>
            </CampoFormulario>

            <CampoFormulario id="estado" etiqueta="Estado del pedido" error={errores.estado}>
              <select id="estado" className="form-group-custom__input" value={estado} onChange={(e) => setEstado(e.target.value)}>
                {ESTADOS_PEDIDO.map((opcion) => (
                  <option key={opcion} value={opcion}>{opcion}</option>
                ))}
              </select>
            </CampoFormulario>

            <div className="form-actions">
              <button type="submit" className="btn-primary-custom" disabled={guardando}>
                {guardando ? 'Guardando…' : 'Guardar cambios'}
              </button>
              <Link to={`/pedidos/${pedido.id_pedido}`} className="btn-primary-custom btn-primary-custom--secondary">Cancelar</Link>
            </div>
          </form>
        </div>
      </section>
    </>
  )
}

export default EditarPedido
