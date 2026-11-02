package com.ganaderia.rochaaltamirano.model;

import java.math.BigDecimal;

/**
 * Entidad Ganado (Animal Bovino)
 * 
 * Representa cada cabeza de ganado individual identificada por su arete.
 * Contiene información biométrica, estado y categoría comercial.
 */
public class Ganado {

    // Primary Key: Número o código único del arete (ej. NI-40291)
    private String arete;
    
    // Identificador de edad ganadera por dentición (ej. "0 Paletas - Ternero", "2 Paletas", "4 Paletas")
    private Integer paletas;
    
    // Color o pelaje del bovino (ej. "Hosco", "Bermejo", "Bramil", "Negro", "Pintado")
    private String color;
    
    // Estado físico/logístico: 'En Finca', 'Entregado a Matadero'
    private String estado;
    
    // Peso inicial registrado al ingresar a la finca en kilogramos
    private BigDecimal pesoEntradaKg;
    
    // Categoría comercial: 'Apto' (para engorde/matadero) o 'Rechazo'
    private String categoria;

    public Ganado() {
    }

    public Ganado(String arete, Integer paletas, String color, String estado, BigDecimal pesoEntradaKg, String categoria) {
        this.arete = arete;
        this.paletas = paletas;
        this.color = color;
        this.estado = estado;
        this.pesoEntradaKg = pesoEntradaKg;
        this.categoria = categoria;
    }

    // Getters y Setters
    public String getArete() {
        return arete;
    }

    public void setArete(String arete) {
        this.arete = arete;
    }

    public Integer getPaletas() {
        return paletas;
    }

    public void setPaletas(Integer paletas) {
        this.paletas = paletas;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getPesoEntradaKg() {
        return pesoEntradaKg;
    }

    public void setPesoEntradaKg(BigDecimal pesoEntradaKg) {
        this.pesoEntradaKg = pesoEntradaKg;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // Métodos utilitarios de negocio
    public boolean isRechazo() {
        return "Rechazo".equalsIgnoreCase(this.categoria);
    }

    public boolean isEnFinca() {
        return "En Finca".equalsIgnoreCase(this.estado);
    }

    public String getDescripcionPaletas() {
        if (paletas == null) return "Sin especificar";
        return switch (paletas) {
            case 0 -> "0 Paletas (Dientes de leche - Ternero/a)";
            case 2 -> "2 Paletas (~18-24 meses)";
            case 4 -> "4 Paletas (~2.5 - 3 años)";
            case 6 -> "6 Paletas (~3.5 años)";
            case 8 -> "8 Paletas (Boca llena - Adulto)";
            default -> paletas + " Paletas";
        };
    }
}
