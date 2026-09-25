<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Detalles del producto</title>

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

    <span class="users-header-bar__title">DETALLES / <?php echo $producto['nombre_p']; ?></span>

  </header>

  <section class="users-content">

    <div class="form-card">

      <div class="image-upload" style="width: 160px; height: 160px;">
        <?php if ($producto['imagen']): ?>
    <img src="/ProyectoSENA/public/img/<?php echo $producto['imagen']; ?>" alt="<?php echo $producto['nombre_p']; ?>">
<?php else: ?>
    <img src="/ProyectoSENA/public/img/LOGOTIPO.png" alt="<?php echo $producto['nombre_p']; ?>">
<?php endif; ?>
      </div>

      <div class="profile-data">
        <p class="profile-data__row"><strong><?php echo $producto['nombre_p']; ?></strong></p>
        <p class="profile-data__row">Precio: $ <?php echo number_format($producto['precio'], 0, ',', '.'); ?></p>
        <p class="profile-data__row"><strong>Descripción</strong></p>
        <p class="profile-data__row">
          <?php echo $producto['descripcion']; ?>
        </p>
        <p class="profile-data__row">Fecha Registro <?php echo $producto['fecha_registro']; ?></p>
      </div>

     <div class="detail-actions-row">
        <a href="/ProyectoSENA/public/index.php?ruta=productos/editar&id=<?php echo $producto['id_pro']; ?>" class="btn-primary-custom">
        <i class="bi bi-pencil-square"></i>
        </a>
        
        <form method="POST" action="/ProyectoSENA/public/index.php?ruta=productos/eliminar" style="display: inline;">
        <input type="hidden" name="id" value="<?php echo $producto['id_pro']; ?>">
        <button type="submit" class="btn-primary-custom" onclick="return confirm('¿Seguro que querés eliminar este producto?');">
            <i class="bi bi-trash"></i>
        </button>
        </form>
    </div>

    </div>

  </section>

</main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>