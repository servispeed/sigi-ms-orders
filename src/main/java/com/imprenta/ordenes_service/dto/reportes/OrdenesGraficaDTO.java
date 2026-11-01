package com.imprenta.ordenes_service.dto.reportes;

import java.math.BigDecimal;

public class OrdenesGraficaDTO {

    private String label;
    private Long totalOrdenes;
    private BigDecimal montoTotal;

    public OrdenesGraficaDTO(String label, Long totalOrdenes, BigDecimal montoTotal) {
        this.label = label;
        this.totalOrdenes = totalOrdenes;
        this.montoTotal = montoTotal;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getTotalOrdenes() {
        return totalOrdenes;
    }

    public void setTotalOrdenes(Long totalOrdenes) {
        this.totalOrdenes = totalOrdenes;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

}
