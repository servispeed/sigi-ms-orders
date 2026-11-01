package com.imprenta.ordenes_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cat_solicitud_estatus")
public class CatSolicitudEstatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_estatus")
    private Integer idSolicitudEstatus;

    @Column(name = "clave", length = 50)
    private String clave;

    @Column(name = "descripcion", length = 50)
    private String descripcion;

    public Integer getIdSolicitudEstatus() {
        return idSolicitudEstatus;
    }

    public void setIdSolicitudEstatus(Integer idSolicitudEstatus) {
        this.idSolicitudEstatus = idSolicitudEstatus;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}