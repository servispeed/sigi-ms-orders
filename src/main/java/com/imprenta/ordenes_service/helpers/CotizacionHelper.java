package com.imprenta.ordenes_service.helpers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.model.DetalleOrden;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.repository.DetalleOrdenRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;

@Service
public class CotizacionHelper {

    private final OrdenRepository ordenRepository;
    private final DetalleOrdenRepository detalleOrdenRepository;
    private final SecurityHelper securityHelper;

    @Autowired
    public CotizacionHelper(OrdenRepository ordenRepository,
            DetalleOrdenRepository detalleOrdenRepository,
            SecurityHelper securityHelper) {
        this.ordenRepository = ordenRepository;
        this.detalleOrdenRepository = detalleOrdenRepository;
        this.securityHelper = securityHelper;
    }

    @Transactional
    public Orden generarCotizacion(Orden orden, List<DetalleOrden> detalles) {

        if (orden == null || detalles == null || detalles.isEmpty()) {
            throw new BadRequestException("Faltan datos para la cotización");
        }

        securityHelper.validarPermiso(orden.getIdUsuario(),
                SecurityHelper.ROL_MOSTRADOR,
                SecurityHelper.ROL_DUENO,
                SecurityHelper.ROL_ADMIN,
                SecurityHelper.ROL_CONTADORA);

        if (orden.getPlazoEstimadoDias() == null || orden.getPlazoEstimadoDias() <= 0) {
            orden.setPlazoEstimadoDias(3);
        }

        if (orden.getFechaCreacion() == null) {
            orden.setFechaCreacion(LocalDateTime.now());
        }

        BigDecimal totalCalculado = calcularTotal(detalles);
        orden.setMontoTotal(totalCalculado);

        orden.setIdOrden(null);
        if (orden.getMontoPagado() == null) {
            orden.setMontoPagado(BigDecimal.ZERO);
        }

        Orden ordenGuardada = ordenRepository.save(orden);
        guardarDetallesDeLaOrden(ordenGuardada.getIdOrden(), detalles);

        return ordenGuardada;
    }

    private void guardarDetallesDeLaOrden(Integer idOrden, List<DetalleOrden> detalles) {
        Orden ordenRef = new Orden();
        ordenRef.setIdOrden(idOrden);

        for (DetalleOrden detalle : detalles) {
            detalle.setOrden(ordenRef);

            detalle.setIdDetalle(null);
            detalleOrdenRepository.save(detalle);
        }
    }

    private BigDecimal calcularTotal(List<DetalleOrden> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleOrden detalle : detalles) {
            if (detalle.getImporte() != null) {
                total = total.add(detalle.getImporte());
            }
        }
        return total;
    }
}