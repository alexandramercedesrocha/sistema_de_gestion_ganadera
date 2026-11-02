package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Proveedor (Tabla: proveedor)
 */
@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefono> telefonos = new ArrayList<>();

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ubicacion> ubicaciones = new ArrayList<>();

    public Proveedor() {
    }

    public Proveedor(Integer idProveedor, String nombre) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
