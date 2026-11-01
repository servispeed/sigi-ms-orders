package com.imprenta.ordenes_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cat_uso_cfdi")
public class CatUsoCfdi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_uso_cfdi")
    private Integer idUsoCfdi;

    @Column(name = "clave", length = 3)
    private String clave;

    @Column(name = "descripcion")
    private String descripcion;

    public Integer getIdUsoCfdi() {
        return idUsoCfdi;
    }

    public void setIdUsoCfdi(Integer idUsoCfdi) {
        this.idUsoCfdi = idUsoCfdi;
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
