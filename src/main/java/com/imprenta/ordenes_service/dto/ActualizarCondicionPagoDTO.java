package com.imprenta.ordenes_service.dto;

public class ActualizarCondicionPagoDTO {
    private Integer idCondicionPago;

    public Integer getIdCondicionPago() {
        return idCondicionPago;
    }

    public void setIdCondicionPago(Integer idCondicionPago) {
        this.idCondicionPago = idCondicionPago;
    }

    public ActualizarCondicionPagoDTO(Integer idCondicionPago) {
        this.idCondicionPago = idCondicionPago;
    }

}
