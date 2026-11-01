package com.imprenta.ordenes_service.dto;

import java.util.List;

public class CrearSolicitudDTO {
    private Integer idUsuario;
    private Integer idOrden;
    private String descripcionGeneral;
    private List<ProductoSolicitudDTO> productos;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<ProductoSolicitudDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoSolicitudDTO> productos) {
        this.productos = productos;
    }

    public Integer getIdOrden() {
        return idOrden;
    }

    public String getDescripcionGeneral() {
        return descripcionGeneral;
    }

    public void setDescripcionGeneral(String descripcionGeneral) {
        this.descripcionGeneral = descripcionGeneral;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
    }

}
