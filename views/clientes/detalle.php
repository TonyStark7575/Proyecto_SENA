<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Detalles del cliente</title>

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

            <span class="users-header-bar__title">CLIENTE / DETALLES</span>

        </header>

        <section class="users-content">

            <div class="form-card">
                <div class="detail-header">
                    <h3 class="form-card__title">Información del Cliente</h3>
                    <a href="/ProyectoSENA/public/index.php?ruta=clientes/editar&id=<?php echo $cliente['id_cliente']; ?>">
                        <i class="bi bi-pencil-square detail-header__edit-icon"></i>
                    </a>
                </div>

                <div class="profile-data profile-data--left">
                    <p class="profile-data__row">Correo: <?php echo $cliente['email_c']; ?></p>
                    <p class="profile-data__row">Nombre: <?php echo $cliente['nombre_c']; ?></p>
                    <p class="profile-data__row">Telefono: <?php echo $cliente['telefono_c']; ?></p>
                    <p class="profile-data__row">Fecha registro: <?php echo date('d/m/Y', strtotime($cliente['fecha_registro'])); ?></p>
                    <p class="profile-data__row">Registrado por: <?php echo $usuarioRegistro['nombre_u']; ?></p>
                </div>

                <form method="POST" action="/ProyectoSENA/public/index.php?ruta=clientes/eliminar">
                    <input type="hidden" name="id" value="<?php echo $cliente['id_cliente']; ?>">
                    <button type="submit" class="btn-primary-custom btn-primary-custom--secondary" onclick="return confirm('¿Seguro que querés eliminar este cliente?');">
                        <i class="bi bi-trash"></i> Eliminar cliente
                    </button>
                </form>
            </div>

            <div class="form-card">
                <h3 class="form-card__title">Historial de Pedidos</h3>
                <p class="profile-data__row">Próximamente (módulo de Pedidos aún no construido).</p>
            </div>

            <div class="form-card">
                <h3 class="form-card__title">Historial de Pagos</h3>
                <p class="profile-data__row">Próximamente (módulo de Pagos aún no construido).</p>
            </div>

        </section>

        <div class="form-actions form-actions--center">
            <a href="/ProyectoSENA/public/index.php?ruta=clientes/lista" class="btn-primary-custom btn-primary-custom--secondary">Volver</a>
        </div>

    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>