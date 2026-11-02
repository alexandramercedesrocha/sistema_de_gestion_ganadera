package com.ganaderia.rochaaltamirano.model;

/**
 * Entidad Matadero
 * 
 * Representa las plantas industriales procesadoras de carne bovina
 * (ej. MACESA en Juigalpa, SAN MARTIN en Nandaime) a las cuales la sociedad entrega ganado.
 */
public class Matadero {

    private Long idMatadero;
    private String nombreMatadero;
    private String ubicacion;
    private String contactoTelefono;

    public Matadero() {
    }

    public Matadero(Long idMatadero, String nombreMatadero, String ubicacion, String contactoTelefono) {
        this.idMatadero = idMatadero;
        this.nombreMatadero = nombreMatadero;
        this.ubicacion = ubicacion;
        this.contactoTelefono = contactoTelefono;
    }

    public Long getIdMatadero() {
        return idMatadero;
    }

    public void setIdMatadero(Long idMatadero) {
        this.idMatadero = idMatadero;
    }

    public String getNombreMatadero() {
        return nombreMatadero;
    }

    public void setNombreMatadero(String nombreMatadero) {
        this.nombreMatadero = nombreMatadero;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getContactoTelefono() {
        return contactoTelefono;
    }

    public void setContactoTelefono(String contactoTelefono) {
        this.contactoTelefono = contactoTelefono;
    }
}
