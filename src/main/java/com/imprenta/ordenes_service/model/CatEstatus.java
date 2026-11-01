package com.imprenta.ordenes_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cat_Estatus")
public class CatEstatus {

    @Id
    @Column(name = "id_estatus")
    private Integer idEstatus;

    @Column(name = "clave", length = 50)
    private String clave;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    public CatEstatus() {

    }

    public Integer getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(Integer idEstatus) {
        this.idEstatus = idEstatus;
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
