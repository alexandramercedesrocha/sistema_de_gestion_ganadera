package com.ganaderia.rochaaltamirano.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Gasto
 * 
 * Contabiliza los desembolsos operativos de la sociedad ganadera
 * tales como sal, vacunas, vitaminas, combustible, alimentación suplementaria, peones y guías de transporte.
 */
public class Gasto {

    private Long idGasto;
    private String nombreGasto;
    private LocalDate fechaGasto;
    private String descripcion;
    private BigDecimal monto;

    public Gasto() {
    }

    public Gasto(Long idGasto, String nombreGasto, LocalDate fechaGasto, String descripcion, BigDecimal monto) {
        this.idGasto = idGasto;
        this.nombreGasto = nombreGasto;
        this.fechaGasto = fechaGasto;
        this.descripcion = descripcion;
        this.monto = monto;
    }

    public Long getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(Long idGasto) {
        this.idGasto = idGasto;
    }

    public String getNombreGasto() {
        return nombreGasto;
    }

    public void setNombreGasto(String nombreGasto) {
        this.nombreGasto = nombreGasto;
    }

    public LocalDate getFechaGasto() {
        return fechaGasto;
    }

    public void setFechaGasto(LocalDate fechaGasto) {
        this.fechaGasto = fechaGasto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
