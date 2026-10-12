// Lo que se muestra mientras llegan los datos o cuando la API respondió con error.
// Devuelve null cuando ya hay datos, para que la página dibuje su contenido
function EstadoCarga({ cargando, error, status, textoNoEncontrado = 'El registro que buscas no existe o fue eliminado.' }) {
  if (cargando) {
    return <p className="text-center text-muted">Cargando…</p>
  }
  if (status === 404) {
    return <div className="alert alert-warning">{textoNoEncontrado}</div>
  }
  if (error) {
    return <div className="alert alert-danger">{error}</div>
  }
  return null
}

export default EstadoCarga
