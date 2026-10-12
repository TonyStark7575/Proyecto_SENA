import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'
import './styles/base.css'
import './styles/components.css'
import './styles/pages.css'
import './styles/extras.css'
import './styles/escritorio.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    {/* BrowserRouter lee la URL del navegador y se la entrega a <Routes> */}
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>,
)
