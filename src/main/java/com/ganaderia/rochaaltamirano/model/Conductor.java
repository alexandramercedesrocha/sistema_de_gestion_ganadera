package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Conductor (Tabla: conductor)
 */
@Entity
@Table(name = "conductor")
public class Conductor {

    @Id
    @Column(name = "cedula", length = 20)
    private String cedula;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "url_foto_licencia", length = 255)
    private String urlFotoLicencia;

    @OneToMany(mappedBy = "conductor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefono> telefonos = new ArrayList<>();

    public Conductor() {
    }

    public Conductor(String cedula, String nombre, String urlFotoLicencia) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.urlFotoLicencia = urlFotoLicencia;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlFotoLicencia() {
        return urlFotoLicencia;
    }

    public void setUrlFotoLicencia(String urlFotoLicencia) {
        this.urlFotoLicencia = urlFotoLicencia;
    }

    public List<Telefono> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<Telefono> telefonos) {
        this.telefonos = telefonos;
    }
}

