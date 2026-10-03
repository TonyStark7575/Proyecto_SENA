<?php

class Usuario
{

    private $conexion;

    public function __construct($conexion)
    {
        $this->conexion = $conexion;
    }

    public function obtenerTodos()
    {
        $sql = "SELECT * FROM usuario ORDER BY nombre_u ASC";
        $consulta = $this->conexion->prepare($sql);
        $consulta->execute();
        return $consulta->fetchAll();
    }

    public function obtenerPorId($id)
    {
        $sql = "SELECT * FROM usuario WHERE id_usuario = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        $consulta->execute();
        return $consulta->fetch();
    }

    
    public function obtenerPorEmail($email) {
        $sql = "SELECT * FROM usuario WHERE email_u = :email LIMIT 1";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':email', $email, PDO::PARAM_STR);
        $consulta->execute();
        return $consulta->fetch();
    }


    public function crear($nombre, $email, $telefono, $password, $rol)
    {
        $passwordHasheado = password_hash($password, PASSWORD_DEFAULT);

        $sql = "INSERT INTO usuario (nombre_u, email_u, telefono_u, password, rol) 
            VALUES (:nombre, :email, :telefono, :password, :rol)";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':nombre', $nombre, PDO::PARAM_STR);
        $consulta->bindParam(':email', $email, PDO::PARAM_STR);
        $consulta->bindParam(':telefono', $telefono, PDO::PARAM_STR);
        $consulta->bindParam(':password', $passwordHasheado, PDO::PARAM_STR);
        $consulta->bindParam(':rol', $rol, PDO::PARAM_STR);
        return $consulta->execute();
    }

    public function actualizar($id, $nombre, $email, $telefono, $rol, $password = null)
    {
        if ($password) {
            $passwordHasheado = password_hash($password, PASSWORD_DEFAULT);
            $sql = "UPDATE usuario 
                SET nombre_u = :nombre, email_u = :email, telefono_u = :telefono, rol = :rol, password = :password 
                WHERE id_usuario = :id";
            $consulta = $this->conexion->prepare($sql);
            $consulta->bindParam(':password', $passwordHasheado, PDO::PARAM_STR);
        } else {
            $sql = "UPDATE usuario 
                SET nombre_u = :nombre, email_u = :email, telefono_u = :telefono, rol = :rol 
                WHERE id_usuario = :id";
            $consulta = $this->conexion->prepare($sql);
        }

        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        $consulta->bindParam(':nombre', $nombre, PDO::PARAM_STR);
        $consulta->bindParam(':email', $email, PDO::PARAM_STR);
        $consulta->bindParam(':telefono', $telefono, PDO::PARAM_STR);
        $consulta->bindParam(':rol', $rol, PDO::PARAM_STR);
        return $consulta->execute();
    }

    public function eliminar($id)
    {
        $sql = "DELETE FROM usuario WHERE id_usuario = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $id, PDO::PARAM_INT);
        return $consulta->execute();
    }

    public function buscarPorNombre($texto)
    {
        $sql = "SELECT * FROM usuario WHERE nombre_u LIKE :texto ORDER BY nombre_u ASC";
        $consulta = $this->conexion->prepare($sql);
        $busqueda = '%' . $texto . '%';
        $consulta->bindParam(':texto', $busqueda, PDO::PARAM_STR);
        $consulta->execute();
        return $consulta->fetchAll();
    }
}
