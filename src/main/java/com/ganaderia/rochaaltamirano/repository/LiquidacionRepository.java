package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Liquidacion;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio Simulado para Liquidaciones de Mataderos
 */
@Repository
public class LiquidacionRepository {

    private final Map<String, Liquidacion> liquidaciones = new ConcurrentHashMap<>();

    public LiquidacionRepository() {
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        guardar(new Liquidacion("LIQ-MACESA-2026-881", "MACESA - Matadero Central S.A.", LocalDate.of(2026, 10, 10), new BigDecimal("18250.00")));
        guardar(new Liquidacion("LIQ-SANMARTIN-2026-402", "Industrial Comercial San Martín", LocalDate.of(2026, 10, 18), new BigDecimal("23400.00")));
        guardar(new Liquidacion("LIQ-MACESA-2026-905", "MACESA - Matadero Central S.A.", LocalDate.of(2026, 9, 29), new BigDecimal("15800.00")));
    }

    public List<Liquidacion> listarTodas() {
        List<Liquidacion> lista = new ArrayList<>(liquidaciones.values());
        lista.sort((l1, l2) -> l2.getFechaLiquidacion().compareTo(l1.getFechaLiquidacion()));
        return lista;
    }

    public Optional<Liquidacion> buscarPorComprobante(String idNumComprobante) {
        if (idNumComprobante == null) return Optional.empty();
        return Optional.ofNullable(liquidaciones.get(idNumComprobante.trim().toUpperCase()));
    }

    public Liquidacion guardar(Liquidacion liquidacion) {
        if (liquidacion != null && liquidacion.getIdNumComprobante() != null) {
            String clave = liquidacion.getIdNumComprobante().trim().toUpperCase();
            liquidacion.setIdNumComprobante(clave);
            liquidaciones.put(clave, liquidacion);
        }
        return liquidacion;
    }
}
