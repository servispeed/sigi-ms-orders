package com.imprenta.ordenes_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cat_Razones_Cancelacion")
public class CatRazonesCancelacion {

    @Id
    @Column(name = "id_razon")
    private Integer idRazon;

    private String descripcion;

    public CatRazonesCancelacion() {
    }

    public Integer getIdRazon() {
        return idRazon;
    }

    public void setIdRazon(Integer idRazon) {
        this.idRazon = idRazon;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
