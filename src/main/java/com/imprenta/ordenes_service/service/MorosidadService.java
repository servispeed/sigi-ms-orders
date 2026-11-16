package com.imprenta.ordenes_service.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imprenta.ordenes_service.client.PersonasClient;
import com.imprenta.ordenes_service.repository.OrdenRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MorosidadService {

    private final OrdenRepository ordenRepository;
    private final PersonasClient personasClient;

    public MorosidadService(OrdenRepository ordenRepository, PersonasClient personasClient) {
        this.ordenRepository = ordenRepository;
        this.personasClient = personasClient;
    }

    // Se ejecuta automáticamente todos los días a las 00:00 (Medianoche)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(readOnly = true)
    public void verificarYActualizarMorosos() {
        List<Integer> clientesVencidos = ordenRepository.encontrarClientesConOrdenesVencidas();

        for (Integer idCliente : clientesVencidos) {
            // Verificamos si realmente debe dinero globalmente
            BigDecimal deuda = ordenRepository.calcularSaldoDeudor(idCliente);
            if (deuda != null && deuda.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    // Armamos el body que espera tu ActualizarCreditoDTO
                    Map<String, Object> actualizacionCredito = new HashMap<>();
                    actualizacionCredito.put("estatusCredito", "ATRASADO");

                    // Llamamos a tu endpoint existente
                    personasClient.actualizarCredito(idCliente, actualizacionCredito);

                } catch (Exception e) {
                    System.err.println("Error al actualizar estatus de morosidad para cliente: " + idCliente);
                }
            }
        }
    }
}