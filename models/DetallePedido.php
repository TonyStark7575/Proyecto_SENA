<?php

class DetallePedido {

    private $conexion;

    public function __construct($conexion) {
        $this->conexion = $conexion;
    }

    public function agregar($idPedido, $idProducto, $cantidad, $precio) {
        $subtotal = $cantidad * $precio;

        $sql = "INSERT INTO detalle_pedido (id_pedido_d, id_pro_d, cantidad, precio, subtotal) 
                VALUES (:idPedido, :idProducto, :cantidad, :precio, :subtotal)";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':idPedido', $idPedido, PDO::PARAM_INT);
        $consulta->bindParam(':idProducto', $idProducto, PDO::PARAM_INT);
        $consulta->bindParam(':cantidad', $cantidad, PDO::PARAM_INT);
        $consulta->bindParam(':precio', $precio);
        $consulta->bindParam(':subtotal', $subtotal);
        return $consulta->execute();
    }

    public function obtenerPorPedido($idPedido) {
        $sql = "SELECT detalle_pedido.*, producto.nombre_p 
                FROM detalle_pedido 
                JOIN producto ON detalle_pedido.id_pro_d = producto.id_pro 
                WHERE detalle_pedido.id_pedido_d = :idPedido";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':idPedido', $idPedido, PDO::PARAM_INT);
        $consulta->execute();
        return $consulta->fetchAll();
    }

    public function eliminar($idDetalle) {
        $sql = "DELETE FROM detalle_pedido WHERE id_detalle = :id";
        $consulta = $this->conexion->prepare($sql);
        $consulta->bindParam(':id', $idDetalle, PDO::PARAM_INT);
        return $consulta->execute();
    }

}