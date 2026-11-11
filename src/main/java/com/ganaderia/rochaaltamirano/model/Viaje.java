package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad Viaje (Tabla: viaje)
 * Control de servicios de transporte, rutas, camión, conductor y estado de pago de flete.
 */
@Entity
@Table(name = "viaje")
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viaje")
    private Integer idViaje;

    @Column(name = "estado_pago", length = 50)
    private String estadoPago; // Pendiente, Pagado

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion_origen")
    private Ubicacion ubicacionOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion_destino")
    private Ubicacion ubicacionDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cedula_conductor")
    private Conductor conductor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "num_placa")
    private Camion camion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrega", nullable = true)
    private Entrega entrega;

    public Viaje() {
    }

    public Viaje(Integer idViaje, String estadoPago, LocalDate fechaPago,
                 Ubicacion ubicacionOrigen, Ubicacion ubicacionDestino,
                 Conductor conductor, Camion camion) {
        this.idViaje = idViaje;
        this.estadoPago = estadoPago;
        this.fechaPago = fechaPago;
        this.ubicacionOrigen = ubicacionOrigen;
        this.ubicacionDestino = ubicacionDestino;
        this.conductor = conductor;
        this.camion = camion;
    }

    public Integer getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(Integer idViaje) {
        this.idViaje = idViaje;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Ubicacion getUbicacionOrigen() {
        return ubicacionOrigen;
    }

    public void setUbicacionOrigen(Ubicacion ubicacionOrigen) {
        this.ubicacionOrigen = ubicacionOrigen;
    }

    public Ubicacion getUbicacionDestino() {
        return ubicacionDestino;
    }

    public void setUbicacionDestino(Ubicacion ubicacionDestino) {
        this.ubicacionDestino = ubicacionDestino;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }
}

