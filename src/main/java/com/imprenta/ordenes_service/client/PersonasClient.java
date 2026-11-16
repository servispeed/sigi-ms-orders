package com.imprenta.ordenes_service.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.imprenta.ordenes_service.dto.ClienteDTO;
import com.imprenta.ordenes_service.dto.UsuarioDTO;

@FeignClient(name = "personas-service", url = "http://host.docker.internal:8081")
public interface PersonasClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO obtenerUsuarioPorId(@PathVariable("id") Integer idUsuario);

    @GetMapping("/api/clientes/{id}")
    ClienteDTO obtenerClientePorId(@PathVariable("id") Integer id);

    @PutMapping("/api/clientes/{idCliente}/credito")
    void actualizarCredito(@PathVariable("idCliente") Integer idCliente, @RequestBody Map<String, Object> dto);

}
