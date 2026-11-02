package com.ganaderia.rochaaltamirano.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Entidad Arqueos Mensuales
 * 
 * Modela el cierre contable periódico para evaluar ingresos brutos,
 * gastos operativos y monto neto a distribuir entre los socios Wilber Rocha y Yader Altamirano (50% - 50%).
 */
public class ArqueoMensual {

    private Long idArqueo;
    private LocalDate fecha;
    private BigDecimal montoBruto;
    private BigDecimal montoNeto;
    private BigDecimal gastosVarios;

    public ArqueoMensual() {
    }

    public ArqueoMensual(Long idArqueo, LocalDate fecha, BigDecimal montoBruto, BigDecimal montoNeto, BigDecimal gastosVarios) {
        this.idArqueo = idArqueo;
        this.fecha = fecha;
        this.montoBruto = montoBruto;
        this.montoNeto = montoNeto;
        this.gastosVarios = gastosVarios;
    }

    public Long getIdArqueo() {
        return idArqueo;
    }

    public void setIdArqueo(Long idArqueo) {
        this.idArqueo = idArqueo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getMontoBruto() {
        return montoBruto;
    }

    public void setMontoBruto(BigDecimal montoBruto) {
        this.montoBruto = montoBruto;
    }

    public BigDecimal getMontoNeto() {
        return montoNeto;
    }

    public void setMontoNeto(BigDecimal montoNeto) {
        this.montoNeto = montoNeto;
    }

    public BigDecimal getGastosVarios() {
        return gastosVarios;
    }

    public void setGastosVarios(BigDecimal gastosVarios) {
        this.gastosVarios = gastosVarios;
    }

    /**
     * Cuota de ganancia correspondiente a Wilber Rocha (50%)
     */
    public BigDecimal getParticipacionWilber() {
        if (montoNeto != null) {
            return montoNeto.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Cuota de ganancia correspondiente a Yader Altamirano (50%)
     */
    public BigDecimal getParticipacionYader() {
        if (montoNeto != null) {
            return montoNeto.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
