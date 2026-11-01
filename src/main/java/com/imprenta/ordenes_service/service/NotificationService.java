package com.imprenta.ordenes_service.service;

import com.imprenta.ordenes_service.config.MqttGateway;
import com.imprenta.ordenes_service.model.Notificacion;
import com.imprenta.ordenes_service.dto.NotificacionPayloadDTO;
import com.imprenta.ordenes_service.repository.NotificacionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificacionRepository notificacionRepository;
    private final MqttGateway mqttGateway;
    private final ObjectMapper objectMapper;

    @Value("${mqtt.default-topic}")
    private String defaultTopic;

    public NotificationService(NotificacionRepository notificacionRepository,
            MqttGateway mqttGateway,
            ObjectMapper objectMapper) {
        this.notificacionRepository = notificacionRepository;
        this.mqttGateway = mqttGateway;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void notificarARoles(String emisorId, List<String> rolesReceptores, String categoria, String titulo,
            String mensaje) {
        List<String> idsDestinatarios = notificacionRepository.findIdsUsuariosPorRoles(rolesReceptores);

        if (idsDestinatarios == null || idsDestinatarios.isEmpty()) {
            log.warn("No se encontraron usuarios para los roles: {}", rolesReceptores);
            return;
        }

        for (String receptorId : idsDestinatarios) {
            this.procesarYNotificar(emisorId, receptorId, categoria, titulo, mensaje);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void procesarYNotificar(String emisorId, String receptorId, String categoria, String titulo,
            String mensaje) {

        Notificacion nuevaNotificacion = new Notificacion();
        nuevaNotificacion.setEmisorId(emisorId);
        nuevaNotificacion.setReceptorId(receptorId);
        nuevaNotificacion.setCategoria(categoria);
        nuevaNotificacion.setTitulo(titulo);
        nuevaNotificacion.setMensaje(mensaje);
        nuevaNotificacion.setLeida(false);

        notificacionRepository.save(nuevaNotificacion);

        NotificacionPayloadDTO.Contenido contenido = new NotificacionPayloadDTO.Contenido();
        contenido.setTitulo(titulo);
        contenido.setMensaje(mensaje);

        NotificacionPayloadDTO payload = new NotificacionPayloadDTO();
        payload.setEmisorId(emisorId);
        payload.setReceptorId(receptorId);
        payload.setCategoria(categoria);
        payload.setContenido(contenido);

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            mqttGateway.sendMqtt(jsonPayload, defaultTopic);
            log.info("Notificación guardada y enviada por MQTT a la cola: {}", defaultTopic);
        } catch (Exception e) {
            // No relanzamos: la notificación ya quedó guardada en Postgres (paso 1),
            // y el fallo de MQTT no debe revertir la operación de negocio que
            // originó esta notificación (ej. cancelar una orden).
            log.error("No se pudo publicar la notificación en MQTT (tópico: {}). "
                    + "La notificación sí quedó guardada en BD con id pendiente de confirmar.", defaultTopic, e);
        }
    }

    @Transactional(readOnly = true)
    public List<Notificacion> obtenerPendientes(String receptorId) {
        return notificacionRepository.findByReceptorIdAndLeidaFalseOrderByCreadaEnDesc(receptorId);
    }

    @Transactional
    public void marcarComoLeida(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación con ID " + notificacionId + " no encontrada"));

        notificacion.setLeida(true);
        notificacion.setLeidaEn(LocalDateTime.now());
        notificacionRepository.save(notificacion);
    }
}