# Los Hilos de Maya

Sistema de gestión para un emprendimiento de tejidos a mano: usuarios, clientes, productos, pedidos y pagos, con un dashboard de ventas.

Proyecto formativo SENA.

- **Backend:** API REST en PHP 8 (MVC sin framework, PDO) + MySQL/MariaDB.
- **Frontend:** React 19 + React Router + Vite, con Bootstrap 5 y Bootstrap Icons.

---

## Estructura

```
ProyectoSENA/
├── .htaccess              Solo deja entrar a public/ desde el navegador
├── config/                Conexión a la BD y credenciales (credenciales.php no va en Git)
├── controllers/api/       Controladores de la API (uno por recurso + ApiController base)
├── database/              DB_proyecto.sql (estructura y triggers) y datos_demo.sql
├── frontend/              Código fuente de la app React (Vite)
├── helpers/               ImagenProducto: validación y guardado de imágenes
├── models/                Modelos (consultas SQL con PDO)
└── public/                Lo único accesible desde el navegador
    ├── index.php          Punto de entrada de la API (?ruta=api/...)
    ├── img/               Logos e imágenes subidas de productos
    └── app/               App React compilada (se genera con npm run build)
```

---

## Requisitos

| Herramienta | Versión probada |
|---|---|
| XAMPP (Apache + PHP + MariaDB) | PHP 8.2.12 · MariaDB 10.4.32 |
| Node.js y npm | Node 24 · npm 11 |

En XAMPP debe estar activo `mod_rewrite` (lo está por defecto) y la extensión `fileinfo` de PHP (también por defecto).

---

## Instalación (XAMPP)

1. **Copiar el proyecto** en `C:\xampp\htdocs\ProyectoSENA` (el nombre de la carpeta importa: ver *Cambiar la ruta* más abajo).

2. **Crear la base de datos.** Con Apache y MySQL encendidos, en phpMyAdmin → *Importar*:
   1. `database/DB_proyecto.sql` (crea la base `los_hilos_maya`, sus tablas y triggers).
   2. Opcional: `database/datos_demo.sql` sobre la base `los_hilos_maya`, para tener datos de ejemplo.

   Por consola, el equivalente es:
   ```
   mysql -u root -p --comments --default-character-set=utf8mb4 < database/DB_proyecto.sql
   mysql -u root -p --default-character-set=utf8mb4 los_hilos_maya < database/datos_demo.sql
   ```
   `--comments` conserva los comentarios de los triggers y `utf8mb4` las tildes.

3. **Credenciales.** Copiar `config/credenciales.example.php` como `config/credenciales.php` y poner los datos de MySQL.

4. **Compilar la app:**
   ```
   cd frontend
   npm install
   npm run build
   ```
   Esto genera `public/app/`.

5. **Abrir** http://localhost/ProyectoSENA/ (lleva a la app).

### Usuarios de demostración

Si se importó `datos_demo.sql`, todos usan la contraseña **`Demo-2026!`**:

| Correo | Rol | Nota |
|---|---|---|
| admin.demo@ejemplo.com | Administradora | |
| carlos.repartidor@ejemplo.com | Repartidor | |
| diana.repartidora@ejemplo.com | Repartidor | Inactiva: muestra el bloqueo de acceso |

Sin datos de demo, el primer usuario se crea directamente en la tabla `usuario`, con la contraseña cifrada con `password_hash()` de PHP.

---

## Desarrollo

Con Apache y MySQL encendidos:

```
cd frontend
npm run dev
```

La app queda en http://localhost:5173 y se recarga sola al guardar. Vite le pasa a Apache (proxy) las peticiones a la API y a las imágenes, así que la sesión funciona igual que en producción.

Otros comandos: `npm run lint` (revisión de código con oxlint) y `npm run build` (compilar para Apache).

---

## API

Todas las rutas son `public/index.php?ruta=api/...`, responden JSON y, salvo `login`, exigen sesión (cookie `PHPSESSID`). Las peticiones con cuerpo deben enviarse como `application/json`.

| Recurso | Métodos | Notas |
|---|---|---|
| `api/login` | POST | `{ email, password }` → `{ id_usuario, nombre, rol }` |
| `api/logout` | POST | 204 |
| `api/sesion` | GET | Usuario de la sesión actual |
| `api/dashboard` | GET | Totales, ventas de hoy y pedidos recientes |
| `api/productos` | GET, POST, PUT, DELETE | `&id=N` para uno |
| `api/productos/imagen` | POST, DELETE | Subir (multipart, campo `imagen`, cabecera `X-Requested-With`) o quitar la imagen |
| `api/clientes` | GET, POST, PUT, DELETE | El detalle incluye su historial de pedidos y pagos |
| `api/pedidos` | GET, POST, PUT, DELETE | POST recibe el carrito: `{ id_cliente, estado, lineas: [{ id_pro, cantidad }] }` |
| `api/pedidos/lineas` | POST, DELETE | Agregar (`&id=pedido`) o quitar (`&id=línea`) productos |
| `api/pagos` | GET, POST, PUT, DELETE | |
| `api/usuarios` | GET, POST, PUT, DELETE | Nunca devuelve la contraseña |
| `api/usuarios/activo` | PUT | `{ activo: true \| false }` |

**Códigos de respuesta:** 200/201/204 éxito · 400 JSON o id inválido · 401 sin sesión · 403 usuario desactivado · 404 no existe · 405 método no permitido · 409 conflicto con una regla de negocio · 415 cuerpo que no es JSON · 422 datos inválidos (`{ errores: { campo: mensaje } }`) · 500 error interno (el detalle queda en el log de Apache).

### Reglas de negocio

- Totales y saldos de los pedidos los calculan **triggers** de la base de datos.
- Un pago no puede superar el saldo pendiente; un pedido **Cancelado** no admite pagos.
- Los pedidos **Entregado** y **Cancelado** no admiten cambios de productos.
- No se puede quitar un producto si el total queda por debajo de lo ya pagado.
- Un pedido con pagos no se elimina (se cancela); un producto o cliente que está en pedidos tampoco.
- Un usuario con clientes o pedidos registrados se desactiva en vez de borrarse. Nadie puede eliminarse, desactivarse ni cambiarse el rol a sí mismo.
- Un usuario desactivado pierde la sesión en su siguiente petición.
- *Ventas Hoy* = suma de los pedidos registrados hoy, sin contar los cancelados.

---

## Seguridad (resumen)

- Contraseñas con `password_hash()`; la sesión se regenera al iniciar sesión.
- Consultas preparadas con PDO en todos los modelos.
- Protección CSRF: la API solo acepta cuerpos JSON (o multipart con `X-Requested-With` para imágenes).
- Imágenes: se valida el tipo real del archivo (JPG, PNG, WEBP, GIF; máximo 2 MB), el servidor pone el nombre y `public/img/.htaccess` impide ejecutar PHP en esa carpeta.
- Desde el navegador solo es accesible `public/` (`.htaccess` de la raíz).
- Los errores no muestran detalles técnicos al usuario; se registran en el log de Apache.

---

## Cambiar la ruta (por ejemplo, para la nube)

La ruta pública está en un solo lugar: `frontend/.env`.

```
VITE_URL_PUBLIC=/ProyectoSENA/public/
```

De ahí salen la URL de la API, la de las imágenes y la ruta base de la app. Para publicar en otro servidor:

1. Apuntar el *document root* del servidor a la carpeta `public/`.
2. Cambiar `VITE_URL_PUBLIC` (con el document root en `public/`, sería `/`).
3. Volver a ejecutar `npm run build`.
