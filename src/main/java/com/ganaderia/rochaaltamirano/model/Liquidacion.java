package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Liquidacion (Tabla: liquidacion)
 * Comprobante oficial de pago y liquidación emitido por el matadero.
 */
@Entity
@Table(name = "liquidacion")
public class Liquidacion {

    @Id
    @Column(name = "id_num_comprobante", length = 50)
    private String idNumComprobante;

    @Column(name = "fecha_liquidacion", nullable = false)
    private LocalDate fechaLiquidacion;

    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matadero")
    private Matadero matadero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrega", nullable = true)
    private Entrega entrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_arqueo")
    private ArqueoMensual arqueo;

    public Liquidacion() {
    }

    public Liquidacion(String idNumComprobante, LocalDate fechaLiquidacion, BigDecimal monto, Matadero matadero) {
        this.idNumComprobante = idNumComprobante;
        this.fechaLiquidacion = fechaLiquidacion;
        this.monto = monto;
        this.matadero = matadero;
    }

    public String getIdNumComprobante() {
        return idNumComprobante;
    }

    public void setIdNumComprobante(String idNumComprobante) {
        this.idNumComprobante = idNumComprobante;
    }

    public LocalDate getFechaLiquidacion() {
        return fechaLiquidacion;
    }

    public void setFechaLiquidacion(LocalDate fechaLiquidacion) {
        this.fechaLiquidacion = fechaLiquidacion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Matadero getMatadero() {
        return matadero;
    }

    public void setMatadero(Matadero matadero) {
        this.matadero = matadero;
    }

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }

    public ArqueoMensual getArqueo() {
        return arqueo;
    }

    public void setArqueo(ArqueoMensual arqueo) {
        this.arqueo = arqueo;
    }

    // Compatibilidad para vistas que accedían a getEmpresa()
    public String getEmpresa() {
        return matadero != null ? matadero.getNombreMatadero() : "Matadero No Especificado";
    }
}
