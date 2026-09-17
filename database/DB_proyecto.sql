-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema los_hilos_maya
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `los_hilos_maya` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `los_hilos_maya` ;

-- -----------------------------------------------------
-- Table `los_hilos_maya`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `los_hilos_maya`.`usuario` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT,
  `nombre_u` VARCHAR(50) NOT NULL,
  `email_u` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `rol` ENUM('ADMINISTRADORA', 'REPARTIDOR') NOT NULL,
  `fecha_registro` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `email_u` (`email_u` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `los_hilos_maya`.`cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `los_hilos_maya`.`cliente` (
  `id_cliente` INT NOT NULL AUTO_INCREMENT,
  `nombre_c` VARCHAR(50) NOT NULL,
  `email_c` VARCHAR(255) NOT NULL,
  `telefono_c` VARCHAR(50) NOT NULL,
  `fecha_registro` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `id_usuario_c` INT NOT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE INDEX `email_c` (`email_c` ASC),
  INDEX `fk_cliente_usuario` (`id_usuario_c` ASC),
  CONSTRAINT `fk_cliente_usuario`
    FOREIGN KEY (`id_usuario_c`)
    REFERENCES `los_hilos_maya`.`usuario` (`id_usuario`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 17
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `los_hilos_maya`.`pedido`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `los_hilos_maya`.`pedido` (
  `id_pedido` INT NOT NULL AUTO_INCREMENT,
  `id_usuario_p` INT NOT NULL,
  `id_cliente_p` INT NOT NULL,
  `fecha_registro` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` ENUM('Entregado', 'Pendiente', 'En proceso', 'Cancelado') NOT NULL DEFAULT 'Pendiente',
  `total` DECIMAL(10,2) NOT NULL,
  `saldo` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id_pedido`),
  INDEX `fk_pedido_cliente` (`id_cliente_p` ASC),
  INDEX `fk_pedido_usuario` (`id_usuario_p` ASC),
  CONSTRAINT `fk_pedido_cliente`
    FOREIGN KEY (`id_cliente_p`)
    REFERENCES `los_hilos_maya`.`cliente` (`id_cliente`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_pedido_usuario`
    FOREIGN KEY (`id_usuario_p`)
    REFERENCES `los_hilos_maya`.`usuario` (`id_usuario`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 15
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `los_hilos_maya`.`producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `los_hilos_maya`.`producto` (
  `id_pro` INT NOT NULL AUTO_INCREMENT,
  `nombre_p` VARCHAR(50) NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  `descripcion` TEXT NULL DEFAULT NULL,
  `fecha_registro` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_pro`))
ENGINE = InnoDB
AUTO_INCREMENT = 11
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `los_hilos_maya`.`detalle_pedido`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `los_hilos_maya`.`detalle_pedido` (
  `id_detalle` INT NOT NULL AUTO_INCREMENT,
  `id_pedido_d` INT NOT NULL,
  `id_pro_d` INT NOT NULL,
  `cantidad` INT NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  `subtotal` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  INDEX `fk_detalle_pedido` (`id_pedido_d` ASC),
  INDEX `fk_detalle_producto` (`id_pro_d` ASC),
  CONSTRAINT `fk_detalle_pedido`
    FOREIGN KEY (`id_pedido_d`)
    REFERENCES `los_hilos_maya`.`pedido` (`id_pedido`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_detalle_producto`
    FOREIGN KEY (`id_pro_d`)
    REFERENCES `los_hilos_maya`.`producto` (`id_pro`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 21
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `los_hilos_maya`.`pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `los_hilos_maya`.`pago` (
  `id_pago` INT NOT NULL AUTO_INCREMENT,
  `id_pedido_p` INT NOT NULL,
  `monto` DECIMAL(10,2) NOT NULL,
  `fecha_p` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `metodo` ENUM('Efectivo', 'Transferencia') NOT NULL,
  PRIMARY KEY (`id_pago`),
  INDEX `fk_pedido` (`id_pedido_p` ASC),
  CONSTRAINT `fk_pedido`
    FOREIGN KEY (`id_pedido_p`)
    REFERENCES `los_hilos_maya`.`pedido` (`id_pedido`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 19
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

USE `los_hilos_maya`;

DELIMITER $$
USE `los_hilos_maya`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `los_hilos_maya`.`trg_pago_after_delete`
AFTER DELETE ON `los_hilos_maya`.`pago`
FOR EACH ROW
BEGIN
    UPDATE pedido
    SET saldo = total - (
        SELECT COALESCE(SUM(monto), 0)
        FROM pago
        WHERE id_pedido_p = OLD.id_pedido_p
    )
    WHERE id_pedido = OLD.id_pedido_p;
END$$

USE `los_hilos_maya`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `los_hilos_maya`.`trg_pago_after_insert`
AFTER INSERT ON `los_hilos_maya`.`pago`
FOR EACH ROW
BEGIN
    UPDATE pedido
    SET saldo = total - (
        SELECT COALESCE(SUM(monto), 0)
        FROM pago
        WHERE id_pedido_p = NEW.id_pedido_p
    )
    WHERE id_pedido = NEW.id_pedido_p;
END$$

USE `los_hilos_maya`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `los_hilos_maya`.`trg_pago_after_update`
AFTER UPDATE ON `los_hilos_maya`.`pago`
FOR EACH ROW
BEGIN
    -- Recalcular el pedido asociado al pago (valor nuevo)
    UPDATE pedido
    SET saldo = total - (
        SELECT COALESCE(SUM(monto), 0)
        FROM pago
        WHERE id_pedido_p = NEW.id_pedido_p
    )
    WHERE id_pedido = NEW.id_pedido_p;

    -- Si el pago se movió a otro pedido, recalcular también el pedido anterior
    IF OLD.id_pedido_p <> NEW.id_pedido_p THEN
        UPDATE pedido
        SET saldo = total - (
            SELECT COALESCE(SUM(monto), 0)
            FROM pago
            WHERE id_pedido_p = OLD.id_pedido_p
        )
        WHERE id_pedido = OLD.id_pedido_p;
    END IF;
END$$

-- -----------------------------------------------------
-- Triggers de detalle_pedido: mantienen actualizado
-- pedido.total (suma de subtotales) y, en cadena,
-- pedido.saldo (total - pagos), ya que el trigger de
-- pago no se dispara cuando lo que cambia es el total.
-- -----------------------------------------------------

USE `los_hilos_maya`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `los_hilos_maya`.`trg_detalle_after_insert`
AFTER INSERT ON `los_hilos_maya`.`detalle_pedido`
FOR EACH ROW
BEGIN
    UPDATE pedido
    SET total = (
            SELECT COALESCE(SUM(subtotal), 0)
            FROM detalle_pedido
            WHERE id_pedido_d = NEW.id_pedido_d
        ),
        saldo = (
            SELECT COALESCE(SUM(subtotal), 0)
            FROM detalle_pedido
            WHERE id_pedido_d = NEW.id_pedido_d
        ) - (
            SELECT COALESCE(SUM(monto), 0)
            FROM pago
            WHERE id_pedido_p = NEW.id_pedido_d
        )
    WHERE id_pedido = NEW.id_pedido_d;
END$$

USE `los_hilos_maya`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `los_hilos_maya`.`trg_detalle_after_update`
AFTER UPDATE ON `los_hilos_maya`.`detalle_pedido`
FOR EACH ROW
BEGIN
    -- Recalcular el pedido asociado (valor nuevo)
    UPDATE pedido
    SET total = (
            SELECT COALESCE(SUM(subtotal), 0)
            FROM detalle_pedido
            WHERE id_pedido_d = NEW.id_pedido_d
        ),
        saldo = (
            SELECT COALESCE(SUM(subtotal), 0)
            FROM detalle_pedido
            WHERE id_pedido_d = NEW.id_pedido_d
        ) - (
            SELECT COALESCE(SUM(monto), 0)
            FROM pago
            WHERE id_pedido_p = NEW.id_pedido_d
        )
    WHERE id_pedido = NEW.id_pedido_d;

    -- Si la línea se movió a otro pedido, recalcular también el pedido anterior
    IF OLD.id_pedido_d <> NEW.id_pedido_d THEN
        UPDATE pedido
        SET total = (
                SELECT COALESCE(SUM(subtotal), 0)
                FROM detalle_pedido
                WHERE id_pedido_d = OLD.id_pedido_d
            ),
            saldo = (
                SELECT COALESCE(SUM(subtotal), 0)
                FROM detalle_pedido
                WHERE id_pedido_d = OLD.id_pedido_d
            ) - (
                SELECT COALESCE(SUM(monto), 0)
                FROM pago
                WHERE id_pedido_p = OLD.id_pedido_d
            )
        WHERE id_pedido = OLD.id_pedido_d;
    END IF;
END$$

USE `los_hilos_maya`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `los_hilos_maya`.`trg_detalle_after_delete`
AFTER DELETE ON `los_hilos_maya`.`detalle_pedido`
FOR EACH ROW
BEGIN
    UPDATE pedido
    SET total = (
            SELECT COALESCE(SUM(subtotal), 0)
            FROM detalle_pedido
            WHERE id_pedido_d = OLD.id_pedido_d
        ),
        saldo = (
            SELECT COALESCE(SUM(subtotal), 0)
            FROM detalle_pedido
            WHERE id_pedido_d = OLD.id_pedido_d
        ) - (
            SELECT COALESCE(SUM(monto), 0)
            FROM pago
            WHERE id_pedido_p = OLD.id_pedido_d
        )
    WHERE id_pedido = OLD.id_pedido_d;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;