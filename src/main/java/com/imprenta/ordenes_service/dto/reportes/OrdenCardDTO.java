package com.imprenta.ordenes_service.dto.reportes;

import java.math.BigDecimal;

public interface OrdenCardDTO {
    Integer getIdOrden();

    String getNombreCliente();

    String getFecha();

    String getProductoPrincipal();

    String getEstatus();

    String getClaveEstatus();

    String getFechaIso();

    String getNombreEncargado();

    BigDecimal getMontoTotal();

    BigDecimal getMontoPagado();

    String getFechaEntrega();

    String getDescripcionEstatus();

    String getDetallesJson();
}
