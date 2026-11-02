package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.*;
import com.ganaderia.rochaaltamirano.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Lógica de Negocio para Entregas a Mataderos y Detalle de Faenado
 */
@Service
@Transactional(readOnly = true)
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final DetalleEntregaRepository detalleEntregaRepository;
    private final GanadoRechazadoRepository ganadoRechazadoRepository;
    private final GanadoRepository ganadoRepository;
    private final MataderoRepository mataderoRepository;

    public EntregaService(EntregaRepository entregaRepository,
                          DetalleEntregaRepository detalleEntregaRepository,
                          GanadoRechazadoRepository ganadoRechazadoRepository,
                          GanadoRepository ganadoRepository,
                          MataderoRepository mataderoRepository) {
        this.entregaRepository = entregaRepository;
        this.detalleEntregaRepository = detalleEntregaRepository;
        this.ganadoRechazadoRepository = ganadoRechazadoRepository;
        this.ganadoRepository = ganadoRepository;
        this.mataderoRepository = mataderoRepository;
    }

    public List<Entrega> listarTodas() {
        return entregaRepository.findAll();
    }

    public Optional<Entrega> buscarPorId(Integer idEntrega) {
        return entregaRepository.findById(idEntrega);
    }

    @Transactional
    public Entrega registrarEntrega(Entrega entrega, Integer idMatadero) {
        if (idMatadero != null) {
            mataderoRepository.findById(idMatadero).ifPresent(entrega::setMatadero);
        }
        return entregaRepository.save(entrega);
    }

    @Transactional
    public DetalleEntrega registrarDetallePesaje(Integer idEntrega, String arete, BigDecimal pesoMataderoKg,
                                                 String estadoDictamen, String motivoRechazo) {
        Entrega entrega = entregaRepository.findById(idEntrega)
                .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada: " + idEntrega));

        Ganado ganado = null;
        if (arete != null && !arete.isBlank()) {
            ganado = ganadoRepository.findById(arete.trim().toUpperCase()).orElse(null);
            if (ganado != null) {
                ganado.setEstado("Entregado a Matadero");
                ganadoRepository.save(ganado);
            }
        }

        DetalleEntrega detalle = new DetalleEntrega();
        detalle.setEntrega(entrega);
        detalle.setGanado(ganado);
        detalle.setPesoMataderoKg(pesoMataderoKg);
        detalle.setEstadoDictamen(estadoDictamen != null ? estadoDictamen : "Apto");

        DetalleEntrega guardado = detalleEntregaRepository.save(detalle);

        // Si es dictamen Rechazado, se registra en ganado_rechazado
        if ("Rechazado".equalsIgnoreCase(estadoDictamen)) {
            GanadoRechazado rechazo = new GanadoRechazado();
            rechazo.setDetalleEntrega(guardado);
            rechazo.setMotivoRechazo(motivoRechazo != null && !motivoRechazo.isBlank() 
                    ? motivoRechazo : "Dictamen desfavorable en báscula de matadero");
            ganadoRechazadoRepository.save(rechazo);
        }

        return guardado;
    }

    public List<DetalleEntrega> listarDetallesPorEntrega(Integer idEntrega) {
        return detalleEntregaRepository.findByEntregaIdEntrega(idEntrega);
    }

    public long contarTotalCabezasEntregadas() {
        return detalleEntregaRepository.count();
    }

    public long contarTotalRechazosMatadero() {
        return ganadoRechazadoRepository.count();
    }

    public BigDecimal calcularKilosTotalesEntregados() {
        return detalleEntregaRepository.findAll().stream()
                .map(DetalleEntrega::getPesoMataderoKg)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

