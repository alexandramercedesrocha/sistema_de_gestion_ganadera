package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Compra;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio Simulado para el Registro de Compras de Ganado en Pie
 */
@Repository
public class CompraRepository {

    private final Map<Long, Compra> compras = new ConcurrentHashMap<>();

    public CompraRepository() {
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        guardar(new Compra(
                101L,
                LocalDate.of(2026, 10, 5),
                18,
                new BigDecimal("2.10"),
                new BigDecimal("2.05"),
                new BigDecimal("6930.00"),
                "Don Chepe Miranda (Hacienda La Gloria)",
                new BigDecimal("14553.00"),
                "Comarca San Jerónimo, Teustepe, Boaco"
        ));

        guardar(new Compra(
                102L,
                LocalDate.of(2026, 10, 11),
                22,
                new BigDecimal("2.15"),
                new BigDecimal("2.10"),
                new BigDecimal("8800.00"),
                "Ganados del Norte S.A. (Matagalpa)",
                new BigDecimal("18920.00"),
                "El Tuma - La Dalia, Matagalpa"
        ));

        guardar(new Compra(
                103L,
                LocalDate.of(2026, 10, 19),
                14,
                new BigDecimal("2.08"),
                new BigDecimal("2.00"),
                new BigDecimal("5320.00"),
                "Finca Santa Rosa (Chontales)",
                new BigDecimal("11065.60"),
                "Acoyapa, Chontales"
        ));
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
