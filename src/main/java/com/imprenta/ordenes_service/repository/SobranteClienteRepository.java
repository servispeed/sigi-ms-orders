package com.imprenta.ordenes_service.repository;

import com.imprenta.ordenes_service.model.SobranteCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SobranteClienteRepository extends JpaRepository<SobranteCliente, Integer> {

    Optional<SobranteCliente> findByIdClienteAndProducto_IdProducto(Integer idCliente, Integer idProducto);

    Page<SobranteCliente> findByIdCliente(Integer idCliente, Pageable pageable);
}