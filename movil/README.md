# Los Hilos de Maya: app móvil (Android)

Cliente Android nativo del sistema. **No tiene backend propio**: consume la misma API PHP (`public/index.php?ruta=api/...`) y la misma base de datos que la app web React.

```
App React (web)  ──┐
                   ├──►  API PHP (MVC)  ──►  MySQL (los_hilos_maya)
App Android      ──┘
```

- **Lenguaje:** Java 17 · **minSdk** 24 (Android 7.0) · **targetSdk/compileSdk** 37
- **Patrón:** MVVM (Model–View–ViewModel) + Repository
- **APK de prueba:** `app/build/outputs/apk/debug/app-debug.apk` (se genera al compilar; no va en Git)

---

## Módulos

Son los mismos de la web, con las mismas reglas de negocio (las aplica la API):

| Módulo | Pantallas | Qué se puede hacer |
|---|---|---|
| **Inicio de sesión** | Login | Entrar con correo y contraseña. Si quedó una sesión abierta, entra directo. Configurar la URL del servidor |
| **Dashboard** | Inicio | Ventas hoy, cobrado hoy, pedidos pendientes, total de clientes y productos, pedidos recientes, cerrar sesión |
| **Productos** | Lista · Detalle · Formulario | Listar y buscar, ver, crear, editar, eliminar, **subir imagen desde la galería** y quitar imagen |
| **Clientes** | Lista · Detalle · Formulario | Listar y buscar, ver con su historial de pedidos y pagos, crear, editar, eliminar |
| **Pedidos** | Lista · Detalle · Formulario | Listar y buscar, ver productos y pagos, crear con carrito, cambiar cliente y estado, agregar y quitar productos, eliminar, registrar pago |
| **Pagos** | Lista · Detalle · Formulario | Listar y buscar, ver, registrar (solo pedidos con saldo y no cancelados), editar monto y método, eliminar |
| **Usuarios** | Lista · Detalle · Formulario | Listar y buscar, ver, crear, editar, activar o desactivar, eliminar (si tiene registros, la API lo desactiva) |

### Mapa de navegación

```
Login ──► Dashboard ──┬──► Productos ──► Detalle producto ──► Formulario producto
                      ├──► Clientes  ──► Detalle cliente  ──► Formulario cliente
                      │                       └──► Detalle pedido / Detalle pago
                      ├──► Pedidos   ──► Detalle pedido   ──► Formulario pedido
                      │                       └──► Formulario pago / Detalle pago / Detalle cliente
                      ├──► Pagos     ──► Detalle pago     ──► Formulario pago
                      │                       └──► Detalle pedido
                      └──► Usuarios  ──► Detalle usuario  ──► Formulario usuario
```

---

## Arquitectura y paquetes

```
app/src/main/java/com/loshilosdemaya/movil/
├── AplicacionMaya.java     Arranque: inicializa la conexión con la API
├── modelo/                 Clases que reflejan el JSON de la API (Producto, Cliente, Pedido, LineaPedido,
│                           Pago, Usuario, Sesion, ResumenDashboard...). @SerializedName mapea snake_case → camelCase
├── red/                    Capa de acceso a datos remotos
│   ├── ApiService          Interfaz Retrofit con todos los endpoints
│   ├── ConexionApi         Singleton: arma Retrofit + OkHttp con la URL del servidor
│   ├── GestorCookies       Guarda la cookie de sesión PHP (PHPSESSID), como lo hace el navegador
│   └── ErrorApi            Lee los errores de la API: { error } y { errores: { campo: mensaje } }
├── repositorio/            Patrón Repository: un repositorio por módulo + Resultado (CARGANDO / EXITO / ERROR)
├── util/                   Lógica sin Android, probada con JUnit: Formato, Validador, CarritoPedido,
│                           CompresorImagen, SesionActual
└── vista/                  Capa de presentación (Activity + ViewModel + Adapter por módulo)
    ├── comun/              Componentes reutilizables: BaseActivity, ListaBaseActivity, ListaViewModel,
    │                       AdaptadorFiltrable (lista con buscador), UtilVista
    ├── login/  dashboard/  productos/  clientes/  pedidos/  pagos/  usuarios/
```

**Flujo de una pantalla:** `Activity` (vista) → `ViewModel` → `Repositorio` → `ApiService` (Retrofit) → API PHP. La respuesta vuelve como `LiveData<Resultado<T>>`, que la vista observa para mostrar el indicador de carga, los datos o el error.

**Patrones usados:** MVVM, Repository, Singleton (`ConexionApi`), Template Method (`ListaBaseActivity` y `ListaViewModel` resuelven lo común y cada módulo completa lo suyo), Adapter (RecyclerView).

### Librerías por capa

| Capa | Librería | Para qué |
|---|---|---|
| Vista | AndroidX AppCompat, Material Components 3, RecyclerView, SwipeRefreshLayout, ConstraintLayout | Interfaz, listas, deslizar para recargar |
| Vista | Glide 4.16 | Descargar y mostrar las imágenes de productos |
| ViewModel | AndroidX Lifecycle (ViewModel + LiveData) 2.10 | MVVM: datos que sobreviven al girar el celular |
| Red | Retrofit 3 + Gson | Llamadas HTTP y conversión JSON ↔ objetos Java |
| Red | OkHttp 4.12 (+ logging-interceptor) | Cliente HTTP, cookies de sesión, registro en Logcat en depuración |
| Pruebas | JUnit 4.13 + MockWebServer | Pruebas unitarias y servidor HTTP falso |

### Seguridad

- La sesión es la misma de la web: cookie de sesión PHP. La app no guarda la contraseña.
- Los formularios validan con las mismas reglas y mensajes de la API (`Validador`), pero **la API vuelve a validar todo**.
- Si la sesión vence o el usuario es desactivado, la API responde 401 y la app vuelve al inicio de sesión.
- Las preferencias (con la cookie) se excluyen de los respaldos en la nube (`data_extraction_rules.xml`).
- **Solo para desarrollo:** se permite HTTP sin cifrar (`network_security_config.xml`) porque XAMPP local no tiene certificado. En producción se usaría HTTPS y se quitaría ese permiso.

---

## Ambiente de desarrollo

| Herramienta | Versión probada |
|---|---|
| Android SDK | Platform 37, Build-Tools 36 |
| Android Gradle Plugin | 9.3.3 |
| Gradle (wrapper) | 9.5.0 |
| JDK para compilar | JDK con `jlink` (el que trae Android Studio, o OpenJDK 26) |
| Backend | XAMPP con el proyecto en `C:\xampp\htdocs\ProyectoSENA` (ver README principal) |

### Compilar

**Con Android Studio:** *File → Open* → carpeta `movil/` → esperar la sincronización de Gradle → *Run*.

**Por consola** (Git Bash), desde `movil/`:
```
export JAVA_HOME="/c/Program Files/OpenJDK/jdk-26.0.1"   # o el JDK de Android Studio
./gradlew assembleDebug        # genera app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest    # pruebas unitarias
./gradlew lintDebug            # análisis estático
```
El archivo `local.properties` (ruta del SDK) es propio de cada computador y no va en Git. Android Studio lo crea solo; por consola se escribe a mano: `sdk.dir=C\:/Users/USUARIO/AppData/Local/Android/Sdk`.

### Conectar la app con la API

La URL del servidor se escribe en la pantalla de inicio de sesión (se recuerda para la próxima vez):

| Dónde corre la app | URL |
|---|---|
| Emulador de Android Studio | `http://10.0.2.2/ProyectoSENA/public/` (valor por defecto) |
| Celular físico en la misma red Wi-Fi | `http://IP-DEL-PC/ProyectoSENA/public/` (la IP sale con `ipconfig`) |

Con celular físico, el Firewall de Windows debe permitir conexiones entrantes a Apache (puerto 80).

Usuarios de prueba: los mismos de la web (`admin.demo@ejemplo.com` / `Demo-2026!`).

---

## Pruebas

**Pruebas unitarias** (`app/src/test/`, 41 pruebas, se corren con `./gradlew testDebugUnitTest`):

| Clase de prueba | Qué verifica |
|---|---|
| `FormatoTest` | Precios ($45.000), fechas (dd/mm/aaaa), códigos de pago (P007) y el buscador, igual que la web |
| `ValidadorTest` | Reglas de producto, cliente, usuario, pago y cantidad, con las mismas llaves y mensajes de la API |
| `CarritoPedidoTest` | Sumar cantidades del mismo producto, total con decimales, límite de 9999, formato de envío |
| `ErrorApiTest` | Lectura de errores generales, errores por campo (422) y respuestas que no son JSON |
| `ApiServiceTest` | Con MockWebServer: rutas y métodos HTTP, JSON enviado, lectura de decimales, cookie de sesión en las peticiones siguientes, cabecera `X-Requested-With`, eliminar usuario (204 / desactivado) |
