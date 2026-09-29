<!DOCTYPE html>
<html lang="es">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Los Hilos de Maya - Dashboard</title>

  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Caveat:wght@700&display=swap" rel="stylesheet">

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <link rel="stylesheet" href="/ProyectoSENA/public/css/base.css">
  <link rel="stylesheet" href="/ProyectoSENA/public/css/components.css">
  <link rel="stylesheet" href="/ProyectoSENA/public/css/pages.css">
</head>

<body>

  <main class="dashboard-page">

    <header class="app-header">

      <div class="app-header__top">
        <button class="app-header__menu" type="button" aria-label="Abrir menú">
          <i class="bi bi-list"></i>
        </button>

        <h1 class="app-header__title">Los Hilos de Maya</h1>

        <button class="app-header__logout" type="button" aria-label="Cerrar sesión">
          <i class="bi bi-box-arrow-right"></i>
        </button>
      </div>

      <div class="search-bar">
        <i class="bi bi-search search-bar__icon"></i>
        <input type="search" class="search-bar__input" placeholder="Buscar..." aria-label="Buscar">
      </div>

    </header>

    <?php require __DIR__ . '/layouts/sidebar.php'; ?>

    <section class="dashboard-content">

      <h2 class="dashboard-greeting">Bienvenida</h2>

      <div class="stat-row">

        <div class="stat-card">
          <p class="stat-card__label">Total Clientes</p>
          <p class="stat-card__value"><?php echo $totalClientes; ?></p>
        </div>

        <div class="stat-card">
          <p class="stat-card__label">Ventas Hoy</p>
          <p class="stat-card__value">$0</p>
        </div>

        <div class="stat-card">
          <p class="stat-card__label">Total Productos</p>
          <p class="stat-card__value"><?php echo $totalProductos; ?></p>
        </div>

      </div>

      <div class="stat-row">
        <div class="stat-card">
          <p class="stat-card__label">Pedidos Pendientes</p>
          <p class="stat-card__value"><?php echo $pedidosPendientes; ?></p>
        </div>
      </div>

      <div class="section-card">
        <h3 class="section-card__title">Pedidos Recientes</h3>

        <?php foreach ($pedidosRecientes as $pedido): ?>
        <div class="order-row">
          <div class="order-row__info">
            <span class="order-row__id">ID: <?php echo $pedido['id_pedido']; ?> | <?php echo $pedido['nombre_c']; ?></span>
          </div>

          <?php
            $claseBadge = match($pedido['estado']) {
                'Entregado' => 'badge-status--entregado',
                'Pendiente' => 'badge-status--pendiente',
                'En proceso' => 'badge-status--en-proceso',
                default => 'badge-status--repartidor',
            };
          ?>
          <span class="badge-status <?php echo $claseBadge; ?>"><?php echo $pedido['estado']; ?></span>
          <span class="order-row__code">$<?php echo number_format($pedido['total'], 0, ',', '.'); ?></span>
          <div class="order-row__actions">
            <a href="/ProyectoSENA/public/index.php?ruta=pedidos/detalle&id=<?php echo $pedido['id_pedido']; ?>">
              <i class="bi bi-eye-fill order-row__icon"></i>
            </a>
          </div>
        </div>
        <?php endforeach; ?>

      </div>

    </section>

  </main>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="/ProyectoSENA/public/js/sidebar.js"></script>

</body>

</html>