package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad Ganado (Tabla: ganado)
 * Representa cada res bovina identificada por su arete.
 * Posee clave foránea id_lote y autorreferencia arete_anterior para re-aretado.
 */
@Entity
@Table(name = "ganado")
public class Ganado {

    @Id
    @Column(name = "arete", length = 20)
    private String arete;

    @Column(name = "paletas", length = 50)
    private String paletas;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "peso_kg", precision = 8, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "condicion_compra", length = 50)
    private String condicionCompra; // ej. Regular, Bueno, Excelente

    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arete_anterior")
    private Ganado areteAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote")
    private Lote lote;

    @Transient
    private String estado = "En Finca"; // En Finca / Entregado a Matadero

    @Transient
    private String categoria = "Apto"; // Apto / Rechazo

    public Ganado() {
    }

    public Ganado(String arete, String paletas, String color, BigDecimal pesoKg, String condicionCompra, Lote lote) {
        this.arete = arete;
        this.paletas = paletas;
        this.color = color;
        this.pesoKg = pesoKg;
        this.condicionCompra = condicionCompra;
        this.lote = lote;
    }

    public String getArete() {
        return arete;
    }

    public String getAreteFormateado() {
        if (arete == null) return "";
        String limpio = arete.replaceAll("\\s+", "");
        if (limpio.length() == 9) {
            return limpio.substring(0, 5) + " " + limpio.substring(5);
        }
        return arete;
    }

    public void setArete(String arete) {
        this.arete = arete;
    }

    public String getPaletas() {
        return paletas;
    }

    public void setPaletas(String paletas) {
        this.paletas = paletas;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    // Compatibilidad con vistas previas
    public BigDecimal getPesoEntradaKg() {
        return pesoKg;
    }

    public void setPesoEntradaKg(BigDecimal pesoEntradaKg) {
        this.pesoKg = pesoEntradaKg;
    }

    public String getCondicionCompra() {
        return condicionCompra;
    }

    public void setCondicionCompra(String condicionCompra) {
        this.condicionCompra = condicionCompra;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioPorKilo() {
        if (precioCompra != null && precioCompra.compareTo(BigDecimal.ZERO) > 0) {
            return precioCompra;
        }
        if (lote != null && lote.getPrecioEstandar() != null) {
            return lote.getPrecioEstandar();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSubtotalCompra() {
        BigDecimal peso = pesoKg != null ? pesoKg : BigDecimal.ZERO;
        return peso.multiply(getPrecioPorKilo());
    }

    public Ganado getAreteAnterior() {
        return areteAnterior;
    }

    public void setAreteAnterior(Ganado areteAnterior) {
        this.areteAnterior = areteAnterior;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
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

    public boolean isEnFinca() {
        return "En Finca".equalsIgnoreCase(this.estado);
    }

    public boolean isRechazo() {
        return "Rechazo".equalsIgnoreCase(this.categoria);
    }
}
