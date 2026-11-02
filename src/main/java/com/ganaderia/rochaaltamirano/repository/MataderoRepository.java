package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Matadero;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio Simulado para Plantas Procesadoras / Mataderos
 */
@Repository
public class MataderoRepository {

    private final Map<Long, Matadero> mataderos = new ConcurrentHashMap<>();

    public MataderoRepository() {
        mataderos.put(1L, new Matadero(1L, "MACESA (Matadero Central S.A.)", "Km 141 Carretera a El Rama, Juigalpa, Chontales", "+505 2512-2240"));
        mataderos.put(2L, new Matadero(2L, "SAN MARTIN (Industrial Comercial San Martín)", "Km 52 Carretera Panamericana Sur, Nandaime, Granada", "+505 2552-8000"));
        mataderos.put(3L, new Matadero(3L, "SUKARNE Nicaragua", "Km 35 Carretera Vieja a Tipitapa", "+505 2298-1500"));
        mataderos.put(4L, new Matadero(4L, "NOVALICSA", "Km 14 Carretera a Masaya", "+505 2279-8800"));
    }

    public List<Matadero> listarTodos() {
        return new ArrayList<>(mataderos.values());
    }

    public Optional<Matadero> buscarPorId(Long id) {
        return Optional.ofNullable(mataderos.get(id));
    }

    public Matadero guardar(Matadero matadero) {
        if (matadero.getIdMatadero() == null) {
            long nuevoId = mataderos.keySet().stream().mapToLong(v -> v).max().orElse(0L) + 1L;
            matadero.setIdMatadero(nuevoId);
        }
        mataderos.put(matadero.getIdMatadero(), matadero);
        return matadero;
    }
}
