package com.imprenta.ordenes_service.dto;

import java.math.BigDecimal;

public class AjusteCajaDTO {

    private BigDecimal monto;
    private String concepto;
    private String tipoAjuste;
    private Integer idUsuario;

    public AjusteCajaDTO(BigDecimal monto, String concepto, String tipoAjuste, Integer idUsuario) {
        this.monto = monto;
        this.concepto = concepto;
        this.tipoAjuste = tipoAjuste;
        this.idUsuario = idUsuario;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(String tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

}
