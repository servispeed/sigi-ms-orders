package com.imprenta.ordenes_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imprenta.ordenes_service.dto.ProductoDTO;
import com.imprenta.ordenes_service.model.Producto;
import com.imprenta.ordenes_service.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductosController {
    private final ProductoService productoService;

    @Autowired
    public ProductosController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody ProductoDTO productoDTO) {
        Producto producto = productoService.crearProducto(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @GetMapping
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam(required = false, defaultValue = "") String q) {
        List<Producto> productos = productoService.buscarProductos(q);
        return ResponseEntity.ok(productos);
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer idProducto,
            @RequestBody ProductoDTO productoDTO) {
        Producto productoActualizado = productoService.actualizarProducto(idProducto, productoDTO);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer idProducto) {
        productoService.eliminarProducto(idProducto);
        return ResponseEntity.noContent().build();
    }
}
