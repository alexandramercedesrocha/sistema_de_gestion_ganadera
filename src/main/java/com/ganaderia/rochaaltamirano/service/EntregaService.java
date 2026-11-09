package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.*;
import com.ganaderia.rochaaltamirano.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final ConductorRepository conductorRepository;
    private final CamionRepository camionRepository;
    private final UbicacionRepository ubicacionRepository;
    private final GastoRepository gastoRepository;
    private final CategoriaGastoRepository categoriaGastoRepository;

    public EntregaService(EntregaRepository entregaRepository,
                          DetalleEntregaRepository detalleEntregaRepository,
                          GanadoRechazadoRepository ganadoRechazadoRepository,
                          GanadoRepository ganadoRepository,
                          MataderoRepository mataderoRepository,
                          ViajeRepository viajeRepository,
                          ConductorRepository conductorRepository,
                          CamionRepository camionRepository,
                          UbicacionRepository ubicacionRepository,
                          GastoRepository gastoRepository,
                          CategoriaGastoRepository categoriaGastoRepository) {
        this.entregaRepository = entregaRepository;
        this.detalleEntregaRepository = detalleEntregaRepository;
        this.ganadoRechazadoRepository = ganadoRechazadoRepository;
        this.ganadoRepository = ganadoRepository;
        this.mataderoRepository = mataderoRepository;
        this.viajeRepository = viajeRepository;
        this.conductorRepository = conductorRepository;
        this.camionRepository = camionRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.gastoRepository = gastoRepository;
        this.categoriaGastoRepository = categoriaGastoRepository;
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

    /**
     * Registra una Entrega a Matadero conformando dinámicamente el Viaje logístico asociado
     * según los componentes: Conductor, Camión, Origen, Destino y Costo/Pago de Flete (Gasto).
     */
    @Transactional
    public Entrega registrarEntregaConViaje(Entrega entrega, Integer idMatadero,
                                            String cedulaConductor, String numPlaca,
                                            Integer idUbicacionOrigen, Integer idUbicacionDestino,
                                            BigDecimal costoFlete, String estadoPagoViaje) {
        Matadero matadero = null;
        if (idMatadero != null) {
            matadero = mataderoRepository.findById(idMatadero).orElse(null);
            entrega.setMatadero(matadero);
        }

        // Si se especificó conductor, camión, o alguna ubicación, se conforma y crea el Viaje
        boolean tieneLogisticaViaje = (cedulaConductor != null && !cedulaConductor.isBlank())
                || (numPlaca != null && !numPlaca.isBlank())
                || idUbicacionOrigen != null
                || idUbicacionDestino != null
                || (costoFlete != null && costoFlete.compareTo(BigDecimal.ZERO) > 0);

        if (tieneLogisticaViaje) {
            Viaje viaje = new Viaje();
            viaje.setEstadoPago(estadoPagoViaje != null && !estadoPagoViaje.isBlank() ? estadoPagoViaje : "Pendiente");
            if ("Pagado".equalsIgnoreCase(viaje.getEstadoPago())) {
                viaje.setFechaPago(entrega.getFechaEntrega() != null ? entrega.getFechaEntrega() : LocalDate.now());
            }

            if (cedulaConductor != null && !cedulaConductor.isBlank()) {
                conductorRepository.findById(cedulaConductor.trim().toUpperCase()).ifPresent(viaje::setConductor);
            }
            if (numPlaca != null && !numPlaca.isBlank()) {
                camionRepository.findById(numPlaca.trim().toUpperCase()).ifPresent(viaje::setCamion);
            }
            if (idUbicacionOrigen != null) {
                ubicacionRepository.findById(idUbicacionOrigen).ifPresent(viaje::setUbicacionOrigen);
            }
            if (idUbicacionDestino != null) {
                ubicacionRepository.findById(idUbicacionDestino).ifPresent(viaje::setUbicacionDestino);
            } else if (matadero != null && matadero.getUbicaciones() != null && !matadero.getUbicaciones().isEmpty()) {
                viaje.setUbicacionDestino(matadero.getUbicaciones().get(0));
            }

            Viaje viajeGuardado = viajeRepository.save(viaje);
            entrega.setViaje(viajeGuardado);

            // Si se ingresó costo de flete, se genera el registro contable de GASTOS asociado al viaje (jerarquía ISA EER)
            if (costoFlete != null && costoFlete.compareTo(BigDecimal.ZERO) > 0) {
                Gasto gasto = new Gasto();
                gasto.setFechaGasto(entrega.getFechaEntrega() != null ? entrega.getFechaEntrega() : LocalDate.now());
                gasto.setMonto(costoFlete);

                String choferTxt = viajeGuardado.getConductor() != null ? viajeGuardado.getConductor().getNombre() : "S/C";
                String camionTxt = viajeGuardado.getCamion() != null ? viajeGuardado.getCamion().getNumPlaca() : "S/P";
                String plantaTxt = matadero != null ? matadero.getNombreMatadero() : "Matadero";
                gasto.setDescripcion("Flete de transporte a " + plantaTxt + " (Camión: " + camionTxt + ", Chofer: " + choferTxt + ")");
                gasto.setViaje(viajeGuardado);

                CategoriaGasto catTransporte = categoriaGastoRepository.findAll().stream()
                        .filter(c -> c.getNombreCategoria() != null && c.getNombreCategoria().toLowerCase().contains("transporte"))
                        .findFirst()
                        .orElseGet(() -> {
                            CategoriaGasto nueva = new CategoriaGasto();
                            nueva.setNombreCategoria("Transporte / Flete");
                            return categoriaGastoRepository.save(nueva);
                        });
                gasto.setCategoria(catTransporte);
                gastoRepository.save(gasto);
            }
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

    @Transactional
    public void asociarGanadoAEntrega(Integer idEntrega, List<String> aretes) {
        asociarGanadoAEntrega(idEntrega, aretes, null);
    }

    @Transactional
    public void asociarGanadoAEntrega(Integer idEntrega, List<String> aretes, java.util.Map<String, BigDecimal> preciosRechazo) {
        if (aretes == null || aretes.isEmpty()) return;
        Entrega entrega = entregaRepository.findById(idEntrega)
                .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada: " + idEntrega));

        for (String arete : aretes) {
            if (arete == null || arete.isBlank()) continue;
            String areteLimpio = arete.trim().toUpperCase();
            Optional<Ganado> optGanado = ganadoRepository.findById(areteLimpio);
            if (optGanado.isPresent()) {
                Ganado ganado = optGanado.get();
                ganado.setCategoria("Apto");
                ganado.setEstado("Entregado a Matadero");
                ganadoRepository.save(ganado);

                DetalleEntrega detalle = new DetalleEntrega();
                detalle.setEntrega(entrega);
                detalle.setGanado(ganado);
                detalle.setPesoMataderoKg(ganado.getPesoKg());
                detalle.setEstadoDictamen("Apto");
                if (preciosRechazo != null && preciosRechazo.containsKey(areteLimpio)) {
                    detalle.setPrecioRechazo(preciosRechazo.get(areteLimpio));
                }
                detalleEntregaRepository.save(detalle);
            }
        }
    }

    @Transactional
    public void actualizarPesajeYDictamen(Integer idDetalle, BigDecimal pesoMataderoKg, String estadoDictamen, String motivoRechazo) {
        DetalleEntrega detalle = detalleEntregaRepository.findById(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado: " + idDetalle));

        if (pesoMataderoKg != null && pesoMataderoKg.compareTo(BigDecimal.ZERO) > 0) {
            detalle.setPesoMataderoKg(pesoMataderoKg);
        }
        if (estadoDictamen != null && !estadoDictamen.isBlank()) {
            detalle.setEstadoDictamen(estadoDictamen);
            Ganado ganado = detalle.getGanado();
            if (ganado != null) {
                if ("Rechazado".equalsIgnoreCase(estadoDictamen)) {
                    ganado.setCategoria("Rechazo");
                    ganado.setEstado("En Finca");
                } else {
                    ganado.setCategoria("Apto");
                    ganado.setEstado("Entregado a Matadero");
                }
                ganadoRepository.save(ganado);
            }

            if ("Rechazado".equalsIgnoreCase(estadoDictamen)) {
                if (detalle.getRechazos() == null || detalle.getRechazos().isEmpty()) {
                    GanadoRechazado rechazo = new GanadoRechazado();
                    rechazo.setDetalleEntrega(detalle);
                    rechazo.setMotivoRechazo(motivoRechazo != null && !motivoRechazo.isBlank() 
                            ? motivoRechazo : "Dictamen desfavorable en báscula de matadero");
                    ganadoRechazadoRepository.save(rechazo);
                }
            }
        }
        detalleEntregaRepository.save(detalle);
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
