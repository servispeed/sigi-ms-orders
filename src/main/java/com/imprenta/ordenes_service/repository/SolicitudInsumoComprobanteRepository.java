package com.imprenta.ordenes_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.imprenta.ordenes_service.model.SolicitudInsumoComprobante;

public interface SolicitudInsumoComprobanteRepository extends JpaRepository<SolicitudInsumoComprobante, Integer> {
}