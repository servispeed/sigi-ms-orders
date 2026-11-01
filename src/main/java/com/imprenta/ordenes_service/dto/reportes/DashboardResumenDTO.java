package com.imprenta.ordenes_service.dto.reportes;

public class DashboardResumenDTO {
    private Long cotizacionesMes;
    private Long ordenesActivas;
    private Long pagosPendientes;
    private Double pagoTotal;

    public DashboardResumenDTO(Long cotizacionesMes, Long ordenesActivas, Long pagosPendientes, Double pagoTotal) {
        this.cotizacionesMes = cotizacionesMes;
        this.ordenesActivas = ordenesActivas;
        this.pagosPendientes = pagosPendientes;
        this.pagoTotal = pagoTotal;
    }

    public Long getCotizacionesMes() {
        return cotizacionesMes;
    }

    public void setCotizacionesMes(Long cotizacionesMes) {
        this.cotizacionesMes = cotizacionesMes;
    }

    public Long getOrdenesActivas() {
        return ordenesActivas;
    }

    public void setOrdenesActivas(Long ordenesActivas) {
        this.ordenesActivas = ordenesActivas;
    }

    public Long getPagosPendientes() {
        return pagosPendientes;
    }

    public void setPagosPendientes(Long pagosPendientes) {
        this.pagosPendientes = pagosPendientes;
    }

    public Double getPagoTotal() {
        return pagoTotal;
    }

    public void setPagoTotal(Double pagoTotal) {
        this.pagoTotal = pagoTotal;
    }

}
