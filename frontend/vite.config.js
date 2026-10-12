import react from '@vitejs/plugin-react'
import { defineConfig, loadEnv } from 'vite'

// https://vite.dev/config/
export default defineConfig(({ command, mode }) => {
  // Lee VITE_URL_PUBLIC de frontend/.env (por ejemplo /ProyectoSENA/public/)
  const { VITE_URL_PUBLIC } = loadEnv(mode, process.cwd())

  return {
    plugins: [react()],

    // En desarrollo la app vive en http://localhost:5173/ ; compilada, la sirve Apache desde public/app/
    base: command === 'build' ? `${VITE_URL_PUBLIC}app/` : '/',

    build: {
      // npm run build deja la app lista en public/app/ (esa carpeta no se sube a Git: se genera)
      outDir: '../public/app',
      emptyOutDir: true,
    },

    server: {
      proxy: {
        // En desarrollo, lo que empiece con /ProyectoSENA/public (API e imágenes) se lo pide Vite a Apache
        [VITE_URL_PUBLIC.replace(/\/$/, '')]: 'http://localhost',
      },
    },
  }
})
