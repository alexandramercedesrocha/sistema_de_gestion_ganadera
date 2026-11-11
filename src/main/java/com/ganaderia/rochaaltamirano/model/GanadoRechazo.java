package com.ganaderia.rochaaltamirano.model;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Ganado de Rechazo (DTO / Modelo de Vista)
 * 
 * Especialización de la entidad Ganado para aquellos animales que no fueron aceptados
 * en la báscula de matadero o requirieron liquidación con castigo de precio.
 */
public class GanadoRechazo extends Ganado {

    // Causa o diagnóstico de rechazo (ej. "Golpe severo en canal", "Fiebre / Condición sanitaria", "Bajo rendimiento", "Quiste")
    private String motivoRechazo;

    // Fecha en la que el matadero o técnico emitió el dictamen de rechazo
    private LocalDate fechaRechazo;

    // Peso oficial registrado en la báscula del matadero al momento del rechazo
    @DecimalMin(value = "0.01", message = "El peso del matadero debe ser mayor a cero")
    private BigDecimal pesoMatadero;

    public GanadoRechazo() {
        super();
        this.setCategoria("Rechazo");
    }

    public GanadoRechazo(String arete, Object paletas, String color, String estado, BigDecimal pesoEntradaKg,
                         String motivoRechazo, LocalDate fechaRechazo, BigDecimal pesoMatadero) {
        super(arete, paletas != null ? paletas.toString() : null, color, pesoEntradaKg, "Rechazo", null);
        this.setEstado(estado);
        this.setCategoria("Rechazo");
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
        if (pesoMatadero != null && pesoMatadero.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El peso de matadero no puede ser negativo");
        }
        this.pesoMatadero = pesoMatadero;
    }

    // Calcula la diferencia o merma entre el peso inicial de entrada y el peso en báscula de matadero
    public BigDecimal getMermaKg() {
        if (getPesoEntradaKg() != null && pesoMatadero != null) {
            return getPesoEntradaKg().subtract(pesoMatadero);
        }
        return BigDecimal.ZERO;
    }
}
