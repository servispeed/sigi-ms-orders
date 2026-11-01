package com.imprenta.ordenes_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Trazabilidad")
public class Trazabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trazabilidad")
    private Integer idTrazabilidad;

    @Column(name = "id_estatus", nullable = false)
    private Integer idEstatus;

    @Column(name = "id_orden", nullable = false)
    private Integer idOrden;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Trazabilidad() {

    }

    public Integer getIdTrazabilidad() {
        return idTrazabilidad;
    }

    public void setIdTrazabilidad(Integer idTrazabilidad) {
        this.idTrazabilidad = idTrazabilidad;
    }

    public Integer getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(Integer idEstatus) {
        this.idEstatus = idEstatus;
    }

    public Integer getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

}
