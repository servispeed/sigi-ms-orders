package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import com.imprenta.ordenes_service.dto.CrearSolicitudDTO;
import com.imprenta.ordenes_service.dto.ProductoSolicitudDTO;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.helpers.FileStorageHelper;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.DetalleSolicitud;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.model.SolicitudInsumo;
import com.imprenta.ordenes_service.model.SolicitudInsumoComprobante;
import com.imprenta.ordenes_service.repository.MovimientoRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.repository.SolicitudInsumoRepository;
import com.imprenta.ordenes_service.repository.SolicitudInsumoComprobanteRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ComprasService {

    private final SolicitudInsumoRepository solicitudInsumoRepository;
    private final OrdenRepository ordenRepository;
    private final MovimientoRepository movimientoRepository;
    private final SolicitudInsumoComprobanteRepository comprobanteRepository;
    private final SecurityHelper securityHelper;
    private final FileStorageHelper fileStorageHelper;
    private final NotificationService notificationService;

    private static final Integer ESTATUS_PENDIENTE = 1;
    private static final Integer ESTATUS_COMPRADA = 2;
    private static final Integer ESTATUS_ATRASADA = 3;

    private static final Integer TIPO_EGRESO = 2;

    public ComprasService(
            SolicitudInsumoRepository solicitudInsumoRepository,
            OrdenRepository ordenRepository,
            MovimientoRepository movimientoRepository,
            SolicitudInsumoComprobanteRepository comprobanteRepository,
            SecurityHelper securityHelper,
            FileStorageHelper fileStorageHelper,
            NotificationService notificationService) {
        this.solicitudInsumoRepository = solicitudInsumoRepository;
        this.ordenRepository = ordenRepository;
        this.movimientoRepository = movimientoRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.securityHelper = securityHelper;
        this.notificationService = notificationService;
        this.fileStorageHelper = fileStorageHelper;
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
    public Map<String, Object> procesarCompraIntegral(Integer idSolicitud, Integer idUsuarioAdmin,
            BigDecimal costoReal, Integer idEstatus,
            LocalDate nuevaFechaEntrega, MultipartFile comprobante) {

        securityHelper.validarPermiso(idUsuarioAdmin, SecurityHelper.ROL_ADMIN, SecurityHelper.ROL_DUENO,
                SecurityHelper.ROL_CONTADORA);

        if (!idEstatus.equals(ESTATUS_COMPRADA) && !idEstatus.equals(ESTATUS_ATRASADA)) {
            throw new IllegalArgumentException("Estatus no permitido. Use 2 (COMPRADA) o 3 (ATRASADA).");
        }

        SolicitudInsumo solicitud = solicitudInsumoRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        String nombreArchivoComprobante = fileStorageHelper.guardarComprobante(comprobante);

        SolicitudInsumoComprobante registroComprobante = new SolicitudInsumoComprobante();
        registroComprobante.setIdSolicitud(idSolicitud);
        registroComprobante.setRutaArchivo(nombreArchivoComprobante);
        registroComprobante.setFechaSubida(LocalDateTime.now());
        comprobanteRepository.save(registroComprobante);

        solicitud.setIdEstatus(idEstatus);
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

        Movimientos movimientoGuardado = movimientoRepository.save(egreso);

        if (idEstatus.equals(ESTATUS_ATRASADA) && nuevaFechaEntrega != null && solicitud.getIdOrden() != null) {
            Orden orden = ordenRepository.findById(solicitud.getIdOrden())
                    .orElseThrow(() -> new ResourceNotFoundException("La orden asociada no existe."));

            orden.setFechaEntregaFormal(nuevaFechaEntrega);
            ordenRepository.save(orden);
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Compra procesada y comprobante guardado exitosamente.");
        respuesta.put("comprobante", registroComprobante);
        respuesta.put("movimiento", movimientoGuardado);
        respuesta.put("solicitud", solicitud);

        return respuesta;
    }

    @Transactional(readOnly = true)
    public List<SolicitudInsumo> obtenerPendientes() {
        return solicitudInsumoRepository.findWithDetallesByIdEstatus(
                ESTATUS_PENDIENTE);
    }
}