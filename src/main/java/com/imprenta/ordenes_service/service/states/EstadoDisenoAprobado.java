package com.imprenta.ordenes_service.service.states;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

import java.util.Map;

@Component
public class EstadoDisenoAprobado implements OrdenState {

    private final SecurityHelper securityHelper;

    private static final Integer ID_ACTUAL = 9;
    private static final Integer ID_PRODUCCION = 5;

    @Autowired
    public EstadoDisenoAprobado(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ACTUAL;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {

        // El Diseñador (entrega) o Jefe Taller (recibe)
        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_DISENADOR,
                SecurityHelper.ROL_JEFE_TALLER);

        if (orden.getRutaArchivo() == null || orden.getRutaArchivo().isEmpty()) {
            throw new BadRequestException("Error crítico: No hay archivo para imprimir.");
        }
        if (Boolean.FALSE.equals(orden.getInsumosVerificados()) || orden.getInsumosVerificados() == null) {
            throw new BadRequestException("BLOQUEO DE PRODUCCIÓN: Se marcó que faltaban insumos. " +
                    "El almacén debe confirmar la compra usando el endpoint '/verificar-insumos' antes de continuar.");
        }
        return ID_PRODUCCION;
    }
}
