package com.imprenta.ordenes_service.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.imprenta.ordenes_service.dto.ActualizarCondicionPagoDTO;
import com.imprenta.ordenes_service.dto.CambiarEstatusDTO;
import com.imprenta.ordenes_service.dto.CrearOrdenDTO;
import com.imprenta.ordenes_service.dto.NotasDisenoDTO;
import com.imprenta.ordenes_service.dto.reportes.HistorialOrdenDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenCardDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenesTotalesDTO;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.model.Trazabilidad;
import com.imprenta.ordenes_service.service.OrdenService;

@RestController
@RequestMapping("api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping
    public ResponseEntity<Orden> crearNuevaCotizacion(@RequestBody CrearOrdenDTO dto) {

        Orden ordenGuardada = ordenService.crearCotizacion(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ordenGuardada.getIdOrden())
                .toUri();

        return ResponseEntity.created(location).body(ordenGuardada);

    }

    @PostMapping("/{idOrden}/pagos")
    public ResponseEntity<Movimientos> registrarMovimiento(
            @PathVariable Integer idOrden,
            @RequestHeader("X-User-Id") Integer idUsuario,
            @RequestBody Map<String, Object> body) {

        BigDecimal monto = new BigDecimal(body.get("monto").toString());
        String referencia = (String) body.get("referencia");

        Movimientos nuevoMovimiento = ordenService.registrarMovimiento(idOrden, monto, referencia, idUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimiento);

    }

    @PutMapping("/{idOrden}/condicion-pago")
    public ResponseEntity<Orden> actualizarCondicionPago(
            @PathVariable Integer idOrden,
            @RequestBody ActualizarCondicionPagoDTO dto) {
        Orden ordenActualizada = ordenService.actualizarCondicionPago(idOrden, dto.getIdCondicionPago());
        return ResponseEntity.ok(ordenActualizada);
    }

    @PostMapping("/{idOrden}/estatus")
    public ResponseEntity<Trazabilidad> cambiarEstatusDeOrden(
            @PathVariable Integer idOrden,
            @RequestHeader("X-User-Id") Integer idUsuario,
            @RequestBody CambiarEstatusDTO dto) {

        Trazabilidad nuevaTraza = ordenService.cambiarEstatusOrden(
                idOrden,
                dto.getClaveEstatus(),
                idUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTraza);

    }

    @GetMapping("/{idOrden}/estatus")
    public ResponseEntity<Map<String, Object>> obtenerEstatusDeOrden(@PathVariable Integer idOrden) {

        Map<String, Object> respuesta = ordenService.obtenerEstatusActual(idOrden);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping
    public ResponseEntity<Page<Orden>> obtenerTodasLasOrdenes(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<Orden> pagina = ordenService.findAll(pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/activas")
    public ResponseEntity<Page<Orden>> obtenerOrdenesActivas(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        Page<Orden> pagina = ordenService.obtenerSoloOrdenes(pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/cotizacion-cancelacion")
    public ResponseEntity<Page<Orden>> obtenerArchivo(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        Page<Orden> pagina = ordenService.obtenerCotizacionesYCanceladas(pageable);
        return ResponseEntity.ok(pagina);
    }

    // @GetMapping
    // public ResponseEntity<List<Orden>> obtenerTodasLasOrdenes() {
    // List<Orden> ordenes = ordenService.findAll();
    // return ResponseEntity.ok(ordenes);
    // }

    @GetMapping("/{id}")
    public ResponseEntity<Orden> obtenerOrdenPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenService.findById(id));
    }

    @PostMapping("/{idOrden}/avanzar")
    public ResponseEntity<Trazabilidad> avanzarFlujo(
            @PathVariable Integer idOrden,
            @RequestHeader("X-User-Id") Integer idUsuario,
            @RequestBody Map<String, Object> body) {

        Trazabilidad traza = ordenService.avanzarEstado(idOrden, idUsuario, body);

        return ResponseEntity.ok(traza);
    }

    @PutMapping("/{idOrden}/asignar-disenador")
    public ResponseEntity<Orden> asignarDisenador(
            @PathVariable Integer idOrden,
            @RequestBody Map<String, Integer> body) {

        Integer idDisenador = body.get("idDisenador");

        Orden ordenActualizada = ordenService.asignarDisenador(idOrden, idDisenador);

        return ResponseEntity.ok(ordenActualizada);
    }

    @PostMapping("/{idOrden}/archivo")
    public ResponseEntity<Orden> subirArchivo(
            @PathVariable Integer idOrden,
            @RequestParam("file") MultipartFile file) {

        Orden orden = ordenService.subirArchivoDiseno(idOrden, file);

        return ResponseEntity.ok(orden);
    }

    @PutMapping("/{idOrden}/verificar-insumos")
    public ResponseEntity<Orden> confirmarLlegadaInsumos(@PathVariable Integer idOrden) {

        Orden orden = ordenService.verificarInsumos(idOrden);
        return ResponseEntity.ok(orden);
    }

    @PostMapping("/{idOrden}/cancelar")
    public ResponseEntity<Trazabilidad> cancelarOrden(
            @PathVariable Integer idOrden,
            @RequestHeader("X-User-Id") Integer idUsuario,
            @RequestBody Map<String, Integer> body) {

        Integer idRazon = body.get("idRazon");

        if (idRazon == null) {
            throw new BadRequestException("Debes seleccionar una razón de cancelación.");
        }

        Trazabilidad traza = ordenService.cancelarOrden(idOrden, idUsuario, idRazon);

        return ResponseEntity.ok(traza);
    }

    @GetMapping("/movil/tarjetas")
    public ResponseEntity<Page<OrdenCardDTO>> obtenerTarjetasParaMovil(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String fecha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ordenService.obtenerTarjetasMovil(busqueda, estado, fecha, page, size));
    }

    @GetMapping("/{idOrden}/historial")
    public ResponseEntity<List<HistorialOrdenDTO>> obtenerHistorial(@PathVariable Integer idOrden) {
        return ResponseEntity.ok(ordenService.obtenerHistorialOrden(idOrden));
    }

    @GetMapping("/por-disenador/{idDisenador}")
    public ResponseEntity<Page<Orden>> getOrdenesPorDisenador(
            @PathVariable Integer idDisenador,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        Page<Orden> ordenes = ordenService.obtenerPendientesDiseñador(idDisenador, pageable);
        return ResponseEntity.ok(ordenes);
    }

    @PutMapping("/{idOrden}/notas-diseno")
    public ResponseEntity<?> updateNotasDiseno(
            @PathVariable("idOrden") Integer idOrden,
            @RequestBody NotasDisenoDTO notasDto) {
        try {
            Orden ordenActualizada = ordenService.actualizarNotasDiseno(idOrden, notasDto.getNotasDiseno());
            return ResponseEntity.ok(ordenActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar notas");
        }
    }

    @GetMapping("/cotizaciones")
    public ResponseEntity<List<Orden>> obtenerCotizacionesTotales(
            @RequestParam(defaultValue = "Mes") String filtro) {
        List<Orden> cotizaciones = ordenService.obtenerCotizacionesFiltradas(filtro);
        return ResponseEntity.ok(cotizaciones);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<OrdenesTotalesDTO> obtenerOrdenesTotales(
            @RequestParam(defaultValue = "Mes") String filtro) {
        OrdenesTotalesDTO stats = ordenService.obtenerOrdenesPorPeriodo(filtro);
        return ResponseEntity.ok(stats);
    }

}
