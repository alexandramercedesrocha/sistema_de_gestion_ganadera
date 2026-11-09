package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.Lote;
import com.ganaderia.rochaaltamirano.model.PagoLote;
import com.ganaderia.rochaaltamirano.model.Proveedor;
import com.ganaderia.rochaaltamirano.repository.GanadoRepository;
import com.ganaderia.rochaaltamirano.repository.LoteRepository;
import com.ganaderia.rochaaltamirano.repository.PagoLoteRepository;
import com.ganaderia.rochaaltamirano.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Lógica de Negocio para la Gestión de Lotes de Compra de Ganado
 */
@Service
@Transactional(readOnly = true)
public class LoteService {

    private final LoteRepository loteRepository;
    private final GanadoRepository ganadoRepository;
    private final PagoLoteRepository pagoLoteRepository;
    private final ProveedorRepository proveedorRepository;

    public LoteService(LoteRepository loteRepository,
                       GanadoRepository ganadoRepository,
                       PagoLoteRepository pagoLoteRepository,
                       ProveedorRepository proveedorRepository) {
        this.loteRepository = loteRepository;
        this.ganadoRepository = ganadoRepository;
        this.pagoLoteRepository = pagoLoteRepository;
        this.proveedorRepository = proveedorRepository;
    }

    public List<Lote> listarLotes() {
        return loteRepository.findAll();
    }

    
    public boolean existeAreteEnSistema(String arete) {
        if (arete == null || arete.isBlank()) return false;
        String original = arete.trim().toUpperCase();
        String limpio = original.replaceAll("\\s+", "");
        return ganadoRepository.existeAreteEnBaseDatos(limpio, original);
    }

    public List<Ganado> buscarGanadoPorAreteFlexible(String arete) {
        if (arete == null || arete.isBlank()) return List.of();
        String original = arete.trim().toUpperCase();
        String limpio = original.replaceAll("\\s+", "");
        return ganadoRepository.buscarPorAreteFlexible(limpio, original);
    }

    public Optional<Lote> buscarPorId(Integer idLote) {
        return loteRepository.findById(idLote);
    }

    @Transactional
    public Lote guardarLote(Lote lote) {
        return loteRepository.save(lote);
    }

    @Transactional
    public Lote registrarLote(Lote lote, Integer idProveedor) {
        if (idProveedor != null) {
            proveedorRepository.findById(idProveedor).ifPresent(lote::setProveedor);
        }
        return loteRepository.save(lote);
    }

    @Transactional
    public Ganado registrarAnimalEnLote(Integer idLote, Ganado animal, String areteAnteriorCodigo) {
        Lote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado: " + idLote));
        animal.setLote(lote);

        if (areteAnteriorCodigo != null && !areteAnteriorCodigo.isBlank()) {
            ganadoRepository.findById(areteAnteriorCodigo.trim().toUpperCase())
                    .ifPresent(animal::setAreteAnterior);
        }

        return ganadoRepository.save(animal);
    }

    @Transactional
    public PagoLote registrarPagoLote(Integer idLote, String comprobanteVoucher, String estadoPago) {
        Lote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado: " + idLote));

        PagoLote pago = new PagoLote();
        pago.setLote(lote);
        pago.setComprobanteVoucher(comprobanteVoucher);
        pago.setEstadoPago(estadoPago != null ? estadoPago : "Pagado");

        return pagoLoteRepository.save(pago);
    }

    public long contarTotalLotes() {
        return loteRepository.count();
    }

    public long contarTotalCabezasCompradas() {
        return ganadoRepository.count();
    }

    public BigDecimal calcularTotalInvertidoEstimado() {
        List<Lote> lotes = loteRepository.findAll();
        BigDecimal total = BigDecimal.ZERO;
        for (Lote lote : lotes) {
            try {
                if (lote.getAnimales() != null && !lote.getAnimales().isEmpty()) {
                    for (Ganado g : lote.getAnimales()) {
                        total = total.add(g.getSubtotalCompra());
                    }
                }
            } catch (Exception e) {
                // Fallback seguro si la sesión lazy estuviera cerrada
                if (lote.getIdLote() != null) {
                    List<Ganado> animales = ganadoRepository.findByLoteIdLote(lote.getIdLote());
                    for (Ganado g : animales) {
                        total = total.add(g.getSubtotalCompra());
                    }
                }
            }
        }
        return total;
    }
}

