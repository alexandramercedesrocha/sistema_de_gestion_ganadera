package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.Compra;
import com.ganaderia.rochaaltamirano.repository.CompraRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la Gestión de Compras de Ganado en Pie
 */
@Service
public class CompraService {

    private final CompraRepository compraRepository;

    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public List<Compra> listarCompras() {
        return compraRepository.listarTodas();
    }

    public Optional<Compra> buscarPorId(Long id) {
        return compraRepository.buscarPorId(id);
    }

    public Compra registrarCompra(Compra compra) {
        // Cálculo automático del monto total si se proporciona precio por kilo y total kilos
        if (compra.getMontoTotal() == null || compra.getMontoTotal().compareTo(BigDecimal.ZERO) == 0) {
            if (compra.getPrecioKg() != null && compra.getTotalKilos() != null) {
                compra.setMontoTotal(compra.getPrecioKg().multiply(compra.getTotalKilos()));
            } else if (compra.getPrecioEstandar() != null && compra.getTotalCabezas() != null) {
                compra.setMontoTotal(compra.getPrecioEstandar().multiply(BigDecimal.valueOf(compra.getTotalCabezas())));
            }
        }
        return compraRepository.guardar(compra);
    }

    public BigDecimal calcularTotalInvertido() {
        return compraRepository.listarTodas().stream()
                .map(c -> c.getMontoTotal() != null ? c.getMontoTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int calcularTotalCabezasCompradas() {
        return compraRepository.listarTodas().stream()
                .mapToInt(c -> c.getTotalCabezas() != null ? c.getTotalCabezas() : 0)
                .sum();
    }
}
