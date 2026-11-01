package com.imprenta.ordenes_service.dto;

import java.util.List;

import com.imprenta.ordenes_service.model.DetalleOrden;
import com.imprenta.ordenes_service.model.Orden;

public class CrearOrdenDTO {

    private Orden orden;
    private List<DetalleOrden> detalles;
    private Integer idUsuarioAccion;

    public CrearOrdenDTO() {

    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public List<DetalleOrden> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleOrden> detalles) {
        this.detalles = detalles;
    }

    public Integer getIdUsuarioAccion() {
        return idUsuarioAccion;
    }

    public void setIdUsuarioAccion(Integer idUsuarioAccion) {
        this.idUsuarioAccion = idUsuarioAccion;
    }

}
