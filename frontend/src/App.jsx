import { useCallback, useEffect, useState } from 'react'
import Login from './components/Login'
import ListaProductos from './components/ListaProductos'

const URL_SESION = '/ProyectoSENA/public/index.php?ruta=api/sesion'
const URL_LOGOUT = '/ProyectoSENA/public/index.php?ruta=api/logout'

function App() {
  const [usuario, setUsuario] = useState(null)       // null = sin sesión
  const [verificando, setVerificando] = useState(true)

  // Al arrancar (o al recargar): ¿la cookie tiene una sesión válida?
  useEffect(() => {
    async function verificarSesion() {
      try {
        const respuesta = await fetch(URL_SESION)
        if (respuesta.ok) {
          setUsuario(await respuesta.json())
        }
      } catch {
        // Sin conexión: se muestra el login, que informará el error al intentar entrar
      } finally {
        setVerificando(false)
      }
    }
    verificarSesion()
  }, [])

  async function cerrarSesion() {
    try {
      await fetch(URL_LOGOUT, { method: 'POST' })
    } finally {
      setUsuario(null) // pase lo que pase, React vuelve al login
    }
  }

  // useCallback: la misma función en cada redibujo, para que el useEffect
  // de ListaProductos no se vuelva a ejecutar sin necesidad
  const sesionExpirada = useCallback(() => setUsuario(null), [])

  if (verificando) {
    return <p className="text-center text-muted mt-5">Cargando…</p>
  }

  if (!usuario) {
    return <Login onLogin={setUsuario} />
  }

  return <ListaProductos onLogout={cerrarSesion} onSesionExpirada={sesionExpirada} />
}

export default App