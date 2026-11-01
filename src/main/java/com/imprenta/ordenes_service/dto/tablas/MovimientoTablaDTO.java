package com.imprenta.ordenes_service.dto.tablas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoTablaDTO {

    private LocalDateTime fecha;
    private String concepto;
    private String tipoMovimiento;
    private BigDecimal monto;
    private String usuario;

    public MovimientoTablaDTO(LocalDateTime fecha, String concepto, String tipoMovimiento, BigDecimal monto,
            String usuario) {
        this.fecha = fecha;
        this.concepto = concepto;
        this.tipoMovimiento = tipoMovimiento;
        this.monto = monto;
        this.usuario = usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

}
