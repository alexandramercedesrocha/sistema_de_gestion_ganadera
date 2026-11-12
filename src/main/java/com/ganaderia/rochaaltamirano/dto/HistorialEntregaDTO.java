package com.ganaderia.rochaaltamirano.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO que representa un evento de entrega a planta matadero en la trazabilidad de un bovino.
 */
public class HistorialEntregaDTO {

    private Integer idEntrega;
    private LocalDate fechaEntrega;
    private String nombreMatadero;
    private String ubicacionMatadero;
    private BigDecimal pesoFincaKg;
    private BigDecimal pesoMataderoKg;
    private BigDecimal kilosMerma;
    private BigDecimal porcentajeMerma;
    private String dictamen;
    private BigDecimal precioRechazo;
    private String motivoRechazo;
    private boolean esRechazo;
    private boolean esDevuelto;
    private boolean esComprado;
    private boolean esApto;
    private String areteUsado;

    public HistorialEntregaDTO() {
    }

    public Integer getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(Integer idEntrega) {
        this.idEntrega = idEntrega;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getNombreMatadero() {
        return nombreMatadero;
    }

    public void setNombreMatadero(String nombreMatadero) {
        this.nombreMatadero = nombreMatadero;
    }

    public String getUbicacionMatadero() {
        return ubicacionMatadero;
    }

    public void setUbicacionMatadero(String ubicacionMatadero) {
        this.ubicacionMatadero = ubicacionMatadero;
    }

    public BigDecimal getPesoFincaKg() {
        return pesoFincaKg;
    }

    public void setPesoFincaKg(BigDecimal pesoFincaKg) {
        this.pesoFincaKg = pesoFincaKg;
    }

    public BigDecimal getPesoMataderoKg() {
        return pesoMataderoKg;
    }

    public void setPesoMataderoKg(BigDecimal pesoMataderoKg) {
        this.pesoMataderoKg = pesoMataderoKg;
    }

    public BigDecimal getKilosMerma() {
        return kilosMerma;
    }

    public void setKilosMerma(BigDecimal kilosMerma) {
        this.kilosMerma = kilosMerma;
    }

    public BigDecimal getPorcentajeMerma() {
        return porcentajeMerma;
    }

    public void setPorcentajeMerma(BigDecimal porcentajeMerma) {
        this.porcentajeMerma = porcentajeMerma;
    }

    public String getDictamen() {
        return dictamen;
    }

    public void setDictamen(String dictamen) {
        this.dictamen = dictamen;
    }

    public BigDecimal getPrecioRechazo() {
        return precioRechazo;
    }

    public void setPrecioRechazo(BigDecimal precioRechazo) {
        this.precioRechazo = precioRechazo;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public boolean isEsRechazo() {
        return esRechazo;
    }

    public void setEsRechazo(boolean esRechazo) {
        this.esRechazo = esRechazo;
    }

    public boolean isEsDevuelto() {
        return esDevuelto;
    }

    public void setEsDevuelto(boolean esDevuelto) {
        this.esDevuelto = esDevuelto;
    }

    public boolean isEsComprado() {
        return esComprado;
    }

    public void setEsComprado(boolean esComprado) {
        this.esComprado = esComprado;
    }

    public boolean isEsApto() {
        return esApto;
    }

    public void setEsApto(boolean esApto) {
        this.esApto = esApto;
    }

    public String getAreteUsado() {
        return areteUsado;
    }

    public void setAreteUsado(String areteUsado) {
        this.areteUsado = areteUsado;
    }
}

