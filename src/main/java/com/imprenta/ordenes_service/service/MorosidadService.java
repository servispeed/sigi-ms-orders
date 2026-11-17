package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imprenta.ordenes_service.client.PersonasClient;
import com.imprenta.ordenes_service.dto.ClienteDTO;
import com.imprenta.ordenes_service.repository.OrdenRepository;

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
            evaluarYActualizarEstatusCliente(idCliente);
        }
    }

    /**
     * Evalúa el saldo deudor del cliente y actualiza su estatus de crédito
     * a AL_CORRIENTE si liquidó su deuda, o ATRASADO si tiene saldo vencido.
     */
    public void evaluarYActualizarEstatusCliente(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            return;
        }

        try {
            BigDecimal deuda = ordenRepository.calcularSaldoDeudor(idCliente);
            BigDecimal saldoActual = (deuda != null) ? deuda : BigDecimal.ZERO;

            ClienteDTO cliente = personasClient.obtenerClientePorId(idCliente);
            String estatusActual = (cliente != null) ? cliente.getEstatusCredito() : null;

            String nuevoEstatus;

            if (saldoActual.compareTo(BigDecimal.ZERO) <= 0) {
                // Ya liquidó todo: pasa a AL_CORRIENTE (incluso si estaba ATRASADO o SUSPENDIDO)
                nuevoEstatus = "AL_CORRIENTE";
            } else {
                // Si está SUSPENDIDO manualmente, no lo degradamos a ATRASADO
                if ("SUSPENDIDO".equalsIgnoreCase(estatusActual)) {
                    return;
                }
                nuevoEstatus = "ATRASADO";
            }

            // Evita llamadas HTTP innecesarias si el estatus ya es el correcto
            if (nuevoEstatus.equalsIgnoreCase(estatusActual)) {
                return;
            }

            Map<String, Object> actualizacionCredito = new HashMap<>();
            actualizacionCredito.put("estatusCredito", nuevoEstatus);

            personasClient.actualizarCredito(idCliente, actualizacionCredito);

        } catch (Exception e) {
            System.err.println("Error al actualizar estatus de crédito para cliente " + idCliente + ": " + e.getMessage());
        }
    }
}