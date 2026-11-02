package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.DetalleEntrega;
import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.GanadoRechazado;
import com.ganaderia.rochaaltamirano.model.GanadoRechazo;
import com.ganaderia.rochaaltamirano.repository.GanadoRechazadoRepository;
import com.ganaderia.rochaaltamirano.repository.GanadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Lógica de Negocio para la Administración de Ganado Bovino
 */
@Service
@Transactional(readOnly = true)
public class GanadoService {

    private final GanadoRepository ganadoRepository;
    private final GanadoRechazadoRepository ganadoRechazadoRepository;

    public GanadoService(GanadoRepository ganadoRepository, GanadoRechazadoRepository ganadoRechazadoRepository) {
        this.ganadoRepository = ganadoRepository;
        this.ganadoRechazadoRepository = ganadoRechazadoRepository;
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
        if (rechazo.getEstado() == null || rechazo.getEstado().isBlank()) {
            rechazo.setEstado("En Finca");
        }
        Ganado g = new Ganado(
                rechazo.getArete(),
                rechazo.getPaletas(),
                rechazo.getColor(),
                rechazo.getPesoEntradaKg(),
                "Rechazo",
                null
        );
        g.setEstado(rechazo.getEstado());
        g.setCategoria("Rechazo");
        ganadoRepository.guardar(g);
        return rechazo;
    }

    public List<Ganado> listarPorEstado(String estado) {
        return ganadoRepository.filtrarPorEstado(estado);
    }

    public List<Ganado> listarPorCategoria(String categoria) {
        return ganadoRepository.filtrarPorCategoria(categoria);
    }

    public List<GanadoRechazo> listarRechazos() {
        List<GanadoRechazo> lista = new ArrayList<>();
        
        try {
            List<GanadoRechazado> rechazosBD = ganadoRechazadoRepository.findAll();
            for (GanadoRechazado gr : rechazosBD) {
                DetalleEntrega det = gr.getDetalleEntrega();
                if (det != null && det.getGanado() != null) {
                    Ganado g = det.getGanado();
                    LocalDate fecha = (det.getEntrega() != null && det.getEntrega().getFechaEntrega() != null)
                            ? det.getEntrega().getFechaEntrega() : LocalDate.now();
                    BigDecimal pesoEntrada = g.getPesoKg() != null ? g.getPesoKg() : BigDecimal.ZERO;
                    BigDecimal pesoMat = det.getPesoMataderoKg() != null ? det.getPesoMataderoKg() : pesoEntrada;

                    GanadoRechazo dto = new GanadoRechazo(
                            g.getArete(),
                            g.getPaletas(),
                            g.getColor(),
                            "Rechazo en Matadero",
                            pesoEntrada,
                            gr.getMotivoRechazo() != null ? gr.getMotivoRechazo() : "Rechazo en planta matadero",
                            fecha,
                            pesoMat
                    );
                    lista.add(dto);
                }
            }
        } catch (Exception ignored) {
        }

        // Si la lista está vacía, buscamos reses marcadas con rechazo en la base de datos
        if (lista.isEmpty()) {
            try {
                List<Ganado> rechazados = ganadoRepository.findGanadoRechazosMatadero();
                for (Ganado g : rechazados) {
                    GanadoRechazo dto = new GanadoRechazo(
                            g.getArete(),
                            g.getPaletas(),
                            g.getColor(),
                            "Rechazo en Matadero",
                            g.getPesoKg() != null ? g.getPesoKg() : BigDecimal.ZERO,
                            "Dictamen desfavorable en báscula de matadero",
                            LocalDate.now(),
                            g.getPesoKg() != null ? g.getPesoKg() : BigDecimal.ZERO
                    );
                    lista.add(dto);
                }
            } catch (Exception ignored) {
            }
        }

        return lista;
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
