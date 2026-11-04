package com.imprenta.ordenes_service.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.imprenta.ordenes_service.dto.ConsumirSobranteDTO;
import com.imprenta.ordenes_service.dto.RegistrarSobranteDTO;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.model.SobranteCliente;
import com.imprenta.ordenes_service.service.SobranteClienteService;

@RestController
@RequestMapping("api/sobrantes")
public class SobranteClienteController {

    private final SobranteClienteService sobranteService;

    public SobranteClienteController(SobranteClienteService sobranteService) {
        this.sobranteService = sobranteService;
    }

    @GetMapping
    public ResponseEntity<Page<SobranteCliente>> obtenerTodosLosSobrantes(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<SobranteCliente> pagina = sobranteService.findAll(pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<Page<SobranteCliente>> obtenerSobrantesPorCliente(
            @PathVariable Integer idCliente,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<SobranteCliente> pagina = sobranteService.findByIdCliente(idCliente, pageable);
        return ResponseEntity.ok(pagina);
    }

    @PostMapping
    public ResponseEntity<SobranteCliente> registrarSobrante(@RequestBody RegistrarSobranteDTO dto) {

        SobranteCliente sobranteGuardado = sobranteService.registrarSobrante(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(sobranteGuardado.getIdSobrante())
                .toUri();

        return ResponseEntity.created(location).body(sobranteGuardado);
    }

    @PutMapping("/{idSobrante}/consumir")
    public ResponseEntity<?> consumirSobrante(
            @PathVariable Integer idSobrante,
            @RequestBody ConsumirSobranteDTO dto) {
        try {
            if (dto.getCantidadConsumida() == null || dto.getCantidadConsumida() <= 0) {
                throw new BadRequestException("La cantidad a consumir debe ser mayor a 0.");
            }

            SobranteCliente sobranteActualizado = sobranteService.consumirSobrante(idSobrante,
                    dto.getCantidadConsumida());
            return ResponseEntity.ok(sobranteActualizado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}