package com.imprenta.ordenes_service.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.imprenta.ordenes_service.dto.CrearSolicitudDTO;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.SolicitudInsumo;
import com.imprenta.ordenes_service.service.ComprasService;

@RestController
@RequestMapping("/api/compras")
public class ComprasController {

    private final ComprasService comprasService;

    public ComprasController(ComprasService comprasService) {
        this.comprasService = comprasService;
    }

    @PostMapping("/solicitar")
    public ResponseEntity<?> crearSolicitud(
            @RequestHeader("X-User-Id") Integer idUsuario,
            @RequestBody CrearSolicitudDTO dto) {
        try {
            if (dto.getProductos() == null || dto.getProductos().isEmpty()) {
                return ResponseEntity.badRequest().body("La lista de productos no puede estar vacía");
            }

            dto.setIdUsuario(idUsuario); // sobreescribe cualquier valor que venga en el body con el del token

            SolicitudInsumo solicitudGuardada = comprasService.crearSolicitud(dto);

            Map<String, Object> respuesta = new HashMap<>();

            int cantidadItems = (solicitudGuardada.getDetalles() != null) ? solicitudGuardada.getDetalles().size() : 0;

            respuesta.put("mensaje", "Solicitud #" + solicitudGuardada.getIdSolicitud() + " creada con " + cantidadItems
                    + " productos.");
            respuesta.put("solicitud", solicitudGuardada);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    @PostMapping("/{idSolicitud}/procesar-compra")
    public ResponseEntity<?> procesarCompraYCerrar(
            @PathVariable Integer idSolicitud,
            @RequestHeader("X-User-Id") Integer idUsuario,
            @RequestParam("costoReal") BigDecimal costoReal,
            @RequestParam("idEstatus") Integer idEstatus,
            @RequestParam(value = "nuevaFechaEntrega", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate nuevaFechaEntrega,
            @RequestParam("comprobante") MultipartFile comprobante) {

        try {
            if (comprobante.isEmpty()) {
                return ResponseEntity.badRequest().body("El comprobante de pago es obligatorio.");
            }

            Map<String, Object> resultado = comprasService.procesarCompraIntegral(
                    idSolicitud, idUsuario, costoReal, idEstatus, nuevaFechaEntrega, comprobante);

            return ResponseEntity.ok(resultado);

        } catch (com.imprenta.ordenes_service.exception.BadRequestException e) {
            // Captura las excepciones de seguridad arrojadas por SecurityHelper
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (com.imprenta.ordenes_service.exception.ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al procesar la compra: " + e.getMessage());
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudInsumo>> verPendientes() {
        return ResponseEntity.ok(comprasService.obtenerPendientes());
    }

}