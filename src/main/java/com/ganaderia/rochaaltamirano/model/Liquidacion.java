package com.ganaderia.rochaaltamirano.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Liquidación
 * 
 * Representa los pagos y comprobantes oficiales emitidos por los mataderos
 * industriales a favor de la sociedad de Wilber Rocha y Yader Altamirano
 * tras el faenado y pesaje en canal de los lotes entregados.
 */
public class Liquidacion {

    // Número de comprobante o factura fiscal emitida por el matadero
    private String idNumComprobante;
    
    // Empresa pagadora (ej. "MACESA - Matadero Central S.A.", "San Martín S.A.")
    private String empresa;
    
    // Fecha efectiva de liquidación
    private LocalDate fechaLiquidacion;
    
    // Monto liquidado a favor de la sociedad
    private BigDecimal monto;

    public Liquidacion() {
    }

    public Liquidacion(String idNumComprobante, String empresa, LocalDate fechaLiquidacion, BigDecimal monto) {
        this.idNumComprobante = idNumComprobante;
        this.empresa = empresa;
        this.fechaLiquidacion = fechaLiquidacion;
        this.monto = monto;
    }

    public String getIdNumComprobante() {
        return idNumComprobante;
    }

    public void setIdNumComprobante(String idNumComprobante) {
        this.idNumComprobante = idNumComprobante;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public LocalDate getFechaLiquidacion() {
        return fechaLiquidacion;
    }

    public void setFechaLiquidacion(LocalDate fechaLiquidacion) {
        this.fechaLiquidacion = fechaLiquidacion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
