import { useState } from 'react'
import { Link, useNavigate, useOutletContext } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import CampoFormulario from '../../components/CampoFormulario'
import Encabezado from '../../components/Encabezado'

// Sirve para crear (cliente = null) y para editar (cliente = el que llegó de la API)
function FormularioCliente({ cliente }) {
  const navigate = useNavigate()
  const { usuario } = useOutletContext()
  const esNuevo = !cliente

  const [nombre, setNombre] = useState(cliente?.nombre_c ?? '')
  const [email, setEmail] = useState(cliente?.email_c ?? '')
  const [telefono, setTelefono] = useState(cliente?.telefono_c ?? '')

  const [errores, setErrores] = useState({})
  const [mensaje, setMensaje] = useState(null)
  const [guardando, setGuardando] = useState(false)

  async function guardar(e) {
    e.preventDefault()
    setErrores({})
    setMensaje(null)
    setGuardando(true)

    const datos = { nombre_c: nombre, email_c: email, telefono_c: telefono }
    const respuesta = esNuevo
      ? await pedirApi('clientes', { metodo: 'POST', datos })
      : await pedirApi(`clientes&id=${cliente.id_cliente}`, { metodo: 'PUT', datos })

    setGuardando(false)

    if (respuesta.ok) {
      navigate(`/clientes/${respuesta.datos.id_cliente}`, {
        state: { mensaje: { tipo: 'success', texto: esNuevo ? 'Cliente creado correctamente.' : 'Cambios guardados.' } },
      })
    } else if (respuesta.status === 422) {
      setErrores(respuesta.datos.errores)
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo={esNuevo ? 'CLIENTE / CREAR NUEVO' : 'CLIENTE / EDITAR CLIENTE'} />

      <section className="users-content">
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <h3 className="form-card__title">Información del cliente</h3>

          <form className="form-fields" onSubmit={guardar} noValidate>
            <CampoFormulario id="nombre" etiqueta="Nombre" error={errores.nombre_c}>
              <input type="text" id="nombre" className="form-group-custom__input" value={nombre} onChange={(e) => setNombre(e.target.value)} />
            </CampoFormulario>

            <CampoFormulario id="email" etiqueta="Correo" error={errores.email_c}>
              <input type="email" id="email" className="form-group-custom__input" value={email} onChange={(e) => setEmail(e.target.value)} />
            </CampoFormulario>

            <CampoFormulario id="telefono" etiqueta="Telefono" error={errores.telefono_c}>
              <input type="tel" id="telefono" className="form-group-custom__input" value={telefono} onChange={(e) => setTelefono(e.target.value)} />
            </CampoFormulario>

            <CampoFormulario id="registrado-por" etiqueta="Registrado por">
              <input type="text" id="registrado-por" className="form-group-custom__input" value={esNuevo ? usuario.nombre : cliente.registrado_por ?? ''} disabled />
            </CampoFormulario>

            <div className="form-actions">
              <button type="submit" className="btn-primary-custom" disabled={guardando}>
                {guardando ? 'Guardando…' : esNuevo ? 'Guardar' : 'Guardar cambios'}
              </button>
              <Link to={esNuevo ? '/clientes' : `/clientes/${cliente.id_cliente}`} className="btn-primary-custom btn-primary-custom--secondary">
                Cancelar
              </Link>
            </div>
          </form>
        </div>
      </section>
    </>
  )
}

export default FormularioCliente
