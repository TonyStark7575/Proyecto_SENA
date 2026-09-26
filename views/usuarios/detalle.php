<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Detalles Usuario</title>

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

            <span class="users-header-bar__title">USUARIOS / DETALLES</span>

        </header>

        <section class="users-content">

            <div class="form-card">
                <div class="detail-header text-center">
                    <h3 class="form-card__title">Perfil del Usuario</h3>
                </div>

                <div class="profile-avatar">
                    <i class="bi bi-person-fill"></i>
                </div>

                <div class="profile-data ">
                    <p class="profile-data__row"><strong>Nombre:</strong> <?php echo $usuario['nombre_u']; ?></p>
                    <p class="profile-data__row"><strong>Correo:</strong> <?php echo $usuario['email_u']; ?></p>
                    <p class="profile-data__row"><strong>Teléfono:</strong> <?php echo $usuario['telefono_u']; ?></p>
                    <p class="profile-data__row">
                        <strong>Rol:</strong>
                        <?php echo $usuario['rol'] === 'ADMINISTRADORA' ? 'Administradora' : 'Repartidor'; ?>
                    </p>
                    <p class="profile-data__row">
                        <strong>Fecha de registro:</strong>
                        <?php echo date('d/m/Y', strtotime($usuario['fecha_registro'])); ?>
                    </p>
                </div>

                <div class="detail-actions-row">
                    <a href="/ProyectoSENA/public/index.php?ruta=usuarios/editar&id=<?php echo $usuario['id_usuario']; ?>" class="btn-primary-custom">
                        <i class="bi bi-pencil-square"></i>
                    </a>

                    <form method="POST" action="/ProyectoSENA/public/index.php?ruta=usuarios/eliminar" style="display: inline;">
                        <input type="hidden" name="id" value="<?php echo $usuario['id_usuario']; ?>">
                        <button type="submit" class="btn-primary-custom" onclick="return confirm('¿Seguro que querés eliminar este usuario?');">
                            <i class="bi bi-trash"></i>
                        </button>
                    </form>
                </div>
            </div>

            <div class="form-actions form-actions--center">
                <a href="/ProyectoSENA/public/index.php?ruta=usuarios/lista" class="btn-primary-custom btn-primary-custom--secondary">Volver</a>
            </div>

        </section>

    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>