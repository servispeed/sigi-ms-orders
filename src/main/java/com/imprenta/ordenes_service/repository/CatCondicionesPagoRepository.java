package com.imprenta.ordenes_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.imprenta.ordenes_service.model.CatCondicionesPago;

@Repository
public interface CatCondicionesPagoRepository extends JpaRepository<CatCondicionesPago, Integer> {

}
