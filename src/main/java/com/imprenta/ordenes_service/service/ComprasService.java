package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.dto.CrearSolicitudDTO;
import com.imprenta.ordenes_service.dto.ProductoSolicitudDTO;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.DetalleSolicitud;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.SolicitudInsumo;
import com.imprenta.ordenes_service.repository.MovimientoRepository;
import com.imprenta.ordenes_service.repository.SolicitudInsumoRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ComprasService {

    private final SolicitudInsumoRepository solicitudInsumoRepository;
    private final MovimientoRepository movimientoRepository;
    private final SecurityHelper securityHelper;
    private final NotificationService notificationService;

    private static final Integer ESTATUS_PENDIENTE = 1;
    private static final Integer ESTATUS_COMPRADA = 2;

    private static final Integer TIPO_EGRESO = 2;

    @Autowired
    public ComprasService(SolicitudInsumoRepository solicitudInsumoRepository,
            MovimientoRepository movimientoRepository,
            SecurityHelper securityHelper,
            NotificationService notificationService) {
        this.solicitudInsumoRepository = solicitudInsumoRepository;
        this.movimientoRepository = movimientoRepository;
        this.securityHelper = securityHelper;
        this.notificationService = notificationService;

    }

    @Transactional
    public SolicitudInsumo crearSolicitud(CrearSolicitudDTO dto) {

        SolicitudInsumo solicitudPadre = new SolicitudInsumo();
        solicitudPadre.setIdUsuarioSolicitante(dto.getIdUsuario());
        solicitudPadre.setIdOrden(dto.getIdOrden());
        solicitudPadre.setIdEstatus(ESTATUS_PENDIENTE);
        solicitudPadre.setFechaCreacion(LocalDateTime.now());

        solicitudPadre.setDescripcionSolicitud(dto.getDescripcionGeneral());

        if (dto.getProductos() != null) {
            for (ProductoSolicitudDTO prod : dto.getProductos()) {

                DetalleSolicitud detalle = new DetalleSolicitud();
                detalle.setIdProducto(prod.getIdProducto());

                try {
                    int cant = Integer.parseInt(prod.getCantidad());
                    detalle.setCantidad(cant);
                } catch (NumberFormatException e) {
                    detalle.setCantidad(1);
                }

                solicitudPadre.agregarDetalle(detalle);
            }
        }

        SolicitudInsumo solicitudGuardada = solicitudInsumoRepository.save(solicitudPadre);

        String mensajeNotificacion = "Se ha generado una nueva solicitud de insumos";
        if (dto.getIdOrden() != null) {
            mensajeNotificacion += " para la orden #" + dto.getIdOrden() + ".";
        } else {
            mensajeNotificacion += ".";
        }

        notificationService.notificarARoles(
                String.valueOf(dto.getIdUsuario()),
                List.of("CONTADORA", "DUEÑO"),
                "SOLICITUD_INSUMOS",
                "Nueva solicitud de insumos",
                mensajeNotificacion);

        return solicitudGuardada;
    }

    @Transactional
    public Movimientos registrarCompra(Integer idSolicitud, BigDecimal costoReal, Integer idUsuarioAdmin) {

        securityHelper.validarPermiso(idUsuarioAdmin, SecurityHelper.ROL_ADMIN, SecurityHelper.ROL_DUENO,
                SecurityHelper.ROL_CONTADORA);

        SolicitudInsumo solicitud = solicitudInsumoRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        solicitud.setIdEstatus(ESTATUS_COMPRADA);
        solicitudInsumoRepository.save(solicitud);

        StringBuilder conceptoBuilder = new StringBuilder();
        conceptoBuilder.append("Compra Sol #").append(solicitud.getIdSolicitud());

        if (solicitud.getDescripcionSolicitud() != null && !solicitud.getDescripcionSolicitud().isEmpty()) {
            conceptoBuilder.append(": ").append(solicitud.getDescripcionSolicitud());
        } else {
            conceptoBuilder.append(": Compra de insumos varios");
        }

        int cantidadItems = (solicitud.getDetalles() != null) ? solicitud.getDetalles().size() : 0;
        conceptoBuilder.append(" (").append(cantidadItems).append(" items)");

        String conceptoFinal = conceptoBuilder.toString();
        if (conceptoFinal.length() > 250) {
            conceptoFinal = conceptoFinal.substring(0, 247) + "...";
        }

        Movimientos egreso = new Movimientos();
        egreso.setIdTipoMovimiento(TIPO_EGRESO);
        egreso.setMonto(costoReal.abs().negate());
        egreso.setConcepto(conceptoFinal);
        egreso.setFechaMovimiento(LocalDateTime.now());
        egreso.setIdUsuarioRegistro(idUsuarioAdmin);
        egreso.setIdOrden(solicitud.getIdOrden());

        return movimientoRepository.save(egreso);
    }

    @Transactional(readOnly = true)
    public List<SolicitudInsumo> obtenerPendientes() {
        List<SolicitudInsumo> lista = solicitudInsumoRepository.findByIdEstatus(ESTATUS_PENDIENTE);

        for (SolicitudInsumo solicitud : lista) {
            Hibernate.initialize(solicitud.getDetalles());
        }

        return lista;
    }
}