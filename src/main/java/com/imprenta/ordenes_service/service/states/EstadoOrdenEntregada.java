package com.imprenta.ordenes_service.service.states;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoOrdenEntregada implements OrdenState {

    @Override
    public Integer getIdEstatusRepresentado() {
        return 12;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {
        throw new BadRequestException("La orden ya fue entregada y el proceso ha finalizado.");
    }
}