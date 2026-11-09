package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
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

    @Column(name = "precio_acordado", precision = 10, scale = 2)
    private BigDecimal precioAcordado;

    @Column(name = "precio_rechazo", precision = 10, scale = 2)
    private BigDecimal precioRechazo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matadero")
    private Matadero matadero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_viaje", nullable = true)
    private Viaje viaje;

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
        this.precioAcordado = precioAcordado;
    }

    public BigDecimal getPrecioRechazo() {
        return precioRechazo;
    }

    public void setPrecioRechazo(BigDecimal precioRechazo) {
        this.precioRechazo = precioRechazo;
    }

    public Matadero getMatadero() {
        return matadero;
    }

    public void setMatadero(Matadero matadero) {
        this.matadero = matadero;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
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
                    .map(DetalleEntrega::getPesoMataderoKg)
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
                BigDecimal peso = d.getPesoMataderoKg() != null ? d.getPesoMataderoKg() : BigDecimal.ZERO;
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
