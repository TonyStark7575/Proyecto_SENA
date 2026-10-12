import { useState } from 'react'
import { mensajeDeError, pedirApi } from '../api'

function Login({ onLogin }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [enviando, setEnviando] = useState(false)

  async function manejarEnvio(e) {
    e.preventDefault() // evita que el formulario recargue la página
    setError(null)
    setEnviando(true)

    const respuesta = await pedirApi('login', { metodo: 'POST', datos: { email, password } })
    setEnviando(false)

    if (respuesta.ok) {
      onLogin(respuesta.datos) // avisa a App: { id_usuario, nombre, rol }
    } else if (respuesta.status === 422) {
      setError('Escribe tu correo y tu contraseña.')
    } else {
      // 401 (credenciales), 403 (desactivado) o sin conexión: el mensaje viene de la API o de pedirApi
      setError(mensajeDeError(respuesta))
    }
  }

  return (
    <main className="login-page">
      <header className="login-header text-center">
        <img
          src="/ProyectoSENA/public/img/Logo-no-texto.jpg"
          alt="Logo Los Hilos de Maya"
          className="login-header__logo"
        />
        <h1 className="login-header__brand">Los Hilos de Maya</h1>
      </header>

      <section className="login-card text-center">
        <h2 className="login-card__title mt-5 mb-5">INICIAR SESIÓN</h2>

        <form className="login-form mb-5" onSubmit={manejarEnvio}>
          <div className="login-form__group">
            <input
              type="email"
              id="email"
              className="login-form__input rounded"
              placeholder="CORREO"
              autoComplete="username"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <i className="bi bi-envelope login-form__icon"></i>

            <br />
            <br />

            <input
              type="password"
              id="password"
              className="login-form__input rounded"
              placeholder="CONTRASEÑA"
              autoComplete="current-password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <i className="bi bi-eye-slash-fill login-form__icon"></i>
          </div>

          {error && (
            <div className="alert alert-danger py-2 mt-3" role="alert">
              {error}
            </div>
          )}

          <button type="submit" className="btn-primary-custom" disabled={enviando}>
            {enviando ? 'Ingresando…' : 'Iniciar Sesión'}
          </button>
        </form>
      </section>
    </main>
  )
}

export default Login