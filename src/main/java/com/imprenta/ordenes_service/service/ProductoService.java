package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.dto.ProductoDTO;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.model.Producto;
import com.imprenta.ordenes_service.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Producto crearProducto(ProductoDTO productoDTO) {

        if (productoDTO.getNombre() == null || productoDTO.getNombre().isBlank()) {
            throw new BadRequestException("El producto añadido debe llevar nombre.");
        }
        if (productoDTO.getDescripcion() == null || productoDTO.getDescripcion().isBlank()) {
            throw new BadRequestException("El producto añadido debe llevar descripción.");
        }
        if (productoDTO.getPrecioUnitario() != null && productoDTO.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("El precio unitario debe ser positivo.");
        }
        if (productoDTO.getTirajeMinimo() != null && productoDTO.getTirajeMinimo() < 0) {
            throw new BadRequestException("Debe ser número positivo.");
        }
        if (productoRepository.findByNombre(productoDTO.getNombre()).isPresent()) {
            throw new BadRequestException("El nombre debe ser único por producto");
        }

        Producto nuevProducto = new Producto();
        nuevProducto.setNombre(productoDTO.getNombre());
        nuevProducto.setDescripcion(productoDTO.getDescripcion());
        nuevProducto.setPrecioUnitario(productoDTO.getPrecioUnitario());
        nuevProducto.setPrecioPaquete(productoDTO.getPrecioPaquete());
        nuevProducto.setCantidadPaquete(productoDTO.getCantidadPaquete());
        nuevProducto.setFormatoTamano(productoDTO.getFormatoTamano());
        nuevProducto.setTirajeMinimo(productoDTO.getTirajeMinimo());

        return productoRepository.save(nuevProducto);

    }

    @Transactional
    public Producto actualizarProducto(Integer idProducto, ProductoDTO productoDTO) {
        Producto productoExistente = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));
        productoRepository.findByNombre(productoDTO.getNombre())
                .ifPresent(producto -> {
                    if (!producto.getIdProducto().equals(idProducto)) {
                        throw new BadRequestException("El nombre debe ser unico, ya esta en uso por otro producto");
                    }
                });

        productoExistente.setNombre(productoDTO.getNombre());
        productoExistente.setDescripcion(productoDTO.getDescripcion());
        productoExistente.setPrecioUnitario(productoDTO.getPrecioUnitario());
        productoExistente.setPrecioPaquete(productoDTO.getPrecioPaquete());
        productoExistente.setCantidadPaquete(productoDTO.getCantidadPaquete());
        productoExistente.setFormatoTamano(productoDTO.getFormatoTamano());
        productoExistente.setTirajeMinimo(productoDTO.getTirajeMinimo());

        return productoRepository.save(productoExistente);
    }

    public List<Producto> buscarProductos(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return productoRepository.findAll();
        }
        return productoRepository.findByDescripcionContainingIgnoreCase(termino);
    }

    @Transactional
    public void eliminarProducto(Integer idProducto) {
        Producto productoExistente = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));
        try {
            productoRepository.delete(productoExistente);

            productoRepository.flush();

        } catch (Exception e) {
            throw new BadRequestException(
                    "No se puede eliminar el producto porque ya está ligado a una orden o cotización existente.");
        }
    }

}
