package com.imprenta.ordenes_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.imprenta.ordenes_service.model.CatUsoCfdi;

@Repository
public interface CatUsoCfdiRepository extends JpaRepository<CatUsoCfdi, Integer> {

}
