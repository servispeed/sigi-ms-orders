package com.imprenta.ordenes_service.service.states;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoDisenoConInsumos implements OrdenState {

    private final SecurityHelper securityHelper;
    private static final Integer ID_ACTUAL = 3;
    private static final Integer ID_SIGUIENTE = 7;

    @Autowired
    public EstadoDisenoConInsumos(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ACTUAL;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {

        // Pueden iniciar el diseño: El Jefe de Taller (asignando) o el Diseñador
        // (tomando)
        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_JEFE_TALLER,
                SecurityHelper.ROL_DISENADOR);
        if (orden.getIdUsuarioDisenador() == null) {
            throw new BadRequestException("Error: Asigna un diseñador a la orden antes de iniciar el trabajo.");
        }

        return ID_SIGUIENTE;
    }
}