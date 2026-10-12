import { Link } from 'react-router'
import Encabezado from './Encabezado'

// Página para las URLs que no existen dentro de la app
function NoEncontrado() {
  return (
    <>
      <Encabezado titulo="NO ENCONTRADO" />
      <section className="users-content text-center">
        <div className="alert alert-warning">La página que buscas no existe.</div>
        <Link to="/" className="btn-primary-custom">Volver al inicio</Link>
      </section>
    </>
  )
}

export default NoEncontrado
