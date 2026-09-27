<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Los Hilos de Maya - Listado de Clientes</title>

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
                <span class="users-header-bar__title">CLIENTES</span>

                <div class="search-bar search-bar--compact">
                    <i class="bi bi-search search-bar__icon"></i>
                    <input type="search" class="search-bar__input" placeholder="Buscar..." aria-label="Buscar">
                </div>
            </div>

        </header>

        <section class="users-content">

            <?php foreach ($clientes as $cliente): ?>
            <div class="entity-card">
                <div class="entity-card__top">
                    <h3 class="entity-card__name"><?php echo $cliente['nombre_c']; ?></h3>
                    <span class="entity-card__date"><?php echo date('d/m/Y', strtotime($cliente['fecha_registro'])); ?></span>
                </div>

                <p class="entity-card__detail"><?php echo $cliente['telefono_c']; ?></p>
                <a href="mailto:<?php echo $cliente['email_c']; ?>" class="entity-card__email"><?php echo $cliente['email_c']; ?></a>

                <div class="entity-card__footer entity-card__footer--divided">
                    <div></div>
                    <div class="entity-card__actions">
                        <a href="/ProyectoSENA/public/index.php?ruta=clientes/detalle&id=<?php echo $cliente['id_cliente']; ?>">
                            <i class="bi bi-eye order-row__icon"></i>
                        </a>
                        <a href="/ProyectoSENA/public/index.php?ruta=clientes/editar&id=<?php echo $cliente['id_cliente']; ?>">
                            <i class="bi bi-pencil-square order-row__icon"></i>
                        </a>
                    </div>
                </div>
            </div>
            <?php endforeach; ?>

        </section>

        <a href="/ProyectoSENA/public/index.php?ruta=clientes/crear" class="btn-primary-custom">Nuevo Cliente</a>

    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>