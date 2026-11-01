package com.imprenta.ordenes_service.dto;

import java.math.BigDecimal;

public class EgresoPorCategoriaDTO {
    private String categoria;
    private BigDecimal total;

    public EgresoPorCategoriaDTO(String categoria, BigDecimal total) {
        this.categoria = categoria;
        this.total = total;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

}
