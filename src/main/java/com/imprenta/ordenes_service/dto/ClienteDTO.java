package com.imprenta.ordenes_service.dto;

import java.math.BigDecimal;

public class ClienteDTO {
    private Boolean creditoHabilitado;
    private String estatusCredito;
    private Integer diasCredito;
    private BigDecimal limiteCredito;
    private BigDecimal saldoActual;

    public Boolean getCreditoHabilitado() {
        return creditoHabilitado;
    }

    public void setCreditoHabilitado(Boolean creditoHabilitado) {
        this.creditoHabilitado = creditoHabilitado;
    }

    public String getEstatusCredito() {
        return estatusCredito;
    }

    public void setEstatusCredito(String estatusCredito) {
        this.estatusCredito = estatusCredito;
    }

    public Integer getDiasCredito() {
        return diasCredito;
    }

    public void setDiasCredito(Integer diasCredito) {
        this.diasCredito = diasCredito;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }
}