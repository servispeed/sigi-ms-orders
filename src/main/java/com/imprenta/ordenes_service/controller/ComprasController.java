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

    @PostMapping("/{idSolicitud}/compra-exitosa")
    public ResponseEntity<?> procesarCompraExitosa(
            @PathVariable Integer idSolicitud,
            @RequestHeader("X-User-Id") Integer idUsuario,
            @RequestParam("costoReal") BigDecimal costoReal,
            @RequestParam("comprobante") MultipartFile comprobante) {

        try {
            if (comprobante.isEmpty()) {
                return ResponseEntity.badRequest().body("El comprobante de pago es obligatorio.");
            }

            Map<String, Object> resultado = comprasService.procesarCompraExitosa(
                    idSolicitud, idUsuario, costoReal, comprobante);

            return ResponseEntity.ok(resultado);

        } catch (com.imprenta.ordenes_service.exception.BadRequestException e) {
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

    @PostMapping("/{idSolicitud}/compra-atrasada")
    public ResponseEntity<?> procesarCompraAtrasada(
            @PathVariable Integer idSolicitud,
            @RequestHeader("X-User-Id") Integer idUsuario,
            @RequestParam("costoReal") BigDecimal costoReal,
            @RequestParam("nuevaFechaEntrega") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate nuevaFechaEntrega,
            @RequestParam("comprobante") MultipartFile comprobante) {

        try {
            if (comprobante.isEmpty()) {
                return ResponseEntity.badRequest().body("El comprobante de pago es obligatorio.");
            }

            if (nuevaFechaEntrega == null) {
                return ResponseEntity.badRequest().body("La nueva fecha de entrega es obligatoria para atrasos.");
            }

            Map<String, Object> resultado = comprasService.procesarCompraAtrasada(
                    idSolicitud, idUsuario, costoReal, nuevaFechaEntrega, comprobante);

            return ResponseEntity.ok(resultado);

        } catch (com.imprenta.ordenes_service.exception.BadRequestException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (com.imprenta.ordenes_service.exception.ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al procesar el atraso: " + e.getMessage());
        }
    }

    @PutMapping("/{idSolicitud}/llegada-atrasada")
    public ResponseEntity<?> registrarLlegadaDeInsumosAtrasados(
            @PathVariable Integer idSolicitud,
            @RequestHeader("X-User-Id") Integer idUsuario) {

        try {
            Map<String, Object> resultado = comprasService.registrarLlegadaDeAtraso(idSolicitud, idUsuario);
            return ResponseEntity.ok(resultado);
        } catch (com.imprenta.ordenes_service.exception.BadRequestException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (com.imprenta.ordenes_service.exception.ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al registrar la llegada de insumos: " + e.getMessage());
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudInsumo>> verPendientes() {
        return ResponseEntity.ok(comprasService.obtenerPendientes());
    }

}