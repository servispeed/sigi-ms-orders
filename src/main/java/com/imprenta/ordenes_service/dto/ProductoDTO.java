package com.imprenta.ordenes_service.dto;

import java.math.BigDecimal;

public class ProductoDTO {

    private String nombre;
    private String descripcion;
    private BigDecimal precioUnitario;
    private BigDecimal precioPaquete;
    private Integer cantidadPaquete;
    private String formatoTamano;
    private Integer tirajeMinimo;

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

    public String getFormatoTamano() {
        return formatoTamano;
    }

    public void setFormatoTamano(String formatoTamano) {
        this.formatoTamano = formatoTamano;
    }

    public Integer getTirajeMinimo() {
        return tirajeMinimo;
    }

    public void setTirajeMinimo(Integer tirajeMinimo) {
        this.tirajeMinimo = tirajeMinimo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
