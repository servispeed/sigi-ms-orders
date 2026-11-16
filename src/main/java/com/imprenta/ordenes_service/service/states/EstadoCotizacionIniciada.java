package com.imprenta.ordenes_service.service.states;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoCotizacionIniciada implements OrdenState {

    private static final Integer ID_ESTATUS_INICIADA = 1;
    private static final Integer ID_SIGUIENTE_PASO = 2;
    private static final Integer ID_CONDICION_CREDITO = 5;

    private final SecurityHelper securityHelper;

    public EstadoCotizacionIniciada(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ESTATUS_INICIADA;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {

        // 1. Se mantienen las reglas de seguridad
        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_ADMIN,
                SecurityHelper.ROL_CONTADORA,
                SecurityHelper.ROL_MOSTRADOR);

        // SE ELIMINÓ TODO EL BLOQUE DE VALIDACIÓN DE ANTICIPOS
        // La orden ahora avanza libremente porque la deuda es global.

        // 2. Se calcula la fecha formal de entrega
        int dias = orden.getPlazoEstimadoDias() == null ? 0 : orden.getPlazoEstimadoDias();
        LocalDate fechaEntrega = LocalDate.now().plusDays(dias);

        orden.setFechaEntregaFormal(fechaEntrega);

        return ID_SIGUIENTE_PASO;
    }

}