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
    private final ViajeRepository viajeRepository;

    public EntregaService(EntregaRepository entregaRepository,
                          DetalleEntregaRepository detalleEntregaRepository,
                          GanadoRechazadoRepository ganadoRechazadoRepository,
                          GanadoRepository ganadoRepository,
                          MataderoRepository mataderoRepository,
                          ViajeRepository viajeRepository) {
        this.entregaRepository = entregaRepository;
        this.detalleEntregaRepository = detalleEntregaRepository;
        this.ganadoRechazadoRepository = ganadoRechazadoRepository;
        this.ganadoRepository = ganadoRepository;
        this.mataderoRepository = mataderoRepository;
        this.viajeRepository = viajeRepository;
    }

    public List<Entrega> listarTodas() {
        return entregaRepository.findAll();
    }

    public Optional<Entrega> buscarPorId(Integer idEntrega) {
        return entregaRepository.findById(idEntrega);
    }

    @Transactional
    public Entrega registrarEntrega(Entrega entrega, Integer idMatadero) {
        return registrarEntrega(entrega, idMatadero, null);
    }

    @Transactional
    public Entrega registrarEntrega(Entrega entrega, Integer idMatadero, Integer idViaje) {
        if (idMatadero != null) {
            mataderoRepository.findById(idMatadero).ifPresent(entrega::setMatadero);
        }
        if (idViaje != null) {
            viajeRepository.findById(idViaje).ifPresent(entrega::setViaje);
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
            Optional<Ganado> optGanado = ganadoRepository.findById(arete.trim().toUpperCase());
            if (optGanado.isPresent()) {
                ganado = optGanado.get();
                // Actualizar estado del animal
                if ("Rechazado".equalsIgnoreCase(estadoDictamen)) {
                    ganado.setCategoria("Rechazo");
                    ganado.setEstado("En Finca"); // Retorna a finca como rechazo
                } else {
                    ganado.setCategoria("Apto");
                    ganado.setEstado("Entregado a Matadero");
                }
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

    public BigDecimal calcularKilosTotalesEntregados() {
        return detalleEntregaRepository.findAll().stream()
                .map(DetalleEntrega::getPesoMataderoKg)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long contarTotalRechazosMatadero() {
        return ganadoRechazadoRepository.count();
    }
}
