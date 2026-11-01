package com.imprenta.ordenes_service.dto.graficas;

import java.math.BigDecimal;
import java.util.List;

public class DashboardIngresosDTO {
    private BigDecimal montoPagado;
    private List<BarraIngresosDTO> datosGrafica;

    public DashboardIngresosDTO(BigDecimal montoPagado, List<BarraIngresosDTO> datosGrafica) {
        this.montoPagado = montoPagado;
        this.datosGrafica = datosGrafica;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public List<BarraIngresosDTO> getDatosGrafica() {
        return datosGrafica;
    }

    public void setDatosGrafica(List<BarraIngresosDTO> datosGrafica) {
        this.datosGrafica = datosGrafica;
    }

}
