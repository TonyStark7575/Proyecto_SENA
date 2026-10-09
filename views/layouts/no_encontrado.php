<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Registro no encontrado</title>

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

            <span class="users-header-bar__title">REGISTRO NO ENCONTRADO</span>

        </header>

    <?php require __DIR__ . '/sidebar.php'; ?>


        <section class="users-content">

            <div class="form-card">
                <h3 class="form-card__title">
                    <i class="bi bi-exclamation-triangle-fill"></i>
                    No encontrado
                </h3>

                <div class="alert alert-warning"><?php echo $mensaje; ?></div>

                <div class="form-actions">
                    <a href="<?php echo $volver; ?>" class="btn-primary-custom">Volver a la lista</a>
                </div>
            </div>

        </section>

    </main>

    <script src="/ProyectoSENA/public/js/sidebar.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>
