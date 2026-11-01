package com.imprenta.ordenes_service.dto.reportes;

import java.math.BigDecimal;

public class CotizacionesGraficaDTO {
    private String label;
    private Long totalCotizaciones;
    private BigDecimal montoTotal;

    public CotizacionesGraficaDTO(String label, Long totalCotizaciones, BigDecimal montoTotal) {
        this.label = label;
        this.totalCotizaciones = totalCotizaciones;
        this.montoTotal = montoTotal;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getTotalCotizaciones() {
        return totalCotizaciones;
    }

    public void setTotalCotizaciones(Long totalCotizaciones) {
        this.totalCotizaciones = totalCotizaciones;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
}
