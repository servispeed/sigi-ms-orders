package com.imprenta.ordenes_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EgresoDTO {

    private Integer idMovimiento;
    private String concepto;
    private BigDecimal monto;
    private LocalDateTime fechaMovimiento;
    private String tipoEgreso;
    private Integer idTipoMovimiento;
    private Integer idOrden;
    private Integer idUsuarioRegistro;

    public EgresoDTO() {
    }

    public EgresoDTO(Integer idMovimiento, String concepto, BigDecimal monto, LocalDateTime fechaMovimiento,
            String tipoEgreso, Integer idOrden, Integer idUsuarioRegistro) {
        this.idMovimiento = idMovimiento;
        this.concepto = concepto;
        this.monto = monto;
        this.fechaMovimiento = fechaMovimiento;
        this.tipoEgreso = tipoEgreso;
        this.idOrden = idOrden;
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getTipoEgreso() {
        return tipoEgreso;
    }

    public void setTipoEgreso(String tipoEgreso) {
        this.tipoEgreso = tipoEgreso;
    }

    public Integer getIdTipoMovimiento() {
        return idTipoMovimiento;
    }

    public void setIdTipoMovimiento(Integer idTipoMovimiento) {
        this.idTipoMovimiento = idTipoMovimiento;
    }

    public Integer getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
    }

    public Integer getIdUsuarioRegistro() {
        return idUsuarioRegistro;
    }

    public void setIdUsuarioRegistro(Integer idUsuarioRegistro) {
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

}