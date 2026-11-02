package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.GanadoRechazo;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repositorio Simulado en Memoria para la entidad Ganado y GanadoRechazo.
 * Diseñado bajo el patrón Repository para desacoplar el acceso a datos.
 */
@Repository
public class GanadoRepository {

    private final Map<String, Ganado> baseDatosGanado = new ConcurrentHashMap<>();

    public GanadoRepository() {
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        // 1. Ganado Apto en Finca
        guardar(new Ganado("NI-10021", 2, "Hosco", "En Finca", new BigDecimal("380.50"), "Apto"));
        guardar(new Ganado("NI-10022", 4, "Bermejo", "En Finca", new BigDecimal("420.00"), "Apto"));
        guardar(new Ganado("NI-10023", 2, "Bramil", "En Finca", new BigDecimal("395.20"), "Apto"));
        guardar(new Ganado("NI-10024", 0, "Negro", "En Finca", new BigDecimal("310.80"), "Apto"));
        guardar(new Ganado("NI-10025", 6, "Pintado", "En Finca", new BigDecimal("455.00"), "Apto"));
        guardar(new Ganado("NI-10026", 4, "Hosco", "En Finca", new BigDecimal("415.50"), "Apto"));

        // 2. Ganado Entregado a Matadero
        guardar(new Ganado("NI-10015", 4, "Bermejo", "Entregado a Matadero", new BigDecimal("460.00"), "Apto"));
        guardar(new Ganado("NI-10016", 6, "Hosco", "Entregado a Matadero", new BigDecimal("478.50"), "Apto"));
        guardar(new Ganado("NI-10017", 4, "Bramil", "Entregado a Matadero", new BigDecimal("450.00"), "Apto"));

        // 3. Ganado de Rechazo (Hereda de Ganado)
        guardar(new GanadoRechazo(
            "NI-10008",
            4,
            "Bermejo",
            "En Finca",
            new BigDecimal("430.00"),
            "Hematoma severo en paleta derecha durante acarreo",
            LocalDate.of(2026, 10, 15),
            new BigDecimal("410.50")
        ));

        guardar(new GanadoRechazo(
            "NI-10009",
            2,
            "Hosco",
            "En Finca",
            new BigDecimal("360.00"),
            "Dictamen sanitario por condición febril leve en retén",
            LocalDate.of(2026, 10, 18),
            new BigDecimal("348.00")
        ));

        guardar(new GanadoRechazo(
            "NI-10010",
            8,
            "Pintado",
            "Entregado a Matadero",
            new BigDecimal("480.00"),
            "Canal sobremadura no apta para mercado exportación",
            LocalDate.of(2026, 9, 28),
            new BigDecimal("462.00")
        ));
    }

    public List<Ganado> listarTodos() {
        return new ArrayList<>(baseDatosGanado.values());
    }

    public Optional<Ganado> buscarPorArete(String arete) {
        if (arete == null) return Optional.empty();
        return Optional.ofNullable(baseDatosGanado.get(arete.trim().toUpperCase()));
    }

    public Ganado guardar(Ganado ganado) {
        if (ganado != null && ganado.getArete() != null) {
            String clave = ganado.getArete().trim().toUpperCase();
            ganado.setArete(clave);
            baseDatosGanado.put(clave, ganado);
        }
        return ganado;
    }

    public boolean eliminarPorArete(String arete) {
        if (arete == null) return false;
        return baseDatosGanado.remove(arete.trim().toUpperCase()) != null;
    }

    public List<Ganado> filtrarPorEstado(String estado) {
        return baseDatosGanado.values().stream()
                .filter(g -> g.getEstado() != null && g.getEstado().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

    public List<Ganado> filtrarPorCategoria(String categoria) {
        return baseDatosGanado.values().stream()
                .filter(g -> g.getCategoria() != null && g.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    public List<GanadoRechazo> listarSoloRechazos() {
        return baseDatosGanado.values().stream()
                .filter(g -> g instanceof GanadoRechazo || "Rechazo".equalsIgnoreCase(g.getCategoria()))
                .map(g -> {
                    if (g instanceof GanadoRechazo) {
                        return (GanadoRechazo) g;
                    } else {
                        return new GanadoRechazo(
                                g.getArete(), g.getPaletas(), g.getColor(), g.getEstado(),
                                g.getPesoEntradaKg(), "Rechazo general", LocalDate.now(), g.getPesoEntradaKg()
                        );
                    }
                })
                .collect(Collectors.toList());
    }
}
