package com.imprenta.ordenes_service.service.states;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoCancelada implements OrdenState {

    private static final Integer ID_ACTUAL = 11;

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ACTUAL;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {
        throw new BadRequestException("La orden está CANCELADA y no puede ser modificada ni avanzada.");
    }

}
