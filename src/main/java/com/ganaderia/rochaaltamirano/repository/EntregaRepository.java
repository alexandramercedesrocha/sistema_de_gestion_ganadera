package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Entrega;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio Simulado para Entregas de Ganado a Mataderos
 */
@Repository
public class EntregaRepository {

    private final Map<Long, Entrega> entregas = new ConcurrentHashMap<>();

    public EntregaRepository() {
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        guardar(new Entrega(501L, LocalDate.of(2026, 10, 8), 16, new BigDecimal("7250.00"), "MACESA (Matadero Central S.A.)"));
        guardar(new Entrega(502L, LocalDate.of(2026, 10, 16), 20, new BigDecimal("9180.00"), "SAN MARTIN (Industrial Comercial San Martín)"));
    }

    public List<Entrega> listarTodas() {
        List<Entrega> lista = new ArrayList<>(entregas.values());
        lista.sort((e1, e2) -> e2.getFechaEntrega().compareTo(e1.getFechaEntrega()));
        return lista;
    }

    public Optional<Entrega> buscarPorId(Long id) {
        return Optional.ofNullable(entregas.get(id));
    }

    public Entrega guardar(Entrega entrega) {
        if (entrega.getIdEntrega() == null) {
            long nuevoId = entregas.keySet().stream().mapToLong(v -> v).max().orElse(500L) + 1L;
            entrega.setIdEntrega(nuevoId);
        }
        entregas.put(entrega.getIdEntrega(), entrega);
        return entrega;
    }
}
