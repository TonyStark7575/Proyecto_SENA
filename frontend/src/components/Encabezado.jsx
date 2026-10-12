import { useState } from 'react'
import { useOutletContext } from 'react-router'
import Sidebar from './Sidebar'

// Encabezado morado + menú lateral, igual que en las vistas PHP. Dos variantes:
// - titulo: la barra "PRODUCTOS", "CLIENTES / DETALLES"... con lo que llegue como children al lado (buscador)
// - tituloApp: el nombre grande entre los botones (solo el dashboard) y children debajo, a lo ancho
function Encabezado({ titulo, tituloApp, children }) {
  // Reemplaza a sidebar.js: el botón ☰ solo cambia este estado
  const [menuAbierto, setMenuAbierto] = useState(false)
  const { onLogout } = useOutletContext()

  return (
    <>
      <header className="app-header">
        <div className="app-header__top">
          <button
            className="app-header__menu"
            type="button"
            aria-label="Abrir menú"
            aria-expanded={menuAbierto}
            onClick={() => setMenuAbierto(!menuAbierto)}
          >
            <i className="bi bi-list"></i>
          </button>

          {tituloApp && <h1 className="app-header__title">{tituloApp}</h1>}

          <button
            className="app-header__logout"
            type="button"
            aria-label="Cerrar sesión"
            onClick={onLogout}
          >
            <i className="bi bi-box-arrow-right"></i>
          </button>
        </div>

        {titulo ? (
          <div className="users-header-bar">
            <span className="users-header-bar__title">{titulo}</span>
            {children}
          </div>
        ) : (
          children
        )}
      </header>

      {/* Al cambiar de página se monta un Encabezado nuevo, así que el menú vuelve a estar cerrado */}
      <Sidebar abierto={menuAbierto} onLogout={onLogout} />
    </>
  )
}

export default Encabezado
