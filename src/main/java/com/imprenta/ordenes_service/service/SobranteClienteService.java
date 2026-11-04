package com.imprenta.ordenes_service.service;

import com.imprenta.ordenes_service.dto.RegistrarSobranteDTO;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.model.Producto;
import com.imprenta.ordenes_service.model.SobranteCliente;
import com.imprenta.ordenes_service.repository.SobranteClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SobranteClienteService {

    private final SobranteClienteRepository repository;

    // Inyección por constructor
    public SobranteClienteService(SobranteClienteRepository repository) {
        this.repository = repository;
    }

    public Page<SobranteCliente> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<SobranteCliente> findByIdCliente(Integer idCliente, Pageable pageable) {
        return repository.findByIdCliente(idCliente, pageable);
    }

    public SobranteCliente registrarSobrante(RegistrarSobranteDTO dto) {
        Optional<SobranteCliente> existente = repository.findByIdClienteAndProducto_IdProducto(
                dto.getIdCliente(), dto.getIdProducto());

        if (existente.isPresent()) {
            SobranteCliente sobrante = existente.get();
            sobrante.setCantidadDisponible(sobrante.getCantidadDisponible() + dto.getCantidad());

            Orden orden = new Orden();
            orden.setIdOrden(dto.getIdOrden());
            sobrante.setOrdenOrigen(orden);

            return repository.save(sobrante);
        } else {
            SobranteCliente nuevo = new SobranteCliente();
            nuevo.setIdCliente(dto.getIdCliente());
            nuevo.setCantidadDisponible(dto.getCantidad());

            Producto producto = new Producto();
            producto.setIdProducto(dto.getIdProducto());
            nuevo.setProducto(producto);

            Orden orden = new Orden();
            orden.setIdOrden(dto.getIdOrden());
            nuevo.setOrdenOrigen(orden);

            return repository.save(nuevo);
        }
    }

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