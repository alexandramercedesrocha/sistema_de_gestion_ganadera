package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;

/**
 * Entidad GanadoRechazado (Tabla: ganado_rechazado)
 * Registra el motivo técnico del rechazo/decomiso originado en el matadero.
 */
@Entity
@Table(name = "ganado_rechazado")
public class GanadoRechazado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rechazo")
    private Integer idRechazo;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_entrega")
    private DetalleEntrega detalleEntrega;

    public GanadoRechazado() {
    }

    public GanadoRechazado(Integer idRechazo, String motivoRechazo, DetalleEntrega detalleEntrega) {
        this.idRechazo = idRechazo;
        this.motivoRechazo = motivoRechazo;
        this.detalleEntrega = detalleEntrega;
    }

    public Integer getIdRechazo() {
        return idRechazo;
    }

    public void setIdRechazo(Integer idRechazo) {
        this.idRechazo = idRechazo;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public DetalleEntrega getDetalleEntrega() {
        return detalleEntrega;
    }

    public void setDetalleEntrega(DetalleEntrega detalleEntrega) {
        this.detalleEntrega = detalleEntrega;
    }
}

