import { useCallback, useEffect, useState } from 'react'
import { Route, Routes } from 'react-router'
import Login from './components/Login'
import Layout from './components/Layout'
import ListaProductos from './components/ListaProductos'
import EnConstruccion from './components/EnConstruccion'
import NoEncontrado from './components/NoEncontrado'

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

  // Sin sesión se muestra el login en cualquier URL; al entrar, React se queda en esa misma URL
  if (!usuario) {
    return <Login onLogin={setUsuario} />
  }

  return (
    <Routes>
      {/* Ruta padre sin path: Layout envuelve a todas las páginas de adentro */}
      <Route
        element={
          <Layout usuario={usuario} onLogout={cerrarSesion} onSesionExpirada={sesionExpirada} />
        }
      >
        <Route index element={<EnConstruccion titulo="DASHBOARD" />} />
        <Route path="productos" element={<ListaProductos />} />
        <Route path="usuarios" element={<EnConstruccion titulo="USUARIOS" />} />
        <Route path="clientes" element={<EnConstruccion titulo="CLIENTES" />} />
        <Route path="pedidos" element={<EnConstruccion titulo="PEDIDOS" />} />
        <Route path="pagos" element={<EnConstruccion titulo="PAGOS" />} />
        {/* "*" atrapa cualquier URL que no coincida con las de arriba */}
        <Route path="*" element={<NoEncontrado />} />
      </Route>
    </Routes>
  )
}

export default App