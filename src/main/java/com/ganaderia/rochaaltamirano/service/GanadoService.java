package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.GanadoRechazo;
import com.ganaderia.rochaaltamirano.repository.GanadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Lógica de Negocio para la Administración de Ganado Bovino
 */
@Service
@Transactional(readOnly = true)
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

    @Transactional
    public Ganado registrarGanado(Ganado ganado) {
        if (ganado.getCategoria() == null || ganado.getCategoria().isBlank()) {
            ganado.setCategoria("Apto");
        }
        if (ganado.getEstado() == null || ganado.getEstado().isBlank()) {
            ganado.setEstado("En Finca");
        }
        return ganadoRepository.guardar(ganado);
    }

    @Transactional
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

    @Transactional
    public boolean eliminarGanado(String arete) {
        return ganadoRepository.eliminarPorArete(arete);
    }

    public List<Ganado> listarGanadoActivoInventario() {
        return ganadoRepository.findGanadoActivoInventario();
    }

    public List<Ganado> listarGanadoVendidosAceptados() {
        return ganadoRepository.findGanadoVendidosAceptados();
    }

    public List<Ganado> listarGanadoRechazosMatadero() {
        return ganadoRepository.findGanadoRechazosMatadero();
    }

    public List<Ganado> listarGanadoEnFincaSinDespacho() {
        return ganadoRepository.findGanadoEnFincaSinDespacho();
    }

    public long contarCabezasEnFinca() {
        return ganadoRepository.findGanadoActivoInventario().size();
    }

    public long contarCabezasEntregadas() {
        return ganadoRepository.findGanadoVendidosAceptados().size();
    }

    public long contarRechazos() {
        return ganadoRepository.findGanadoRechazosMatadero().size();
    }

    public BigDecimal calcularTotalKilosEnFinca() {
        return ganadoRepository.findGanadoActivoInventario().stream()
                .map(g -> g.getPesoKg() != null ? g.getPesoKg() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
