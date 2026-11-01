package com.imprenta.ordenes_service.dto.graficas;

import java.math.BigDecimal;
import java.util.List;

public class DashboardEgresosDTO {

    private BigDecimal montoEgresado;
    private List<BarraEgresosDTO> datosGrafica;

    public DashboardEgresosDTO(BigDecimal montoEgresado, List<BarraEgresosDTO> datosGrafica) {
        this.montoEgresado = montoEgresado;
        this.datosGrafica = datosGrafica;
    }

    public BigDecimal getMontoEgresado() {
        return montoEgresado;
    }

    public void setMontoEgresado(BigDecimal montoEgresado) {
        this.montoEgresado = montoEgresado;
    }

    public List<BarraEgresosDTO> getDatosGrafica() {
        return datosGrafica;
    }

    public void setDatosGrafica(List<BarraEgresosDTO> datosGrafica) {
        this.datosGrafica = datosGrafica;
    }

}
