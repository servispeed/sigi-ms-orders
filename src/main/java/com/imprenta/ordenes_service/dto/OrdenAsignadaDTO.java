package com.imprenta.ordenes_service.dto;

public interface OrdenAsignadaDTO {
    Integer getIdOrden();

    String getProducto();

    String getEstatus();

    String getFechaEntrega();

    Integer getIdDisenador();
}