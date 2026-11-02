package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;

/**
 * Entidad Camión (Tabla: camion)
 */
@Entity
@Table(name = "camion")
public class Camion {

    @Id
    @Column(name = "num_placa", length = 20)
    private String numPlaca;

    @Column(name = "url_foto_circulacion", length = 255)
    private String urlFotoCirculacion;

    public Camion() {
    }

    public Camion(String numPlaca, String urlFotoCirculacion) {
        this.numPlaca = numPlaca;
        this.urlFotoCirculacion = urlFotoCirculacion;
    }

    public String getNumPlaca() {
        return numPlaca;
    }

    public void setNumPlaca(String numPlaca) {
        this.numPlaca = numPlaca;
    }

    public String getUrlFotoCirculacion() {
        return urlFotoCirculacion;
    }

    public void setUrlFotoCirculacion(String urlFotoCirculacion) {
        this.urlFotoCirculacion = urlFotoCirculacion;
    }
}

