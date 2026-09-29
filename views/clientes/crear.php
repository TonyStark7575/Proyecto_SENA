<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Crear cliente</title>

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

    <span class="users-header-bar__title">CLIENTE / CREAR NUEVO</span>

  </header>

    <?php require __DIR__ . '/../layouts/sidebar.php'; ?>


  <section class="users-content">

    <div class="form-card">
      <h3 class="form-card__title">Información del cliente</h3>

      <form class="form-fields" method="POST" action="/ProyectoSENA/public/index.php?ruta=clientes/crear">

        <div class="form-group-custom">
          <label for="nombre" class="form-group-custom__label">Nombre</label>
          <input type="text" id="nombre" name="nombre" class="form-group-custom__input">
        </div>

        <div class="form-group-custom">
          <label for="email" class="form-group-custom__label">Correo</label>
          <input type="email" id="email" name="email" class="form-group-custom__input">
        </div>

        <div class="form-group-custom">
          <label for="telefono" class="form-group-custom__label">Telefono</label>
          <input type="tel" id="telefono" name="telefono" class="form-group-custom__input">
        </div>

        <div class="form-group-custom">
          <label for="id_usuario" class="form-group-custom__label">Registrado por</label>
          <select id="id_usuario" name="id_usuario" class="form-group-custom__input">
            <option value="" disabled selected>Selecciona un usuario</option>
            <?php foreach ($usuarios as $usuario): ?>
              <option value="<?php echo $usuario['id_usuario']; ?>"><?php echo $usuario['nombre_u']; ?></option>
            <?php endforeach; ?>
          </select>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-primary-custom">Guardar</button>
          <a href="/ProyectoSENA/public/index.php?ruta=clientes/lista" class="btn-primary-custom btn-primary-custom--secondary">Cancelar</a>
        </div>

      </form>
    </div>

  </section>

</main>

    <script src="/ProyectoSENA/public/js/sidebar.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>