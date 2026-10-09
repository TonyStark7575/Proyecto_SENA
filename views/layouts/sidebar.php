<?php
// Módulo actual según la ruta (ej: "clientes/detalle" -> "clientes"), para resaltarlo en el menú
$moduloActivo = explode('/', $_GET['ruta'] ?? 'inicio')[0];

$itemsMenu = [
    'inicio'    => ['icono' => 'bi-grid-fill',        'texto' => 'Dashboard', 'ruta' => 'inicio'],
    'usuarios'  => ['icono' => 'bi-people-fill',      'texto' => 'Usuarios',  'ruta' => 'usuarios/lista'],
    'clientes'  => ['icono' => 'bi-person-fill',      'texto' => 'Clientes',  'ruta' => 'clientes/lista'],
    'productos' => ['icono' => 'bi-tag-fill',         'texto' => 'Productos', 'ruta' => 'productos/lista'],
    'pedidos'   => ['icono' => 'bi-cart-fill',        'texto' => 'Pedidos',   'ruta' => 'pedidos/lista'],
    'pagos'     => ['icono' => 'bi-credit-card-fill', 'texto' => 'Pagos',     'ruta' => 'pagos/lista'],
];
?>
<nav class="sidebar-menu" id="sidebarMenu">

    <?php foreach ($itemsMenu as $modulo => $item): ?>
    <a href="/ProyectoSENA/public/index.php?ruta=<?php echo $item['ruta']; ?>"
       class="sidebar-menu__item <?php echo $modulo === $moduloActivo ? 'activo' : ''; ?>">
        <i class="bi <?php echo $item['icono']; ?> sidebar-menu__icon"></i>
        <?php echo $item['texto']; ?>
    </a>
    <?php endforeach; ?>

    <form id="formLogout" method="POST" action="/ProyectoSENA/public/index.php?ruta=logout">
        <button type="submit" class="sidebar-menu__item">
            <i class="bi bi-box-arrow-right sidebar-menu__icon"></i>
            Cerrar sesión
        </button>
    </form>

</nav>
