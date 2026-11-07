package com.imprenta.ordenes_service.dto;

import java.time.LocalDateTime;

public class SobranteResponseDTO {
    private Integer idSobrante;
    private Integer idCliente;
    private ProductoMiniDTO producto;
    private OrdenMiniDTO ordenOrigen;
    private Integer cantidadDisponible;
    private LocalDateTime fechaRegistro;

    public static class ProductoMiniDTO {
        private Integer idProducto;

        public ProductoMiniDTO(Integer idProducto) {
            this.idProducto = idProducto;
        }

        public Integer getIdProducto() {
            return idProducto;
        }

        public void setIdProducto(Integer idProducto) {
            this.idProducto = idProducto;
        }
    }

    public static class OrdenMiniDTO {
        private Integer idOrden;
        private Integer idCliente;

        public OrdenMiniDTO(Integer idOrden, Integer idCliente) {
            this.idOrden = idOrden;
            this.idCliente = idCliente;
        }

        public Integer getIdOrden() {
            return idOrden;
        }

        public void setIdOrden(Integer idOrden) {
            this.idOrden = idOrden;
        }

        public Integer getIdCliente() {
            return idCliente;
        }

        public void setIdCliente(Integer idCliente) {
            this.idCliente = idCliente;
        }
    }

    public Integer getIdSobrante() {
        return idSobrante;
    }

    public void setIdSobrante(Integer idSobrante) {
        this.idSobrante = idSobrante;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public ProductoMiniDTO getProducto() {
        return producto;
    }

    public void setProducto(ProductoMiniDTO producto) {
        this.producto = producto;
    }

    public OrdenMiniDTO getOrdenOrigen() {
        return ordenOrigen;
    }

    public void setOrdenOrigen(OrdenMiniDTO ordenOrigen) {
        this.ordenOrigen = ordenOrigen;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}