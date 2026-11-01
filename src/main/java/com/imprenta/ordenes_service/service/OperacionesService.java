package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.dto.AjusteCajaDTO;
import com.imprenta.ordenes_service.dto.EgresoDTO;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.model.CatCondicionesPago;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.repository.CatCondicionesPagoRepository;
import com.imprenta.ordenes_service.repository.CatTipoMovimientoRepository;
import com.imprenta.ordenes_service.repository.MovimientoRepository;

import jakarta.transaction.Transactional;

@Service
public class OperacionesService {

    private final CatCondicionesPagoRepository condicionesPagoRepository;
    private final CatTipoMovimientoRepository catTipoMovimientoRepository;
    private final MovimientoRepository movimientoRepository;

    @Autowired
    public OperacionesService(CatCondicionesPagoRepository condicionesPagoRepository,
            CatTipoMovimientoRepository catTipoMovimientoRepository,
            MovimientoRepository movimientoRepository) {
        this.condicionesPagoRepository = condicionesPagoRepository;
        this.catTipoMovimientoRepository = catTipoMovimientoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public List<CatCondicionesPago> obtenerTodasLasCondiciones() {
        return condicionesPagoRepository.findAll();
    }

    @Transactional
    public Movimientos registrarEgreso(EgresoDTO dto) {
        catTipoMovimientoRepository.findById(dto.getIdTipoMovimiento())
                .orElseThrow(() -> new BadRequestException(
                        "La categoría de gasto no existe con el ID: " + dto.getIdTipoMovimiento()));

        Movimientos nuevoEgreso = new Movimientos();
        nuevoEgreso.setMonto(dto.getMonto());
        nuevoEgreso.setConcepto(dto.getConcepto());
        nuevoEgreso.setIdTipoMovimiento(dto.getIdTipoMovimiento());
        nuevoEgreso.setIdOrden(null);
        nuevoEgreso.setIdUsuarioRegistro(dto.getIdUsuarioRegistro());
        nuevoEgreso.setFechaMovimiento(LocalDateTime.now());

        return movimientoRepository.save(nuevoEgreso);
    }

    public void registrarAjusteCaja(AjusteCajaDTO ajusteCajaDTO) {
        if (ajusteCajaDTO.getMonto() == null || ajusteCajaDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del ajuste debe ser mayor a cero.");
        }
        if (ajusteCajaDTO.getConcepto() == null || ajusteCajaDTO.getConcepto().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un concepto válido para el ajuste");
        }
        if (ajusteCajaDTO.getTipoAjuste() == null || (!ajusteCajaDTO.getTipoAjuste().equalsIgnoreCase("ENTRADA")
                && !ajusteCajaDTO.getTipoAjuste().equalsIgnoreCase("SALIDA"))) {
            throw new IllegalArgumentException("El tipo de ajuste debe ser estrictamente ENTRADA o SALIDA");
        }

        Movimientos nuevoMovimiento = new Movimientos();
        nuevoMovimiento.setFechaMovimiento(LocalDateTime.now());
        nuevoMovimiento.setConcepto(ajusteCajaDTO.getConcepto());
        nuevoMovimiento.setIdUsuarioRegistro(ajusteCajaDTO.getIdUsuario());

        nuevoMovimiento.setIdTipoMovimiento(5);

        if ("ENTRADA".equalsIgnoreCase(ajusteCajaDTO.getTipoAjuste())) {
            nuevoMovimiento.setMonto(ajusteCajaDTO.getMonto().negate());
        } else {
            nuevoMovimiento.setMonto(ajusteCajaDTO.getMonto());
        }

        movimientoRepository.save(nuevoMovimiento);
    }
}
