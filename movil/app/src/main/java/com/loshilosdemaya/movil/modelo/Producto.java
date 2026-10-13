package com.loshilosdemaya.movil.modelo;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Tabla producto. La misma clase sirve para leer la respuesta de la API
 * y para enviar el cuerpo de crear/actualizar ({ nombre_p, precio, descripcion }).
 * Gson no envía los campos en null, así que al crear solo viajan los que se llenaron.
 */
public class Producto {

    @SerializedName("id_pro")
    private Integer idPro;

    @SerializedName("nombre_p")
    private String nombreP;

    // DECIMAL(10,2): la API lo entrega como texto ("45000.00"); BigDecimal evita errores de redondeo
    @SerializedName("precio")
    private BigDecimal precio;

    @SerializedName("descripcion")
    private String descripcion;

    // Nombre del archivo en public/img/ (null si no tiene imagen: se muestra el logo)
    @SerializedName("imagen")
    private String imagen;

    @SerializedName("fecha_registro")
    private String fechaRegistro;

    public Producto() {
    }

    // Constructor para el cuerpo de crear/actualizar
    public Producto(String nombreP, BigDecimal precio, String descripcion) {
        this.nombreP = nombreP;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public Integer getIdPro() {
        return idPro;
    }

    public String getNombreP() {
        return nombreP;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setIdPro(Integer idPro) {
        this.idPro = idPro;
    }

    // Lo que muestran los menús desplegables (por ejemplo al armar un pedido)
    @Override
    public String toString() {
        return nombreP;
    }
}
