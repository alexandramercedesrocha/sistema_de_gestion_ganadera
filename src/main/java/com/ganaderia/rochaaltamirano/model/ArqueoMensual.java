package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad ArqueoMensual (Tabla: arqueos_mensuales)
 * Registra el cierre contable mensual con liquidaciones y gastos asociados.
 */
@Entity
@Table(name = "arqueos_mensuales")
public class ArqueoMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_arqueo")
    private Integer idArqueo;

    @Column(name = "fecha")
    private LocalDate fecha;

    @OneToMany(mappedBy = "arqueo")
    private List<Liquidacion> liquidaciones = new ArrayList<>();

    @OneToMany(mappedBy = "arqueo")
    private List<Gasto> gastos = new ArrayList<>();

    public ArqueoMensual() {
    }

    public ArqueoMensual(Integer idArqueo, LocalDate fecha) {
        this.idArqueo = idArqueo;
        this.fecha = fecha;
    }

    public Integer getIdArqueo() {
        return idArqueo;
    }

    public void setIdArqueo(Integer idArqueo) {
        this.idArqueo = idArqueo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<Liquidacion> getLiquidaciones() {
        return liquidaciones;
    }

    public void setLiquidaciones(List<Liquidacion> liquidaciones) {
        this.liquidaciones = liquidaciones;
    }

    public List<Gasto> getGastos() {
        return gastos;
    }

    public void setGastos(List<Gasto> gastos) {
        this.gastos = gastos;
    }

    // Cálculos contables dinámicos
    public BigDecimal getMontoBruto() {
        if (liquidaciones == null || liquidaciones.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return liquidaciones.stream()
                .map(Liquidacion::getMonto)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getGastosVarios() {
        if (gastos == null || gastos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return gastos.stream()
                .map(Gasto::getMonto)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMontoNeto() {
        return getMontoBruto().subtract(getGastosVarios());
    }

    // Métodos para compatibilidad previa
    public void setMontoBruto(BigDecimal montoBruto) {}
    public void setMontoNeto(BigDecimal montoNeto) {}
    public void setGastosVarios(BigDecimal gastosVarios) {}
}
