<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Editar Productos</title>

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

    <span class="users-header-bar__title">PRODUCTOS/ EDITAR PRODUCTO</span>

  </header>

  <section class="users-content">

    <div class="form-card">
      <h3 class="form-card__title">Información del producto</h3>

      <form class="form-fields" method="POST" action="/ProyectoSENA/public/index.php?ruta=productos/editar" enctype="multipart/form-data">

        <input type="hidden" name="id" value="<?php echo $producto['id_pro']; ?>">
        <input type="hidden" name="imagen_actual" value="<?php echo $producto['imagen']; ?>">

        <div class="image-upload">
          <?php if ($producto['imagen']): ?>
              <img src="/ProyectoSENA/public/img/<?php echo $producto['imagen']; ?>" alt="Imagen actual">
          <?php else: ?>
              <img src="/ProyectoSENA/public/img/LOGOTIPO.png" alt="Sin imagen">
          <?php endif; ?>
        </div>

        <div class="form-group-custom">
          <label for="imagen" class="form-group-custom__label">Cambiar imagen (opcional)</label>
          <input type="file" id="imagen" name="imagen" class="form-group-custom__input" accept="image/*">
        </div>

        <div class="form-group-custom">
          <label for="nombre" class="form-group-custom__label">Nombre del producto</label>
          <input type="text" id="nombre" name="nombre" class="form-group-custom__input" value="<?php echo $producto['nombre_p']; ?>">
        </div>

        <div class="form-group-custom">
          <label for="precio" class="form-group-custom__label">Precio</label>
          <input type="number" id="precio" name="precio" class="form-group-custom__input" value="<?php echo $producto['precio']; ?>">
        </div>

        <div class="form-group-custom">
          <label for="descripcion" class="form-group-custom__label">Descripción</label>
          <textarea id="descripcion" name="descripcion" class="form-group-custom__input"><?php echo $producto['descripcion']; ?></textarea>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-primary-custom">Guardar cambios</button>
          <a href="/ProyectoSENA/public/index.php?ruta=productos/lista" class="btn-primary-custom btn-primary-custom--secondary">Cancelar</a>
        </div>

      </form>
    </div>

  </section>

</main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>