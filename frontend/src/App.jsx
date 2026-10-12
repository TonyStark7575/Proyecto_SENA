import TarjetaProducto from './components/TarjetaProducto'

// Datos fijos con la MISMA forma que devuelve tu API (GET api/productos)
const productos = [
  { id_pro: 1, nombre_p: 'Gorro tejido', precio: '45000.00', imagen: null },
  { id_pro: 2, nombre_p: 'Bufanda de lana', precio: '60000.00', imagen: '' },
  { id_pro: 3, nombre_p: 'Amigurumi conejo', precio: '35000.50', imagen: null },
]

function App() {
  return (
    <main className="users-page">
      <section className="users-content">
        <div className="products-grid">
          {productos.map((producto) => (
            <TarjetaProducto key={producto.id_pro} producto={producto} />
          ))}
        </div>
      </section>
    </main>
  )
}

export default App