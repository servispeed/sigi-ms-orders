package com.imprenta.ordenes_service.service.states;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoDisenoSinInsumos implements OrdenState {

    private final SecurityHelper securityHelper;

    private static final Integer ID_ACTUAL = 4;
    private static final Integer ID_SIGUIENTE = 7;

    @Autowired
    public EstadoDisenoSinInsumos(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ACTUAL;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {

        // Permisos: Jefe de Taller (autoriza) o Diseñador (acepta el riesgo)
        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_JEFE_TALLER,
                SecurityHelper.ROL_DISENADOR);

        if (orden.getIdUsuarioDisenador() == null) {
            throw new BadRequestException("Error: Asigna un diseñador a la orden antes de iniciar el trabajo.");
        }

        // (Opcional) Aquí podrías agregar un log o notificación extra:
        // "Advertencia: Iniciando diseño sin insumos físicos verificados."

        return ID_SIGUIENTE;
    }
}