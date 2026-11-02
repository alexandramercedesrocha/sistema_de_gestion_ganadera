package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Gasto (Tabla: gastos)
 * Gastos operativos relacionados a categorías, viajes, pagos de lote o arqueos.
 */
@Entity
@Table(name = "gastos")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gasto")
    private Integer idGasto;

    @Column(name = "fecha_gasto", nullable = false)
    private LocalDate fechaGasto;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private CategoriaGasto categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_arqueo")
    private ArqueoMensual arqueo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pago")
    private PagoLote pagoLote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_viaje")
    private Viaje viaje;

    public Gasto() {
    }

    public Gasto(Integer idGasto, LocalDate fechaGasto, String descripcion, BigDecimal monto, CategoriaGasto categoria) {
        this.idGasto = idGasto;
        this.fechaGasto = fechaGasto;
        this.descripcion = descripcion;
        this.monto = monto;
        this.categoria = categoria;
    }

    public Integer getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(Integer idGasto) {
        this.idGasto = idGasto;
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

    public CategoriaGasto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaGasto categoria) {
        this.categoria = categoria;
    }

    public ArqueoMensual getArqueo() {
        return arqueo;
    }

    public void setArqueo(ArqueoMensual arqueo) {
        this.arqueo = arqueo;
    }

    public PagoLote getPagoLote() {
        return pagoLote;
    }

    public void setPagoLote(PagoLote pagoLote) {
        this.pagoLote = pagoLote;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    // Compatibilidad previa con vistas
    public String getNombreGasto() {
        return categoria != null ? categoria.getNombreCategoria() : descripcion;
    }

    public void setNombreGasto(String nombreGasto) {
        if (this.descripcion == null || this.descripcion.isBlank()) {
            this.descripcion = nombreGasto;
        }
    }
}
