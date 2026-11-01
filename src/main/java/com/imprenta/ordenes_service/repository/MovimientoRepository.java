package com.imprenta.ordenes_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.imprenta.ordenes_service.model.Movimientos;

import org.springframework.data.repository.query.Param;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimientos, Integer> {
    List<Movimientos> findByIdOrden(Integer idOrden);

    List<Movimientos> findByIdTipoMovimientoInOrderByFechaMovimientoDesc(List<Integer> idsTIpo);

    @Query("SELECT m FROM Movimientos m WHERE m.idTipoMovimiento IN :tipos AND m.fechaMovimiento >= :inicio AND m.fechaMovimiento <= :fin")
    List<Movimientos> findEgresosPorRango(
            @Param("tipos") List<Integer> tipos,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
