package com.imprenta.ordenes_service.service.states;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoDisenoEnProceso implements OrdenState {

    private static final Integer ID_ACTUAL = 7;
    private static final Integer ID_SIGUIENTE = 8;

    private final SecurityHelper securityHelper;

    @Autowired
    public EstadoDisenoEnProceso(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ACTUAL;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {

        securityHelper.validarEsDiseñadorAsignado(idUsuario, orden.getIdUsuarioDisenador());

        if (orden.getRutaArchivo() == null || orden.getRutaArchivo().isEmpty()) {
            throw new BadRequestException("Error: Debes subir el archivo de diseño final antes de enviar a impresión.");
        }

        return ID_SIGUIENTE;
    }
}