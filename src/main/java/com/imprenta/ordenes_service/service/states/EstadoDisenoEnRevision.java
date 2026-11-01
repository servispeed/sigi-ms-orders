package com.imprenta.ordenes_service.service.states;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoDisenoEnRevision implements OrdenState {

    private final SecurityHelper securityHelper;

    private static final Integer ID_ACTUAL = 8;
    private static final Integer ID_APROBADO = 9;
    private static final Integer ID_RECHAZADO = 10;

    @Autowired
    public EstadoDisenoEnRevision(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ACTUAL;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {

        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_MOSTRADOR,
                SecurityHelper.ROL_DISENADOR);

        Boolean aprobado = (Boolean) params.get("clienteAprobo");

        if (aprobado == null) {
            throw new BadRequestException("Error: Debes indicar si el cliente aprobó (true) o rechazó (false).");
        }

        if (aprobado) {
            return ID_APROBADO;
        } else {
            return ID_RECHAZADO;
        }
    }
}