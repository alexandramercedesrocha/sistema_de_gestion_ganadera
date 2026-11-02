package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;

/**
 * Entidad Ubicación (Tabla: ubicacion)
 * Lugares, fincas, comarcas o plantas con enlace a Google Maps.
 */
@Entity
@Table(name = "ubicacion")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Integer idUbicacion;

    @Column(name = "nombre_lugar", nullable = false, length = 100)
    private String nombreLugar;

    @Column(name = "tipo_lugar", length = 50)
    private String tipoLugar; // ej. Finca, Matadero, Báscula, Acopio

    @Column(name = "url", length = 255)
    private String url; // Enlace satelital de Google Maps

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matadero")
    private Matadero matadero;

    public Ubicacion() {
    }

    public Ubicacion(Integer idUbicacion, String nombreLugar, String tipoLugar, String url) {
        this.idUbicacion = idUbicacion;
        this.nombreLugar = nombreLugar;
        this.tipoLugar = tipoLugar;
        this.url = url;
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(Integer idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getNombreLugar() {
        return nombreLugar;
    }

    public void setNombreLugar(String nombreLugar) {
        this.nombreLugar = nombreLugar;
    }

    public String getTipoLugar() {
        return tipoLugar;
    }

    public void setTipoLugar(String tipoLugar) {
        this.tipoLugar = tipoLugar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Matadero getMatadero() {
        return matadero;
    }

    public void setMatadero(Matadero matadero) {
        this.matadero = matadero;
    }
}

