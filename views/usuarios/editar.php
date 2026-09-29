<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Editar Usuario</title>

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

    <span class="users-header-bar__title">USUARIOS / EDITAR USUARIO</span>

  </header>

    <?php require __DIR__ . '/../layouts/sidebar.php'; ?>


  <section class="users-content">

    <div class="form-card">
      <h3 class="form-card__title">Información del usuario</h3>

      <?php if (isset($error)): ?>
    <div class="alert alert-danger"><?php echo $error; ?></div>
<?php endif; ?>

      <form class="form-fields" method="POST" action="/ProyectoSENA/public/index.php?ruta=usuarios/editar">

  <input type="hidden" name="id" value="<?php echo $usuario['id_usuario']; ?>">

  <div class="form-group-custom">
    <label for="nombre" class="form-group-custom__label">Nombre</label>
    <input type="text" id="nombre" name="nombre" class="form-group-custom__input" value="<?php echo $usuario['nombre_u']; ?>">
  </div>

  <div class="form-group-custom">
    <label for="email" class="form-group-custom__label">Correo</label>
    <input type="email" id="email" name="email" class="form-group-custom__input" value="<?php echo $usuario['email_u']; ?>">
  </div>

  <div class="form-group-custom">
    <label for="telefono" class="form-group-custom__label">Teléfono</label>
    <input type="text" id="telefono" name="telefono" class="form-group-custom__input" value="<?php echo $usuario['telefono_u']; ?>">
</div>

  <div class="form-group-custom">
    <label for="password" class="form-group-custom__label">Cambiar contraseña (opcional)</label>
    <input type="password" id="password" name="password" class="form-group-custom__input" placeholder="Dejar en blanco para no cambiarla">
  </div>

  <div class="form-group-custom">
    <label for="confirmar-password" class="form-group-custom__label">Confirmar contraseña</label>
    <input type="password" id="confirmar-password" name="confirmar-password" class="form-group-custom__input">
  </div>

  <div class="form-group-custom">
    <label for="rol" class="form-group-custom__label">Rol</label>
    <select id="rol" name="rol" class="form-group-custom__input">
      <option value="ADMINISTRADORA" <?php echo $usuario['rol'] === 'ADMINISTRADORA' ? 'selected' : ''; ?>>Administradora</option>
      <option value="REPARTIDOR" <?php echo $usuario['rol'] === 'REPARTIDOR' ? 'selected' : ''; ?>>Repartidor</option>
    </select>
  </div>

  <div class="form-actions">
    <button type="submit" class="btn-primary-custom">Guardar Cambios</button>
    <a href="/ProyectoSENA/public/index.php?ruta=usuarios/lista" class="btn-primary-custom btn-primary-custom--secondary">Cancelar</a>
  </div>

</form>
    </div>

  </section>

</main>

    <script src="/ProyectoSENA/public/js/sidebar.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>