package com.imprenta.ordenes_service.service.states;

import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Component
public class OrdenStateFactory {

    private final Map<Integer, OrdenState> mapaEstados = new HashMap<>();

    // Spring busca todas las clases que implementen 'OrdenState' y las inyecta aquí
    public OrdenStateFactory(List<OrdenState> estados) {
        for (OrdenState estado : estados) {
            mapaEstados.put(estado.getIdEstatusRepresentado(), estado);
        }
    }

    public OrdenState obtenerEstado(Integer idEstatus) {
        OrdenState estado = mapaEstados.get(idEstatus);
        if (estado == null) {
            throw new BadRequestException("No hay lógica definida para el estatus: " + idEstatus);
        }
        return estado;
    }
}