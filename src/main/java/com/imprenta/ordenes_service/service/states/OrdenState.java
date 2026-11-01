package com.imprenta.ordenes_service.service.states;

import com.imprenta.ordenes_service.model.Orden;
import java.util.Map;

public interface OrdenState {
    Integer getIdEstatusRepresentado();

    Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params);
}
