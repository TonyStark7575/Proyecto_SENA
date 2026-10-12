import { useParams } from 'react-router'
import Encabezado from '../../components/Encabezado'
import EstadoCarga from '../../components/EstadoCarga'
import { useDatosApi } from '../../hooks/useDatosApi'
import FormularioProducto from './FormularioProducto'

// Carga el producto y, cuando llega, muestra el formulario ya lleno
function EditarProducto() {
  const { id } = useParams()
  const { datos: producto, cargando, error, status } = useDatosApi(`productos&id=${id}`)

  if (!producto) {
    return (
      <>
        <Encabezado titulo="PRODUCTOS/ EDITAR PRODUCTO" />
        <section className="users-content">
          <EstadoCarga cargando={cargando} error={error} status={status} textoNoEncontrado="El producto que buscas no existe o fue eliminado." />
        </section>
      </>
    )
  }

  // key: si cambia el producto, React crea un formulario nuevo con sus valores iniciales
  return <FormularioProducto key={producto.id_pro} producto={producto} />
}

export default EditarProducto
