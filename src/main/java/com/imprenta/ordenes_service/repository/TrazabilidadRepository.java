package com.imprenta.ordenes_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.imprenta.ordenes_service.dto.reportes.HistorialOrdenDTO;
import com.imprenta.ordenes_service.model.Trazabilidad;

@Repository
public interface TrazabilidadRepository extends JpaRepository<Trazabilidad, Integer> {

    @Query(value = """
            SELECT
                ce.descripcion as estatus,
                ce.clave as claveEstatus,
                u.nombre as usuario,
                TO_CHAR(t.fecha_actualizacion, 'DD Mon YYYY - HH12:MI AM') as fecha
            FROM trazabilidad t
            JOIN cat_estatus ce ON t.id_estatus = ce.id_estatus
            JOIN usuarios u ON t.id_usuario = u.id_usuario
            WHERE t.id_orden = :idOrden
            ORDER BY t.fecha_actualizacion ASC
            """, nativeQuery = true)
    List<HistorialOrdenDTO> obtenerHistorialPorOrden(Integer idOrden);

}
