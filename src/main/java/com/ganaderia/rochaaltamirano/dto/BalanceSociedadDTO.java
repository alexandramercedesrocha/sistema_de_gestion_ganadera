package com.ganaderia.rochaaltamirano.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DTO BalanceSociedadDTO
 * 
 * Objeto de transferencia de datos para presentar el estado de resultados consolidado
 * y la distribución equitativa (50% Wilber Rocha / 50% Yader Altamirano).
 */
public class BalanceSociedadDTO {

    // Ingresos
    private BigDecimal totalLiquidaciones;
    
    // Egresos desglosados
    private BigDecimal totalCompras;
    private BigDecimal totalTransporte;
    private BigDecimal totalGastosVarios;
    private BigDecimal totalEgresos;
    
    // Rentabilidad
    private BigDecimal utilidadNeta;
    private BigDecimal cuotaWilberRocha; // 50%
    private BigDecimal cuotaYaderAltamirano; // 50%
    
    // Métricas de inventario ganadero
    private int totalCabezasActivas;
    private int cabezasEnFinca;
    private int cabezasEntregadas;
    private int cabezasRechazo;
    private BigDecimal totalKilosEnFinca;

    public BalanceSociedadDTO() {
        this.totalLiquidaciones = BigDecimal.ZERO;
        this.totalCompras = BigDecimal.ZERO;
        this.totalTransporte = BigDecimal.ZERO;
        this.totalGastosVarios = BigDecimal.ZERO;
        this.totalEgresos = BigDecimal.ZERO;
        this.utilidadNeta = BigDecimal.ZERO;
        this.cuotaWilberRocha = BigDecimal.ZERO;
        this.cuotaYaderAltamirano = BigDecimal.ZERO;
        this.totalKilosEnFinca = BigDecimal.ZERO;
    }

    public void calcularTotales() {
        this.totalEgresos = totalCompras.add(totalTransporte).add(totalGastosVarios);
        this.utilidadNeta = totalLiquidaciones.subtract(this.totalEgresos);
        
        // Reparto 50-50
        this.cuotaWilberRocha = this.utilidadNeta.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
        this.cuotaYaderAltamirano = this.utilidadNeta.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
    }

    // Getters y Setters
    public BigDecimal getTotalLiquidaciones() {
        return totalLiquidaciones;
    }

    public void setTotalLiquidaciones(BigDecimal totalLiquidaciones) {
        this.totalLiquidaciones = totalLiquidaciones;
    }

    public BigDecimal getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(BigDecimal totalCompras) {
        this.totalCompras = totalCompras;
    }

    public BigDecimal getTotalTransporte() {
        return totalTransporte;
    }

    public void setTotalTransporte(BigDecimal totalTransporte) {
        this.totalTransporte = totalTransporte;
    }

    public BigDecimal getTotalGastosVarios() {
        return totalGastosVarios;
    }

    public void setTotalGastosVarios(BigDecimal totalGastosVarios) {
        this.totalGastosVarios = totalGastosVarios;
    }

    public void setTotalGastos(BigDecimal totalGastos) {
        this.totalGastosVarios = totalGastos;
    }

    public BigDecimal getTotalGastos() {
        return totalGastosVarios;
    }

    public BigDecimal getTotalEgresos() {
        return totalEgresos;
    }

    public void setTotalEgresos(BigDecimal totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    public BigDecimal getUtilidadNeta() {
        return utilidadNeta;
    }

    public void setUtilidadNeta(BigDecimal utilidadNeta) {
        this.utilidadNeta = utilidadNeta;
    }

    public BigDecimal getCuotaWilberRocha() {
        return cuotaWilberRocha;
    }

    public void setCuotaWilberRocha(BigDecimal cuotaWilberRocha) {
        this.cuotaWilberRocha = cuotaWilberRocha;
    }

    public BigDecimal getCuotaYaderAltamirano() {
        return cuotaYaderAltamirano;
    }

    public void setCuotaYaderAltamirano(BigDecimal cuotaYaderAltamirano) {
        this.cuotaYaderAltamirano = cuotaYaderAltamirano;
    }

    public int getTotalCabezasActivas() {
        return totalCabezasActivas;
    }

    public void setTotalCabezasActivas(int totalCabezasActivas) {
        this.totalCabezasActivas = totalCabezasActivas;
    }

    public int getCabezasEnFinca() {
        return cabezasEnFinca;
    }

    public void setCabezasEnFinca(int cabezasEnFinca) {
        this.cabezasEnFinca = cabezasEnFinca;
    }

    public void setTotalCabezasEnFinca(long cabezas) {
        this.cabezasEnFinca = (int) cabezas;
    }

    public int getCabezasEntregadas() {
        return cabezasEntregadas;
    }

    public void setCabezasEntregadas(int cabezasEntregadas) {
        this.cabezasEntregadas = cabezasEntregadas;
    }

    public int getCabezasRechazo() {
        return cabezasRechazo;
    }

    public void setCabezasRechazo(int cabezasRechazo) {
        this.cabezasRechazo = cabezasRechazo;
    }

    public BigDecimal getTotalKilosEnFinca() {
        return totalKilosEnFinca;
    }

    public void setTotalKilosEnFinca(BigDecimal totalKilosEnFinca) {
        this.totalKilosEnFinca = totalKilosEnFinca;
    }
}
