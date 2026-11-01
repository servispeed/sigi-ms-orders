package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.dto.EgresoDTO;
import com.imprenta.ordenes_service.dto.graficas.BarraEgresosDTO;
import com.imprenta.ordenes_service.dto.graficas.BarraIngresosDTO;
import com.imprenta.ordenes_service.dto.graficas.DashboardEgresosDTO;
import com.imprenta.ordenes_service.dto.graficas.DashboardFinanzasDTO;
import com.imprenta.ordenes_service.dto.graficas.DashboardIngresosDTO;
import com.imprenta.ordenes_service.dto.graficas.GraficaFinanzasDTO;
import com.imprenta.ordenes_service.dto.reportes.CotizacionesGraficaDTO;
import com.imprenta.ordenes_service.dto.reportes.DashboardResumenDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenesGraficaDTO;
import com.imprenta.ordenes_service.dto.tablas.FlujoCajaDTO;
import com.imprenta.ordenes_service.dto.tablas.MovimientoTablaDTO;
import com.imprenta.ordenes_service.model.CatTipoMovimiento;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.repository.CatTipoMovimientoRepository;
import com.imprenta.ordenes_service.repository.MovimientoRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;

@Service
public class GraficasService {

    private final OrdenRepository ordenRepository;
    private final MovimientoRepository movimientoRepository;
    private final CatTipoMovimientoRepository catTipoMovimientoRepository;
    private static final List<Integer> IDS_TIPOS_EGRESOS = List.of(2, 3, 4, 5);

    @Autowired
    public GraficasService(OrdenRepository ordenRepository, MovimientoRepository movimientoRepository,
            CatTipoMovimientoRepository catTipoMovimientoRepository) {
        this.ordenRepository = ordenRepository;
        this.movimientoRepository = movimientoRepository;
        this.catTipoMovimientoRepository = catTipoMovimientoRepository;
    }

    public Map<String, Object> obtenerDatosGraficasMovil(
            Integer idUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        Map<String, Object> respuesta = new HashMap<>();

        var pastel = ordenRepository.obtenerResumenEstatus(fechaInicio, fechaFin);
        respuesta.put("datosPastel", pastel);

        var radar = ordenRepository.obtenerRazonesRechazo(fechaInicio, fechaFin);
        respuesta.put("datosRadar", radar);

        return respuesta;
    }

    public DashboardResumenDTO obtenerResumen() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioMes = ahora.withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime finMes = ahora.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        Long totalCotizaciones = ordenRepository.contarCotizacionesMes(inicioMes, finMes);
        Long ordenesActivas = ordenRepository.contarOrdenesActivas();
        Long pagosPendientes = ordenRepository.contarPagosPendientes();
        Double pagosTotal = ordenRepository.sumarDineroPendiente();

        return new DashboardResumenDTO(totalCotizaciones, ordenesActivas, pagosPendientes, pagosTotal);
    }

    public DashboardIngresosDTO obtenerDashboardIngresos(String filtroTiempo) {
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
            case "año":
            case "anio":
                inicio = ahora.withDayOfYear(1).with(inicioDia);
                fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfYear()).with(finDia);
                break;
            case "mes":
            default:
                inicio = ahora.withDayOfMonth(1).with(inicioDia);
                fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(finDia);
                break;
        }

        List<Orden> ordenes = ordenRepository.encontrarOrdenesParaFinanzas(inicio, fin);

        BigDecimal total = ordenes.stream()
                .map(o -> o.getMontoPagado() != null ? o.getMontoPagado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BarraIngresosDTO> barras = new ArrayList<>();

        if ("Semana".equalsIgnoreCase(filtroTiempo)) {
            String[] dias = { "Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom" };
            for (int i = 0; i < 7; i++) {
                int diaSemana = i + 1;
                double sumaDia = ordenes.stream()
                        .filter(o -> o.getFechaCreacion().getDayOfWeek().getValue() == diaSemana)
                        .mapToDouble(o -> o.getMontoPagado() != null ? o.getMontoPagado().doubleValue() : 0.0)
                        .sum();

                String color = (ahora.getDayOfWeek().getValue() == diaSemana ? "#5C7CFA" : "#E0E7FF");
                barras.add(new BarraIngresosDTO(sumaDia, dias[i], color));
            }
        } else if ("Mes".equalsIgnoreCase(filtroTiempo)) {
            for (int i = 0; i < 4; i++) {
                int diaInicio = (i * 7) + 1;
                int diaFin = (i + 1) * 7;

                double sumaSemana = ordenes.stream()
                        .filter(o -> {
                            int diaMes = o.getFechaCreacion().getDayOfMonth();
                            return diaMes >= diaInicio && diaMes <= diaFin;
                        })
                        .mapToDouble(o -> o.getMontoPagado() != null ? o.getMontoPagado().doubleValue() : 0.0)
                        .sum();

                int diaHoy = ahora.getDayOfMonth();
                String color = (diaHoy >= diaInicio && diaHoy <= diaFin) ? "#5C7CFA" : "#E0E7FF";

                barras.add(new BarraIngresosDTO(sumaSemana, "Sem " + (i + 1), color));
            }
        } else if ("Año".equalsIgnoreCase(filtroTiempo) || "Anio".equalsIgnoreCase(filtroTiempo)) {
            String[] meses = { "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };
            for (int i = 0; i < 12; i++) {
                int mesNumero = i + 1;
                double sumaMes = ordenes.stream()
                        .filter(o -> o.getFechaCreacion().getMonthValue() == mesNumero)
                        .mapToDouble(o -> o.getMontoPagado() != null ? o.getMontoPagado().doubleValue() : 0.0)
                        .sum();

                String color = (ahora.getMonthValue() == mesNumero ? "#5C7CFA" : "#E0E7FF");
                barras.add(new BarraIngresosDTO(sumaMes, meses[i], color));
            }
        } else {
            barras.add(new BarraIngresosDTO(total.doubleValue(), "Hoy", "#5C7CFA"));
        }

        return new DashboardIngresosDTO(total, barras);
    }

    public List<EgresoDTO> obtenerEgresos() {
        List<Movimientos> movimientos = movimientoRepository
                .findByIdTipoMovimientoInOrderByFechaMovimientoDesc(IDS_TIPOS_EGRESOS);

        Map<Integer, String> mapaTipos = catTipoMovimientoRepository.findAll().stream()
                .collect(Collectors.toMap(CatTipoMovimiento::getIdTipo, CatTipoMovimiento::getDescripcion));

        return movimientos.stream()
                .map(m -> new EgresoDTO(
                        m.getIdMovimiento(),
                        m.getConcepto(),
                        m.getMonto(),
                        m.getFechaMovimiento(),
                        mapaTipos.getOrDefault(m.getIdTipoMovimiento(), "Desconocido"),
                        m.getIdOrden(),
                        m.getIdUsuarioRegistro()))
                .collect(Collectors.toList());
    }

    public List<CotizacionesGraficaDTO> obtenerGraficaCotizaciones(String filtroTiempo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalTime inicioDia = LocalTime.of(0, 0, 0);
        LocalTime finDia = LocalTime.of(23, 59, 59);

        LocalDateTime inicio;
        LocalDateTime fin;
        String filtro = filtroTiempo.toLowerCase();

        switch (filtro) {
            case "hoy":
            case "dia":
                inicio = ahora.with(inicioDia);
                fin = ahora.with(finDia);
                break;
            case "semana":
                inicio = ahora.with(java.time.DayOfWeek.MONDAY).with(inicioDia);
                fin = ahora.with(java.time.DayOfWeek.SUNDAY).with(finDia);
                break;
            case "año":
            case "anio":
                inicio = ahora.withDayOfYear(1).with(inicioDia);
                fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfYear()).with(finDia);
                break;
            case "mes":
            default:
                inicio = ahora.withDayOfMonth(1).with(inicioDia);
                fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(finDia);
                break;
        }

        List<Orden> cotizaciones = ordenRepository.findCotizacionesPorRangoParaGrafica(inicio, fin);

        Map<Integer, CotizacionesGraficaDTO> mapaPuntos = new LinkedHashMap<>();

        if (filtro.equals("hoy") || filtro.equals("dia")) {
            for (int hora = 0; hora <= 23; hora++) {
                String etiqueta = String.format("%02d:00", hora);
                mapaPuntos.put(hora, new CotizacionesGraficaDTO(etiqueta, 0L, BigDecimal.ZERO));
            }
        } else if (filtro.equals("año") || filtro.equals("anio")) {
            String[] meses = { "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };
            for (int i = 1; i <= 12; i++) {
                mapaPuntos.put(i, new CotizacionesGraficaDTO(meses[i - 1], 0L, BigDecimal.ZERO));
            }
        } else {
            LocalDate diaActual = inicio.toLocalDate();
            LocalDate diaFin = fin.toLocalDate();
            while (!diaActual.isAfter(diaFin)) {
                String etiqueta = formatearEtiquetaDia(diaActual, filtro);
                mapaPuntos.put(diaActual.getDayOfYear(), new CotizacionesGraficaDTO(etiqueta, 0L, BigDecimal.ZERO));
                diaActual = diaActual.plusDays(1);
            }
        }

        for (Orden o : cotizaciones) {
            LocalDateTime fecha = o.getFechaCreacion();
            Integer claveMapa;

            if (filtro.equals("hoy") || filtro.equals("dia")) {
                claveMapa = fecha.getHour();
            } else if (filtro.equals("año") || filtro.equals("anio")) {
                claveMapa = fecha.getMonthValue();
            } else {
                claveMapa = fecha.getDayOfYear();
            }

            if (mapaPuntos.containsKey(claveMapa)) {
                CotizacionesGraficaDTO punto = mapaPuntos.get(claveMapa);
                punto.setTotalCotizaciones(punto.getTotalCotizaciones() + 1);

                if (o.getMontoTotal() != null) {
                    punto.setMontoTotal(punto.getMontoTotal().add(o.getMontoTotal()));
                }
            }
        }

        return new ArrayList<>(mapaPuntos.values());
    }

    public List<OrdenesGraficaDTO> obtenerGraficaOrdenes(String filtroTiempo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalTime inicioDia = LocalTime.of(0, 0, 0);
        LocalTime finDia = LocalTime.of(23, 59, 59);

        LocalDateTime inicio;
        LocalDateTime fin;
        String filtro = filtroTiempo.toLowerCase();

        switch (filtro) {
            case "hoy":
            case "dia":
                inicio = ahora.with(inicioDia);
                fin = ahora.with(finDia);
                break;
            case "semana":
                inicio = ahora.with(java.time.DayOfWeek.MONDAY).with(inicioDia);
                fin = ahora.with(java.time.DayOfWeek.SUNDAY).with(finDia);
                break;
            case "año":
            case "anio":
                inicio = ahora.withDayOfYear(1).with(inicioDia);
                fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfYear()).with(finDia);
                break;
            case "mes":
            default:
                inicio = ahora.withDayOfMonth(1).with(inicioDia);
                fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(finDia);
                break;
        }

        List<Orden> ordenes = ordenRepository.findOrdenesPorRangoParaGrafica(inicio, fin);

        Map<Integer, OrdenesGraficaDTO> mapaPuntos = new LinkedHashMap<>();

        if (filtro.equals("hoy") || filtro.equals("dia")) {
            for (int hora = 0; hora <= 23; hora++) {
                String etiqueta = String.format("%02d:00", hora);
                mapaPuntos.put(hora, new OrdenesGraficaDTO(etiqueta, 0L, BigDecimal.ZERO));
            }
        } else if (filtro.equals("año") || filtro.equals("anio")) {
            String[] meses = { "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };
            for (int i = 1; i <= 12; i++) {
                mapaPuntos.put(i, new OrdenesGraficaDTO(meses[i - 1], 0L, BigDecimal.ZERO));
            }
        } else {
            LocalDate diaActual = inicio.toLocalDate();
            LocalDate diaFin = fin.toLocalDate();
            while (!diaActual.isAfter(diaFin)) {
                String etiqueta = formatearEtiquetaDia(diaActual, filtro);
                mapaPuntos.put(diaActual.getDayOfYear(), new OrdenesGraficaDTO(etiqueta, 0L, BigDecimal.ZERO));
                diaActual = diaActual.plusDays(1);
            }
        }

        for (Orden o : ordenes) {
            LocalDateTime fecha = o.getFechaCreacion();
            Integer claveMapa;

            if (filtro.equals("hoy") || filtro.equals("dia")) {
                claveMapa = fecha.getHour();
            } else if (filtro.equals("año") || filtro.equals("anio")) {
                claveMapa = fecha.getMonthValue();
            } else {
                claveMapa = fecha.getDayOfYear();
            }

            if (mapaPuntos.containsKey(claveMapa)) {
                OrdenesGraficaDTO punto = mapaPuntos.get(claveMapa);
                punto.setTotalOrdenes(punto.getTotalOrdenes() + 1);
                if (o.getMontoTotal() != null) {
                    punto.setMontoTotal(punto.getMontoTotal().add(o.getMontoTotal()));
                }
            }
        }

        return new ArrayList<>(mapaPuntos.values());
    }

    public DashboardEgresosDTO obtenerDashboardEgresos(String filtroTiempo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicio = ahora;
        LocalDateTime fin = ahora;
        String filtro = filtroTiempo != null ? filtroTiempo.toLowerCase() : "mes";

        if ("hoy".equals(filtro) || "dia".equals(filtro)) {
            inicio = ahora.with(LocalTime.MIN);
            fin = ahora.with(LocalTime.MAX);
        } else if ("semana".equals(filtro)) {
            inicio = ahora.with(java.time.DayOfWeek.MONDAY).with(LocalTime.MIN);
            fin = ahora.with(java.time.DayOfWeek.SUNDAY).with(LocalTime.MAX);
        } else if ("mes".equals(filtro)) {
            inicio = ahora.withDayOfMonth(1).with(LocalTime.MIN);
            fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        } else if ("año".equals(filtro) || "anio".equals(filtro)) {
            inicio = ahora.withDayOfYear(1).with(LocalTime.MIN);
            fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfYear()).with(LocalTime.MAX);
        }

        List<Movimientos> movimientosBD = movimientoRepository.findEgresosPorRango(IDS_TIPOS_EGRESOS, inicio, fin);

        List<Movimientos> egresos = movimientosBD.stream()
                .filter(m -> {
                    if (m.getMonto() == null)
                        return false;
                    if (m.getIdTipoMovimiento() != null && m.getIdTipoMovimiento() == 5
                            && m.getMonto().compareTo(BigDecimal.ZERO) < 0) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        BigDecimal montoEgresado = egresos.stream()
                .map(m -> m.getMonto() != null ? m.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BarraEgresosDTO> barras = new ArrayList<>();

        if ("semana".equals(filtro)) {
            String[] dias = { "Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom" };
            for (int i = 0; i < 7; i++) {
                int diaSemana = i + 1;
                BigDecimal sumaDia = egresos.stream()
                        .filter(m -> m.getFechaMovimiento().getDayOfWeek().getValue() == diaSemana)
                        .map(m -> m.getMonto() != null ? m.getMonto() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                barras.add(new BarraEgresosDTO(dias[i], sumaDia));
            }
        } else if ("mes".equals(filtro)) {
            for (int i = 0; i < 4; i++) {
                int diaInicio = (i * 7) + 1;
                int diaFin = (i + 1) * 7;

                BigDecimal sumaSemana = egresos.stream()
                        .filter(m -> {
                            int diaMes = m.getFechaMovimiento().getDayOfMonth();
                            return diaMes >= diaInicio && diaMes <= diaFin;
                        })
                        .map(m -> m.getMonto() != null ? m.getMonto() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                barras.add(new BarraEgresosDTO("Sem " + (i + 1), sumaSemana));
            }
        } else if ("año".equals(filtro) || "anio".equals(filtro)) {
            String[] meses = { "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };
            for (int i = 0; i < 12; i++) {
                int mesNumero = i + 1;
                BigDecimal sumaMes = egresos.stream()
                        .filter(m -> m.getFechaMovimiento().getMonthValue() == mesNumero)
                        .map(m -> m.getMonto() != null ? m.getMonto() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                barras.add(new BarraEgresosDTO(meses[i], sumaMes));
            }
        } else {
            barras.add(new BarraEgresosDTO("Hoy", montoEgresado));
        }

        return new DashboardEgresosDTO(montoEgresado, barras);
    }

    public List<MovimientoTablaDTO> obtenerTablaFLujoCaja(String filtroTiempo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicio = ahora;
        LocalDateTime fin = ahora;
        String filtro = filtroTiempo != null ? filtroTiempo.toLowerCase() : "mes";

        if ("hoy".equals(filtro) || "dia".equals(filtro)) {
            inicio = ahora.with(LocalTime.MIN);
            fin = ahora.with(LocalTime.MAX);
        } else if ("semana".equals(filtro)) {
            inicio = ahora.with(java.time.DayOfWeek.MONDAY).with(LocalTime.MIN);
            fin = ahora.with(java.time.DayOfWeek.SUNDAY).with(LocalTime.MAX);
        } else if ("mes".equals(filtro)) {
            inicio = ahora.withDayOfMonth(1).with(LocalTime.MIN);
            fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        } else if ("año".equals(filtro) || "anio".equals(filtro)) {
            inicio = ahora.withDayOfYear(1).with(LocalTime.MIN);
            fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfYear()).with(LocalTime.MAX);
        }

        List<Orden> ordenes = ordenRepository.findOrdenesPorRangoParaGrafica(inicio, fin);
        List<Movimientos> egresos = movimientoRepository.findEgresosPorRango(IDS_TIPOS_EGRESOS, inicio, fin);

        List<MovimientoTablaDTO> tablaFlujo = new ArrayList<>();

        for (Orden o : ordenes) {
            String concepto = "Orden #" + o.getIdOrden();
            BigDecimal monto = o.getMontoPagado() != null ? o.getMontoPagado() : BigDecimal.ZERO;
            String usuario = String.valueOf(o.getIdUsuario());

            tablaFlujo.add(new MovimientoTablaDTO(o.getFechaCreacion(), concepto, "Ingreso", monto, usuario));
        }
        for (Movimientos m : egresos) {
            BigDecimal monto = m.getMonto() != null ? m.getMonto() : BigDecimal.ZERO;
            String usuario = String.valueOf(m.getIdUsuarioRegistro());

            String tipoMov = "Egreso";

            if (m.getIdTipoMovimiento() != null && m.getIdTipoMovimiento() == 5) {
                if (monto.compareTo(BigDecimal.ZERO) < 0) {
                    tipoMov = "Ingreso (Ajuste)";
                    monto = monto.abs();
                } else {
                    tipoMov = "Egreso (Ajuste)";
                }
            }

            tablaFlujo.add(new MovimientoTablaDTO(m.getFechaMovimiento(), m.getConcepto(), tipoMov, monto, usuario));
        }

        tablaFlujo.sort((mov1, mov2) -> mov2.getFecha().compareTo(mov1.getFecha()));

        return tablaFlujo;
    }

    public FlujoCajaDTO obtenerFlujoCaja(String filtroTiempo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicio = ahora;
        LocalDateTime fin = ahora;
        String filtro = filtroTiempo != null ? filtroTiempo.toLowerCase() : "mes";

        if ("hoy".equals(filtro) || "dia".equals(filtro)) {
            inicio = ahora.with(LocalTime.MIN);
            fin = ahora.with(LocalTime.MAX);
        } else if ("semana".equals(filtro)) {
            inicio = ahora.with(java.time.DayOfWeek.MONDAY).with(LocalTime.MIN);
            fin = ahora.with(java.time.DayOfWeek.SUNDAY).with(LocalTime.MAX);
        } else if ("mes".equals(filtro)) {
            inicio = ahora.withDayOfMonth(1).with(LocalTime.MIN);
            fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        } else if ("año".equals(filtro) || "anio".equals(filtro)) {
            inicio = ahora.withDayOfYear(1).with(LocalTime.MIN);
            fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfYear()).with(LocalTime.MAX);
        }

        List<Orden> ordenes = ordenRepository.findOrdenesPorRangoParaGrafica(inicio, fin);
        List<Movimientos> egresos = movimientoRepository.findEgresosPorRango(IDS_TIPOS_EGRESOS, inicio, fin);

        BigDecimal totalIngresos = ordenes.stream()
                .map(o -> o.getMontoPagado() != null ? o.getMontoPagado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal entradaCaja = egresos.stream()
                .filter(m -> m.getIdTipoMovimiento() != null && m.getIdTipoMovimiento() == 5)
                .filter(m -> m.getMonto() != null && m.getMonto().compareTo(BigDecimal.ZERO) < 0)
                .map(m -> m.getMonto().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEgresos = egresos.stream()
                .filter(m -> {
                    if (m.getMonto() == null)
                        return false;
                    if (m.getIdTipoMovimiento() != null && m.getIdTipoMovimiento() == 5
                            && m.getMonto().compareTo(BigDecimal.ZERO) < 0) {
                        return false;
                    }
                    return true;
                })
                .map(Movimientos::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIngresos.add(entradaCaja).subtract(totalEgresos);

        return new FlujoCajaDTO(totalIngresos, entradaCaja, totalEgresos, balance);
    }

    public DashboardFinanzasDTO obtenerGraficaFinanzas(String filtroTiempo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicio = ahora;
        LocalDateTime fin = ahora;
        String filtro = filtroTiempo != null ? filtroTiempo.toLowerCase() : "semana";

        Map<String, GraficaFinanzasDTO> graficaMap = new LinkedHashMap<>();

        if ("hoy".equals(filtro) || "dia".equals(filtro)) {
            inicio = ahora.with(LocalTime.MIN);
            fin = ahora.with(LocalTime.MAX);

            for (int i = 8; i <= 20; i++) {
                String label = String.format("%02d:00", i);
                graficaMap.put(label, new GraficaFinanzasDTO(label, BigDecimal.ZERO, BigDecimal.ZERO));
            }
        } else if ("mes".equals(filtro)) {
            inicio = ahora.withDayOfMonth(1).with(LocalTime.MIN);
            fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

            for (int i = 1; i <= 5; i++) {
                String label = "S" + i;
                graficaMap.put(label, new GraficaFinanzasDTO(label, BigDecimal.ZERO, BigDecimal.ZERO));
            }
        } else {
            inicio = ahora.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
            fin = ahora.with(DayOfWeek.SATURDAY).with(LocalTime.MAX);

            for (DayOfWeek day : DayOfWeek.values()) {
                String label = day.getDisplayName(TextStyle.SHORT, new Locale("es", "MX"));
                label = label.substring(0, 1).toUpperCase() + label.substring(1);
                graficaMap.put(label, new GraficaFinanzasDTO(label, BigDecimal.ZERO, BigDecimal.ZERO));
            }
        }
        List<Orden> ordenes = ordenRepository.findOrdenesPorRangoParaGrafica(inicio, fin);
        List<Movimientos> movimientosBD = movimientoRepository.findEgresosPorRango(IDS_TIPOS_EGRESOS, inicio, fin);

        BigDecimal balanceTotal = BigDecimal.ZERO;
        BigDecimal ingresosTotales = BigDecimal.ZERO;
        BigDecimal egresosTotales = BigDecimal.ZERO;

        for (Orden o : ordenes) {
            if (o.getMontoPagado() != null) {
                String label = obtenerEtiqueta(o.getFechaCreacion(), filtro);
                graficaMap.putIfAbsent(label, new GraficaFinanzasDTO(label, BigDecimal.ZERO, BigDecimal.ZERO));

                graficaMap.get(label).sumarIngreso(o.getMontoPagado());
                ingresosTotales = ingresosTotales.add(o.getMontoPagado());
            }
        }

        for (Movimientos m : movimientosBD) {
            if (m.getMonto() == null)
                continue;

            if (m.getIdTipoMovimiento() != null && m.getIdTipoMovimiento() == 5
                    && m.getMonto().compareTo(BigDecimal.ZERO) < 0) {
                continue;
            }

            String label = obtenerEtiqueta(m.getFechaMovimiento(), filtro);
            graficaMap.putIfAbsent(label, new GraficaFinanzasDTO(label, BigDecimal.ZERO, BigDecimal.ZERO));

            graficaMap.get(label).sumarEgreso(m.getMonto());
            egresosTotales = egresosTotales.add(m.getMonto());
        }

        balanceTotal = ingresosTotales.subtract(egresosTotales);

        return new DashboardFinanzasDTO(balanceTotal, new ArrayList<>(graficaMap.values()));
    }

    private String obtenerEtiqueta(LocalDateTime fecha, String filtro) {
        if ("hoy".equals(filtro) || "dia".equals(filtro)) {
            return String.format("%02d:00", fecha.getHour());

        } else if ("mes".equals(filtro)) {
            int semana = ((fecha.getDayOfMonth() - 1) / 7) + 1;
            return "S" + semana;

        } else {
            String label = fecha.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "MX"));
            return label.substring(0, 1).toUpperCase() + label.substring(1);
        }
    }

    private String formatearEtiquetaDia(LocalDate fecha, String filtro) {
        if ("semana".equals(filtro)) {
            String diaStr = fecha.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "MX"));
            return diaStr.substring(0, 1).toUpperCase() + diaStr.substring(1);
        } else {
            return fecha.format(DateTimeFormatter.ofPattern("dd MMM", new Locale("es", "MX")));
        }
    }
}