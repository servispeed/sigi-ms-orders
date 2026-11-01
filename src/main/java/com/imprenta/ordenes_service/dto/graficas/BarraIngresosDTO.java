package com.imprenta.ordenes_service.dto.graficas;

public class BarraIngresosDTO {
    private Double value;
    private String label;
    private String frontColor;

    public BarraIngresosDTO(Double value, String label, String frontColor) {
        this.value = value;
        this.label = label;
        this.frontColor = frontColor;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFrontColor() {
        return frontColor;
    }

    public void setFrontColor(String frontColor) {
        this.frontColor = frontColor;
    }

}
