package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.Transporte;
import com.ganaderia.rochaaltamirano.repository.TransporteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Control de Transporte y Logística Ganadera
 */
@Service
public class TransporteService {

    private final TransporteRepository transporteRepository;

    public TransporteService(TransporteRepository transporteRepository) {
        this.transporteRepository = transporteRepository;
    }

    public List<Transporte> listarTodos() {
        return transporteRepository.listarTodos();
    }

    public Optional<Transporte> buscarPorId(Long id) {
        return transporteRepository.buscarPorId(id);
    }

    public Transporte registrarTransporte(Transporte transporte) {
        if (transporte.getEstadoPago() == null || transporte.getEstadoPago().isBlank()) {
            transporte.setEstadoPago("Pendiente");
        }
        return transporteRepository.guardar(transporte);
    }

    public boolean marcarComoPagado(Long idTransporte) {
        return transporteRepository.actualizarEstadoPago(idTransporte, "Pagado");
    }

    public BigDecimal calcularTotalCostosTransporte() {
        return transporteRepository.listarTodos().stream()
                .map(t -> t.getCostoViaje() != null ? t.getCostoViaje() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalPendientePago() {
        return transporteRepository.filtrarPorEstadoPago("Pendiente").stream()
                .map(t -> t.getCostoViaje() != null ? t.getCostoViaje() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
