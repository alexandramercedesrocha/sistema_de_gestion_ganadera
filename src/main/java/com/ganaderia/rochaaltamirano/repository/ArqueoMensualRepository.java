package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.ArqueoMensual;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio Simulado para Arqueos Mensuales y Cierres Contables
 */
@Repository
public class ArqueoMensualRepository {

    private final Map<Long, ArqueoMensual> arqueos = new ConcurrentHashMap<>();

    public ArqueoMensualRepository() {
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        guardar(new ArqueoMensual(
                1L,
                LocalDate.of(2026, 8, 31),
                new BigDecimal("48500.00"),
                new BigDecimal("9800.00"),
                new BigDecimal("38700.00")
        ));

        guardar(new ArqueoMensual(
                2L,
                LocalDate.of(2026, 9, 30),
                new BigDecimal("54200.00"),
                new BigDecimal("12350.00"),
                new BigDecimal("41850.00")
        ));
    }

    public List<ArqueoMensual> listarTodos() {
        List<ArqueoMensual> lista = new ArrayList<>(arqueos.values());
        lista.sort((a1, a2) -> a2.getFecha().compareTo(a1.getFecha()));
        return lista;
    }

    public Optional<ArqueoMensual> buscarPorId(Long id) {
        return Optional.ofNullable(arqueos.get(id));
    }

    public ArqueoMensual guardar(ArqueoMensual arqueo) {
        if (arqueo.getIdArqueo() == null) {
            long nuevoId = arqueos.keySet().stream().mapToLong(v -> v).max().orElse(0L) + 1L;
            arqueo.setIdArqueo(nuevoId);
        }
        arqueos.put(arqueo.getIdArqueo(), arqueo);
        return arqueo;
    }
}
