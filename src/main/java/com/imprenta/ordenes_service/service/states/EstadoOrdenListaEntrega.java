package com.imprenta.ordenes_service.service.states;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoOrdenListaEntrega implements OrdenState {

    private static final Integer ID_ACTUAL = 6;
    private static final Integer ID_FINALIZADA = 12;

    private final SecurityHelper securityHelper;

    @Autowired
    public EstadoOrdenListaEntrega(SecurityHelper securityHelper) {
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
                SecurityHelper.ROL_JEFE_TALLER);

        BigDecimal total = orden.getMontoTotal();
        BigDecimal pagado = orden.getMontoPagado() == null ? BigDecimal.ZERO : orden.getMontoPagado();

        if (pagado.compareTo(total) < 0) {
            BigDecimal deuda = total.subtract(pagado);
            throw new BadRequestException("NO SE PUEDE ENTREGAR. El cliente tiene un saldo pendiente de: $" + deuda);
        }
        return ID_FINALIZADA;
    }
}