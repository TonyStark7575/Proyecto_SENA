<?php

class Cliente {

    private $conexion;

    public function __construct($conexion) {
        $this->conexion = $conexion;
    }

    public function obtenerTodos() {
        $sql = "SELECT * FROM cliente ORDER BY nombre_c ASC";
        $consulta = $this->conexion->prepare($sql);
        $consulta->execute();
        return $consulta->fetchAll();
    }

    public function obtenerPorId($id) {
        $sql = "SELECT * FROM cliente WHERE id_cliente = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        $consulta->execute();
        return $consulta->fetch();
    }

    public function crear($nombre, $email, $telefono, $idUsuario) {
        $sql = "INSERT INTO cliente (nombre_c, email_c, telefono_c, id_usuario_c) 
                VALUES (:nombre, :email, :telefono, :idUsuario)";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':nombre', $nombre, PDO::PARAM_STR);
        $consulta->bindParam(':email', $email, PDO::PARAM_STR);
        $consulta->bindParam(':telefono', $telefono, PDO::PARAM_STR);
        $consulta->bindParam(':idUsuario', $idUsuario, PDO::PARAM_INT);
        return $consulta->execute();
    }

    public function actualizar($id, $nombre, $email, $telefono, $idUsuario) {
        $sql = "UPDATE cliente 
                SET nombre_c = :nombre, email_c = :email, telefono_c = :telefono, id_usuario_c = :idUsuario 
                WHERE id_cliente = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        $consulta->bindParam(':nombre', $nombre, PDO::PARAM_STR);
        $consulta->bindParam(':email', $email, PDO::PARAM_STR);
        $consulta->bindParam(':telefono', $telefono, PDO::PARAM_STR);
        $consulta->bindParam(':idUsuario', $idUsuario, PDO::PARAM_INT);
        return $consulta->execute();
    }

    public function eliminar($id) {
        $sql = "DELETE FROM cliente WHERE id_cliente = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        return $consulta->execute();
    }

    public function buscarPorNombre($texto) {
        $sql = "SELECT * FROM cliente WHERE nombre_c LIKE :texto ORDER BY nombre_c ASC";
        $consulta = $this->conexion->prepare($sql);
        $busqueda = '%' . $texto . '%';
        $consulta->bindParam(':texto', $busqueda, PDO::PARAM_STR);
        $consulta->execute();
        return $consulta->fetchAll();
    }

}