<!DOCTYPE html>
<html lang="es">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Los Hilos de Maya - Iniciar Sesión</title>

  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Caveat:wght@700&display=swap" rel="stylesheet">

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <link rel="stylesheet" href="/ProyectoSENA/public/css/base.css">
  <link rel="stylesheet" href="/ProyectoSENA/public/css/components.css">
  <link rel="stylesheet" href="/ProyectoSENA/public/css/pages.css">
</head>

<body>

    <main class="login-page">

    <header class="login-header text-center">
        <img src="/ProyectoSENA/public/img/Logo-no-texto.jpg" alt="Logo Los Hilos de Maya" class="login-header__logo">
        <h1 class="login-header__brand">Los Hilos de Maya</h1>
    </header>

    <section class="login-card text-center">
        <h2 class="login-card__title mt-5 mb-5">INICIAR SESIÓN</h2>

        <form class="login-form mb-5" method="POST" action="/ProyectoSENA/public/index.php?ruta=login">
            <div class="login-form__group">
                <input 
                    type="text"
                    id="usuario"
                    name="usuario"
                    class="login-form__input rounded"
                    placeholder="USUARIO"
                    autocomplete="username"
                    required
                >
                <i class="bi bi-person login-form__icon"></i> 

                <br><br>

                 <input 
                    type="password"
                    id="password"
                    name="password"
                    class="login-form__input rounded"
                    placeholder="CONTRASEÑA"
                    autocomplete="current-password"
                    required
                >
                <i class="bi bi-eye-slash-fill login-form__icon"></i>
            </div>

            <button type="submit" class="btn-primary-custom">Iniciar Sesión</button>
        </form>

    </section>

    </main>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>