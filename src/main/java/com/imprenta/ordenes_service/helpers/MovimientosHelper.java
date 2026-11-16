package com.imprenta.ordenes_service.helpers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.repository.CatCondicionesPagoRepository;
import com.imprenta.ordenes_service.repository.MovimientoRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;

@Service
public class MovimientosHelper {

    private final OrdenRepository ordenRepository;
    private final MovimientoRepository movimientoRepository;
    private final SecurityHelper securityHelper;

    private static final Integer TIPO_MOVIMIENTO_INGRESO = 1;

    public MovimientosHelper(OrdenRepository ordenRepository,
            MovimientoRepository movimientoRepository,
            CatCondicionesPagoRepository condicionesPagoRepository,
            SecurityHelper securityHelper) {
        this.ordenRepository = ordenRepository;
        this.movimientoRepository = movimientoRepository;
        this.securityHelper = securityHelper;
    }

    @Transactional
    public Movimientos registrarAbono(Integer idCliente, Integer idOrden, BigDecimal monto, String referencia,
            Integer idUsuario) {

        // 1. Validar permisos de seguridad
        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_MOSTRADOR,
                SecurityHelper.ROL_CONTADORA,
                SecurityHelper.ROL_ADMIN,
                SecurityHelper.ROL_DUENO);

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El monto del abono debe ser mayor a 0.");
        }
        
        // 2. Preparar el registro del movimiento
        Movimientos mov = new Movimientos();
        mov.setIdCliente(idCliente); // Nuevo campo obligatorio en tu BD
        mov.setMonto(monto);
        mov.setConcepto("Abono a Orden: " + referencia);
        mov.setIdTipoMovimiento(TIPO_MOVIMIENTO_INGRESO);
        mov.setIdUsuarioRegistro(idUsuario);
        mov.setFechaMovimiento(LocalDateTime.now());
        // 3. Evaluar si es un pago a una orden específica o un abono global
        if (idOrden != null) {
            // Flujo A: Pago referenciado a una orden
            Orden orden = ordenRepository.findById(idOrden)
                    .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + idOrden));

            // Validar que la orden realmente pertenezca al cliente que está pagando
            if (!orden.getIdCliente().equals(idCliente)) {
                throw new BadRequestException(
                        "Inconsistencia: La orden #" + idOrden + " no pertenece al cliente indicado.");
            }

            mov.setIdOrden(idOrden);
            mov.setConcepto("Abono a Orden #" + idOrden + " - " + referencia);

            // Actualizar el montoPagado específico de la orden (para historial/UI)
            BigDecimal pagadoActual = orden.getMontoPagado() == null ? BigDecimal.ZERO : orden.getMontoPagado();
            orden.setMontoPagado(pagadoActual.add(monto));
            ordenRepository.save(orden);

        } else {
            // Flujo B: Abono global a la cuenta corriente del cliente (idOrden es null)
            mov.setIdOrden(null);
            mov.setConcepto("Abono Global a Cuenta - " + referencia);
        }

        // 4. Guardar el movimiento en la base de datos
        return movimientoRepository.save(mov);
    }
}