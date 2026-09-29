document.addEventListener('DOMContentLoaded', function () {

    const botonMenu = document.querySelector('.app-header__menu');
    const menu = document.getElementById('sidebarMenu');

    if (botonMenu && menu) {
        botonMenu.addEventListener('click', function () {
            menu.classList.toggle('sidebar-menu--abierto');
        });
    }

});