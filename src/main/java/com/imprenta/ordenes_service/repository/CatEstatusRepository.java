package com.imprenta.ordenes_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.imprenta.ordenes_service.model.CatEstatus;

@Repository
public interface CatEstatusRepository extends JpaRepository<CatEstatus, Integer> {
    Optional<CatEstatus> findByClave(String clave);
}
