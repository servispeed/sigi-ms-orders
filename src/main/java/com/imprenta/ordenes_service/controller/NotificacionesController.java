package com.imprenta.ordenes_service.controller;

import com.imprenta.ordenes_service.model.Notificacion;
import com.imprenta.ordenes_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionesController {

    private final NotificationService notificationService;

    public NotificacionesController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Notificacion>> obtenerPendientes(@RequestHeader("X-User-Id") String receptorId) {
        List<Notificacion> pendientes = notificationService.obtenerPendientes(receptorId);
        return ResponseEntity.ok(pendientes);
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id) {
        notificationService.marcarComoLeida(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test-enviar")
    public ResponseEntity<String> probarEnvio(@RequestParam String receptorId) {
        notificationService.procesarYNotificar(
                "sistema",
                receptorId,
                "ORDEN_ACTUALIZADA",
                "¡Prueba desde Postman!",
                "Si estás leyendo esto, PostgreSQL y Mosquitto están funcionando.");
        return ResponseEntity.ok("Notificación enviada y guardada con éxito");
    }
}