import { claseEstadoPedido } from '../formato'

// Etiqueta de color con el estado de un pedido (Pendiente, En proceso, Entregado, Cancelado)
function BadgeEstado({ estado }) {
  return <span className={`badge-status ${claseEstadoPedido(estado)}`}>{estado}</span>
}

export default BadgeEstado
