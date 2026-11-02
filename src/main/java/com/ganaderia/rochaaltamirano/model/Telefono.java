package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;

/**
 * Entidad Teléfono (Tabla: telefono)
 * Directorio telefónico multi-entidad (Proveedor, Matadero o Conductor).
 */
@Entity
@Table(name = "telefono")
public class Telefono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_telefono")
    private Integer idTelefono;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "compañia", length = 50)
    private String compania; // Claro, Tigo, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matadero")
    private Matadero matadero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cedula_conductor")
    private Conductor conductor;

    public Telefono() {
    }

    public Telefono(Integer idTelefono, String telefono, String compania) {
        this.idTelefono = idTelefono;
        this.telefono = telefono;
        this.compania = compania;
    }

    public Integer getIdTelefono() {
        return idTelefono;
    }

    public void setIdTelefono(Integer idTelefono) {
        this.idTelefono = idTelefono;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
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

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }
}

