package com.imprenta.ordenes_service.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/{idSolicitud}/comprar")
    public ResponseEntity<Movimientos> registrarCompra(
            @PathVariable Integer idSolicitud,
            @RequestBody Map<String, Object> body) {

        Integer idUsuario = Integer.valueOf(body.get("idUsuario").toString());
        BigDecimal costoReal = new BigDecimal(body.get("costoReal").toString());

        Movimientos egreso = comprasService.registrarCompra(idSolicitud, costoReal, idUsuario);

        return ResponseEntity.ok(egreso);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudInsumo>> verPendientes() {
        return ResponseEntity.ok(comprasService.obtenerPendientes());
    }

}