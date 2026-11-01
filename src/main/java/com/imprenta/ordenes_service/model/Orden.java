package com.imprenta.ordenes_service.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Integer idOrden;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "id_usuario_disenador")
    private Integer idUsuarioDisenador;

    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "plazo_estimado_dias")
    private Integer plazoEstimadoDias;

    @Column(name = "fecha_entrega_formal")
    private LocalDate fechaEntregaFormal;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "requiere_factura", nullable = false)
    private boolean requiereFactura;

    @Column(name = "id_condicion_pago")
    private Integer idCondicionPago;

    @Column(name = "ruta_archivo", nullable = false, length = 255)
    private String rutaArchivo;

    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;

    @Column(name = "insumos_verificados")
    private Boolean insumosVerificados;

    @Column(name = "id_estatus_actual")
    private Integer idEstatusActual;

    @Column(name = "monto_pagado", precision = 10, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "id_razon_cancelacion")
    private Integer idRazonCancelacion;

    @Column(name = "notas_diseno", columnDefinition = "TEXT")
    private String notasDiseno;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<DetalleOrden> detalles;

    public Orden() {

    }

    public Integer getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdUsuarioDisenador() {
        return idUsuarioDisenador;
    }

    public void setIdUsuarioDisenador(Integer idUsuarioDisenador) {
        this.idUsuarioDisenador = idUsuarioDisenador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getPlazoEstimadoDias() {
        return plazoEstimadoDias;
    }

    public void setPlazoEstimadoDias(Integer plazoEStimadoDias) {
        this.plazoEstimadoDias = plazoEStimadoDias;
    }

    public LocalDate getFechaEntregaFormal() {
        return fechaEntregaFormal;
    }

    public void setFechaEntregaFormal(LocalDate fechaEntregaFormal) {
        this.fechaEntregaFormal = fechaEntregaFormal;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public boolean isRequiereFactura() {
        return requiereFactura;
    }

    public void setRequiereFactura(boolean requiereFactura) {
        this.requiereFactura = requiereFactura;
    }

    public Integer getIdCondicionPago() {
        return idCondicionPago;
    }

    public void setIdCondicionPago(Integer idCondicionPago) {
        this.idCondicionPago = idCondicionPago;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public Boolean getInsumosVerificados() {
        return insumosVerificados;
    }

    public void setInsumosVerificados(Boolean insumosVerificados) {
        this.insumosVerificados = insumosVerificados;
    }

    public Integer getIdEstatusActual() {
        return idEstatusActual;
    }

    public void setIdEstatusActual(Integer idEstatusActual) {
        this.idEstatusActual = idEstatusActual;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdRazonCancelacion() {
        return idRazonCancelacion;
    }

    public void setIdRazonCancelacion(Integer idRazonCancelacion) {
        this.idRazonCancelacion = idRazonCancelacion;
    }

    public List<DetalleOrden> getDetalles() {
        return detalles;
    }

    public String getNotasDiseno() {
        return notasDiseno;
    }

    public void setNotasDiseno(String notasDiseno) {
        this.notasDiseno = notasDiseno;
    }

    public void setDetalles(List<DetalleOrden> detalles) {
        this.detalles = detalles;
        if (detalles != null) {
            for (DetalleOrden detalle : detalles) {
                detalle.setOrden(this);
            }
        }
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

}
