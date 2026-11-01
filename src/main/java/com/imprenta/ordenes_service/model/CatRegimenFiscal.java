package com.imprenta.ordenes_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cat_regimen_fiscal")
public class CatRegimenFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regimen")
    private Integer idRegimen;

    @Column(name = "clave", length = 10)
    private String clave;

    @Column(name = "descripcion")
    private String descripcion;

    public Integer getIdRegimen() {
        return idRegimen;
    }

    public void setIdRegimen(Integer idRegimen) {
        this.idRegimen = idRegimen;
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
