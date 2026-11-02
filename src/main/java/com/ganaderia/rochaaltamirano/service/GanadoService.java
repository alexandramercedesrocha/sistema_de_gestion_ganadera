package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.GanadoRechazo;
import com.ganaderia.rochaaltamirano.repository.GanadoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Lógica de Negocio para la Administración de Ganado Bovino
 */
@Service
public class GanadoService {

    private final GanadoRepository ganadoRepository;

    public GanadoService(GanadoRepository ganadoRepository) {
        this.ganadoRepository = ganadoRepository;
    }

    public List<Ganado> listarInventarioCompleto() {
        return ganadoRepository.listarTodos();
    }

    public Optional<Ganado> buscarPorArete(String arete) {
        return ganadoRepository.buscarPorArete(arete);
    }

    public Ganado registrarGanado(Ganado ganado) {
        // Validación de reglas de negocio
        if (ganado.getCategoria() == null || ganado.getCategoria().isBlank()) {
            ganado.setCategoria("Apto");
        }
        if (ganado.getEstado() == null || ganado.getEstado().isBlank()) {
            ganado.setEstado("En Finca");
        }
        return ganadoRepository.guardar(ganado);
    }

    public GanadoRechazo registrarRechazo(GanadoRechazo rechazo) {
        rechazo.setCategoria("Rechazo");
        return (GanadoRechazo) ganadoRepository.guardar(rechazo);
    }

    public List<Ganado> listarPorEstado(String estado) {
        return ganadoRepository.filtrarPorEstado(estado);
    }

    public List<Ganado> listarPorCategoria(String categoria) {
        return ganadoRepository.filtrarPorCategoria(categoria);
    }

    public List<GanadoRechazo> listarRechazos() {
        return ganadoRepository.listarSoloRechazos();
    }

    public boolean eliminarGanado(String arete) {
        return ganadoRepository.eliminarPorArete(arete);
    }

    public long contarCabezasEnFinca() {
        return ganadoRepository.listarTodos().stream()
                .filter(Ganado::isEnFinca)
                .count();
    }

    public long contarCabezasEntregadas() {
        return ganadoRepository.listarTodos().stream()
                .filter(g -> "Entregado a Matadero".equalsIgnoreCase(g.getEstado()))
                .count();
    }

    public long contarRechazos() {
        return ganadoRepository.listarTodos().stream()
                .filter(Ganado::isRechazo)
                .count();
    }

    public BigDecimal calcularTotalKilosEnFinca() {
        return ganadoRepository.listarTodos().stream()
                .filter(Ganado::isEnFinca)
                .map(g -> g.getPesoEntradaKg() != null ? g.getPesoEntradaKg() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
