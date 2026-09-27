<?php

class Pedido {

    private $conexion;

    public function __construct($conexion) {
        $this->conexion = $conexion;
    }

    public function obtenerTodos() {
        $sql = "SELECT pedido.*, cliente.nombre_c, cliente.telefono_c 
                FROM pedido 
                JOIN cliente ON pedido.id_cliente_p = cliente.id_cliente 
                ORDER BY pedido.fecha_registro DESC";
        $consulta = $this->conexion->prepare($sql);
        $consulta->execute();
        return $consulta->fetchAll();
    }

    public function obtenerPorId($id) {
        $sql = "SELECT pedido.*, cliente.nombre_c 
                FROM pedido 
                JOIN cliente ON pedido.id_cliente_p = cliente.id_cliente 
                WHERE pedido.id_pedido = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        $consulta->execute();
        return $consulta->fetch();
    }

    public function crear($idCliente, $idUsuario, $estado) {
        $sql = "INSERT INTO pedido (id_cliente_p, id_usuario_p, estado, total, saldo) 
                VALUES (:idCliente, :idUsuario, :estado, 0, 0)";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':idCliente', $idCliente, PDO::PARAM_INT);
        $consulta->bindParam(':idUsuario', $idUsuario, PDO::PARAM_INT);
        $consulta->bindParam(':estado', $estado, PDO::PARAM_STR);
        $consulta->execute();
        return $this->conexion->lastInsertId();
    }

    public function actualizar($id, $idCliente, $estado) {
        $sql = "UPDATE pedido SET id_cliente_p = :idCliente, estado = :estado WHERE id_pedido = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        $consulta->bindParam(':idCliente', $idCliente, PDO::PARAM_INT);
        $consulta->bindParam(':estado', $estado, PDO::PARAM_STR);
        return $consulta->execute();
    }

    public function eliminar($id) {
        $sql = "DELETE FROM pedido WHERE id_pedido = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        return $consulta->execute();
    }

}