package com.imprenta.ordenes_service.dto.tablas;

import java.math.BigDecimal;

public class FlujoCajaDTO {

    private BigDecimal totalIngresos;
    private BigDecimal entradaCaja;
    private BigDecimal totalEgresos;
    private BigDecimal balance;

    public FlujoCajaDTO(BigDecimal totalIngresos, BigDecimal entradaCaja, BigDecimal totalEgresos, BigDecimal balance) {
        this.totalIngresos = totalIngresos;
        this.entradaCaja = entradaCaja;
        this.totalEgresos = totalEgresos;
        this.balance = balance;
    }

    public BigDecimal getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(BigDecimal totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public BigDecimal getTotalEgresos() {
        return totalEgresos;
    }

    public void setTotalEgresos(BigDecimal totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getEntradaCaja() {
        return entradaCaja;
    }

    public void setEntradaCaja(BigDecimal entradaCaja) {
        this.entradaCaja = entradaCaja;
    }

}
