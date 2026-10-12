import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router'

// Mensaje que una página deja a la siguiente al redirigir, por ejemplo:
//   navigate('/productos', { state: { mensaje: { tipo: 'success', texto: 'Producto creado.' } } })
// Igual que $_SESSION['mensaje'] en PHP: se muestra una sola vez (no reaparece al recargar)
export function useMensajeFlash() {
  const location = useLocation()
  const navigate = useNavigate()
  const [mensaje] = useState(location.state?.mensaje ?? null)

  useEffect(() => {
    if (location.state?.mensaje) {
      // Borra el mensaje del historial sin cambiar de página
      navigate(location.pathname + location.search, { replace: true, state: null })
    }
  }, [location, navigate])

  return mensaje
}
