// Mensaje de Bootstrap: { tipo: 'success' | 'danger' | 'warning', texto: '...' }
function Alerta({ mensaje }) {
  if (!mensaje) return null
  return (
    <div className={`alert alert-${mensaje.tipo}`} role="alert">
      {mensaje.texto}
    </div>
  )
}

export default Alerta
