import { useCallback, useEffect, useState } from 'react'
import { mensajeDeError, pedirApi } from '../api'

// Carga datos de la API al mostrar la página y cada vez que cambia la ruta.
// Devuelve { datos, cargando, error, status, recargar }
export function useDatosApi(ruta) {
  const [version, setVersion] = useState(0)
  const clave = `${ruta}#${version}`
  const [resultado, setResultado] = useState({ clave: null, datos: null, error: null, status: null })

  useEffect(() => {
    // Si la página cambia antes de que llegue la respuesta, esa respuesta se ignora
    let vigente = true

    pedirApi(ruta).then((respuesta) => {
      if (!vigente) return
      setResultado({
        clave: `${ruta}#${version}`,
        datos: respuesta.ok ? respuesta.datos : null,
        error: respuesta.ok ? null : mensajeDeError(respuesta),
        status: respuesta.status,
      })
    })

    return () => {
      vigente = false
    }
  }, [ruta, version])

  // Para volver a pedir los mismos datos (por ejemplo, después de agregar una línea a un pedido)
  const recargar = useCallback(() => setVersion((v) => v + 1), [])

  // Mientras la respuesta guardada no corresponda a la ruta actual, se está cargando.
  // Al recargar se conservan los datos anteriores para que la pantalla no parpadee
  const cargando = resultado.clave !== clave
  const mismaRuta = resultado.clave?.startsWith(`${ruta}#`)

  return {
    datos: mismaRuta ? resultado.datos : null,
    error: cargando ? null : resultado.error,
    status: cargando ? null : resultado.status,
    cargando: cargando && !mismaRuta,
    recargar,
  }
}
