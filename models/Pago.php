<?php

class Pago {

    private $conexion;

    public function __construct($conexion) {
        $this->conexion = $conexion;
    }

    public function obtenerTodos() {
        $sql = "SELECT pago.*, pedido.id_pedido, pedido.saldo, cliente.nombre_c 
                FROM pago 
                JOIN pedido ON pago.id_pedido_p = pedido.id_pedido 
                JOIN cliente ON pedido.id_cliente_p = cliente.id_cliente 
                ORDER BY pago.fecha_p DESC";
        $consulta = $this->conexion->prepare($sql);
        $consulta->execute();
        return $consulta->fetchAll();
    }

    public function obtenerPorId($id) {
        $sql = "SELECT pago.*, pedido.id_pedido, pedido.saldo, pedido.total, cliente.nombre_c 
                FROM pago 
                JOIN pedido ON pago.id_pedido_p = pedido.id_pedido 
                JOIN cliente ON pedido.id_cliente_p = cliente.id_cliente 
                WHERE pago.id_pago = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        $consulta->execute();
        return $consulta->fetch();
    }

    public function obtenerPorCliente($idCliente) {
        $sql = "SELECT pago.*
                FROM pago
                JOIN pedido ON pago.id_pedido_p = pedido.id_pedido
                WHERE pedido.id_cliente_p = :idCliente
                ORDER BY pago.fecha_p DESC";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':idCliente', $idCliente, PDO::PARAM_INT);
        $consulta->execute();
        return $consulta->fetchAll();
    }

    public function obtenerPorPedido($idPedido) {
        $sql = "SELECT * FROM pago WHERE id_pedido_p = :idPedido ORDER BY fecha_p DESC";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':idPedido', $idPedido, PDO::PARAM_INT);
        $consulta->execute();
        return $consulta->fetchAll();
    }

    // Cobrado hoy: dinero que entró hoy, sumando todos los pagos registrados hoy
    public function cobradoDeHoy() {
        $sql = "SELECT COALESCE(SUM(monto), 0) AS total, COUNT(*) AS cantidad
                FROM pago
                WHERE DATE(fecha_p) = CURDATE()";
        $consulta = $this->conexion->prepare($sql);
        $consulta->execute();
        return $consulta->fetch();
    }

    public function crear($idPedido, $monto, $metodo) {
        $sql = "INSERT INTO pago (id_pedido_p, monto, metodo) VALUES (:idPedido, :monto, :metodo)";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':idPedido', $idPedido, PDO::PARAM_INT);
        $consulta->bindParam(':monto', $monto);
        $consulta->bindParam(':metodo', $metodo, PDO::PARAM_STR);
        $consulta->execute();
        return $this->conexion->lastInsertId();
    }

    public function actualizar($id, $monto, $metodo) {
        $sql = "UPDATE pago SET monto = :monto, metodo = :metodo WHERE id_pago = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        $consulta->bindParam(':monto', $monto);
        $consulta->bindParam(':metodo', $metodo, PDO::PARAM_STR);
        return $consulta->execute();
    }

    public function eliminar($id) {
        $sql = "DELETE FROM pago WHERE id_pago = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        return $consulta->execute();
    }

}