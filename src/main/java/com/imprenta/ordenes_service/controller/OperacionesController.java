package com.imprenta.ordenes_service.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imprenta.ordenes_service.dto.AjusteCajaDTO;
import com.imprenta.ordenes_service.dto.EgresoDTO;
import com.imprenta.ordenes_service.dto.OrdenAsignadaDTO;
import com.imprenta.ordenes_service.model.CatCondicionesPago;
import com.imprenta.ordenes_service.model.CatEstatus;
import com.imprenta.ordenes_service.model.CatRazonesCancelacion;
import com.imprenta.ordenes_service.model.CatRegimenFiscal;
import com.imprenta.ordenes_service.model.CatSolicitudEstatus;
import com.imprenta.ordenes_service.model.CatTipoMovimiento;
import com.imprenta.ordenes_service.model.CatUsoCfdi;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.service.CatalagosService;
import com.imprenta.ordenes_service.service.OperacionesService;

@RestController
@RequestMapping("/api/operaciones")
public class OperacionesController {

    private final OperacionesService operacionesService;
    private final OrdenRepository ordenRepository;
    private final CatalagosService catalagosService;

    @Autowired
    public OperacionesController(OperacionesService operacionesService, OrdenRepository ordenRepository,
            CatalagosService catalagosService) {
        this.operacionesService = operacionesService;
        this.ordenRepository = ordenRepository;
        this.catalagosService = catalagosService;
    }

    @GetMapping("/condiciones-pago")
    public ResponseEntity<List<CatCondicionesPago>> obtenerCondiciones() {
        return ResponseEntity.ok(operacionesService.obtenerTodasLasCondiciones());
    }

    @GetMapping("/carga-trabajo-diseno")
    public ResponseEntity<Map<Integer, List<OrdenAsignadaDTO>>> obtenerCargaTrabajo() {

        List<OrdenAsignadaDTO> lista = ordenRepository.obtenerOrdenesPorDisenador();
        Map<Integer, List<OrdenAsignadaDTO>> agrupado = lista.stream()
                .collect(Collectors.groupingBy(OrdenAsignadaDTO::getIdDisenador));

        return ResponseEntity.ok(agrupado);
    }

    @GetMapping("/razones-cancelacion")
    public ResponseEntity<List<CatRazonesCancelacion>> obtenerRazonesCancelacion() {
        return ResponseEntity.ok(catalagosService.obtenerRazonesCancelacion());
    }

    @GetMapping("/regimenes-fiscales")
    public ResponseEntity<List<CatRegimenFiscal>> obtenerRegimenesFiscales() {
        return ResponseEntity.ok(catalagosService.obtenerRegimenesFiscales());
    }

    @GetMapping("/usos-cfdi")
    public ResponseEntity<List<CatUsoCfdi>> obtenerUsosCfdi() {
        return ResponseEntity.ok(catalagosService.obtenerUsosCfdi());
    }

    @GetMapping("/estatus-solicitud")
    public ResponseEntity<List<CatSolicitudEstatus>> obtenerEstatusSolicitud() {
        return ResponseEntity.ok(catalagosService.obtenerEstatusSolicitud());
    }

    @GetMapping("/tipos-movimiento")
    public ResponseEntity<List<CatTipoMovimiento>> obtenerTiposMovimiento() {
        return ResponseEntity.ok(catalagosService.obtenerTiposMovimiento());
    }

    @GetMapping("/estatus")
    public ResponseEntity<List<CatEstatus>> obtenerEstatus() {
        return ResponseEntity.ok(catalagosService.obtenerEstatus());
    }

    @PostMapping("/egresos")
    public ResponseEntity<Movimientos> registrarEgreso(@Validated @RequestBody EgresoDTO egresoDTO) {
        Movimientos egresoGuardado = operacionesService.registrarEgreso(egresoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(egresoGuardado);
    }

    @PostMapping("/ajuste-caja")
    public ResponseEntity<?> registrarAjusteCaja(@RequestBody AjusteCajaDTO ajusteCajaDTO) {
        try {
            operacionesService.registrarAjusteCaja(ajusteCajaDTO);
            return ResponseEntity.ok("{\"mensaje\": \"Ajuste de caja registrado correctamente.\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");

        }
    }
}