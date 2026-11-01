package com.imprenta.ordenes_service.helpers;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.client.PersonasClient;
import com.imprenta.ordenes_service.dto.UsuarioDTO;
import com.imprenta.ordenes_service.exception.BadRequestException;

@Service
public class SecurityHelper {

    private final PersonasClient personasClient;

    public static final Integer ROL_DUENO = 1;
    public static final Integer ROL_ADMIN = 2;
    public static final Integer ROL_CONTADORA = 3;
    public static final Integer ROL_MOSTRADOR = 4;
    public static final Integer ROL_DISENADOR = 5;
    public static final Integer ROL_JEFE_TALLER = 6;

    @Autowired
    public SecurityHelper(PersonasClient personasClient) {
        this.personasClient = personasClient;
    }

    public void validarPermiso(Integer idUsuario, Integer... rolesPermitidos) {
        UsuarioDTO usuario = obtenerUsuarioSeguro(idUsuario);
        Integer rolUsuario = usuario.getIdRol();

        if (ROL_DUENO.equals(rolUsuario) || ROL_ADMIN.equals(rolUsuario)) {
            return;
        }

        List<Integer> permitidos = Arrays.asList(rolesPermitidos);
        if (!permitidos.contains(rolUsuario)) {
            throw new BadRequestException("ACCESO DENEGADO: El usuario '" + usuario.getNombre() +
                    "' no tiene los permisos necesarios.");
        }
    }

    public void validarEsDiseñadorAsignado(Integer idUsuario, Integer idAsignado) {
        UsuarioDTO usuario = obtenerUsuarioSeguro(idUsuario);
        Integer rol = usuario.getIdRol();

        if (ROL_DUENO.equals(rol) || ROL_ADMIN.equals(rol))
            return;

        if (ROL_DISENADOR.equals(rol)) {
            if (idAsignado == null || !idAsignado.equals(idUsuario)) {
                throw new BadRequestException(
                        "ACCESO DENEGADO: Solo el diseñador asignado a esta orden puede avanzar.");
            }
        } else {
            throw new BadRequestException("ACCESO DENEGADO: Se requieren permisos de diseño.");
        }
    }

    private UsuarioDTO obtenerUsuarioSeguro(Integer idUsuario) {
        try {
            UsuarioDTO u = personasClient.obtenerUsuarioPorId(idUsuario);
            if (u == null || u.getIdRol() == null) {
                throw new RuntimeException("Usuario o Rol nulo");
            }
            return u;
        } catch (Exception e) {
            throw new BadRequestException(
                    "Error de seguridad: No se pudo validar la identidad del usuario ID " + idUsuario);
        }
    }
}