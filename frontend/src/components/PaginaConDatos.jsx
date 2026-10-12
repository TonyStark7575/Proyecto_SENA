import Encabezado from './Encabezado'
import EstadoCarga from './EstadoCarga'
import { useDatosApi } from '../hooks/useDatosApi'

// Carga "ruta" de la API. Mientras carga (o si falla) muestra el encabezado con el estado;
// cuando llegan los datos, llama a children(datos, recargar) para dibujar la página.
// Ejemplo: <PaginaConDatos ruta="clientes&id=3" titulo="CLIENTE">{(cliente) => <Detalle cliente={cliente} />}</PaginaConDatos>
function PaginaConDatos({ ruta, titulo, textoNoEncontrado, children }) {
  const { datos, cargando, error, status, recargar } = useDatosApi(ruta)

  if (!datos) {
    return (
      <>
        <Encabezado titulo={titulo} />
        <section className="users-content">
          <EstadoCarga cargando={cargando} error={error} status={status} textoNoEncontrado={textoNoEncontrado} />
        </section>
      </>
    )
  }

  return children(datos, recargar)
}

export default PaginaConDatos
