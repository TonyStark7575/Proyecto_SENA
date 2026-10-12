// Funciones para mostrar los datos de la API en pantalla

// Equivale a number_format($valor, 0, ',', '.') → "45.000".
// Number() porque la API entrega los DECIMAL como texto ("45000.00")
export function formatearPrecio(valor) {
  return Number(valor).toLocaleString('es-CO', { maximumFractionDigits: 0 })
}

// Equivale a date('d/m/Y', strtotime($fecha)): "2026-10-11 15:29:21" → "11/10/2026".
// Se arma a mano para no depender de la zona horaria del navegador
export function formatearFecha(fechaHora) {
  if (!fechaHora) return ''
  const [anio, mes, dia] = fechaHora.slice(0, 10).split('-')
  return `${dia}/${mes}/${anio}`
}

// Equivale a 'P' . str_pad($id, 3, '0', STR_PAD_LEFT) → "P007"
export function codigoPago(idPago) {
  return 'P' + String(idPago).padStart(3, '0')
}

// Clase CSS del color de cada estado de pedido
export function claseEstadoPedido(estado) {
  const clases = {
    Entregado: 'badge-status--entregado',
    Pendiente: 'badge-status--pendiente',
    'En proceso': 'badge-status--en-proceso',
  }
  return clases[estado] ?? 'badge-status--repartidor'
}

export const ESTADOS_PEDIDO = ['Pendiente', 'En proceso', 'Entregado', 'Cancelado']

export const METODOS_PAGO = ['Efectivo', 'Transferencia']

// Para los buscadores: true si alguno de los textos contiene la búsqueda
// (sin distinguir mayúsculas ni espacios a los lados)
export function coincide(busqueda, ...textos) {
  const buscado = busqueda.trim().toLowerCase()
  return textos.some((texto) => String(texto ?? '').toLowerCase().includes(buscado))
}

// Las imágenes de productos las sirve Apache (la ruta sale de config.js)
export { URL_IMAGENES } from './config'
