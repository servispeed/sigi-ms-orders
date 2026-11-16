package com.imprenta.ordenes_service.repository;

import java.math.BigDecimal;

public interface ClienteSaldoProjection {
    Integer getIdCliente();
    BigDecimal getSaldoDeudor();
}