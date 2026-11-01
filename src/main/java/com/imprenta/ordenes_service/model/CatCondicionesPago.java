package com.imprenta.ordenes_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cat_Condiciones_Pago")
public class CatCondicionesPago {

    @Id
    @Column(name = "id_condicion")
    private Integer idCondicion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "numero_plazos")
    private Integer numeroPlazos;

    public CatCondicionesPago() {

    }

    public Integer getIdCondicion() {
        return idCondicion;
    }

    public void setIdCondicion(Integer idCondicion) {
        this.idCondicion = idCondicion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getNumeroPlazos() {
        return numeroPlazos;
    }

    public void setNumeroPlazos(Integer numeroPlazos) {
        this.numeroPlazos = numeroPlazos;
    }

}
