// "Completo" si el pedido ya no debe nada; "Parcial" si todavía tiene saldo (igual que en PHP)
function BadgePago({ saldo }) {
  return Number(saldo) === 0
    ? <span className="badge-status badge-status--completo">Completo</span>
    : <span className="badge-status badge-status--parcial">Parcial</span>
}

export default BadgePago
