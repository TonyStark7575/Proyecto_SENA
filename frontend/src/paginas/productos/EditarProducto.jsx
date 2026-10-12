import { useParams } from 'react-router'
import PaginaConDatos from '../../components/PaginaConDatos'
import FormularioProducto from './FormularioProducto'

// Carga el producto y, cuando llega, muestra el formulario ya lleno
function EditarProducto() {
  const { id } = useParams()

  return (
    <PaginaConDatos ruta={`productos&id=${id}`} titulo="PRODUCTOS/ EDITAR PRODUCTO" textoNoEncontrado="El producto que buscas no existe o fue eliminado.">
      {/* key: si cambia el producto, React crea un formulario nuevo con sus valores iniciales */}
      {(producto) => <FormularioProducto key={producto.id_pro} producto={producto} />}
    </PaginaConDatos>
  )
}

export default EditarProducto
