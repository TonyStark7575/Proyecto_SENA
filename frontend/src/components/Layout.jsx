import { Outlet } from 'react-router'

// Ruta padre de todas las páginas con sesión.
// <Outlet> es el hueco donde React Router dibuja la página de la URL actual,
// y "context" le comparte a esa página (y a su Encabezado) el usuario y el logout
function Layout({ usuario, onLogout }) {
  return (
    <main className="users-page">
      <Outlet context={{ usuario, onLogout }} />
    </main>
  )
}

export default Layout
