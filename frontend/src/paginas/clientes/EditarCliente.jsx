import { useParams } from 'react-router'
import PaginaConDatos from '../../components/PaginaConDatos'
import FormularioCliente from './FormularioCliente'

function EditarCliente() {
  const { id } = useParams()

  return (
    <PaginaConDatos ruta={`clientes&id=${id}`} titulo="CLIENTE / EDITAR CLIENTE" textoNoEncontrado="El cliente que buscas no existe o fue eliminado.">
      {(cliente) => <FormularioCliente key={cliente.id_cliente} cliente={cliente} />}
    </PaginaConDatos>
  )
}

export default EditarCliente
