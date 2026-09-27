<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Pedidos</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Caveat:wght@700&display=swap" rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <link rel="stylesheet" href="/ProyectoSENA/public/css/base.css">
    <link rel="stylesheet" href="/ProyectoSENA/public/css/components.css">
    <link rel="stylesheet" href="/ProyectoSENA/public/css/pages.css">
</head>

<body>

    <main class="users-page">

  <header class="app-header">

    <div class="app-header__top">
      <button class="app-header__menu" type="button" aria-label="Abrir menú">
        <i class="bi bi-list"></i>
      </button>
      <button class="app-header__logout" type="button" aria-label="Cerrar sesión">
        <i class="bi bi-box-arrow-right"></i>
      </button>
    </div>

    <div class="users-header-bar">
      <span class="users-header-bar__title">PEDIDOS</span>
      <div class="search-bar search-bar--compact">
        <i class="bi bi-search search-bar__icon"></i>
        <input type="search" class="search-bar__input" placeholder="Buscar..." aria-label="Buscar">
      </div>
    </div>

  </header>

  <section class="users-content">

    <div class="filter-pills">
      <button type="button" class="filter-pill filter-pill--en-proceso">En proceso</button>
      <button type="button" class="filter-pill filter-pill--pendiente">Pendientes</button>
      <button type="button" class="filter-pill filter-pill--entregado">Entregados</button>
    </div>

    <?php foreach ($pedidos as $pedido): ?>
    <div class="entity-card">
      <div class="entity-card__top">
        <p class="entity-card__detail">ID: <?php echo $pedido['id_pedido']; ?></p>
        <?php
          $claseBadge = match($pedido['estado']) {
              'Entregado' => 'badge-status--entregado',
              'Pendiente' => 'badge-status--pendiente',
              'En proceso' => 'badge-status--en-proceso',
              default => 'badge-status--repartidor',
          };
        ?>
        <span class="badge-status <?php echo $claseBadge; ?>"><?php echo $pedido['estado']; ?></span>
      </div>
      <p class="entity-card__detail">Cliente: <?php echo $pedido['nombre_c']; ?></p>
      <p class="entity-card__detail">Tel: <?php echo $pedido['telefono_c']; ?></p>

      <div class="entity-card__footer">
        <div></div>
        <div class="entity-card__actions">
          <a href="/ProyectoSENA/public/index.php?ruta=pedidos/detalle&id=<?php echo $pedido['id_pedido']; ?>">
            <i class="bi bi-eye order-row__icon"></i>
          </a>
          <a href="/ProyectoSENA/public/index.php?ruta=pedidos/editar&id=<?php echo $pedido['id_pedido']; ?>">
            <i class="bi bi-pencil-square order-row__icon"></i>
          </a>
        </div>
      </div>
    </div>
    <?php endforeach; ?>

  </section>

  <a href="/ProyectoSENA/public/index.php?ruta=pedidos/crear" class="btn-primary-custom">Nuevo Pedido</a>

</main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>