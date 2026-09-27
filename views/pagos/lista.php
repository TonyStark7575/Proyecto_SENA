<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Pagos</title>

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

    <span class="users-header-bar__title">PAGOS</span>

  </header>

  <section class="users-content">

    <?php foreach ($pagos as $pago): ?>
    <div class="entity-card">
      <div class="entity-card__top">
        <h3 class="entity-card__name">P<?php echo str_pad($pago['id_pago'], 3, '0', STR_PAD_LEFT); ?></h3>
        <span class="entity-card__date">Pedido #<?php echo $pago['id_pedido']; ?></span>
      </div>
      <p class="entity-card__detail">Cliente: <?php echo $pago['nombre_c']; ?></p>
      <p class="entity-card__detail">Método: <?php echo $pago['metodo']; ?></p>
      <p class="entity-card__detail">
        Monto: $<?php echo number_format($pago['monto'], 0, ',', '.'); ?> &nbsp;·&nbsp;
        <?php if ($pago['saldo'] == 0): ?>
          <span class="badge-status badge-status--completo">Completo</span>
        <?php else: ?>
          <span class="badge-status badge-status--parcial">Parcial</span>
        <?php endif; ?>
      </p>

      <div class="entity-card__footer entity-card__footer--divided">
        <div></div>
        <div class="entity-card__actions">
          <a href="/ProyectoSENA/public/index.php?ruta=pagos/detalle&id=<?php echo $pago['id_pago']; ?>">
            <i class="bi bi-eye order-row__icon"></i>
          </a>
          <a href="/ProyectoSENA/public/index.php?ruta=pagos/editar&id=<?php echo $pago['id_pago']; ?>">
            <i class="bi bi-pencil-square order-row__icon"></i>
          </a>
        </div>
      </div>
    </div>
    <?php endforeach; ?>

  </section>

  <a href="/ProyectoSENA/public/index.php?ruta=pagos/crear" class="btn-primary-custom">Registrar pago</a>

</main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>