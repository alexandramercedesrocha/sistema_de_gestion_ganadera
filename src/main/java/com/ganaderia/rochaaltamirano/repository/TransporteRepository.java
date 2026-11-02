package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Transporte;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repositorio Simulado para Servicios de Transporte Ganadero (Viajes y Fletes)
 */
@Repository
public class TransporteRepository {

    private final Map<Long, Transporte> transportes = new ConcurrentHashMap<>();

    public TransporteRepository() {
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        guardar(new Transporte(
                301L,
                "Don Marvin Centeno (Camión Jaula Isuzu)",
                LocalDate.of(2026, 10, 5),
                "Teustepe, Boaco",
                "Finca Santa Elena (Sociedad Rocha & Altamirano)",
                new BigDecimal("450.00"),
                "Pagado"
        ));

        guardar(new Transporte(
                302L,
                "Transportes Hernández (Rastra Ganadera 32 pies)",
                LocalDate.of(2026, 10, 8),
                "Finca Santa Elena",
                "MACESA - Juigalpa, Chontales",
                new BigDecimal("680.00"),
                "Pagado"
        ));

        guardar(new Transporte(
                303L,
                "Carlos Blandón (Camión Hino)",
                LocalDate.of(2026, 10, 11),
                "El Tuma - La Dalia, Matagalpa",
                "Finca Santa Elena",
                new BigDecimal("520.00"),
                "Pendiente"
        ));

        guardar(new Transporte(
                304L,
                "Transportes Hernández (Rastra Ganadera 32 pies)",
                LocalDate.of(2026, 10, 16),
                "Finca Santa Elena",
                "SAN MARTIN - Nandaime, Granada",
                new BigDecimal("750.00"),
                "Pendiente"
        ));
    }

    public List<Transporte> listarTodos() {
        List<Transporte> lista = new ArrayList<>(transportes.values());
        lista.sort((t1, t2) -> t2.getFechaViaje().compareTo(t1.getFechaViaje()));
        return lista;
    }

    public Optional<Transporte> buscarPorId(Long id) {
        return Optional.ofNullable(transportes.get(id));
    }

    public Transporte guardar(Transporte transporte) {
        if (transporte.getIdTransporte() == null) {
            long nuevoId = transportes.keySet().stream().mapToLong(v -> v).max().orElse(300L) + 1L;
            transporte.setIdTransporte(nuevoId);
        }
        transportes.put(transporte.getIdTransporte(), transporte);
        return transporte;
    }

    public List<Transporte> filtrarPorEstadoPago(String estado) {
        return transportes.values().stream()
                .filter(t -> t.getEstadoPago() != null && t.getEstadoPago().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

    public boolean actualizarEstadoPago(Long idTransporte, String nuevoEstado) {
        Transporte transporte = transportes.get(idTransporte);
        if (transporte != null) {
            transporte.setEstadoPago(nuevoEstado);
            return true;
        }
        return false;
    }
}
