package com.imprenta.ordenes_service.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "precio_paquete", precision = 10, scale = 2)
    private BigDecimal precioPaquete;

    @Column(name = "cantidad_paquete")
    private Integer cantidadPaquete;

    @Column(name = "tiempo_produccion_dias")
    private Integer tiempoProduccionDias;

    @Column(name = "formato_tamano", length = 50)
    private String formatoTamano;

    @Column(name = "unidad_venta", length = 50)
    private String unidadVenta;

    @Column(name = "tiraje_minimo")
    private Integer tirajeMinimo;

    @Column(name = "volumen_descuento_cantidad")
    private Integer volumenDescuentoCantidad;

    public Producto() {
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getPrecioPaquete() {
        return precioPaquete;
    }

    public void setPrecioPaquete(BigDecimal precioPaquete) {
        this.precioPaquete = precioPaquete;
    }

    public Integer getCantidadPaquete() {
        return cantidadPaquete;
    }

    public void setCantidadPaquete(Integer cantidadPaquete) {
        this.cantidadPaquete = cantidadPaquete;
    }

    public Integer getTiempoProduccionDias() {
        return tiempoProduccionDias;
    }

    public void setTiempoProduccionDias(Integer tiempoProduccionDias) {
        this.tiempoProduccionDias = tiempoProduccionDias;
    }

    public String getFormatoTamano() {
        return formatoTamano;
    }

    public void setFormatoTamano(String formatoTamano) {
        this.formatoTamano = formatoTamano;
    }

    public String getUnidadVenta() {
        return unidadVenta;
    }

    public void setUnidadVenta(String unidadVenta) {
        this.unidadVenta = unidadVenta;
    }

    public Integer getTirajeMinimo() {
        return tirajeMinimo;
    }

    public void setTirajeMinimo(Integer tirajeMinimo) {
        this.tirajeMinimo = tirajeMinimo;
    }

    public Integer getVolumenDescuentoCantidad() {
        return volumenDescuentoCantidad;
    }

    public void setVolumenDescuentoCantidad(Integer volumenDescuentoCantidad) {
        this.volumenDescuentoCantidad = volumenDescuentoCantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}