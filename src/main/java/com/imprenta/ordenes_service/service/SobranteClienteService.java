package com.imprenta.ordenes_service.service;

import com.imprenta.ordenes_service.dto.RegistrarSobranteDTO;
import com.imprenta.ordenes_service.dto.SobranteResponseDTO;
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

    public Page<SobranteResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::mapearADTO);
    }

    public Page<SobranteResponseDTO> findByIdCliente(Integer idCliente, Pageable pageable) {
        return repository.findByIdCliente(idCliente, pageable).map(this::mapearADTO);
    }

    @Transactional
    public SobranteResponseDTO registrarSobrante(RegistrarSobranteDTO dto) {
        Producto productoReal = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getIdProducto()));

        Orden ordenReal = ordenRepository.findById(dto.getIdOrden())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + dto.getIdOrden()));

        Optional<SobranteCliente> existente = repository.findByIdClienteAndProducto_IdProducto(
                dto.getIdCliente(), dto.getIdProducto());

        if (existente.isPresent()) {
            SobranteCliente sobrante = existente.get();
            sobrante.setCantidadDisponible(sobrante.getCantidadDisponible() + dto.getCantidad());
            sobrante.setOrdenOrigen(ordenReal);
            return mapearADTO(repository.save(sobrante));
        } else {
            SobranteCliente nuevo = new SobranteCliente();
            nuevo.setIdCliente(dto.getIdCliente());
            nuevo.setCantidadDisponible(dto.getCantidad());
            nuevo.setProducto(productoReal);
            nuevo.setOrdenOrigen(ordenReal);
            return mapearADTO(repository.save(nuevo));
        }
    }

    @Transactional
    public SobranteResponseDTO consumirSobrante(Integer idSobrante, Integer cantidadConsumida) {
        return repository.findById(idSobrante).map(sobrante -> {
            if (sobrante.getCantidadDisponible() < cantidadConsumida) {
                throw new IllegalArgumentException("No hay suficiente cantidad disponible del sobrante.");
            }
            sobrante.setCantidadDisponible(sobrante.getCantidadDisponible() - cantidadConsumida);
            return mapearADTO(repository.save(sobrante));
        }).orElseThrow(() -> new RuntimeException("Sobrante no encontrado"));
    }

    public SobranteResponseDTO mapearADTO(SobranteCliente entidad) {
        SobranteResponseDTO dto = new SobranteResponseDTO();
        dto.setIdSobrante(entidad.getIdSobrante());
        dto.setIdCliente(entidad.getIdCliente());
        dto.setCantidadDisponible(entidad.getCantidadDisponible());
        dto.setFechaRegistro(entidad.getFechaRegistro());

        dto.setProducto(new SobranteResponseDTO.ProductoMiniDTO(
                entidad.getProducto().getIdProducto()));

        dto.setOrdenOrigen(new SobranteResponseDTO.OrdenMiniDTO(
                entidad.getOrdenOrigen().getIdOrden(),
                entidad.getOrdenOrigen().getIdCliente()));

        return dto;
    }
}