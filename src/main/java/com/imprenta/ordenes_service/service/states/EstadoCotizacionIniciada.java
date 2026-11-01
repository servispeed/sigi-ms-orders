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

        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_ADMIN,
                SecurityHelper.ROL_CONTADORA,
                SecurityHelper.ROL_MOSTRADOR);

        if (orden.getIdCondicionPago() != null && !orden.getIdCondicionPago().equals(ID_CONDICION_CREDITO)) {
            BigDecimal porcentajeAnticipo;

            if (orden.getIdCondicionPago() == 1) {
                porcentajeAnticipo = new BigDecimal("1.00");
            } else if (orden.getIdCondicionPago() == 3) {
                porcentajeAnticipo = new BigDecimal("0.50");
            } else {
                throw new BadRequestException(
                        "Condición de pago no válida. Las opciones permitidas son Crédito, Pago Único o Anticipo del 50%.");
            }

            BigDecimal minimoRequerido = orden.getMontoTotal().multiply(porcentajeAnticipo);
            BigDecimal pagado = orden.getMontoPagado() == null ? BigDecimal.ZERO : orden.getMontoPagado();

            if (pagado.compareTo(minimoRequerido) < 0) {
                throw new BadRequestException(
                        String.format("Falta anticipo para iniciar la orden. Pagado: $%s. Mínimo requerido: $%s",
                                pagado, minimoRequerido));
            }

        }

        int dias = orden.getPlazoEstimadoDias() == null ? 0 : orden.getPlazoEstimadoDias();

        LocalDate fechaEntrega = LocalDate.now().plusDays(dias);

        orden.setFechaEntregaFormal(fechaEntrega);

        return ID_SIGUIENTE_PASO;
    }

}
