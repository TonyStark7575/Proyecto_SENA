<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Lista de productos</title>

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
      <span class="users-header-bar__title">PRODUCTOS</span>
      <div class="search-bar search-bar--compact">
        <i class="bi bi-search search-bar__icon"></i>
        <input type="search" class="search-bar__input" placeholder="Buscar..." aria-label="Buscar">
      </div>
    </div>

  </header>

  <a href="/ProyectoSENA/public/index.php?ruta=productos/crear" class="btn-primary-custom btn-primary-custom--pill">Nuevo Producto</a>

  <section class="users-content">

    <div class="products-grid">

      <?php foreach ($productos as $producto): ?>
      <div class="product-card">
        <div class="product-card__image">
          <?php if ($producto['imagen']): ?>
    <img src="/ProyectoSENA/public/img/<?php echo $producto['imagen']; ?>" alt="<?php echo $producto['nombre_p']; ?>">
<?php else: ?>
    <img src="/ProyectoSENA/public/img/Logo-no-texto.jpg" alt="<?php echo $producto['nombre_p']; ?>">
<?php endif; ?>
        </div>
        <p class="product-card__name"><?php echo $producto['nombre_p']; ?></p>
        <p class="product-card__price">$ <?php echo number_format($producto['precio'], 0, ',', '.'); ?></p>
        <div class="product-card__actions">
          <a href="/ProyectoSENA/public/index.php?ruta=productos/detalle&id=<?php echo $producto['id_pro']; ?>">
            <i class="bi bi-eye order-row__icon"></i>
          </a>

          <a href="/ProyectoSENA/public/index.php?ruta=productos/editar&id=<?php echo $producto['id_pro']; ?>">
            <i class="bi bi-pencil-square order-row__icon"></i>
          </a>
        </div>
      </div>
      <?php endforeach; ?>

    </div>

  </section>

</main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>