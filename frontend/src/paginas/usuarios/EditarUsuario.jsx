import { useParams } from 'react-router'
import PaginaConDatos from '../../components/PaginaConDatos'
import FormularioUsuario from './FormularioUsuario'

function EditarUsuario() {
  const { id } = useParams()

  return (
    <PaginaConDatos ruta={`usuarios&id=${id}`} titulo="USUARIOS / EDITAR USUARIO" textoNoEncontrado="El usuario que buscas no existe o fue eliminado.">
      {(usuario) => <FormularioUsuario key={usuario.id_usuario} usuarioEditado={usuario} />}
    </PaginaConDatos>
  )
}

export default EditarUsuario
