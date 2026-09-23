<?php

class Producto {

    private $conexion;

    public function __construct($conexion) {
        $this->conexion = $conexion;
    }

    public function obtenerTodos() {
        $sql = "SELECT * FROM producto ORDER BY nombre_p ASC";
        $consulta = $this->conexion->prepare($sql);
        $consulta->execute();
        return $consulta->fetchAll();
    }

    public function obtenerPorId($id) {
        $sql = "SELECT * FROM producto WHERE id_pro = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        $consulta->execute();
        return $consulta->fetch();
    }

    public function crear($nombre, $precio, $descripcion) {
        $sql = "INSERT INTO producto (nombre_p, precio, descripcion) 
                VALUES (:nombre, :precio, :descripcion)";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':nombre', $nombre, PDO::PARAM_STR);
        $consulta->bindParam(':precio', $precio);
        $consulta->bindParam(':descripcion', $descripcion, PDO::PARAM_STR);
        return $consulta->execute();
    }

    public function actualizar($id, $nombre, $precio, $descripcion) {
        $sql = "UPDATE producto 
                SET nombre_p = :nombre, precio = :precio, descripcion = :descripcion 
                WHERE id_pro = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        $consulta->bindParam(':nombre', $nombre, PDO::PARAM_STR);
        $consulta->bindParam(':precio', $precio);
        $consulta->bindParam(':descripcion', $descripcion, PDO::PARAM_STR);
        return $consulta->execute();
    }

    public function eliminar($id) {
        $sql = "DELETE FROM producto WHERE id_pro = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        return $consulta->execute();
    }

    public function buscarPorNombre($texto) {
        $sql = "SELECT * FROM producto WHERE nombre_p LIKE :texto ORDER BY nombre_p ASC";
        $consulta = $this->conexion->prepare($sql);
        $busqueda = '%' . $texto . '%';
        $consulta->bindParam(':texto', $busqueda, PDO::PARAM_STR);
        $consulta->execute();
        return $consulta->fetchAll();
    }

}