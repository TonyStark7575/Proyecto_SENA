// Punto único para hablar con la API PHP (llega a Apache a través del proxy de Vite)
const URL_BASE = '/ProyectoSENA/public/index.php?ruta=api/'

// App registra aquí qué hacer cuando la API responde 401 (sesión vencida o usuario desactivado)
let manejarSesionExpirada = () => {}

export function alExpirarSesion(funcion) {
  manejarSesionExpirada = funcion
}

// Hace la petición y SIEMPRE devuelve { ok, status, datos }; nunca lanza error.
// - ruta: lo que va después de "api/", por ejemplo "productos" o "productos&id=3"
// - datos: objeto que se envía como JSON (POST y PUT)
// - formulario: un FormData, para subir archivos (imágenes)
export async function pedirApi(ruta, { metodo = 'GET', datos, formulario } = {}) {
  const opciones = { method: metodo, headers: {} }

  if (datos !== undefined) {
    opciones.headers['Content-Type'] = 'application/json'
    opciones.body = JSON.stringify(datos)
  }

  if (formulario) {
    // El navegador pone solo el Content-Type multipart con su "boundary".
    // Esta cabecera la exige la API para aceptar archivos (un formulario de otra página no puede enviarla)
    opciones.headers['X-Requested-With'] = 'fetch'
    opciones.body = formulario
  }

  let respuesta
  try {
    respuesta = await fetch(URL_BASE + ruta, opciones)
  } catch {
    return { ok: false, status: 0, datos: { error: 'No se pudo conectar con el servidor.' } }
  }

  let cuerpo = null
  if (respuesta.status !== 204) {
    try {
      cuerpo = await respuesta.json()
    } catch {
      // Por ejemplo, Apache apagado o una respuesta en HTML
      cuerpo = { error: 'La respuesta del servidor no es válida.' }
    }
  }

  // En el login un 401 significa "credenciales incorrectas", no "sesión vencida"
  if (respuesta.status === 401 && ruta !== 'login') {
    manejarSesionExpirada()
  }

  return { ok: respuesta.ok, status: respuesta.status, datos: cuerpo }
}

// Texto para mostrar cuando una respuesta no fue exitosa
export function mensajeDeError(respuesta) {
  return respuesta.datos?.error ?? `Error del servidor (${respuesta.status}).`
}
