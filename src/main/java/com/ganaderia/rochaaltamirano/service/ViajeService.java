package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.*;
import com.ganaderia.rochaaltamirano.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Lógica de Negocio para el Módulo de Transporte y Viajes
 */
@Service
@Transactional(readOnly = true)
public class ViajeService {

    private final ViajeRepository viajeRepository;
    private final CamionRepository camionRepository;
    private final ConductorRepository conductorRepository;
    private final UbicacionRepository ubicacionRepository;

    public ViajeService(ViajeRepository viajeRepository,
                        CamionRepository camionRepository,
                        ConductorRepository conductorRepository,
                        UbicacionRepository ubicacionRepository) {
        this.viajeRepository = viajeRepository;
        this.camionRepository = camionRepository;
        this.conductorRepository = conductorRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    public List<Viaje> listarTodos() {
        return viajeRepository.findAll();
    }

    public Optional<Viaje> buscarPorId(Integer idViaje) {
        return viajeRepository.findById(idViaje);
    }

    @Transactional
    public Viaje registrarViaje(Viaje viaje, Integer idOrigen, Integer idDestino, String cedulaConductor, String numPlaca) {
        if (idOrigen != null) {
            ubicacionRepository.findById(idOrigen).ifPresent(viaje::setUbicacionOrigen);
        }
        if (idDestino != null) {
            ubicacionRepository.findById(idDestino).ifPresent(viaje::setUbicacionDestino);
        }
        if (cedulaConductor != null && !cedulaConductor.isBlank()) {
            conductorRepository.findById(cedulaConductor).ifPresent(viaje::setConductor);
        }
        if (numPlaca != null && !numPlaca.isBlank()) {
            camionRepository.findById(numPlaca).ifPresent(viaje::setCamion);
        }
        if (viaje.getEstadoPago() == null || viaje.getEstadoPago().isBlank()) {
            viaje.setEstadoPago("Pendiente");
        }
        return viajeRepository.save(viaje);
    }

    @Transactional
    public boolean marcarComoPagado(Integer idViaje) {
        Optional<Viaje> opt = viajeRepository.findById(idViaje);
        if (opt.isPresent()) {
            Viaje viaje = opt.get();
            viaje.setEstadoPago("Pagado");
            viaje.setFechaPago(LocalDate.now());
            viajeRepository.save(viaje);
            return true;
        }
        return false;
    }

    public long contarViajesPendientes() {
        return viajeRepository.findByEstadoPago("Pendiente").size();
    }
}

