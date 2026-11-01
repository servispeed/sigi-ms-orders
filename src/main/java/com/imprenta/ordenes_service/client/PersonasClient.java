package com.imprenta.ordenes_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.imprenta.ordenes_service.dto.ClienteDTO;
import com.imprenta.ordenes_service.dto.UsuarioDTO;

@FeignClient(name = "personas-service", url = "http://10.0.0.1:8081")
public interface PersonasClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO obtenerUsuarioPorId(@PathVariable("id") Integer idUsuario);

    @GetMapping("/api/clientes/{id}")
    ClienteDTO obtenerClientePorId(@PathVariable("id") Integer id);

}
