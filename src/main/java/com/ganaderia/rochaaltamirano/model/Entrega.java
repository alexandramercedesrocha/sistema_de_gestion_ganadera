package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Entrega (Tabla: entrega)
 * Modela los despachos y entregas de ganado al matadero.
 */
@Entity
@Table(name = "entrega")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrega")
    private Integer idEntrega;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @DecimalMin(value = "0.01", message = "El precio acordado debe ser mayor a cero")
    @Column(name = "precio_acordado", precision = 10, scale = 2)
    private BigDecimal precioAcordado;

    @DecimalMin(value = "0.0", message = "El precio de rechazo no puede ser negativo")
    @Column(name = "precio_rechazo", precision = 10, scale = 2)
    private BigDecimal precioRechazo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matadero")
    private Matadero matadero;

    @OneToMany(mappedBy = "entrega", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Viaje> viajes = new ArrayList<>();

    @OneToMany(mappedBy = "entrega", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleEntrega> detalles = new ArrayList<>();

    public Entrega() {
    }

    public Entrega(Integer idEntrega, LocalDate fechaEntrega, BigDecimal precioAcordado, BigDecimal precioRechazo, Matadero matadero) {
        this.idEntrega = idEntrega;
        this.fechaEntrega = fechaEntrega;
        this.precioAcordado = precioAcordado;
        this.precioRechazo = precioRechazo;
        this.matadero = matadero;
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

    public BigDecimal getPrecioAcordado() {
        return precioAcordado;
    }

    public void setPrecioAcordado(BigDecimal precioAcordado) {
        if (precioAcordado != null && precioAcordado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio acordado no puede ser negativo");
        }
        this.precioAcordado = precioAcordado;
    }

    public BigDecimal getPrecioRechazo() {
        return precioRechazo;
    }

    public void setPrecioRechazo(BigDecimal precioRechazo) {
        if (precioRechazo != null && precioRechazo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio de rechazo no puede ser negativo");
        }
        this.precioRechazo = precioRechazo;
    }

    public Matadero getMatadero() {
        return matadero;
    }

    public void setMatadero(Matadero matadero) {
        this.matadero = matadero;
    }

    public List<Viaje> getViajes() {
        return viajes;
    }

    public void setViajes(List<Viaje> viajes) {
        this.viajes = viajes;
    }

    // Compatibilidad para vistas o llamadas a getViaje()
    public Viaje getViaje() {
        return (viajes != null && !viajes.isEmpty()) ? viajes.get(0) : null;
    }

    public void setViaje(Viaje viaje) {
        if (this.viajes == null) {
            this.viajes = new ArrayList<>();
        }
        if (viaje != null) {
            viaje.setEntrega(this);
            if (!this.viajes.contains(viaje)) {
                this.viajes.add(viaje);
            }
        }
    }

    public int getTotalViajes() {
        return viajes != null ? viajes.size() : 0;
    }

    public List<DetalleEntrega> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleEntrega> detalles) {
        this.detalles = detalles;
    }

    public int getTotalCabezas() {
        try {
            return detalles != null ? detalles.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public BigDecimal getPesoTotal() {
        try {
            if (detalles == null || detalles.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return detalles.stream()
                    .map(d -> d.getPesoMataderoKg() != null ? d.getPesoMataderoKg() : (d.getGanado() != null ? d.getGanado().getPesoKg() : BigDecimal.ZERO))
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getMontoTotalEstimado() {
        try {
            if (detalles == null || detalles.isEmpty()) {
                return BigDecimal.ZERO;
            }
            BigDecimal total = BigDecimal.ZERO;
            for (DetalleEntrega d : detalles) {
                BigDecimal peso = d.getPesoMataderoKg() != null ? d.getPesoMataderoKg() : (d.getGanado() != null && d.getGanado().getPesoKg() != null ? d.getGanado().getPesoKg() : BigDecimal.ZERO);
                if ("Rechazado".equalsIgnoreCase(d.getEstadoDictamen())) {
                    BigDecimal pRech = d.getPrecioRechazo() != null ? d.getPrecioRechazo() : (precioRechazo != null ? precioRechazo : BigDecimal.ZERO);
                    total = total.add(peso.multiply(pRech));
                } else {
                    BigDecimal pApto = precioAcordado != null ? precioAcordado : BigDecimal.ZERO;
                    total = total.add(peso.multiply(pApto));
                }
            }
            return total;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
