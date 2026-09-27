<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Registrar Pago</title>

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

    <span class="users-header-bar__title">PAGOS/ REGISTRAR NUEVO</span>

  </header>

  <section class="users-content">

    <div class="form-card">
      <h3 class="form-card__title">Registrar Nuevo Pago</h3>

      <form class="form-fields" method="POST" action="/ProyectoSENA/public/index.php?ruta=pagos/crear">

        <div class="form-group-custom">
          <label for="id_pedido" class="form-group-custom__label">Pedido</label>
          <select id="id_pedido" name="id_pedido" class="form-group-custom__input">
            <option value="" disabled selected>Selecciona un pedido</option>
            <?php foreach ($pedidos as $pedido): ?>
              <option value="<?php echo $pedido['id_pedido']; ?>">
                #<?php echo $pedido['id_pedido']; ?> - <?php echo $pedido['nombre_c']; ?> (Saldo: $<?php echo number_format($pedido['saldo'], 0, ',', '.'); ?>)
              </option>
            <?php endforeach; ?>
          </select>
        </div>

        <div class="form-row">
          <div class="form-group-custom">
            <label for="monto" class="form-group-custom__label">Monto</label>
            <input type="number" id="monto" name="monto" class="form-group-custom__input" placeholder="0" step="0.01">
          </div>

          <div class="form-group-custom">
            <label for="metodo" class="form-group-custom__label">Método</label>
            <select id="metodo" name="metodo" class="form-group-custom__input">
              <option value="Efectivo" selected>Efectivo</option>
              <option value="Transferencia">Transferencia</option>
            </select>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-primary-custom">Guardar pago</button>
          <a href="/ProyectoSENA/public/index.php?ruta=pagos/lista" class="btn-primary-custom btn-primary-custom--secondary">Cancelar</a>
        </div>

      </form>
    </div>

  </section>

</main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>