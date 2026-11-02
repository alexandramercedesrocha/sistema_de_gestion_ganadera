package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Gasto;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio Simulado para Gastos Operativos de la Finca y Comercialización
 */
@Repository
public class GastoRepository {

    private final Map<Long, Gasto> gastos = new ConcurrentHashMap<>();

    public GastoRepository() {
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        guardar(new Gasto(1L, "Vacunación y Vitaminas", LocalDate.of(2026, 10, 4), "Dosis Ivermectina + complejo B para 50 cabezas", new BigDecimal("320.00")));
        guardar(new Gasto(2L, "Suplemento y Sal Mineral", LocalDate.of(2026, 10, 7), "15 sacos de sal mineralizada Ganasal 8%", new BigDecimal("450.00")));
        guardar(new Gasto(3L, "Planilla de Peones y Vaqueros", LocalDate.of(2026, 10, 15), "Quincena vaqueros de encierro y pesaje", new BigDecimal("700.00")));
        guardar(new Gasto(4L, "Trámites Guías IPSA y Alcaldía", LocalDate.of(2026, 10, 16), "Guías de movilización y cartas de venta", new BigDecimal("185.00")));
        guardar(new Gasto(5L, "Mantenimiento Báscula y Corrales", LocalDate.of(2026, 10, 20), "Reparación tranqueras y calibración de báscula", new BigDecimal("250.00")));
    }

    public List<Gasto> listarTodos() {
        List<Gasto> lista = new ArrayList<>(gastos.values());
        lista.sort((g1, g2) -> g2.getFechaGasto().compareTo(g1.getFechaGasto()));
        return lista;
    }

    public Optional<Gasto> buscarPorId(Long id) {
        return Optional.ofNullable(gastos.get(id));
    }

    public Gasto guardar(Gasto gasto) {
        if (gasto.getIdGasto() == null) {
            long nuevoId = gastos.keySet().stream().mapToLong(v -> v).max().orElse(0L) + 1L;
            gasto.setIdGasto(nuevoId);
        }
        gastos.put(gasto.getIdGasto(), gasto);
        return gasto;
    }
}
