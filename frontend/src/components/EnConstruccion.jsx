import Encabezado from './Encabezado'

// Página provisional para los módulos que todavía no están en React
function EnConstruccion({ titulo }) {
  return (
    <>
      <Encabezado titulo={titulo} />
      <section className="users-content">
        <p className="text-center text-muted">
          Este módulo todavía no está disponible en esta versión.
        </p>
      </section>
    </>
  )
}

export default EnConstruccion
