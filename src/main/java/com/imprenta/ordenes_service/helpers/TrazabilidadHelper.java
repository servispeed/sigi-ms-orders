package com.imprenta.ordenes_service.helpers;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.imprenta.ordenes_service.dto.reportes.HistorialOrdenDTO;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.model.CatEstatus;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.model.Trazabilidad;
import com.imprenta.ordenes_service.repository.CatEstatusRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.repository.TrazabilidadRepository;

@Service
public class TrazabilidadHelper {

    private final OrdenRepository ordenRepository;
    private final TrazabilidadRepository trazabilidadRepository;
    private final CatEstatusRepository catEstatusRepository;

    @Autowired
    public TrazabilidadHelper(OrdenRepository ordenRepository,
            TrazabilidadRepository trazabilidadRepository,
            CatEstatusRepository catEstatusRepository) {
        this.ordenRepository = ordenRepository;
        this.trazabilidadRepository = trazabilidadRepository;
        this.catEstatusRepository = catEstatusRepository;
    }

    @Transactional
    public Trazabilidad registrarCambio(Integer idOrden, String claveEstatus, Integer idUsuario) {
        CatEstatus estatus = catEstatusRepository.findByClave(claveEstatus)
                .orElseThrow(() -> new ResourceNotFoundException("Estatus no encontrado: " + claveEstatus));

        Integer nuevoEstatusId = estatus.getIdEstatus();

        return ejecutarCambio(idOrden, nuevoEstatusId, idUsuario);
    }

    @Transactional
    public Trazabilidad registrarCambioPorId(Integer idOrden, Integer nuevoEstatusId, Integer idUsuario) {
        return ejecutarCambio(idOrden, nuevoEstatusId, idUsuario);
    }

    private Trazabilidad ejecutarCambio(Integer idOrden, Integer nuevoEstatusId, Integer idUsuario) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + idOrden));

        orden.setIdEstatusActual(nuevoEstatusId);
        ordenRepository.save(orden);

        Trazabilidad nuevaTraza = new Trazabilidad();
        nuevaTraza.setIdOrden(idOrden);
        nuevaTraza.setIdEstatus(nuevoEstatusId);
        nuevaTraza.setIdUsuario(idUsuario);
        nuevaTraza.setFechaActualizacion(LocalDateTime.now());

        return trazabilidadRepository.save(nuevaTraza);
    }

    public List<HistorialOrdenDTO> obtenerHistorial(Integer idOrden) {
        if (!ordenRepository.existsById(idOrden)) {
            throw new ResourceNotFoundException("Orden no encontrada con ID: " + idOrden);
        }
        return trazabilidadRepository.obtenerHistorialPorOrden(idOrden);
    }
}