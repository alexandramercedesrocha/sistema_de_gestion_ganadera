package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[0-9]{9,15}$", message = "El número de comprobante debe contener únicamente entre 9 y 15 dígitos numéricos")
    @Column(name = "id_num_comprobante", length = 50)
    private String idNumComprobante;

    @Column(name = "fecha_liquidacion", nullable = false)
    private LocalDate fechaLiquidacion;

    @DecimalMin(value = "0.01", message = "El monto liquidado debe ser mayor a cero")
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
        if (idNumComprobante != null) {
            this.idNumComprobante = idNumComprobante.trim();
        } else {
            this.idNumComprobante = null;
        }
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
        if (monto != null && monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto liquidado no puede ser negativo");
        }
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
