package com.imprenta.ordenes_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.model.CatEstatus;
import com.imprenta.ordenes_service.model.CatRazonesCancelacion;
import com.imprenta.ordenes_service.model.CatRegimenFiscal;
import com.imprenta.ordenes_service.model.CatSolicitudEstatus;
import com.imprenta.ordenes_service.model.CatTipoMovimiento;
import com.imprenta.ordenes_service.model.CatUsoCfdi;
import com.imprenta.ordenes_service.repository.CatEstatusRepository;
import com.imprenta.ordenes_service.repository.CatRazonesCancelacionRepository;
import com.imprenta.ordenes_service.repository.CatRegimenFiscalRepository;
import com.imprenta.ordenes_service.repository.CatSolicitudEstatusRepository;
import com.imprenta.ordenes_service.repository.CatTipoMovimientoRepository;
import com.imprenta.ordenes_service.repository.CatUsoCfdiRepository;

@Service
public class CatalagosService {

    @Autowired
    private CatRazonesCancelacionRepository catRazonesCancelacionRepository;
    @Autowired
    private CatRegimenFiscalRepository catRegimenFiscalRepository;
    @Autowired
    private CatUsoCfdiRepository catUsoCfdiRepository;
    @Autowired
    private CatSolicitudEstatusRepository catSolicitudEstatusRepository;
    @Autowired
    private CatTipoMovimientoRepository catTipoMovimientoRepository;
    @Autowired
    private CatEstatusRepository catEstatusRepository;

    public List<CatRazonesCancelacion> obtenerRazonesCancelacion() {
        return catRazonesCancelacionRepository.findAll();
    }

    public List<CatRegimenFiscal> obtenerRegimenesFiscales() {
        return catRegimenFiscalRepository.findAll();
    }

    public List<CatUsoCfdi> obtenerUsosCfdi() {
        return catUsoCfdiRepository.findAll();
    }

    public List<CatSolicitudEstatus> obtenerEstatusSolicitud() {
        return catSolicitudEstatusRepository.findAll();
    }

    public List<CatTipoMovimiento> obtenerTiposMovimiento() {
        return catTipoMovimientoRepository.findAll();
    }

    public List<CatEstatus> obtenerEstatus() {
        return catEstatusRepository.findAll();
    }

}
