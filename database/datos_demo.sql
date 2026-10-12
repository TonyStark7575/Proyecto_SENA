-- ===========================================================
-- Los Hilos de Maya - DATOS DE DEMOSTRACIÓN
--
-- Cómo usarlo: importar DESPUÉS de DB_proyecto.sql, sobre la base recién creada (vacía).
--   phpMyAdmin: seleccionar la base los_hilos_maya > Importar > este archivo.
--   Consola:    mysql -u root -p --default-character-set=utf8mb4 los_hilos_maya < database/datos_demo.sql
--
-- Usuarios de demo (todos con la contraseña  Demo-2026! ):
--   admin.demo@ejemplo.com           Administradora
--   carlos.repartidor@ejemplo.com    Repartidor
--   diana.repartidora@ejemplo.com    Repartidor (INACTIVO: sirve para mostrar el bloqueo de acceso)
--
-- Los correos usan el dominio ejemplo.com, reservado para pruebas: no son de personas reales.
-- Las fechas son relativas a "hoy" (NOW()), así el dashboard siempre muestra ventas del día.
-- Los totales y saldos de los pedidos los calculan los triggers al insertar líneas y pagos.
-- ===========================================================

SET NAMES utf8mb4;

START TRANSACTION;

-- ----------------------------- USUARIOS -----------------------------
-- Hash de "Demo-2026!" generado con password_hash() de PHP (el mismo que usa la app)
INSERT INTO usuario (nombre_u, email_u, telefono_u, password, rol, activo, fecha_registro) VALUES
('Maya Administradora', 'admin.demo@ejemplo.com', '3001112233', '$2y$10$5mJixhT5kEY8dttxD0utfuySetGgXAFm2t7PY/PNyir3CDndc0cmK', 'ADMINISTRADORA', 1, NOW() - INTERVAL 60 DAY);
SET @u_admin = LAST_INSERT_ID();

INSERT INTO usuario (nombre_u, email_u, telefono_u, password, rol, activo, fecha_registro) VALUES
('Carlos Repartidor', 'carlos.repartidor@ejemplo.com', '3104445566', '$2y$10$5mJixhT5kEY8dttxD0utfuySetGgXAFm2t7PY/PNyir3CDndc0cmK', 'REPARTIDOR', 1, NOW() - INTERVAL 45 DAY);
SET @u_carlos = LAST_INSERT_ID();

INSERT INTO usuario (nombre_u, email_u, telefono_u, password, rol, activo, fecha_registro) VALUES
('Diana Repartidora', 'diana.repartidora@ejemplo.com', '3157778899', '$2y$10$5mJixhT5kEY8dttxD0utfuySetGgXAFm2t7PY/PNyir3CDndc0cmK', 'REPARTIDOR', 0, NOW() - INTERVAL 40 DAY);

-- ----------------------------- CLIENTES -----------------------------
INSERT INTO cliente (nombre_c, email_c, telefono_c, id_usuario_c, fecha_registro) VALUES ('Laura Gómez', 'laura.gomez@ejemplo.com', '3012345678', @u_admin, NOW() - INTERVAL 30 DAY);
SET @c_laura = LAST_INSERT_ID();
INSERT INTO cliente (nombre_c, email_c, telefono_c, id_usuario_c, fecha_registro) VALUES ('Andrés Pérez', 'andres.perez@ejemplo.com', '3023456789', @u_admin, NOW() - INTERVAL 25 DAY);
SET @c_andres = LAST_INSERT_ID();
INSERT INTO cliente (nombre_c, email_c, telefono_c, id_usuario_c, fecha_registro) VALUES ('Camila Rodríguez', 'camila.rodriguez@ejemplo.com', '3134567890', @u_carlos, NOW() - INTERVAL 20 DAY);
SET @c_camila = LAST_INSERT_ID();
INSERT INTO cliente (nombre_c, email_c, telefono_c, id_usuario_c, fecha_registro) VALUES ('Jorge Martínez', 'jorge.martinez@ejemplo.com', '3145678901', @u_carlos, NOW() - INTERVAL 15 DAY);
SET @c_jorge = LAST_INSERT_ID();
INSERT INTO cliente (nombre_c, email_c, telefono_c, id_usuario_c, fecha_registro) VALUES ('Valentina Torres', 'valentina.torres@ejemplo.com', '3206789012', @u_admin, NOW() - INTERVAL 10 DAY);
SET @c_valentina = LAST_INSERT_ID();
INSERT INTO cliente (nombre_c, email_c, telefono_c, id_usuario_c, fecha_registro) VALUES ('Sofía Ramírez', 'sofia.ramirez@ejemplo.com', '3217890123', @u_admin, NOW() - INTERVAL 5 DAY);
SET @c_sofia = LAST_INSERT_ID();

-- ----------------------------- PRODUCTOS -----------------------------
-- Sin imagen: la app muestra el logo. Las fotos se suben desde la pantalla de Productos.
INSERT INTO producto (nombre_p, precio, descripcion, fecha_registro) VALUES ('Gorro tejido de lana', 45000, 'Gorro tejido a mano en lana gruesa, talla única.', NOW() - INTERVAL 50 DAY);
SET @p_gorro = LAST_INSERT_ID();
INSERT INTO producto (nombre_p, precio, descripcion, fecha_registro) VALUES ('Bufanda tejida', 60000, 'Bufanda larga en punto trenzado, varios colores.', NOW() - INTERVAL 50 DAY);
SET @p_bufanda = LAST_INSERT_ID();
INSERT INTO producto (nombre_p, precio, descripcion, fecha_registro) VALUES ('Amigurumi conejo', 35000, 'Muñeco de conejo tejido a crochet, 20 cm.', NOW() - INTERVAL 48 DAY);
SET @p_conejo = LAST_INSERT_ID();
INSERT INTO producto (nombre_p, precio, descripcion, fecha_registro) VALUES ('Mantita para bebé', 90000, 'Manta suave de 80 x 80 cm en hilo antialérgico.', NOW() - INTERVAL 45 DAY);
SET @p_mantita = LAST_INSERT_ID();
INSERT INTO producto (nombre_p, precio, descripcion, fecha_registro) VALUES ('Bolso tejido a crochet', 75000, 'Bolso de mano con forro interior y cierre.', NOW() - INTERVAL 40 DAY);
SET @p_bolso = LAST_INSERT_ID();
INSERT INTO producto (nombre_p, precio, descripcion, fecha_registro) VALUES ('Guantes de lana', 30000, 'Par de guantes tejidos, tallas S, M y L.', NOW() - INTERVAL 35 DAY);
SET @p_guantes = LAST_INSERT_ID();
INSERT INTO producto (nombre_p, precio, descripcion, fecha_registro) VALUES ('Llavero amigurumi', 12000, 'Llavero pequeño en forma de animalito.', NOW() - INTERVAL 30 DAY);
SET @p_llavero = LAST_INSERT_ID();
INSERT INTO producto (nombre_p, precio, descripcion, fecha_registro) VALUES ('Cojín tejido', 55000, 'Funda de cojín de 40 x 40 cm con relleno.', NOW() - INTERVAL 20 DAY);
SET @p_cojin = LAST_INSERT_ID();

-- ----------------------------- PEDIDOS, LÍNEAS Y PAGOS -----------------------------
-- Cada línea guarda el precio del producto en ese momento; el subtotal es cantidad x precio.

-- Pedido 1: Entregado y pagado completo en dos pagos (150.000)
INSERT INTO pedido (id_cliente_p, id_usuario_p, estado, total, saldo, fecha_registro) VALUES (@c_laura, @u_admin, 'Entregado', 0, 0, NOW() - INTERVAL 12 DAY);
SET @ped = LAST_INSERT_ID();
INSERT INTO detalle_pedido (id_pedido_d, id_pro_d, cantidad, precio, subtotal) VALUES (@ped, @p_gorro, 2, 45000, 90000), (@ped, @p_bufanda, 1, 60000, 60000);
INSERT INTO pago (id_pedido_p, monto, metodo, fecha_p) VALUES (@ped, 100000, 'Transferencia', NOW() - INTERVAL 12 DAY), (@ped, 50000, 'Efectivo', NOW() - INTERVAL 10 DAY);

-- Pedido 2: Entregado y pagado en un solo pago (90.000)
INSERT INTO pedido (id_cliente_p, id_usuario_p, estado, total, saldo, fecha_registro) VALUES (@c_andres, @u_admin, 'Entregado', 0, 0, NOW() - INTERVAL 6 DAY);
SET @ped = LAST_INSERT_ID();
INSERT INTO detalle_pedido (id_pedido_d, id_pro_d, cantidad, precio, subtotal) VALUES (@ped, @p_mantita, 1, 90000, 90000);
INSERT INTO pago (id_pedido_p, monto, metodo, fecha_p) VALUES (@ped, 90000, 'Efectivo', NOW() - INTERVAL 6 DAY);

-- Pedido 3: En proceso, con abono parcial (129.000, pagado 50.000)
INSERT INTO pedido (id_cliente_p, id_usuario_p, estado, total, saldo, fecha_registro) VALUES (@c_camila, @u_carlos, 'En proceso', 0, 0, NOW() - INTERVAL 3 DAY);
SET @ped = LAST_INSERT_ID();
INSERT INTO detalle_pedido (id_pedido_d, id_pro_d, cantidad, precio, subtotal) VALUES (@ped, @p_conejo, 3, 35000, 105000), (@ped, @p_llavero, 2, 12000, 24000);
INSERT INTO pago (id_pedido_p, monto, metodo, fecha_p) VALUES (@ped, 50000, 'Transferencia', NOW() - INTERVAL 3 DAY);

-- Pedido 4: Pendiente, sin pagos (75.000)
INSERT INTO pedido (id_cliente_p, id_usuario_p, estado, total, saldo, fecha_registro) VALUES (@c_jorge, @u_carlos, 'Pendiente', 0, 0, NOW() - INTERVAL 2 DAY);
SET @ped = LAST_INSERT_ID();
INSERT INTO detalle_pedido (id_pedido_d, id_pro_d, cantidad, precio, subtotal) VALUES (@ped, @p_bolso, 1, 75000, 75000);

-- Pedido 5: Cancelado, sin pagos (no cuenta en las ventas)
INSERT INTO pedido (id_cliente_p, id_usuario_p, estado, total, saldo, fecha_registro) VALUES (@c_valentina, @u_admin, 'Cancelado', 0, 0, NOW() - INTERVAL 1 DAY);
SET @ped = LAST_INSERT_ID();
INSERT INTO detalle_pedido (id_pedido_d, id_pro_d, cantidad, precio, subtotal) VALUES (@ped, @p_cojin, 1, 55000, 55000);

-- Pedido 6: de hoy, Pendiente, con abono (105.000, pagado 20.000)
INSERT INTO pedido (id_cliente_p, id_usuario_p, estado, total, saldo, fecha_registro) VALUES (@c_laura, @u_admin, 'Pendiente', 0, 0, NOW());
SET @ped = LAST_INSERT_ID();
INSERT INTO detalle_pedido (id_pedido_d, id_pro_d, cantidad, precio, subtotal) VALUES (@ped, @p_guantes, 2, 30000, 60000), (@ped, @p_gorro, 1, 45000, 45000);
INSERT INTO pago (id_pedido_p, monto, metodo, fecha_p) VALUES (@ped, 20000, 'Efectivo', NOW());

-- Pedido 7: de hoy, En proceso, sin pagos (115.000)
INSERT INTO pedido (id_cliente_p, id_usuario_p, estado, total, saldo, fecha_registro) VALUES (@c_sofia, @u_carlos, 'En proceso', 0, 0, NOW());
SET @ped = LAST_INSERT_ID();
INSERT INTO detalle_pedido (id_pedido_d, id_pro_d, cantidad, precio, subtotal) VALUES (@ped, @p_bufanda, 1, 60000, 60000), (@ped, @p_cojin, 1, 55000, 55000);

COMMIT;

-- Resultado esperado en el dashboard:
--   Total Clientes 6 · Total Productos 8 · Pedidos Pendientes 2
--   Ventas Hoy $220.000 (pedidos 6 y 7; el cancelado no cuenta)
