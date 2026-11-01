package com.imprenta.ordenes_service.dto.graficas;

import java.math.BigDecimal;

public class GraficaFinanzasDTO {
    private String label;
    private BigDecimal ingresos;
    private BigDecimal egresos;

    public GraficaFinanzasDTO(String label, BigDecimal ingresos, BigDecimal egresos) {
        this.label = label;
        this.ingresos = ingresos;
        this.egresos = egresos;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public BigDecimal getEgresos() {
        return egresos;
    }

    public void setEgresos(BigDecimal egresos) {
        this.egresos = egresos;
    }

    public void sumarIngreso(BigDecimal monto) {
        this.ingresos = this.ingresos.add(monto);
    }

    public void sumarEgreso(BigDecimal monto) {
        this.egresos = this.egresos.add(monto);
    }
}
