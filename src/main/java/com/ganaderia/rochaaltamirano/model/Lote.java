package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Lote (Tabla: lote)
 * Representa la adquisición de un lote de ganado en pie.
 */
@Entity
@Table(name = "lote")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Integer idLote;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    @DecimalMin(value = "0.0", message = "El precio de rechazo no puede ser negativo")
    @Column(name = "precio_de_rechazo", precision = 10, scale = 2)
    private BigDecimal precioDeRechazo;

    @DecimalMin(value = "0.01", message = "El precio estándar debe ser mayor a cero")
    @Column(name = "precio_estandar", precision = 10, scale = 2)
    private BigDecimal precioEstandar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL)
    @OrderBy("fechaIngreso ASC, arete ASC")
    private List<Ganado> animales = new ArrayList<>();

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL)
    private List<PagoLote> pagos = new ArrayList<>();

    public Lote() {
    }

    public Lote(Integer idLote, LocalDate fechaCompra, BigDecimal precioDeRechazo, BigDecimal precioEstandar, Proveedor proveedor) {
        this.idLote = idLote;
        this.fechaCompra = fechaCompra;
        this.precioDeRechazo = precioDeRechazo;
        this.precioEstandar = precioEstandar;
        this.proveedor = proveedor;
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

    public BigDecimal getPrecioDeRechazo() {
        return precioDeRechazo;
    }

    public void setPrecioDeRechazo(BigDecimal precioDeRechazo) {
        if (precioDeRechazo != null && precioDeRechazo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio de rechazo no puede ser negativo");
        }
        this.precioDeRechazo = precioDeRechazo;
    }

    public BigDecimal getPrecioEstandar() {
        return precioEstandar;
    }

    public void setPrecioEstandar(BigDecimal precioEstandar) {
        if (precioEstandar != null && precioEstandar.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio estándar no puede ser negativo");
        }
        this.precioEstandar = precioEstandar;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public List<Ganado> getAnimales() {
        return animales;
    }

    public void setAnimales(List<Ganado> animales) {
        this.animales = animales;
    }

    public List<PagoLote> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoLote> pagos) {
        this.pagos = pagos;
    }

    // Métodos utilitarios protegidos contra LazyInitializationException
    public int getTotalCabezas() {
        try {
            return animales != null ? animales.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public BigDecimal getTotalKilos() {
        try {
            if (animales == null || animales.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return animales.stream()
                    .map(Ganado::getPesoKg)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getMontoTotalEstimado() {
        try {
            if (animales == null || animales.isEmpty()) {
                return BigDecimal.ZERO;
            }
            BigDecimal total = BigDecimal.ZERO;
            for (Ganado g : animales) {
                total = total.add(g.getSubtotalCompra());
            }
            return total;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}

