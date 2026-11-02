package com.ganaderia.rochaaltamirano.model;

/**
 * Entidad Proveedor
 * 
 * Representa a los finqueros, ganaderos y productores a quienes
 * la sociedad compra lotes de ganado.
 */
public class Proveedor {

    private Long idProveedor;
    private String nombre;
    private String telefono;

    public Proveedor() {
    }

    public Proveedor(Long idProveedor, String nombre, String telefono) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public Long getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Long idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
