<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Detalle del Pago</title>

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

    <span class="users-header-bar__title">PAGOS/ DETALLE #P<?php echo str_pad($pago['id_pago'], 3, '0', STR_PAD_LEFT); ?></span>

  </header>

  <section class="users-content">

    <div class="form-card">
      <h3 class="form-card__title">Detalle del Pago #P<?php echo str_pad($pago['id_pago'], 3, '0', STR_PAD_LEFT); ?></h3>

      <div class="profile-data profile-data--left">
        <p class="profile-data__row">ID de pago: #P<?php echo str_pad($pago['id_pago'], 3, '0', STR_PAD_LEFT); ?></p>
        <p class="profile-data__row">Pedido asociado: #<?php echo $pago['id_pedido']; ?></p>
      </div>

      <a href="/ProyectoSENA/public/index.php?ruta=pedidos/detalle&id=<?php echo $pago['id_pedido']; ?>" class="btn-primary-custom btn-primary-custom--secondary btn-primary-custom--sm">
        <i class="bi bi-eye"></i> Ver pedido completo
      </a>

      <div class="profile-data profile-data--left" style="margin-top: 16px;">
        <p class="profile-data__row">Cliente: <?php echo $pago['nombre_c']; ?></p>
        <p class="profile-data__row">Monto: $<?php echo number_format($pago['monto'], 0, ',', '.'); ?></p>
        <p class="profile-data__row">Método: <?php echo $pago['metodo']; ?></p>
        <p class="profile-data__row">Fecha: <?php echo date('d/m/Y', strtotime($pago['fecha_p'])); ?></p>
        <p class="profile-data__row">
          Estado del pedido:
          <?php if ($pago['saldo'] == 0): ?>
            <span class="badge-status badge-status--completo">Completo</span>
          <?php else: ?>
            <span class="badge-status badge-status--parcial">Parcial</span>
          <?php endif; ?>
        </p>
      </div>

      <div class="form-actions form-actions--end">
        <a href="/ProyectoSENA/public/index.php?ruta=pagos/editar&id=<?php echo $pago['id_pago']; ?>" class="btn-primary-custom">Editar</a>

        <form method="POST" action="/ProyectoSENA/public/index.php?ruta=pagos/eliminar" style="display: inline;">
          <input type="hidden" name="id" value="<?php echo $pago['id_pago']; ?>">
          <button type="submit" class="btn-primary-custom btn-primary-custom--secondary" onclick="return confirm('¿Eliminar este pago? El saldo del pedido se recalculará.');">
            Eliminar
          </button>
        </form>

        <a href="/ProyectoSENA/public/index.php?ruta=pagos/lista" class="btn-primary-custom btn-primary-custom--secondary">Volver</a>
      </div>

    </div>

  </section>

</main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>