// Barra de búsqueda del encabezado de las listas
function Buscador({ valor, onCambio }) {
  return (
    <div className="search-bar search-bar--compact">
      <i className="bi bi-search search-bar__icon"></i>
      <input
        type="search"
        className="search-bar__input"
        placeholder="Buscar..."
        aria-label="Buscar"
        value={valor}
        onChange={(e) => onCambio(e.target.value)}
      />
    </div>
  )
}

export default Buscador
