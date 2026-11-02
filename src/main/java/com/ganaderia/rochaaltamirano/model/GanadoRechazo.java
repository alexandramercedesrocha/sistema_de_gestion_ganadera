package com.ganaderia.rochaaltamirano.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Ganado_Rechazo (Subclase de Ganado)
 * 
 * Modela la especialización de aquellos animales que fueron devueltos o
 * clasificados como no aptos por el matadero o por inspección sanitaria.
 * Hereda todos los atributos de {@link Ganado}.
 */
public class GanadoRechazo extends Ganado {

    // Causa o diagnóstico de rechazo (ej. "Golpe severo en canal", "Fiebre / Condición sanitaria", "Bajo rendimiento", "Quiste")
    private String motivoRechazo;

    // Fecha en la que el matadero o técnico emitió el dictamen de rechazo
    private LocalDate fechaRechazo;

    // Peso oficial registrado en la báscula del matadero al momento del rechazo
    private BigDecimal pesoMatadero;

    public GanadoRechazo() {
        super();
        this.setCategoria("Rechazo");
    }

    public GanadoRechazo(String arete, Integer paletas, String color, String estado, BigDecimal pesoEntradaKg,
                         String motivoRechazo, LocalDate fechaRechazo, BigDecimal pesoMatadero) {
        super(arete, paletas, color, estado, pesoEntradaKg, "Rechazo");
        this.motivoRechazo = motivoRechazo;
        this.fechaRechazo = fechaRechazo;
        this.pesoMatadero = pesoMatadero;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public LocalDate getFechaRechazo() {
        return fechaRechazo;
    }

    public void setFechaRechazo(LocalDate fechaRechazo) {
        this.fechaRechazo = fechaRechazo;
    }

    public BigDecimal getPesoMatadero() {
        return pesoMatadero;
    }

    public void setPesoMatadero(BigDecimal pesoMatadero) {
        this.pesoMatadero = pesoMatadero;
    }

    /**
     * Diferencia en kilos entre la entrada en finca y el pesaje en matadero
     */
    public BigDecimal getMermaKg() {
        if (getPesoEntradaKg() != null && pesoMatadero != null) {
            return getPesoEntradaKg().subtract(pesoMatadero);
        }
        return BigDecimal.ZERO;
    }
}
