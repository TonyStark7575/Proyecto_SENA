<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Detalle del Pedido</title>

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

    <span class="users-header-bar__title">PEDIDOS/ DETALLE #<?php echo $pedido['id_pedido']; ?></span>

  </header>

  <section class="users-content">

    <div class="form-card">
      <h3 class="form-card__title">Detalle del Pedido #<?php echo $pedido['id_pedido']; ?></h3>

      <div class="profile-data profile-data--left">
        <p class="profile-data__row">ID del pedido: #<?php echo $pedido['id_pedido']; ?></p>
        <p class="profile-data__row">Fecha: <?php echo date('d/m/Y', strtotime($pedido['fecha_registro'])); ?></p>
        <p class="profile-data__row">Cliente: <?php echo $pedido['nombre_c']; ?></p>
        <p class="profile-data__row">
          Estado: 
          <?php
            $claseBadge = match($pedido['estado']) {
                'Entregado' => 'badge-status--entregado',
                'Pendiente' => 'badge-status--pendiente',
                'En proceso' => 'badge-status--en-proceso',
                default => 'badge-status--repartidor',
            };
          ?>
          <span class="badge-status <?php echo $claseBadge; ?>"><?php echo $pedido['estado']; ?></span>
        </p>
      </div>

      <div class="subsection">
        <h4 class="subsection__title">Detalle de productos</h4>

        <?php foreach ($lineas as $linea): ?>
        <div class="order-row">
          <div>
            <p class="order-row__id"><strong><?php echo $linea['nombre_p']; ?></strong></p>
            <p class="order-row__code">Cantidad: <?php echo $linea['cantidad']; ?> · $<?php echo number_format($linea['precio'], 0, ',', '.'); ?> c/u</p>
          </div>
          <div class="order-row__actions">
            <span class="order-row__code"><strong>$<?php echo number_format($linea['subtotal'], 0, ',', '.'); ?></strong></span>
            <form method="POST" action="/ProyectoSENA/public/index.php?ruta=pedidos/eliminarLinea" style="display: inline;">
              <input type="hidden" name="id_detalle" value="<?php echo $linea['id_detalle']; ?>">
              <input type="hidden" name="id_pedido" value="<?php echo $pedido['id_pedido']; ?>">
              <button type="submit" style="background:none; border:none; padding:0;" onclick="return confirm('¿Quitar este producto del pedido?');">
                <i class="bi bi-trash order-row__icon order-row__icon--delete"></i>
              </button>
            </form>
          </div>
        </div>
        <?php endforeach; ?>

        <div class="order-row">
          <span class="order-row__id"><strong>Total</strong></span>
          <span class="order-row__code"><strong>$<?php echo number_format($pedido['total'], 0, ',', '.'); ?></strong></span>
        </div>

        <div class="order-row">
          <span class="order-row__id">Saldo pendiente</span>
          <span class="order-row__code">$<?php echo number_format($pedido['saldo'], 0, ',', '.'); ?></span>
        </div>
      </div>

      <div class="subsection">
        <h4 class="subsection__title">Agregar producto a este pedido</h4>

        <form method="POST" action="/ProyectoSENA/public/index.php?ruta=pedidos/agregarProducto">
          <input type="hidden" name="id_pedido" value="<?php echo $pedido['id_pedido']; ?>">

          <div class="form-row">
            <div class="form-group-custom">
              <label for="id_producto" class="form-group-custom__label">Producto</label>
              <select id="id_producto" name="id_producto" class="form-group-custom__input">
                <option value="" disabled selected>Selecciona un producto</option>
                <?php foreach ($productos as $producto): ?>
                  <option value="<?php echo $producto['id_pro']; ?>">
                    <?php echo $producto['nombre_p']; ?> — $<?php echo number_format($producto['precio'], 0, ',', '.'); ?>
                  </option>
                <?php endforeach; ?>
              </select>
            </div>

            <div class="form-group-custom">
              <label for="cantidad" class="form-group-custom__label">Cantidad</label>
              <input type="number" id="cantidad" name="cantidad" class="form-group-custom__input" value="1" min="1">
            </div>
          </div>

          <button type="submit" class="btn-primary-custom btn-primary-custom--secondary btn-primary-custom--sm">
            + Agregar producto
          </button>
        </form>
      </div>

      <div class="subsection">
        <h4 class="subsection__title">Pagos registrados para este pedido</h4>
        <p class="profile-data__row">Próximamente (módulo de Pagos aún no construido).</p>
      </div>

      <div class="form-actions form-actions--end">
        <a href="/ProyectoSENA/public/index.php?ruta=pedidos/editar&id=<?php echo $pedido['id_pedido']; ?>" class="btn-primary-custom">Editar</a>
        <a href="/ProyectoSENA/public/index.php?ruta=pagos/crear" class="btn-primary-custom btn-primary-custom--secondary">Registrar pago</a>
        <a href="/ProyectoSENA/public/index.php?ruta=pedidos/lista" class="btn-primary-custom btn-primary-custom--secondary">Volver</a>
      </div>

    </div>

  </section>

</main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>