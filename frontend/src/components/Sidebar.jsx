import { NavLink } from 'react-router'

// Mismo menú que views/layouts/sidebar.php, pero con rutas de React
const ITEMS_MENU = [
  { ruta: '/', icono: 'bi-grid-fill', texto: 'Dashboard' },
  { ruta: '/usuarios', icono: 'bi-people-fill', texto: 'Usuarios' },
  { ruta: '/clientes', icono: 'bi-person-fill', texto: 'Clientes' },
  { ruta: '/productos', icono: 'bi-tag-fill', texto: 'Productos' },
  { ruta: '/pedidos', icono: 'bi-cart-fill', texto: 'Pedidos' },
  { ruta: '/pagos', icono: 'bi-credit-card-fill', texto: 'Pagos' },
]

function Sidebar({ abierto, onLogout }) {
  return (
    <nav className={'sidebar-menu' + (abierto ? ' sidebar-menu--abierto' : '')}>
      {ITEMS_MENU.map((item) => (
        // NavLink sabe si su ruta es la actual: reemplaza el $moduloActivo de PHP.
        // "end" en "/" evita que Dashboard quede activo en todas las páginas
        <NavLink
          key={item.ruta}
          to={item.ruta}
          end={item.ruta === '/'}
          className={({ isActive }) => 'sidebar-menu__item' + (isActive ? ' activo' : '')}
        >
          <i className={`bi ${item.icono} sidebar-menu__icon`}></i>
          {item.texto}
        </NavLink>
      ))}

      <button type="button" className="sidebar-menu__item" onClick={onLogout}>
        <i className="bi bi-box-arrow-right sidebar-menu__icon"></i>
        Cerrar sesión
      </button>
    </nav>
  )
}

export default Sidebar
