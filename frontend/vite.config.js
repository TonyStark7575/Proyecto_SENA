import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Todo lo que empiece con /ProyectoSENA se lo pide Vite a Apache
      '/ProyectoSENA': 'http://localhost',
    },
  },
})