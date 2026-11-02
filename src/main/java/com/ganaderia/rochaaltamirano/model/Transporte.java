package com.ganaderia.rochaaltamirano.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Servicio de Transporte (Viaje / Flete)
 * 
 * Registra los traslados de ganado desde fincas de proveedores hacia la finca
 * o desde la finca hacia los mataderos (MACESA, SAN MARTIN).
 */
public class Transporte {

    private Long idTransporte;
    private String nombreTransportista;
    private LocalDate fechaViaje;
    private String origen;
    private String destino;
    private BigDecimal costoViaje;
    
    // Estado del pago del flete: 'Pendiente', 'Pagado'
    private String estadoPago;

    public Transporte() {
    }

    public Transporte(Long idTransporte, String nombreTransportista, LocalDate fechaViaje,
                      String origen, String destino, BigDecimal costoViaje, String estadoPago) {
        this.idTransporte = idTransporte;
        this.nombreTransportista = nombreTransportista;
        this.fechaViaje = fechaViaje;
        this.origen = origen;
        this.destino = destino;
        this.costoViaje = costoViaje;
        this.estadoPago = estadoPago;
    }

    public Long getIdTransporte() {
        return idTransporte;
    }

    public void setIdTransporte(Long idTransporte) {
        this.idTransporte = idTransporte;
    }

    public String getNombreTransportista() {
        return nombreTransportista;
    }

    public void setNombreTransportista(String nombreTransportista) {
        this.nombreTransportista = nombreTransportista;
    }

    public LocalDate getFechaViaje() {
        return fechaViaje;
    }

    public void setFechaViaje(LocalDate fechaViaje) {
        this.fechaViaje = fechaViaje;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public BigDecimal getCostoViaje() {
        return costoViaje;
    }

    public void setCostoViaje(BigDecimal costoViaje) {
        this.costoViaje = costoViaje;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public boolean isPagado() {
        return "Pagado".equalsIgnoreCase(this.estadoPago);
    }
}
