package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Compra;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio de Compras (Sin datos precargados)
 */
@Repository
public class CompraRepository {

    private final Map<Long, Compra> compras = new ConcurrentHashMap<>();

    public CompraRepository() {
        // Inicialmente vacío: se alimenta de la base de datos o registros manuales
    }

    public List<Compra> listarTodas() {
        List<Compra> lista = new ArrayList<>(compras.values());
        lista.sort((c1, c2) -> c2.getFechaCompra().compareTo(c1.getFechaCompra()));
        return lista;
    }

    public Optional<Compra> buscarPorId(Long id) {
        return Optional.ofNullable(compras.get(id));
    }

    public Compra guardar(Compra compra) {
        if (compra.getIdCompra() == null) {
            long nuevoId = compras.keySet().stream().mapToLong(v -> v).max().orElse(100L) + 1L;
            compra.setIdCompra(nuevoId);
        }
        compras.put(compra.getIdCompra(), compra);
        return compra;
    }
}
