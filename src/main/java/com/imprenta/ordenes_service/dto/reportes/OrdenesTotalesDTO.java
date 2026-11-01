package com.imprenta.ordenes_service.dto.reportes;

import java.math.BigDecimal;

public class OrdenesTotalesDTO {

    private String periodo;
    private Long totalOrdenes;
    private BigDecimal montoTotalGenerado;

    public OrdenesTotalesDTO(String periodo, Long totalOrdenes, BigDecimal montoTotalGenerado) {
        this.periodo = periodo;
        this.totalOrdenes = totalOrdenes;
        this.montoTotalGenerado = montoTotalGenerado;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Long getTotalOrdenes() {
        return totalOrdenes;
    }

    public void setTotalOrdenes(Long totalOrdenes) {
        this.totalOrdenes = totalOrdenes;
    }

    public BigDecimal getMontoTotalGenerado() {
        return montoTotalGenerado;
    }

    public void setMontoTotalGenerado(BigDecimal montoTotalGenerado) {
        this.montoTotalGenerado = montoTotalGenerado;
    }
}
