package com.imprenta.ordenes_service.repository;

import com.imprenta.ordenes_service.dto.OrdenAsignadaDTO;
import com.imprenta.ordenes_service.dto.reportes.GraficaPastelDTO;
import com.imprenta.ordenes_service.dto.reportes.GraficaRadarDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenCardDTO;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.dto.reportes.TopClienteDTO;
import com.imprenta.ordenes_service.dto.reportes.TopProductoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Integer> {

    Page<Orden> findByIdEstatusActualNotIn(List<Integer> estatusExcluidos, Pageable pageable);

    Page<Orden> findByIdEstatusActualIn(List<Integer> estatusIncluidos, Pageable pageable);

    Page<Orden> findByIdUsuarioDisenadorAndIdEstatusActualNotIn(
            Integer idUsuarioDisenador,
            List<Integer> estatusExcluidos,
            Pageable pageable);

    List<Orden> findByIdEstatusActual(Integer idEstatusActual);

    @Query(value = """
            SELECT
                o.id_orden as idOrden,
                c.nombre as nombreCliente,
                TO_CHAR(o.fecha_creacion, 'Month DD') as fecha,
                COALESCE(p_principal.descripcion, 'Varios Productos') as productoPrincipal,
                CASE
                    WHEN ce.id_estatus = 12 THEN 'Completado'
                    WHEN ce.id_estatus = 11 THEN 'Cancelada'
                    WHEN ce.id_estatus = 6  THEN 'Lista para entrega'
                    ELSE 'En curso'
                END as estatus,
                ce.descripcion as descripcionEstatus,
                ce.clave as claveEstatus,
                TO_CHAR(o.fecha_creacion, 'YYYY-MM-DD') as fechaIso,
                u.nombre as nombreEncargado,
                o.monto_total as montoTotal,
                COALESCE(o.monto_pagado, 0) as montoPagado,
                COALESCE(TO_CHAR(o.fecha_entrega_formal, 'DD Mon YYYY'), 'Por definir') as fechaEntrega,
                (
                    SELECT json_agg(json_build_object(
                        'cantidad', do2.cantidad,
                        'descripcion', p2.descripcion
                    ))::text
                    FROM detalle_orden do2
                    JOIN productos p2 ON do2.id_producto = p2.id_producto
                    WHERE do2.id_orden = o.id_orden
                ) as detallesJson
            FROM ordenes o
            JOIN clientes c ON o.id_cliente = c.id_cliente
            JOIN cat_estatus ce ON o.id_estatus_actual = ce.id_estatus
            JOIN usuarios u ON o.id_usuario = u.id_usuario
            LEFT JOIN LATERAL (
                SELECT p.descripcion
                FROM detalle_orden do3
                JOIN productos p ON do3.id_producto = p.id_producto
                WHERE do3.id_orden = o.id_orden
                LIMIT 1
            ) p_principal ON true
            WHERE
                (CAST(:fecha AS text) IS NULL OR TO_CHAR(o.fecha_creacion, 'YYYY-MM-DD') LIKE CONCAT(CAST(:fecha AS text), '%'))
                AND (
                    CAST(:estado AS text) IS NULL
                    OR (CAST(:estado AS text) = 'Todas' AND ce.id_estatus != 1)
                    OR (CAST(:estado AS text) = 'Completadas' AND ce.id_estatus = 12)
                    OR (CAST(:estado AS text) = 'Canceladas' AND ce.id_estatus = 11)
                    OR (CAST(:estado AS text) = 'Listas' AND ce.id_estatus IN (6, 12))
                    OR (CAST(:estado AS text) = 'En curso' AND ce.id_estatus NOT IN (1,6, 11, 12))
                )
                AND (
                    CAST(:busqueda AS text) IS NULL
                    OR CAST(o.id_orden AS text) ILIKE CONCAT('%', CAST(:busqueda AS text), '%')
                    OR c.nombre ILIKE CONCAT('%', CAST(:busqueda AS text), '%')
                    OR p_principal.descripcion ILIKE CONCAT('%', CAST(:busqueda AS text), '%')
                )
            ORDER BY o.fecha_creacion DESC
            """, countQuery = """
            SELECT count(o.id_orden)
            FROM ordenes o
            JOIN clientes c ON o.id_cliente = c.id_cliente
            JOIN cat_estatus ce ON o.id_estatus_actual = ce.id_estatus
            LEFT JOIN LATERAL (
                SELECT p.descripcion
                FROM detalle_orden do3
                JOIN productos p ON do3.id_producto = p.id_producto
                WHERE do3.id_orden = o.id_orden
                LIMIT 1
            ) p_principal ON true
            WHERE
                (CAST(:fecha AS text) IS NULL OR TO_CHAR(o.fecha_creacion, 'YYYY-MM-DD') LIKE CONCAT(CAST(:fecha AS text), '%'))
                AND (
                    CAST(:estado AS text) IS NULL
                    OR (CAST(:estado AS text) = 'Todas' AND ce.id_estatus != 1)
                    OR (CAST(:estado AS text) = 'Completadas' AND ce.id_estatus = 12)
                    OR (CAST(:estado AS text) = 'Canceladas' AND ce.id_estatus = 11)
                    OR (CAST(:estado AS text) = 'Listas' AND ce.id_estatus IN (6, 12))
                    OR (CAST(:estado AS text) = 'En curso' AND ce.id_estatus NOT IN (1, 6, 11, 12))
                )
                AND (
                    CAST(:busqueda AS text) IS NULL
                    OR CAST(o.id_orden AS text) ILIKE CONCAT('%', CAST(:busqueda AS text), '%')
                    OR c.nombre ILIKE CONCAT('%', CAST(:busqueda AS text), '%')
                    OR p_principal.descripcion ILIKE CONCAT('%', CAST(:busqueda AS text), '%')
                )
            """, nativeQuery = true)
    Page<OrdenCardDTO> obtenerTarjetasMovilDinamico(
            @Param("busqueda") String busqueda,
            @Param("estado") String estado,
            @Param("fecha") String fecha,
            Pageable pageable);

    @Query(value = """
            SELECT
                o.id_orden as idOrden,
                o.id_usuario_disenador as idDisenador,
                TO_CHAR(o.fecha_entrega_formal, 'DD Mon') as fechaEntrega,
                ce.descripcion as estatus,
                (SELECT p.descripcion FROM detalle_orden do2
                 JOIN productos p ON do2.id_producto = p.id_producto
                 WHERE do2.id_orden = o.id_orden LIMIT 1) as producto
            FROM ordenes o
            JOIN cat_estatus ce ON o.id_estatus_actual = ce.id_estatus
            WHERE o.id_usuario_disenador IS NOT NULL
              AND o.id_estatus_actual IN (3, 4, 7, 8, 10)
            ORDER BY o.fecha_entrega_formal ASC
            """, nativeQuery = true)
    List<OrdenAsignadaDTO> obtenerOrdenesPorDisenador();

    @Query(value = """
            SELECT
                CASE
                    WHEN o.id_estatus_actual = 1 THEN 'Pendientes'
                    WHEN o.id_estatus_actual = 11 THEN 'Canceladas'
                    ELSE 'Aprobadas'
                END as categoria,
                COUNT(*) as cantidad
            FROM ordenes o
            WHERE
                (CAST(:inicio AS timestamp) IS NULL OR o.fecha_creacion >= :inicio)
                AND
                (CAST(:fin AS timestamp) IS NULL OR o.fecha_creacion <= :fin)
            GROUP BY categoria
            """, nativeQuery = true)
    List<GraficaPastelDTO> obtenerResumenEstatus(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query(value = """
            SELECT
                r.descripcion AS etiqueta,
                COUNT(o.id_orden) AS valor
            FROM ordenes o
            JOIN cat_razones_cancelacion r
                ON o.id_razon_cancelacion = r.id_razon
            WHERE
                o.id_estatus_actual = 11
                AND
                (CAST(:inicio AS timestamp) IS NULL OR o.fecha_creacion >= :inicio)
                AND
                (CAST(:fin AS timestamp) IS NULL OR o.fecha_creacion <= :fin)
            GROUP BY r.descripcion
            ORDER BY valor DESC
            """, nativeQuery = true)
    List<GraficaRadarDTO> obtenerRazonesRechazo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(o) FROM Orden o WHERE o.fechaCreacion BETWEEN :fechaInicio AND :fechaFin AND o.idEstatusActual = 1")
    Long contarCotizacionesMes(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(o) FROM Orden o WHERE o.idEstatusActual NOT IN (1, 11, 12)")
    Long contarOrdenesActivas();

    @Query("SELECT COUNT(o) FROM Orden o WHERE (o.montoTotal > COALESCE(o.montoPagado, 0)) AND o.idEstatusActual NOT IN (1, 11)")
    Long contarPagosPendientes();

    @Query("SELECT SUM(o.montoTotal - COALESCE(o.montoPagado, 0)) FROM Orden o WHERE (o.montoTotal > COALESCE(o.montoPagado, 0)) AND o.idEstatusActual NOT IN (1, 11)")
    Double sumarDineroPendiente();

    @Query("SELECT o FROM Orden o WHERE o.fechaCreacion BETWEEN :inicio AND :fin AND o.idEstatusActual NOT IN (1, 11)")
    List<Orden> encontrarOrdenesParaFinanzas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT o From Orden o WHERE o.idEstatusActual = 1 AND o.fechaCreacion >= :inicio AND o.fechaCreacion <= :fin ORDER BY o.fechaCreacion DESC")
    List<Orden> findCotizacionesPorRangoDeFechas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(o) FROM Orden o WHERE o.fechaCreacion >= :inicio AND o.fechaCreacion <= :fin")
    Long countOrdenesPorRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(o.montoTotal), 0) FROM Orden o WHERE o.fechaCreacion >= :inicio AND o.fechaCreacion <= :fin")
    BigDecimal sumMontoOrdenesPorRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT o FROM Orden o WHERE o.idEstatusActual > 1 AND o.fechaCreacion >= :inicio AND o.fechaCreacion <= :fin ORDER BY o.fechaCreacion ASC")
    List<Orden> findOrdenesPorRangoParaGrafica(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT o FROM Orden o WHERE o.idEstatusActual = 1 AND o.fechaCreacion >= :inicio AND o.fechaCreacion <= :fin ORDER BY o.fechaCreacion ASC")
    List<Orden> findCotizacionesPorRangoParaGrafica(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query(value = "SELECT " +
            "COALESCE((SELECT SUM(monto_total) FROM ordenes WHERE id_cliente = :idCliente AND id_estatus_actual >= 2 AND id_estatus_actual != 11), 0) - "
            +
            "COALESCE((SELECT SUM(monto) FROM movimientos WHERE id_cliente = :idCliente), 0)", nativeQuery = true)
    BigDecimal calcularSaldoDeudor(@Param("idCliente") Integer idCliente);

    @Query(value = "SELECT DISTINCT id_cliente FROM ordenes WHERE fecha_vencimiento < CURRENT_TIMESTAMP AND id_estatus_actual NOT IN (1, 11)", nativeQuery = true)
    List<Integer> encontrarClientesConOrdenesVencidas();

    @Query("SELECT COUNT(o) FROM Orden o WHERE o.idCliente = :idCliente AND o.idEstatusActual = 12")
    Integer contarComprasCompletadasPorCliente(@Param("idCliente") Integer idCliente);

    @Query(value = """
            SELECT
                o.id_cliente AS "idCliente",
                COALESCE(SUM(o.monto_total), 0) -
                COALESCE((
                    SELECT SUM(m.monto)
                    FROM movimientos m
                    WHERE m.id_cliente = o.id_cliente
                ), 0) AS "saldoDeudor"
            FROM ordenes o
            WHERE o.id_cliente IS NOT NULL
                AND o.id_estatus_actual >= 2
                AND o.id_estatus_actual != 11
            GROUP BY o.id_cliente
            HAVING
                COALESCE(SUM(o.monto_total), 0) -
                COALESCE((
                    SELECT SUM(m.monto)
                    FROM movimientos m
                    WHERE m.id_cliente = o.id_cliente
                ), 0) > 0
            ORDER BY "saldoDeudor" DESC
            """, countQuery = """
            SELECT COUNT(*)
            FROM (
                SELECT o.id_cliente
                FROM ordenes o
                WHERE o.id_cliente IS NOT NULL
                    AND o.id_estatus_actual >= 2
                    AND o.id_estatus_actual != 11
                GROUP BY o.id_cliente
                HAVING
                    COALESCE(SUM(o.monto_total), 0) -
                    COALESCE((
                        SELECT SUM(m.monto)
                        FROM movimientos m
                        WHERE m.id_cliente = o.id_cliente
                    ), 0) > 0
            ) AS clientes_con_saldo
            """, nativeQuery = true)
    Page<ClienteSaldoProjection> encontrarClientesConSaldoDeudor(Pageable pageable);

    @Query(value = """
            SELECT p.descripcion as descripcion, SUM(do2.cantidad) as cantidad
            FROM detalle_orden do2
            JOIN productos p ON do2.id_producto = p.id_producto
            JOIN ordenes o ON do2.id_orden = o.id_orden
            WHERE o.id_estatus_actual NOT IN (1, 11)
            GROUP BY p.descripcion
            ORDER BY cantidad DESC
            LIMIT 4
            """, nativeQuery = true)
    List<TopProductoDTO> obtenerProductosMasSolicitados();

    @Query(value = """
            SELECT c.nombre as nombre, COUNT(o.id_orden) as totalCompras
            FROM ordenes o
            JOIN clientes c ON o.id_cliente = c.id_cliente
            WHERE o.id_estatus_actual = 12
            GROUP BY c.id_cliente, c.nombre
            ORDER BY totalCompras DESC
            LIMIT 5
            """, nativeQuery = true)
    List<TopClienteDTO> obtenerTopClientesPagados();
}