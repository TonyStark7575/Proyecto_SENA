// Rutas que dependen de dónde esté publicado el proyecto.
// Se configuran en frontend/.env (VITE_URL_PUBLIC); aquí solo se arman a partir de esa variable.

// Carpeta public/ del proyecto, vista desde el navegador (por ejemplo /ProyectoSENA/public/)
export const URL_PUBLIC = import.meta.env.VITE_URL_PUBLIC

// La API y las imágenes de productos viven dentro de public/
export const URL_API = `${URL_PUBLIC}index.php?ruta=api/`
export const URL_IMAGENES = `${URL_PUBLIC}img/`

// Dónde está montada la app: "/" en desarrollo, ".../public/app" compilada.
// React Router lo necesita para que /clientes signifique .../public/app/clientes
export const BASE_APP = import.meta.env.BASE_URL.replace(/\/$/, '')
