package com.imprenta.ordenes_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.imprenta.ordenes_service.model.Notificacion;

import feign.Param;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByReceptorIdAndLeidaFalseOrderByCreadaEnDesc(String receptorId);

    @Query(value = "SELECT u.id_usuario FROM usuarios u " +
            "INNER JOIN cat_roles r ON u.id_rol = r.id_rol " +
            "WHERE UPPER(r.descripcion) IN (:roles)", nativeQuery = true)
    List<String> findIdsUsuariosPorRoles(@Param("roles") List<String> roles);
}
