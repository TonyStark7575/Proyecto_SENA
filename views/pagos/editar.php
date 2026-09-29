<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Editar Pago</title>

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

    <span class="users-header-bar__title">PAGOS/ EDITAR PAGO #P<?php echo str_pad($pago['id_pago'], 3, '0', STR_PAD_LEFT); ?></span>

  </header>

    <?php require __DIR__ . '/../layouts/sidebar.php'; ?>


  <section class="users-content">

    <div class="form-card">
      <h3 class="form-card__title">Editar Pago #P<?php echo str_pad($pago['id_pago'], 3, '0', STR_PAD_LEFT); ?></h3>

      <div class="profile-data profile-data--left">
        <p class="profile-data__row">Pedido: #<?php echo $pago['id_pedido']; ?></p>
        <p class="profile-data__row">Cliente: <?php echo $pago['nombre_c']; ?></p>
      </div>

      <form class="form-fields" method="POST" action="/ProyectoSENA/public/index.php?ruta=pagos/editar">

        <input type="hidden" name="id" value="<?php echo $pago['id_pago']; ?>">

        <div class="form-row">
          <div class="form-group-custom">
            <label for="monto" class="form-group-custom__label">Monto</label>
            <input type="number" id="monto" name="monto" class="form-group-custom__input" value="<?php echo $pago['monto']; ?>" step="0.01">
          </div>

          <div class="form-group-custom">
            <label for="metodo" class="form-group-custom__label">Método</label>
            <select id="metodo" name="metodo" class="form-group-custom__input">
              <option value="Efectivo" <?php echo $pago['metodo'] === 'Efectivo' ? 'selected' : ''; ?>>Efectivo</option>
              <option value="Transferencia" <?php echo $pago['metodo'] === 'Transferencia' ? 'selected' : ''; ?>>Transferencia</option>
            </select>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-primary-custom">Guardar cambios</button>
          <a href="/ProyectoSENA/public/index.php?ruta=pagos/detalle&id=<?php echo $pago['id_pago']; ?>" class="btn-primary-custom btn-primary-custom--secondary">Cancelar</a>
        </div>

      </form>
    </div>

  </section>

</main>

    <script src="/ProyectoSENA/public/js/sidebar.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>