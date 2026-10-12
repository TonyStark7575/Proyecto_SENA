import { useState } from 'react'
import { Link, useNavigate, useOutletContext } from 'react-router'
import { mensajeDeError, pedirApi } from '../../api'
import Alerta from '../../components/Alerta'
import CampoFormulario from '../../components/CampoFormulario'
import Encabezado from '../../components/Encabezado'

// Sirve para crear (usuarioEditado = null) y para editar
function FormularioUsuario({ usuarioEditado }) {
  const navigate = useNavigate()
  const { usuario: sesion, onRecargarSesion } = useOutletContext()
  const esNuevo = !usuarioEditado
  const esYo = !esNuevo && Number(usuarioEditado.id_usuario) === Number(sesion.id_usuario)

  const [nombre, setNombre] = useState(usuarioEditado?.nombre_u ?? '')
  const [email, setEmail] = useState(usuarioEditado?.email_u ?? '')
  const [telefono, setTelefono] = useState(usuarioEditado?.telefono_u ?? '')
  const [password, setPassword] = useState('')
  const [confirmar, setConfirmar] = useState('')
  const [rol, setRol] = useState(usuarioEditado?.rol ?? '')

  const [errores, setErrores] = useState({})
  const [mensaje, setMensaje] = useState(null)
  const [guardando, setGuardando] = useState(false)

  async function guardar(e) {
    e.preventDefault()
    setMensaje(null)

    // "Confirmar contraseña" solo existe en el formulario: se revisa aquí, antes de llamar a la API
    if (password !== confirmar) {
      setErrores({ confirmar: 'Las contraseñas no coinciden' })
      return
    }

    setErrores({})
    setGuardando(true)

    const datos = { nombre_u: nombre, email_u: email, telefono_u: telefono, password, rol }
    const respuesta = esNuevo
      ? await pedirApi('usuarios', { metodo: 'POST', datos })
      : await pedirApi(`usuarios&id=${usuarioEditado.id_usuario}`, { metodo: 'PUT', datos })

    setGuardando(false)

    if (respuesta.ok) {
      if (esYo) {
        await onRecargarSesion() // React se entera del nombre nuevo
      }
      navigate(`/usuarios/${respuesta.datos.id_usuario}`, {
        state: { mensaje: { tipo: 'success', texto: esNuevo ? 'Usuario creado correctamente.' : 'Cambios guardados.' } },
      })
    } else if (respuesta.status === 422) {
      setErrores(respuesta.datos.errores)
    } else {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(respuesta) })
    }
  }

  return (
    <>
      <Encabezado titulo={esNuevo ? 'USUARIOS / CREAR NUEVO' : 'USUARIOS / EDITAR USUARIO'} />

      <section className="users-content">
        <Alerta mensaje={mensaje} />

        <div className="form-card">
          <h3 className="form-card__title">Información del usuario</h3>

          <form className="form-fields" onSubmit={guardar} noValidate>
            <CampoFormulario id="nombre" etiqueta="Nombre" error={errores.nombre_u}>
              <input type="text" id="nombre" className="form-group-custom__input" value={nombre} onChange={(e) => setNombre(e.target.value)} />
            </CampoFormulario>

            <CampoFormulario id="email" etiqueta="Correo" error={errores.email_u}>
              <input type="email" id="email" className="form-group-custom__input" value={email} onChange={(e) => setEmail(e.target.value)} />
            </CampoFormulario>

            <CampoFormulario id="telefono" etiqueta="Teléfono" error={errores.telefono_u}>
              <input type="text" id="telefono" className="form-group-custom__input" value={telefono} onChange={(e) => setTelefono(e.target.value)} />
            </CampoFormulario>

            <CampoFormulario id="password" etiqueta={esNuevo ? 'Contraseña' : 'Cambiar contraseña (opcional)'} error={errores.password}>
              <input
                type="password"
                id="password"
                className="form-group-custom__input"
                autoComplete="new-password"
                placeholder={esNuevo ? 'Mínimo 8 caracteres' : 'Dejar en blanco para no cambiarla'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </CampoFormulario>

            <CampoFormulario id="confirmar-password" etiqueta="Confirmar contraseña" error={errores.confirmar}>
              <input type="password" id="confirmar-password" className="form-group-custom__input" autoComplete="new-password" value={confirmar} onChange={(e) => setConfirmar(e.target.value)} />
            </CampoFormulario>

            <CampoFormulario id="rol" etiqueta="Rol" error={errores.rol}>
              {/* La API no deja que cambies tu propio rol: aquí se muestra bloqueado */}
              <select id="rol" className="form-group-custom__input" value={rol} disabled={esYo} onChange={(e) => setRol(e.target.value)}>
                <option value="" disabled>Selecciona un rol</option>
                <option value="ADMINISTRADORA">Administradora</option>
                <option value="REPARTIDOR">Repartidor</option>
              </select>
            </CampoFormulario>
            {esYo && <p className="text-muted small">No puedes cambiar tu propio rol.</p>}

            <div className="form-actions">
              <button type="submit" className="btn-primary-custom" disabled={guardando}>
                {guardando ? 'Guardando…' : esNuevo ? 'Guardar' : 'Guardar cambios'}
              </button>
              <Link to={esNuevo ? '/usuarios' : `/usuarios/${usuarioEditado.id_usuario}`} className="btn-primary-custom btn-primary-custom--secondary">
                Cancelar
              </Link>
            </div>
          </form>
        </div>
      </section>
    </>
  )
}

export default FormularioUsuario
