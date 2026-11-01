package com.imprenta.ordenes_service.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imprenta.ordenes_service.dto.EgresoDTO;
import com.imprenta.ordenes_service.dto.graficas.DashboardEgresosDTO;
import com.imprenta.ordenes_service.dto.graficas.DashboardFinanzasDTO;
import com.imprenta.ordenes_service.dto.graficas.DashboardIngresosDTO;
import com.imprenta.ordenes_service.dto.reportes.CotizacionesGraficaDTO;
import com.imprenta.ordenes_service.dto.reportes.DashboardResumenDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenesGraficaDTO;
import com.imprenta.ordenes_service.dto.tablas.FlujoCajaDTO;
import com.imprenta.ordenes_service.dto.tablas.MovimientoTablaDTO;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.service.GraficasService;

@RestController
@RequestMapping("/api/dashboard")
public class GraficasController {

    private final SecurityHelper securityHelper;
    private final GraficasService graficasService;

    @Autowired
    public GraficasController(SecurityHelper securityHelper, GraficasService graficasService) {
        this.securityHelper = securityHelper;
        this.graficasService = graficasService;
    }

    @GetMapping("/movil/graficas")
    public ResponseEntity<Map<String, Object>> getGraficasMovil(
            @RequestParam Integer idUsuario,
            @RequestParam(name = "fechaInicio", required = false) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin", required = false) LocalDate fechaFin) {

        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_DUENO,
                SecurityHelper.ROL_ADMIN);

        LocalDateTime inicio = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null) ? fechaFin.atTime(LocalTime.MAX) : null;

        return ResponseEntity.ok(graficasService.obtenerDatosGraficasMovil(idUsuario, inicio, fin));
    }

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> getResumen() {
        return ResponseEntity.ok(graficasService.obtenerResumen());
    }

    @GetMapping("/ingresos/grafica")
    public ResponseEntity<DashboardIngresosDTO> obtenerGraficaIngresos(
            @RequestParam(defaultValue = "Semana") String filtro) {
        return ResponseEntity.ok(graficasService.obtenerDashboardIngresos(filtro));
    }

    @GetMapping("/egresos")
    public List<EgresoDTO> listarEgresos() {
        return graficasService.obtenerEgresos();
    }

    @GetMapping("/cotizaciones/grafica")
    public ResponseEntity<List<CotizacionesGraficaDTO>> obtenerGraficaCotizaciones(
            @RequestParam(defaultValue = "Semana") String filtro) {

        List<CotizacionesGraficaDTO> puntos = graficasService.obtenerGraficaCotizaciones(filtro);
        return ResponseEntity.ok(puntos);
    }

    @GetMapping("/ordenes/grafica")
    public ResponseEntity<List<OrdenesGraficaDTO>> obtenerOrdenesGrafica(
            @RequestParam(defaultValue = "Semana") String filtro) {

        List<OrdenesGraficaDTO> puntos = graficasService.obtenerGraficaOrdenes(filtro);
        return ResponseEntity.ok(puntos);
    }

    @GetMapping("/egresos/grafica")
    public ResponseEntity<DashboardEgresosDTO> obtenerDashboardEgresos(
            @RequestParam(defaultValue = "Semana") String filtro) {

        DashboardEgresosDTO dashboard = graficasService.obtenerDashboardEgresos(filtro);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/tabla/flujo-caja")
    public ResponseEntity<List<MovimientoTablaDTO>> obtenerFlujoCaja(
            @RequestParam(defaultValue = "Mes") String filtro) {
        List<MovimientoTablaDTO> tabla = graficasService.obtenerTablaFLujoCaja(filtro);

        return ResponseEntity.ok(tabla);
    }

    @GetMapping("/flujo-caja")
    public ResponseEntity<FlujoCajaDTO> obtenerFLujoCaja(
            @RequestParam(defaultValue = "Mes") String filtro) {
        FlujoCajaDTO resumen = graficasService.obtenerFlujoCaja(filtro);
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/finanzas/grafica-app")
    public ResponseEntity<DashboardFinanzasDTO> obtenerGraficaFinanzas(
            @RequestParam(defaultValue = "semana") String filtro) {
        DashboardFinanzasDTO respuesta = graficasService.obtenerGraficaFinanzas(filtro);
        return ResponseEntity.ok(respuesta);
    }

}
