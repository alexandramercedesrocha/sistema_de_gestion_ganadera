package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
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

    @DecimalMin(value = "0.01", message = "El peso en matadero/venta debe ser mayor a cero")
    @Column(name = "peso_matadero_kg", precision = 8, scale = 2)
    private BigDecimal pesoMataderoKg;

    @Column(name = "estado_dictamen", length = 50)
    private String estadoDictamen; // ej. Apto, Rechazado

    @DecimalMin(value = "0.0", message = "El precio de rechazo no puede ser negativo")
    @Column(name = "precio_rechazo", precision = 10, scale = 2)
    private BigDecimal precioRechazo;

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
        if (pesoMataderoKg != null && pesoMataderoKg.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El peso de matadero no puede ser negativo");
        }
        this.pesoMataderoKg = pesoMataderoKg;
    }

    public String getEstadoDictamen() {
        return estadoDictamen;
    }

    public void setEstadoDictamen(String estadoDictamen) {
        this.estadoDictamen = estadoDictamen;
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
     * Devuelve el primer motivo de rechazo de forma segura sin fallos en SpEL.
     */
    public String getPrimerMotivoRechazo() {
        if (rechazos != null && !rechazos.isEmpty() && rechazos.get(0) != null) {
            String mot = rechazos.get(0).getMotivoRechazo();
            return mot != null ? mot : "";
        }
        return "";
    }

    /**
     * Indica si la res cuenta con al menos un registro de rechazo.
     */
    public boolean isTieneRechazo() {
        return rechazos != null && !rechazos.isEmpty();
    }

    /**
     * Indica si la merma supera el 12% (aviso de pérdida excesiva).
     */
    public boolean isMermaAlta() {
        BigDecimal pm = getPorcentajeMerma();
        return pm != null && pm.compareTo(new BigDecimal("12")) > 0;
    }

    /**
     * Devuelve el arete formateado o estándar de forma 100% segura.
     */
    public String getAreteDisplay() {
        if (ganado == null) return "S/A";
        String form = ganado.getAreteFormateado();
        return (form != null && !form.isBlank()) ? form : (ganado.getArete() != null ? ganado.getArete() : "S/A");
    }

    /**
     * Devuelve el arete plano (limpio para comparaciones y búsquedas).
     */
    public String getAreteCodigo() {
        if (ganado == null || ganado.getArete() == null) return "";
        return ganado.getArete();
    }

    /**
     * Calcula la pérdida de peso física (en kg) entre el despacho en finca y la báscula de matadero:
     * Kilos Merma = Peso Finca - Peso Matadero
     */
    public BigDecimal getKilosMerma() {
        if (ganado == null || ganado.getPesoKg() == null || pesoMataderoKg == null) {
            return null;
        }
        return ganado.getPesoKg().subtract(pesoMataderoKg);
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

