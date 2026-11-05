package com.imprenta.ordenes_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.imprenta.ordenes_service.model.SolicitudInsumo;

public interface SolicitudInsumoRepository extends JpaRepository<SolicitudInsumo, Integer> {

    List<SolicitudInsumo> findByIdEstatus(Integer idEstatus);

    List<SolicitudInsumo> findByIdOrden(Integer idOrden);

    @EntityGraph(attributePaths = { "detalles" })
    List<SolicitudInsumo> findWithDetallesByIdEstatus(Integer idEstatus);

    @EntityGraph(attributePaths = { "detalles" })
    List<SolicitudInsumo> findWithDetallesByIdEstatusIn(List<Integer> estatusList);
}