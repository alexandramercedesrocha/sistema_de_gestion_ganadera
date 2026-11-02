package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Matadero (Tabla: matadero)
 */
@Entity
@Table(name = "matadero")
public class Matadero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matadero")
    private Integer idMatadero;

    @Column(name = "nombre_matadero", nullable = false, length = 100)
    private String nombreMatadero;

    @OneToMany(mappedBy = "matadero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefono> telefonos = new ArrayList<>();

    @OneToMany(mappedBy = "matadero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ubicacion> ubicaciones = new ArrayList<>();

    public Matadero() {
    }

    public Matadero(Integer idMatadero, String nombreMatadero) {
        this.idMatadero = idMatadero;
        this.nombreMatadero = nombreMatadero;
    }

    public Integer getIdMatadero() {
        return idMatadero;
    }

    public void setIdMatadero(Integer idMatadero) {
        this.idMatadero = idMatadero;
    }

    public String getNombreMatadero() {
        return nombreMatadero;
    }

    public void setNombreMatadero(String nombreMatadero) {
        this.nombreMatadero = nombreMatadero;
    }

    public List<Telefono> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<Telefono> telefonos) {
        this.telefonos = telefonos;
    }

    public List<Ubicacion> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(List<Ubicacion> ubicaciones) {
        this.ubicaciones = ubicaciones;
    }
}
