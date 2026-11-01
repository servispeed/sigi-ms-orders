package com.imprenta.ordenes_service.service.states;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoOrdenEnImpresion implements OrdenState {

    private static final Integer ID_ACTUAL = 5;
    private static final Integer ID_SIGUIENTE = 6;

    private final SecurityHelper securityHelper;

    @Autowired
    public EstadoOrdenEnImpresion(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ACTUAL;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {

        securityHelper.validarPermiso(idUsuario, SecurityHelper.ROL_JEFE_TALLER);

        return ID_SIGUIENTE;
    }
}