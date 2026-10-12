// Etiqueta + campo + mensaje de error, con las mismas clases de los formularios PHP.
// El campo (input, select o textarea) llega como children.
// "error" es el texto que la API devolvió para este campo en { errores: { campo: '...' } }
function CampoFormulario({ id, etiqueta, error, children }) {
  return (
    <div className="form-group-custom">
      <label htmlFor={id} className="form-group-custom__label">
        {etiqueta}
      </label>
      {children}
      {error && (
        <p className="text-danger small mt-1 mb-0" role="alert">
          {error}
        </p>
      )}
    </div>
  )
}

export default CampoFormulario
