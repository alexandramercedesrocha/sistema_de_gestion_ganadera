package com.ganaderia.rochaaltamirano.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Entrega
 * 
 * Modela los despachos de ganado bovino hacia los mataderos autorizados.
 */
public class Entrega {

    private Long idEntrega;
    private LocalDate fechaEntrega;
    private Integer totalCabezas;
    private BigDecimal pesoTotal;
    private String matadero; // Nombre del matadero receptor

    public Entrega() {
    }

    public Entrega(Long idEntrega, LocalDate fechaEntrega, Integer totalCabezas, BigDecimal pesoTotal, String matadero) {
        this.idEntrega = idEntrega;
        this.fechaEntrega = fechaEntrega;
        this.totalCabezas = totalCabezas;
        this.pesoTotal = pesoTotal;
        this.matadero = matadero;
    }

    public Long getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(Long idEntrega) {
        this.idEntrega = idEntrega;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Integer getTotalCabezas() {
        return totalCabezas;
    }

    public void setTotalCabezas(Integer totalCabezas) {
        this.totalCabezas = totalCabezas;
    }

    public BigDecimal getPesoTotal() {
        return pesoTotal;
    }

    public void setPesoTotal(BigDecimal pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

    public String getMatadero() {
        return matadero;
    }

    public void setMatadero(String matadero) {
        this.matadero = matadero;
    }

    public BigDecimal getPesoPromedioPorCabeza() {
        if (totalCabezas != null && totalCabezas > 0 && pesoTotal != null) {
            return pesoTotal.divide(BigDecimal.valueOf(totalCabezas), 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
