import { useCallback, useEffect, useState } from 'react'
import { Route, Routes } from 'react-router'
import { alExpirarSesion, pedirApi } from './api'
import Login from './components/Login'
import Layout from './components/Layout'
import EnConstruccion from './components/EnConstruccion'
import NoEncontrado from './components/NoEncontrado'
import ListaProductos from './paginas/productos/ListaProductos'
import DetalleProducto from './paginas/productos/DetalleProducto'
import FormularioProducto from './paginas/productos/FormularioProducto'
import EditarProducto from './paginas/productos/EditarProducto'
import ListaClientes from './paginas/clientes/ListaClientes'
import DetalleCliente from './paginas/clientes/DetalleCliente'
import FormularioCliente from './paginas/clientes/FormularioCliente'
import EditarCliente from './paginas/clientes/EditarCliente'

function App() {
  const [usuario, setUsuario] = useState(null)       // null = sin sesión
  const [verificando, setVerificando] = useState(true)

  // useCallback: la misma función en cada redibujo
  const sesionExpirada = useCallback(() => setUsuario(null), [])

  // Cualquier 401 de la API (sesión vencida, usuario desactivado) vuelve al login
  useEffect(() => {
    alExpirarSesion(sesionExpirada)
  }, [sesionExpirada])

  // Al arrancar (o al recargar): ¿la cookie tiene una sesión válida?
  useEffect(() => {
    pedirApi('sesion').then((respuesta) => {
      if (respuesta.ok) {
        setUsuario(respuesta.datos)
      }
      setVerificando(false)
    })
  }, [])

  async function cerrarSesion() {
    await pedirApi('logout', { metodo: 'POST' })
    setUsuario(null) // pase lo que pase, React vuelve al login
  }

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
      <Route element={<Layout usuario={usuario} onLogout={cerrarSesion} />}>
        <Route index element={<EnConstruccion titulo="DASHBOARD" />} />
        <Route path="productos" element={<ListaProductos />} />
        {/* /productos/nuevo no se confunde con :id: React Router prefiere la ruta fija sobre la variable */}
        <Route path="productos/nuevo" element={<FormularioProducto producto={null} />} />
        <Route path="productos/:id" element={<DetalleProducto />} />
        <Route path="productos/:id/editar" element={<EditarProducto />} />
        <Route path="usuarios" element={<EnConstruccion titulo="USUARIOS" />} />
        <Route path="clientes" element={<ListaClientes />} />
        <Route path="clientes/nuevo" element={<FormularioCliente cliente={null} />} />
        <Route path="clientes/:id" element={<DetalleCliente />} />
        <Route path="clientes/:id/editar" element={<EditarCliente />} />
        <Route path="pedidos" element={<EnConstruccion titulo="PEDIDOS" />} />
        <Route path="pagos" element={<EnConstruccion titulo="PAGOS" />} />
        {/* "*" atrapa cualquier URL que no coincida con las de arriba */}
        <Route path="*" element={<NoEncontrado />} />
      </Route>
    </Routes>
  )
}

export default App
