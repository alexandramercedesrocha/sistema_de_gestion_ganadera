package com.ganaderia.rochaaltamirano.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO principal del "Aparato de Trazabilidad e Historial del Bovino".
 * Contiene toda la información de origen, compras, re-aretados anteriores y posteriores,
 * y los despachos/dictámenes ocurridos en mataderos.
 */
public class HistorialBovinoDTO {

    private String arete;
    private String areteFormateado;
    private String color;
    private String paletas;
    private BigDecimal pesoActualKg;
    private String condicion;
    private String estado;
    private String categoria;
    private LocalDateTime fechaIngreso;

    // Origen de compra / Lote
    private Integer idLote;
    private LocalDate fechaCompra;
    private String proveedorNombre;
    private BigDecimal precioCompraKg;
    private BigDecimal totalCompra;

    // Trazabilidad de Re-aretado
    private String areteAnterior;
    private String areteAnteriorFormateado;
    private boolean tieneAreteAnterior;
    private boolean fueRearetado;
    private String nuevoArete;
    private String nuevoAreteFormateado;
    private List<String> cadenaTrazabilidad = new ArrayList<>();

    // Historial de eventos en mataderos
    private List<HistorialEntregaDTO> entregas = new ArrayList<>();

    // Resumen de rechazo
    private boolean tieneHistorialRechazo;
    private String ultimoMotivoRechazo;
    private String ultimoMataderoRechazo;

    public HistorialBovinoDTO() {
    }

    public String getArete() {
        return arete;
    }

    public void setArete(String arete) {
        this.arete = arete;
    }

    public String getAreteFormateado() {
        return areteFormateado;
    }

    public void setAreteFormateado(String areteFormateado) {
        this.areteFormateado = areteFormateado;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPaletas() {
        return paletas;
    }

    public void setPaletas(String paletas) {
        this.paletas = paletas;
    }

    public BigDecimal getPesoActualKg() {
        return pesoActualKg;
    }

    public void setPesoActualKg(BigDecimal pesoActualKg) {
        this.pesoActualKg = pesoActualKg;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Integer getIdLote() {
        return idLote;
    }

    public void setIdLote(Integer idLote) {
        this.idLote = idLote;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public BigDecimal getPrecioCompraKg() {
        return precioCompraKg;
    }

    public void setPrecioCompraKg(BigDecimal precioCompraKg) {
        this.precioCompraKg = precioCompraKg;
    }

    public BigDecimal getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(BigDecimal totalCompra) {
        this.totalCompra = totalCompra;
    }

    public String getAreteAnterior() {
        return areteAnterior;
    }

    public void setAreteAnterior(String areteAnterior) {
        this.areteAnterior = areteAnterior;
    }

    public String getAreteAnteriorFormateado() {
        return areteAnteriorFormateado;
    }

    public void setAreteAnteriorFormateado(String areteAnteriorFormateado) {
        this.areteAnteriorFormateado = areteAnteriorFormateado;
    }

    public boolean isTieneAreteAnterior() {
        return tieneAreteAnterior;
    }

    public void setTieneAreteAnterior(boolean tieneAreteAnterior) {
        this.tieneAreteAnterior = tieneAreteAnterior;
    }

    public boolean isFueRearetado() {
        return fueRearetado;
    }

    public void setFueRearetado(boolean fueRearetado) {
        this.fueRearetado = fueRearetado;
    }

    public String getNuevoArete() {
        return nuevoArete;
    }

    public void setNuevoArete(String nuevoArete) {
        this.nuevoArete = nuevoArete;
    }

    public String getNuevoAreteFormateado() {
        return nuevoAreteFormateado;
    }

    public void setNuevoAreteFormateado(String nuevoAreteFormateado) {
        this.nuevoAreteFormateado = nuevoAreteFormateado;
    }

    public List<String> getCadenaTrazabilidad() {
        return cadenaTrazabilidad;
    }

    public void setCadenaTrazabilidad(List<String> cadenaTrazabilidad) {
        this.cadenaTrazabilidad = cadenaTrazabilidad;
    }

    public List<HistorialEntregaDTO> getEntregas() {
        return entregas;
    }

    public void setEntregas(List<HistorialEntregaDTO> entregas) {
        this.entregas = entregas;
    }

    public boolean isTieneHistorialRechazo() {
        return tieneHistorialRechazo;
    }

    public void setTieneHistorialRechazo(boolean tieneHistorialRechazo) {
        this.tieneHistorialRechazo = tieneHistorialRechazo;
    }

    public String getUltimoMotivoRechazo() {
        return ultimoMotivoRechazo;
    }

    public void setUltimoMotivoRechazo(String ultimoMotivoRechazo) {
        this.ultimoMotivoRechazo = ultimoMotivoRechazo;
    }

    public String getUltimoMataderoRechazo() {
        return ultimoMataderoRechazo;
    }

    public void setUltimoMataderoRechazo(String ultimoMataderoRechazo) {
        this.ultimoMataderoRechazo = ultimoMataderoRechazo;
    }
}

