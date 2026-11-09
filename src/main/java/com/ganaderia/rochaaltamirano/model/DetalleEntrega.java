package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad DetalleEntrega (Tabla: detalle_entrega)
 * Relaciona cada res entregada al matadero con su peso en báscula y dictamen sanitario.
 */
@Entity
@Table(name = "detalle_entrega")
public class DetalleEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_entrega")
    private Integer idDetalleEntrega;

    @Column(name = "peso_matadero_kg", precision = 8, scale = 2)
    private BigDecimal pesoMataderoKg;

    @Column(name = "estado_dictamen", length = 50)
    private String estadoDictamen; // ej. Apto, Rechazado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arete")
    private Ganado ganado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrega")
    private Entrega entrega;

    @OneToMany(mappedBy = "detalleEntrega", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GanadoRechazado> rechazos = new ArrayList<>();

    public DetalleEntrega() {
    }

    public DetalleEntrega(Integer idDetalleEntrega, BigDecimal pesoMataderoKg, String estadoDictamen, Ganado ganado, Entrega entrega) {
        this.idDetalleEntrega = idDetalleEntrega;
        this.pesoMataderoKg = pesoMataderoKg;
        this.estadoDictamen = estadoDictamen;
        this.ganado = ganado;
        this.entrega = entrega;
    }

    public Integer getIdDetalleEntrega() {
        return idDetalleEntrega;
    }

    public void setIdDetalleEntrega(Integer idDetalleEntrega) {
        this.idDetalleEntrega = idDetalleEntrega;
    }

    public BigDecimal getPesoMataderoKg() {
        return pesoMataderoKg;
    }

    public void setPesoMataderoKg(BigDecimal pesoMataderoKg) {
        this.pesoMataderoKg = pesoMataderoKg;
    }

    public String getEstadoDictamen() {
        return estadoDictamen;
    }

    public void setEstadoDictamen(String estadoDictamen) {
        this.estadoDictamen = estadoDictamen;
    }

    public Ganado getGanado() {
        return ganado;
    }

    public void setGanado(Ganado ganado) {
        this.ganado = ganado;
    }

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }

    public List<GanadoRechazado> getRechazos() {
        return rechazos;
    }

    public void setRechazos(List<GanadoRechazado> rechazos) {
        this.rechazos = rechazos;
    }

    /**
     * Calcula el porcentaje de merma en peso entre la entrada (finca) y la báscula de matadero:
     * % Merma = ((Peso Entrada - Peso Matadero) / Peso Entrada) * 100
     */
    public BigDecimal getPorcentajeMerma() {
        if (ganado == null || ganado.getPesoKg() == null || pesoMataderoKg == null) {
            return null;
        }
        BigDecimal pesoEntrada = ganado.getPesoKg();
        if (pesoEntrada.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        BigDecimal diferencia = pesoEntrada.subtract(pesoMataderoKg);
        return diferencia.multiply(new BigDecimal("100"))
                .divide(pesoEntrada, 2, java.math.RoundingMode.HALF_UP);
    }
}

