package com.imprenta.ordenes_service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.imprenta.ordenes_service.model.SolicitudInsumo;

public interface SolicitudInsumoRepository extends JpaRepository<SolicitudInsumo, Integer> {

    List<SolicitudInsumo> findByIdEstatus(Integer idEstatus);

    List<SolicitudInsumo> findByIdOrden(Integer idOrden);

    @EntityGraph(attributePaths = { "detalles" })
    List<SolicitudInsumo> findWithDetallesByIdEstatus(Integer idEstatus);

    @Query("""
            SELECT s.idSolicitud
            FROM SolicitudInsumo s
            WHERE s.idEstatus IN :estatusList
            """)
    Page<Integer> encontrarIdsPendientes(
            @Param("estatusList") List<Integer> estatusList,
            Pageable pageable);

    @EntityGraph(attributePaths = { "detalles" })
    List<SolicitudInsumo> findWithDetallesByIdSolicitudIn(List<Integer> ids);
}