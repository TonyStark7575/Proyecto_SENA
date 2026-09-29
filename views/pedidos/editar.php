<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Editar Pedido</title>

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

    <span class="users-header-bar__title">PEDIDOS/ EDITAR PEDIDO #<?php echo $pedido['id_pedido']; ?></span>

  </header>

    <?php require __DIR__ . '/../layouts/sidebar.php'; ?>


  <section class="users-content">

    <div class="form-card">
      <h3 class="form-card__title">Editar Pedido #<?php echo $pedido['id_pedido']; ?></h3>

      <form class="form-fields" method="POST" action="/ProyectoSENA/public/index.php?ruta=pedidos/editar">

        <input type="hidden" name="id" value="<?php echo $pedido['id_pedido']; ?>">

        <div class="form-group-custom">
          <label for="id_cliente" class="form-group-custom__label">Cliente</label>
          <select id="id_cliente" name="id_cliente" class="form-group-custom__input">
            <?php foreach ($clientes as $cliente): ?>
              <option value="<?php echo $cliente['id_cliente']; ?>" <?php echo $cliente['id_cliente'] == $pedido['id_cliente_p'] ? 'selected' : ''; ?>>
                <?php echo $cliente['nombre_c']; ?>
              </option>
            <?php endforeach; ?>
          </select>
        </div>

        <div class="form-group-custom">
          <label for="estado" class="form-group-custom__label">Estado del pedido</label>
          <select id="estado" name="estado" class="form-group-custom__input">
            <option value="Pendiente" <?php echo $pedido['estado'] === 'Pendiente' ? 'selected' : ''; ?>>Pendiente</option>
            <option value="En proceso" <?php echo $pedido['estado'] === 'En proceso' ? 'selected' : ''; ?>>En proceso</option>
            <option value="Entregado" <?php echo $pedido['estado'] === 'Entregado' ? 'selected' : ''; ?>>Entregado</option>
            <option value="Cancelado" <?php echo $pedido['estado'] === 'Cancelado' ? 'selected' : ''; ?>>Cancelado</option>
          </select>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-primary-custom">Guardar cambios</button>
          <a href="/ProyectoSENA/public/index.php?ruta=pedidos/detalle&id=<?php echo $pedido['id_pedido']; ?>" class="btn-primary-custom btn-primary-custom--secondary">Cancelar</a>
        </div>

      </form>
    </div>

  </section>

</main>

    <script src="/ProyectoSENA/public/js/sidebar.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>