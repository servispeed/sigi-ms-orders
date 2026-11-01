package com.imprenta.ordenes_service.service.states;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoDisenoRechazado implements OrdenState {

    private static final Integer ID_ACTUAL = 10;
    private static final Integer ID_SIGUIENTE = 7;

    private final SecurityHelper securityHelper;

    @Autowired
    public EstadoDisenoRechazado(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ACTUAL;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {

        securityHelper.validarEsDiseñadorAsignado(idUsuario, orden.getIdUsuarioDisenador());

        return ID_SIGUIENTE;
    }
}