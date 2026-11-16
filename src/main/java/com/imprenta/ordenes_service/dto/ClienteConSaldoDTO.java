package com.imprenta.ordenes_service.dto;

import java.math.BigDecimal;

public record ClienteConSaldoDTO(
        Integer idCliente,
        ClienteDTO cliente,
        BigDecimal saldoDeudor) {
}