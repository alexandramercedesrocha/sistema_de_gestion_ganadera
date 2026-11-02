package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;

/**
 * Entidad CategoriaGasto (Tabla: categoria_gasto)
 * Clasificación de los gastos operativos (ej. Medicina, Sal, Peones, Mantenimiento).
 */
@Entity
@Table(name = "categoria_gasto")
public class CategoriaGasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "nombre_categoria", nullable = false, length = 100)
    private String nombreCategoria;

    public CategoriaGasto() {
    }

    public CategoriaGasto(Integer idCategoria, String nombreCategoria) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
}

