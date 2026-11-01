package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.imprenta.ordenes_service.client.PersonasClient;
import com.imprenta.ordenes_service.dto.ClienteDTO;
import com.imprenta.ordenes_service.dto.CrearOrdenDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenCardDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenesTotalesDTO;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.helpers.CotizacionHelper;
import com.imprenta.ordenes_service.helpers.FileStorageHelper;
import com.imprenta.ordenes_service.helpers.MovimientosHelper;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.helpers.TrazabilidadHelper;
import com.imprenta.ordenes_service.model.CatEstatus;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.model.Trazabilidad;
import com.imprenta.ordenes_service.repository.CatEstatusRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.service.states.OrdenState;
import com.imprenta.ordenes_service.service.states.OrdenStateFactory;
import com.imprenta.ordenes_service.dto.reportes.HistorialOrdenDTO;

import jakarta.transaction.Transactional;

@Service
public class OrdenService {
    private final OrdenRepository ordenRepository;
    private final CotizacionHelper cotizacionHelper;
    private final TrazabilidadHelper trazabilidadHelper;
    private final MovimientosHelper movimientosHelper;
    private final OrdenStateFactory stateFactory;
    private final FileStorageHelper fileStorageHelper;
    private final SecurityHelper securityHelper;
    private final CatEstatusRepository catEstatusRepository;
    private final NotificationService notificationService;
    private final PersonasClient personasClient;

    private static final String ESTATUS_INICIAL = "COT_INICIADA";
    private static final Integer ID_ESTATUS_CANCELADA = 11;
    private static final Integer ID_CONDICION_CREDITO = 5;
    private final List<Integer> IDS_NO_ORDENES = Arrays.asList(1, 11);

    public OrdenService(OrdenRepository ordenRepository,
            CotizacionHelper cotizacionHelper,
            TrazabilidadHelper trazabilidadHelper,
            MovimientosHelper movimientosHelper,
            OrdenStateFactory stateFactory,
            FileStorageHelper fileStorageHelper,
            SecurityHelper securityHelper,
            CatEstatusRepository catEstatusRepository,
            NotificationService notificationService,
            PersonasClient personasClient) {
        this.ordenRepository = ordenRepository;
        this.cotizacionHelper = cotizacionHelper;
        this.trazabilidadHelper = trazabilidadHelper;
        this.movimientosHelper = movimientosHelper;
        this.stateFactory = stateFactory;
        this.fileStorageHelper = fileStorageHelper;
        this.securityHelper = securityHelper;
        this.catEstatusRepository = catEstatusRepository;
        this.notificationService = notificationService;
        this.personasClient = personasClient;
    }

    @Transactional
    public Orden crearCotizacion(CrearOrdenDTO dto) {
        Orden ordenDatos = dto.getOrden();

        if (ordenDatos.getIdCondicionPago() != null && ordenDatos.getIdCondicionPago().equals(ID_CONDICION_CREDITO)) {
            ClienteDTO cliente = personasClient.obtenerClientePorId(ordenDatos.getIdCliente());

            if (cliente.getCreditoHabilitado() == null || !cliente.getCreditoHabilitado()) {
                throw new BadRequestException("Transacción rechazada: El cliente no tiene crédito habilitado.");
            }

            if ("ATRASADO".equalsIgnoreCase(cliente.getEstatusCredito())) {
                throw new BadRequestException(
                        "Transacción rechazada: El cliente tiene un estatus de crédito ATRASADO.");
            }

            // 3. Validar el límite de crédito disponible con la deuda real
            BigDecimal limiteCredito = cliente.getLimiteCredito() != null ? cliente.getLimiteCredito()
                    : BigDecimal.ZERO;
            BigDecimal saldoDeudor = ordenRepository.calcularSaldoDeudor(ordenDatos.getIdCliente());
            BigDecimal creditoDisponible = limiteCredito.subtract(saldoDeudor);
            BigDecimal montoOrden = ordenDatos.getMontoTotal() != null ? ordenDatos.getMontoTotal() : BigDecimal.ZERO;

            if (montoOrden.compareTo(creditoDisponible) > 0) {
                throw new BadRequestException(
                        String.format(
                                "Transacción rechazada: El monto de la orden ($%s) sumado a la deuda actual ($%s) supera el límite de crédito del cliente ($%s).",
                                montoOrden, saldoDeudor, limiteCredito));
            }

            Integer dias = cliente.getDiasCredito() != null ? cliente.getDiasCredito() : 0;
            ordenDatos.setFechaVencimiento(java.time.LocalDateTime.now().plusDays(dias));
        } else {
            ordenDatos.setFechaVencimiento(java.time.LocalDateTime.now());
        }
        Orden nuevaOrden = cotizacionHelper.generarCotizacion(
                dto.getOrden(),
                dto.getDetalles());

        trazabilidadHelper.registrarCambio(
                nuevaOrden.getIdOrden(),
                ESTATUS_INICIAL,
                dto.getIdUsuarioAccion());
        return nuevaOrden;
    }

    @Transactional
    public Movimientos registrarMovimiento(Integer idOrden, BigDecimal monto, String referencia, Integer idUsuario) {
        Movimientos movimiento = movimientosHelper.registrarAbono(idOrden, monto, referencia, idUsuario);

        notificationService.notificarARoles(
                String.valueOf(idUsuario),
                List.of("CONTADORA", "JEFE DE TALLER", "DISEÑADOR", "DUEÑO"),
                "COTIZACION_PAGADA",
                "Cotización Pagada - Orden #" + idOrden,
                "Se registró un pago de $" + monto + " para la orden #" + idOrden + ".");

        return movimiento;
    }

    @Transactional
    public Trazabilidad cambiarEstatusOrden(Integer idOrden, String nuevaClaveEstatus, Integer idUsuario) {
        Trazabilidad resultado = trazabilidadHelper.registrarCambio(idOrden, nuevaClaveEstatus, idUsuario);
        Orden ordenActualizada = findById(idOrden);
        notificarCambioDeEstatus(idOrden, idUsuario, ordenActualizada.getIdEstatusActual());
        return resultado;
    }

    @Transactional
    public Page<Orden> findAll(Pageable pageable) {
        return ordenRepository.findAll(pageable);
    }

    public Page<Orden> obtenerSoloOrdenes(Pageable pageable) {
        return ordenRepository.findByIdEstatusActualNotIn(IDS_NO_ORDENES, pageable);
    }

    public Page<Orden> obtenerCotizacionesYCanceladas(Pageable pageable) {
        return ordenRepository.findByIdEstatusActualIn(IDS_NO_ORDENES, pageable);
    }

    public Orden findById(Integer id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
    }

    @Transactional
    public Trazabilidad avanzarEstado(Integer idOrden, Integer idUsuario, Map<String, Object> params) {
        Orden orden = findById(idOrden);
        OrdenState estadoActual = stateFactory.obtenerEstado(orden.getIdEstatusActual());
        Integer idSiguientePaso = estadoActual.procesarSiguientePaso(orden, idUsuario, params);

        Trazabilidad resultado = trazabilidadHelper.registrarCambioPorId(idOrden, idSiguientePaso, idUsuario);
        notificarCambioDeEstatus(idOrden, idUsuario, idSiguientePaso);
        return resultado;
    }

    private void notificarCambioDeEstatus(Integer idOrden, Integer idUsuario, Integer idEstatusDestino) {
        if (idEstatusDestino == 9) {
            notificationService.notificarARoles(
                    String.valueOf(idUsuario),
                    List.of("JEFE DE TALLER"),
                    "ORDEN_IMPRESION",
                    "Orden lista para impresión",
                    "La orden #" + idOrden + " ha sido aprobada y pasó a la cola de impresión.");
        }

        else if (idEstatusDestino == 6) {
            notificationService.notificarARoles(
                    String.valueOf(idUsuario),
                    List.of("CONTADORA", "DUEÑO", "MOSTRADOR"),
                    "ORDEN_ENTREGADA",
                    "Orden lista para entrega",
                    "La orden #" + idOrden + " ha sido terminada en taller y está lista para entregarse.");
        }
    }

    @Transactional
    public Map<String, Object> obtenerEstatusActual(Integer idOrden) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + idOrden));

        CatEstatus estatus = catEstatusRepository.findById(orden.getIdEstatusActual())
                .orElseThrow(() -> new ResourceNotFoundException("Estatus no encontrado en el cátalogo"));

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("idOrden", orden.getIdOrden());
        respuesta.put("idEstatus", orden.getIdEstatusActual());
        respuesta.put("clave", estatus.getClave());
        respuesta.put("descripcion", estatus.getDescripcion());

        return respuesta;
    }

    @Transactional
    public Orden asignarDisenador(Integer idOrden, Integer idUsuarioDisenador) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + idOrden));

        orden.setIdUsuarioDisenador(idUsuarioDisenador);
        Orden ordenActualizada = ordenRepository.save(orden);

        return ordenActualizada;
    }

    @Transactional
    public Orden subirArchivoDiseno(Integer idOrden, MultipartFile file) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        String nombreArchivo = fileStorageHelper.guardarArchivo(file);

        orden.setRutaArchivo(nombreArchivo);
        orden.setFechaSubida(LocalDateTime.now());

        Orden ordenActualizada = ordenRepository.save(orden);

        return ordenActualizada;
    }

    @Transactional
    public Orden verificarInsumos(Integer idOrden) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        orden.setInsumosVerificados(true);
        return ordenRepository.save(orden);
    }

    @Transactional
    public Trazabilidad cancelarOrden(Integer idOrden, Integer idUsuario, Integer idRazon) {

        securityHelper.validarPermiso(idUsuario, SecurityHelper.ROL_MOSTRADOR);

        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if (orden.getIdEstatusActual().equals(12) || orden.getIdEstatusActual().equals(11)) {
            throw new BadRequestException("No se puede cancelar una orden finalizada o ya cancelada.");
        }
        orden.setIdRazonCancelacion(idRazon);
        Trazabilidad resultado = trazabilidadHelper.registrarCambioPorId(idOrden, ID_ESTATUS_CANCELADA, idUsuario);

        notificationService.notificarARoles(
                String.valueOf(idUsuario),
                List.of("JEFE DE TALLER", "DISEÑADOR", "CONTADORA", "DUEÑO"),
                "ORDEN_CANCELADA",
                "Alerta: Orden Cancelada",
                "La orden #" + idOrden + " acaba de ser cancelada en el sistema.");

        return resultado;
    }

    public Page<OrdenCardDTO> obtenerTarjetasMovil(String busqueda, String estado, String fecha, int page, int size) {
        String busquedaLimpia = (busqueda != null && !busqueda.trim().isEmpty()) ? busqueda.trim() : null;
        String estadoLimpio = (estado != null && !estado.trim().isEmpty()) ? estado.trim() : null;
        String fechaLimpia = (fecha != null && !fecha.trim().isEmpty()) ? fecha.trim() : null;

        Pageable pageable = PageRequest.of(page, size);

        return ordenRepository.obtenerTarjetasMovilDinamico(busquedaLimpia, estadoLimpio, fechaLimpia, pageable);
    }

    public List<HistorialOrdenDTO> obtenerHistorialOrden(Integer idOrden) {
        if (!ordenRepository.existsById(idOrden)) {
            throw new ResourceNotFoundException("Orden no encontrada: " + idOrden);
        }
        return trazabilidadHelper.obtenerHistorial(idOrden);
    }

    public Orden actualizarCondicionPago(Integer idOrden, Integer nuevaCondicion) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + idOrden));

        orden.setIdCondicionPago(nuevaCondicion);
        return ordenRepository.save(orden);
    }

    public Page<Orden> obtenerPendientesDiseñador(Integer idDisenador, Pageable pageable) {
        return ordenRepository.findByIdUsuarioDisenadorAndIdEstatusActualNotIn(
                idDisenador,
                IDS_NO_ORDENES,
                pageable);
    }

    public Orden actualizarNotasDiseno(Integer idOrden, String nuevasNotas) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + idOrden));

        orden.setNotasDiseno(nuevasNotas);
        return ordenRepository.save(orden);
    }

    public List<Orden> obtenerCotizacionesFiltradas(String filtro) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicio;
        LocalDateTime fin;

        LocalTime inicioDia = LocalTime.of(0, 0, 0);
        LocalTime finDia = LocalTime.of(23, 59, 59);

        switch (filtro.toLowerCase()) {
            case "dia":
            case "hoy":
                inicio = ahora.with(inicioDia);
                fin = ahora.with(finDia);
                break;

            case "semana":
                inicio = ahora.with(DayOfWeek.MONDAY).with(inicioDia);
                fin = ahora.with(DayOfWeek.SUNDAY).with(finDia);
                break;

            case "año":
            case "ano":
                inicio = ahora.withDayOfYear(1).with(inicioDia);
                fin = ahora.with(TemporalAdjusters.lastDayOfYear()).with(finDia);
                break;

            case "mes":
            default:
                inicio = ahora.withDayOfMonth(1).with(inicioDia);
                fin = ahora.with(TemporalAdjusters.lastDayOfMonth()).with(finDia);
                break;
        }

        return ordenRepository.findCotizacionesPorRangoDeFechas(inicio, fin);
    }

    public OrdenesTotalesDTO obtenerOrdenesPorPeriodo(String filtroTiempo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalTime inicioDia = LocalTime.of(0, 0, 0);
        LocalTime finDia = LocalTime.of(23, 59, 59);

        LocalDateTime inicio;
        LocalDateTime fin;

        switch (filtroTiempo.toLowerCase()) {
            case "hoy":
            case "dia":
                inicio = ahora.with(inicioDia);
                fin = ahora.with(finDia);
                break;
            case "semana":
                inicio = ahora.with(java.time.DayOfWeek.MONDAY).with(inicioDia);
                fin = ahora.with(java.time.DayOfWeek.SUNDAY).with(finDia);
                break;
            case "mes":
                inicio = ahora.withDayOfMonth(1).with(inicioDia);
                fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(finDia);
                break;
            case "año":
            case "anio":
                inicio = ahora.withDayOfYear(1).with(inicioDia);
                fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfYear()).with(finDia);
                break;
            default:
                inicio = ahora.withDayOfMonth(1).with(inicioDia);
                fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(finDia);
                break;
        }
        Long conteo = ordenRepository.countOrdenesPorRango(inicio, fin);
        BigDecimal sumaDinero = ordenRepository.sumMontoOrdenesPorRango(inicio, fin);

        return new OrdenesTotalesDTO(filtroTiempo, conteo, sumaDinero);
    }

}