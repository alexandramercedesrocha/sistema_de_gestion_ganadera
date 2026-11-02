package com.ganaderia.rochaaltamirano.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Compra
 * 
 * Registra cada transacción de adquisición de ganado en pie a proveedores.
 * Almacena totales de cabezas, kilos, precios por kilo o estándar, y monto total.
 */
public class Compra {

    private Long idCompra;
    private LocalDate fechaCompra;
    private Integer totalCabezas;
    private BigDecimal precioKg;
    private BigDecimal precioEstandar;
    private BigDecimal totalKilos;
    private String proveedor; // Nombre o referencia del proveedor
    private BigDecimal montoTotal;
    private String direccion; // Lugar / Comarca / Finca de origen de la compra

    public Compra() {
    }

    public Compra(Long idCompra, LocalDate fechaCompra, Integer totalCabezas, BigDecimal precioKg,
                  BigDecimal precioEstandar, BigDecimal totalKilos, String proveedor,
                  BigDecimal montoTotal, String direccion) {
        this.idCompra = idCompra;
        this.fechaCompra = fechaCompra;
        this.totalCabezas = totalCabezas;
        this.precioKg = precioKg;
        this.precioEstandar = precioEstandar;
        this.totalKilos = totalKilos;
        this.proveedor = proveedor;
        this.montoTotal = montoTotal;
        this.direccion = direccion;
    }

    public Long getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Long idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Integer getTotalCabezas() {
        return totalCabezas;
    }

    public void setTotalCabezas(Integer totalCabezas) {
        this.totalCabezas = totalCabezas;
    }

    public BigDecimal getPrecioKg() {
        return precioKg;
    }

    public void setPrecioKg(BigDecimal precioKg) {
        this.precioKg = precioKg;
    }

    public BigDecimal getPrecioEstandar() {
        return precioEstandar;
    }

    public void setPrecioEstandar(BigDecimal precioEstandar) {
        this.precioEstandar = precioEstandar;
    }

    public BigDecimal getTotalKilos() {
        return totalKilos;
    }

    public void setTotalKilos(BigDecimal totalKilos) {
        this.totalKilos = totalKilos;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Calcula el promedio de kilos por cabeza de este lote
     */
    public BigDecimal getPromedioKilosPorCabeza() {
        if (totalCabezas != null && totalCabezas > 0 && totalKilos != null) {
            return totalKilos.divide(BigDecimal.valueOf(totalCabezas), 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
