package com.imprenta.ordenes_service.dto.graficas;

import java.math.BigDecimal;

public class BarraEgresosDTO {

    private String label;
    private BigDecimal total;

    public BarraEgresosDTO(String label, BigDecimal total) {
        this.label = label;
        this.total = total;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

}
