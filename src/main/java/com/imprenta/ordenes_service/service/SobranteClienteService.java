package com.imprenta.ordenes_service.service;

import com.imprenta.ordenes_service.dto.RegistrarSobranteDTO;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.model.Producto;
import com.imprenta.ordenes_service.model.SobranteCliente;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.repository.ProductoRepository;
import com.imprenta.ordenes_service.repository.SobranteClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SobranteClienteService {

    private final SobranteClienteRepository repository;
    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;

    public SobranteClienteService(SobranteClienteRepository repository,
            OrdenRepository ordenRepository,
            ProductoRepository productoRepository) {
        this.repository = repository;
        this.ordenRepository = ordenRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public Page<SobranteCliente> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<SobranteCliente> findByIdCliente(Integer idCliente, Pageable pageable) {
        return repository.findByIdCliente(idCliente, pageable);
    }

    @Transactional
    public SobranteCliente registrarSobrante(RegistrarSobranteDTO dto) {
        Optional<SobranteCliente> existente = repository.findByIdClienteAndProducto_IdProducto(
                dto.getIdCliente(), dto.getIdProducto());

        Orden ordenRef = ordenRepository.getReferenceById(dto.getIdOrden());

        if (existente.isPresent()) {
            SobranteCliente sobrante = existente.get();
            sobrante.setCantidadDisponible(sobrante.getCantidadDisponible() + dto.getCantidad());

            sobrante.setOrdenOrigen(ordenRef);

            return repository.save(sobrante);
        } else {
            SobranteCliente nuevo = new SobranteCliente();
            nuevo.setIdCliente(dto.getIdCliente());
            nuevo.setCantidadDisponible(dto.getCantidad());

            Producto productoRef = productoRepository.getReferenceById(dto.getIdProducto());
            nuevo.setProducto(productoRef);
            nuevo.setOrdenOrigen(ordenRef);

            return repository.save(nuevo);
        }
    }

    @Transactional
    public SobranteCliente consumirSobrante(Integer idSobrante, Integer cantidadConsumida) {
        return repository.findById(idSobrante).map(sobrante -> {
            if (sobrante.getCantidadDisponible() < cantidadConsumida) {
                throw new IllegalArgumentException("No hay suficiente cantidad disponible del sobrante.");
            }
            sobrante.setCantidadDisponible(sobrante.getCantidadDisponible() - cantidadConsumida);
            return repository.save(sobrante);
        }).orElseThrow(() -> new RuntimeException("Sobrante no encontrado"));
    }
}