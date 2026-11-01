package com.imprenta.ordenes_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.imprenta.ordenes_service.model.CatTipoMovimiento;

@Repository
public interface CatTipoMovimientoRepository extends JpaRepository<CatTipoMovimiento, Integer> {

}
