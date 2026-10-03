document.addEventListener('DOMContentLoaded', function () {

    const botonMenu = document.querySelector('.app-header__menu');
    const menu = document.getElementById('sidebarMenu');

    if (botonMenu && menu) {
        botonMenu.addEventListener('click', function () {
            menu.classList.toggle('sidebar-menu--abierto');
        });
    }

    const botonLogout = document.querySelector('.app-header__logout');
    const formLogout = document.getElementById('formLogout');

    if (botonLogout && formLogout) {
        botonLogout.addEventListener('click', function () {
            formLogout.submit();
        });
    }

});