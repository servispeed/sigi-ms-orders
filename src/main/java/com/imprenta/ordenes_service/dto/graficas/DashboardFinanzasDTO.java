package com.imprenta.ordenes_service.dto.graficas;

import java.math.BigDecimal;
import java.util.List;

public class DashboardFinanzasDTO {
    private BigDecimal balanceTotal;
    private List<GraficaFinanzasDTO> datosGrafica;

    public DashboardFinanzasDTO(BigDecimal balanceTotal, List<GraficaFinanzasDTO> datosGrafica) {
        this.balanceTotal = balanceTotal;
        this.datosGrafica = datosGrafica;
    }

    public BigDecimal getBalanceTotal() {
        return balanceTotal;
    }

    public void setBalanceTotal(BigDecimal balanceTotal) {
        this.balanceTotal = balanceTotal;
    }

    public List<GraficaFinanzasDTO> getDatosGrafica() {
        return datosGrafica;
    }

    public void setDatosGrafica(List<GraficaFinanzasDTO> datosGrafica) {
        this.datosGrafica = datosGrafica;
    }
}
