package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.*;
import com.ganaderia.rochaaltamirano.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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

    public static class DatosViajeDto {
        private String cedulaConductor;
        private String numPlaca;
        private Integer idUbicacionOrigen;
        private Integer idUbicacionDestino;
        private BigDecimal costoFlete;
        private String estadoPago;

        public DatosViajeDto() {}

        public DatosViajeDto(String cedulaConductor, String numPlaca, Integer idUbicacionOrigen,
                             Integer idUbicacionDestino, BigDecimal costoFlete, String estadoPago) {
            this.cedulaConductor = cedulaConductor;
            this.numPlaca = numPlaca;
            this.idUbicacionOrigen = idUbicacionOrigen;
            this.idUbicacionDestino = idUbicacionDestino;
            this.costoFlete = costoFlete;
            this.estadoPago = estadoPago;
        }

        public String getCedulaConductor() { return cedulaConductor; }
        public void setCedulaConductor(String cedulaConductor) { this.cedulaConductor = cedulaConductor; }

        public String getNumPlaca() { return numPlaca; }
        public void setNumPlaca(String numPlaca) { this.numPlaca = numPlaca; }

        public Integer getIdUbicacionOrigen() { return idUbicacionOrigen; }
        public void setIdUbicacionOrigen(Integer idUbicacionOrigen) { this.idUbicacionOrigen = idUbicacionOrigen; }

        public Integer getIdUbicacionDestino() { return idUbicacionDestino; }
        public void setIdUbicacionDestino(Integer idUbicacionDestino) { this.idUbicacionDestino = idUbicacionDestino; }

        public BigDecimal getCostoFlete() { return costoFlete; }
        public void setCostoFlete(BigDecimal costoFlete) { this.costoFlete = costoFlete; }

        public String getEstadoPago() { return estadoPago; }
        public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    }

    @Transactional
    public Entrega registrarEntrega(Entrega entrega, Integer idMatadero) {
        if (idMatadero != null) {
            mataderoRepository.findById(idMatadero).ifPresent(entrega::setMatadero);
        }
        return entregaRepository.save(entrega);
    }

    @Transactional
    public Entrega registrarEntrega(Entrega entrega, Integer idMatadero, Integer idViaje) {
        if (idMatadero != null) {
            mataderoRepository.findById(idMatadero).ifPresent(entrega::setMatadero);
        }
        Entrega guardada = entregaRepository.save(entrega);
        if (idViaje != null) {
            viajeRepository.findById(idViaje).ifPresent(v -> {
                v.setEntrega(guardada);
                viajeRepository.save(v);
                guardada.getViajes().add(v);
            });
        }
        return guardada;
    }

    /**
     * Sobrecarga para registrar una entrega con un solo viaje.
     */
    @Transactional
    public Entrega registrarEntregaConViaje(Entrega entrega, Integer idMatadero,
                                            String cedulaConductor, String numPlaca,
                                            Integer idUbicacionOrigen, Integer idUbicacionDestino,
                                            BigDecimal costoFlete, String estadoPagoViaje) {
        List<DatosViajeDto> lista = new ArrayList<>();
        lista.add(new DatosViajeDto(cedulaConductor, numPlaca, idUbicacionOrigen, idUbicacionDestino, costoFlete, estadoPagoViaje));
        return registrarEntregaConViajes(entrega, idMatadero, lista);
    }

    /**
     * Registra una Entrega a Matadero conformando dinámicamente uno o MÚLTIPLES Viajes logísticos asociados
     * según los componentes: Conductor, Camión, Origen, Destino y Costo/Pago de Flete (Gasto).
     */
    @Transactional
    public Entrega registrarEntregaConViajes(Entrega entrega, Integer idMatadero, List<DatosViajeDto> listaViajes) {
        if (entrega.getPrecioAcordado() != null && entrega.getPrecioAcordado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio acordado debe ser mayor a cero");
        }

        Matadero matadero = null;
        if (idMatadero != null) {
            matadero = mataderoRepository.findById(idMatadero).orElse(null);
            entrega.setMatadero(matadero);
        }

        // Guardar la Entrega primero para que tenga su ID autogenerado
        Entrega entregaGuardada = entregaRepository.save(entrega);

        if (listaViajes != null && !listaViajes.isEmpty()) {
            for (DatosViajeDto dto : listaViajes) {
                if (dto == null) continue;
                if (dto.getCostoFlete() != null && dto.getCostoFlete().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("El costo de flete no puede ser negativo");
                }
                String cedula = dto.getCedulaConductor();
                String placa = dto.getNumPlaca();

                // Un viaje válido requiere al menos chofer o camión
                boolean tieneDatos = (cedula != null && !cedula.isBlank())
                        || (placa != null && !placa.isBlank())
                        || dto.getIdUbicacionOrigen() != null
                        || (dto.getCostoFlete() != null && dto.getCostoFlete().compareTo(BigDecimal.ZERO) > 0);

                if (!tieneDatos) continue;

                Viaje viaje = new Viaje();
                viaje.setEntrega(entregaGuardada);
                viaje.setEstadoPago(dto.getEstadoPago() != null && !dto.getEstadoPago().isBlank() ? dto.getEstadoPago() : "Pendiente");
                if ("Pagado".equalsIgnoreCase(viaje.getEstadoPago())) {
                    viaje.setFechaPago(entregaGuardada.getFechaEntrega() != null ? entregaGuardada.getFechaEntrega() : LocalDate.now());
                }

                if (cedula != null && !cedula.isBlank()) {
                    conductorRepository.findById(cedula.trim().toUpperCase()).ifPresent(viaje::setConductor);
                }
                if (placa != null && !placa.isBlank()) {
                    camionRepository.findById(placa.trim().toUpperCase()).ifPresent(viaje::setCamion);
                }
                if (dto.getIdUbicacionOrigen() != null) {
                    ubicacionRepository.findById(dto.getIdUbicacionOrigen()).ifPresent(viaje::setUbicacionOrigen);
                }
                if (dto.getIdUbicacionDestino() != null) {
                    ubicacionRepository.findById(dto.getIdUbicacionDestino()).ifPresent(viaje::setUbicacionDestino);
                } else if (matadero != null && matadero.getUbicaciones() != null && !matadero.getUbicaciones().isEmpty()) {
                    viaje.setUbicacionDestino(matadero.getUbicaciones().get(0));
                }

                Viaje viajeGuardado = viajeRepository.save(viaje);
                entregaGuardada.getViajes().add(viajeGuardado);

                // Si se especificó costo de flete, se genera el registro contable de GASTOS asociado a este viaje
                if (dto.getCostoFlete() != null && dto.getCostoFlete().compareTo(BigDecimal.ZERO) > 0) {
                    Gasto gasto = new Gasto();
                    gasto.setFechaGasto(entregaGuardada.getFechaEntrega() != null ? entregaGuardada.getFechaEntrega() : LocalDate.now());
                    gasto.setMonto(dto.getCostoFlete());

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
        }

        return entregaGuardada;
    }

    @Transactional
    public DetalleEntrega registrarDetallePesaje(Integer idEntrega, String arete, BigDecimal pesoMataderoKg,
                                                 String estadoDictamen, String motivoRechazo) {
        if (pesoMataderoKg != null && pesoMataderoKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El peso de matadero debe ser mayor a cero");
        }
        Entrega entrega = entregaRepository.findById(idEntrega)
                .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada: " + idEntrega));

        Ganado ganado = null;
        if (arete != null && !arete.isBlank()) {
            Optional<Ganado> optGanado = ganadoRepository.findById(arete.trim().toUpperCase());
            if (optGanado.isPresent()) {
                ganado = optGanado.get();
                // Actualizar estado del animal según el tipo de dictamen
                if (estadoDictamen != null && estadoDictamen.toLowerCase().contains("devuelto")) {
                    ganado.setCategoria("Rechazo");
                    ganado.setEstado("En Finca"); // Retorna a finca vivo para recuperación
                } else if (estadoDictamen != null && estadoDictamen.toLowerCase().startsWith("rechazado")) {
                    ganado.setCategoria("Rechazo Comprado");
                    ganado.setEstado("Entregado a Matadero"); // Se queda en planta
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

        // Si es dictamen Rechazado (comprado o devuelto), se registra en ganado_rechazado
        if (estadoDictamen != null && estadoDictamen.toLowerCase().startsWith("rechazado")) {
            GanadoRechazado rechazo = new GanadoRechazado();
            rechazo.setDetalleEntrega(guardado);
            rechazo.setMotivoRechazo(motivoRechazo != null && !motivoRechazo.isBlank() 
                    ? motivoRechazo : (estadoDictamen.toLowerCase().contains("devuelto") ? "Devuelto vivo a finca por dictamen veterinario/físico" : "Dictamen desfavorable en báscula de matadero"));
            ganadoRechazadoRepository.save(rechazo);
        }

        return guardado;
    }

    public List<DetalleEntrega> listarDetallesPorEntrega(Integer idEntrega) {
        return detalleEntregaRepository.findByEntregaIdEntrega(idEntrega);
    }

    @Transactional
    public void asociarGanadoAEntrega(Integer idEntrega, List<String> aretes) {
        asociarGanadoAEntrega(idEntrega, aretes, null, null);
    }

    @Transactional
    public void asociarGanadoAEntrega(Integer idEntrega, List<String> aretes, java.util.Map<String, BigDecimal> preciosRechazo) {
        asociarGanadoAEntrega(idEntrega, aretes, preciosRechazo, null);
    }

    @Transactional
    public void asociarGanadoAEntrega(Integer idEntrega, List<String> aretes, 
                                      java.util.Map<String, BigDecimal> preciosRechazo,
                                      java.util.Map<String, BigDecimal> pesosSalida) {
        if (aretes == null || aretes.isEmpty()) return;
        Entrega entrega = entregaRepository.findById(idEntrega)
                .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada: " + idEntrega));

        // Obtener detalles ya existentes para evitar duplicados en la misma entrega
        List<DetalleEntrega> existentes = detalleEntregaRepository.findByEntregaIdEntrega(idEntrega);
        java.util.Set<String> aretesYaAsociados = new java.util.HashSet<>();
        for (DetalleEntrega d : existentes) {
            if (d.getGanado() != null && d.getGanado().getArete() != null) {
                aretesYaAsociados.add(d.getGanado().getArete().trim().toUpperCase());
                aretesYaAsociados.add(d.getGanado().getArete().replaceAll("\\s+", "").toUpperCase());
            }
        }

        for (String arete : aretes) {
            if (arete == null || arete.isBlank()) continue;
            String areteLimpio = arete.trim().toUpperCase();
            String areteSinEspacios = areteLimpio.replaceAll("\\s+", "");

            if (aretesYaAsociados.contains(areteLimpio) || aretesYaAsociados.contains(areteSinEspacios)) {
                continue; // Ya está asociado a esta entrega
            }

            // Búsqueda flexible de la res en la base de datos (con o sin espacio)
            Optional<Ganado> optGanado = ganadoRepository.findById(areteLimpio);
            if (optGanado.isEmpty()) {
                List<Ganado> flex = ganadoRepository.buscarPorAreteFlexible(areteSinEspacios, areteLimpio);
                if (!flex.isEmpty()) {
                    optGanado = Optional.of(flex.get(0));
                }
            }

            if (optGanado.isPresent()) {
                Ganado ganado = optGanado.get();

                // Actualizar nuevo peso de salida si el animal incrementó de peso por engorde antes de entregarse
                if (pesosSalida != null) {
                    BigDecimal nuevoPeso = pesosSalida.get(areteLimpio);
                    if (nuevoPeso == null) {
                        nuevoPeso = pesosSalida.get(areteSinEspacios);
                    }
                    if (nuevoPeso != null && nuevoPeso.compareTo(BigDecimal.ZERO) > 0) {
                        ganado.setPesoKg(nuevoPeso);
                    }
                }

                ganado.setCategoria("Apto");
                ganado.setEstado("Entregado a Matadero");
                ganadoRepository.save(ganado);

                DetalleEntrega detalle = new DetalleEntrega();
                detalle.setEntrega(entrega);
                detalle.setGanado(ganado);
                // Inicialmente el peso de matadero queda pendiente hasta que la planta registre su pesaje oficial
                detalle.setPesoMataderoKg(null);
                detalle.setEstadoDictamen("Apto");
                if (preciosRechazo != null) {
                    BigDecimal pRech = preciosRechazo.get(areteLimpio);
                    if (pRech == null) pRech = preciosRechazo.get(areteSinEspacios);
                    if (pRech != null) {
                        detalle.setPrecioRechazo(pRech);
                    }
                }
                detalleEntregaRepository.save(detalle);

                aretesYaAsociados.add(areteLimpio);
                aretesYaAsociados.add(areteSinEspacios);
            }
        }
    }

    @Transactional
    public Entrega guardarEntrega(Entrega entrega) {
        return entregaRepository.save(entrega);
    }

    @Transactional
    public void actualizarPesajeYDictamen(Integer idDetalle, BigDecimal pesoMataderoKg, String estadoDictamen, String motivoRechazo) {
        actualizarPesajeYDictamen(idDetalle, pesoMataderoKg, null, estadoDictamen, motivoRechazo, null);
    }

    @Transactional
    public void actualizarPesajeYDictamen(Integer idDetalle, BigDecimal pesoMataderoKg, BigDecimal pesoFincaKg, String estadoDictamen, String motivoRechazo) {
        actualizarPesajeYDictamen(idDetalle, pesoMataderoKg, pesoFincaKg, estadoDictamen, motivoRechazo, null);
    }

    @Transactional
    public void actualizarPesajeYDictamen(Integer idDetalle, BigDecimal pesoMataderoKg, BigDecimal pesoFincaKg, 
                                          String estadoDictamen, String motivoRechazo, BigDecimal precioRechazo) {
        if (pesoFincaKg != null && pesoFincaKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El peso en finca debe ser mayor a cero");
        }
        if (pesoMataderoKg != null && pesoMataderoKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El peso en matadero debe ser mayor a cero");
        }
        if (precioRechazo != null && precioRechazo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio de rechazo no puede ser negativo");
        }
        DetalleEntrega detalle = detalleEntregaRepository.findById(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado: " + idDetalle));

        if (pesoFincaKg != null && pesoFincaKg.compareTo(BigDecimal.ZERO) > 0 && detalle.getGanado() != null) {
            detalle.getGanado().setPesoKg(pesoFincaKg);
            ganadoRepository.save(detalle.getGanado());
        }

        if (pesoMataderoKg != null && pesoMataderoKg.compareTo(BigDecimal.ZERO) > 0) {
            detalle.setPesoMataderoKg(pesoMataderoKg);
        }
        if (estadoDictamen != null && !estadoDictamen.isBlank()) {
            detalle.setEstadoDictamen(estadoDictamen);
            Ganado ganado = detalle.getGanado();
            if (ganado != null) {
                if (estadoDictamen.toLowerCase().contains("devuelto")) {
                    ganado.setCategoria("Rechazo");
                    ganado.setEstado("En Finca"); // Retorna a finca vivo para recuperación y re-aretado
                } else if (estadoDictamen.toLowerCase().startsWith("rechazado")) {
                    ganado.setCategoria("Rechazo Comprado");
                    ganado.setEstado("Entregado a Matadero"); // Se queda en planta
                } else {
                    ganado.setCategoria("Apto");
                    ganado.setEstado("Entregado a Matadero");
                }
                ganadoRepository.save(ganado);
            }

            boolean esRechazo = estadoDictamen.toLowerCase().startsWith("rechazado");
            boolean esDevuelto = estadoDictamen.toLowerCase().contains("devuelto");

            if (esRechazo) {
                if (esDevuelto) {
                    detalle.setPrecioRechazo(null); // No se cobra a la planta si fue devuelto vivo
                } else {
                    // Asignar precio de rechazo individual o heredar de la entrega
                    if (precioRechazo != null && precioRechazo.compareTo(BigDecimal.ZERO) > 0) {
                        detalle.setPrecioRechazo(precioRechazo);
                        // Si la entrega no tenía precio de rechazo general asignado, asignarlo también
                        if (detalle.getEntrega() != null && (detalle.getEntrega().getPrecioRechazo() == null || detalle.getEntrega().getPrecioRechazo().compareTo(BigDecimal.ZERO) <= 0)) {
                            detalle.getEntrega().setPrecioRechazo(precioRechazo);
                            entregaRepository.save(detalle.getEntrega());
                        }
                    } else if (detalle.getPrecioRechazo() == null && detalle.getEntrega() != null && detalle.getEntrega().getPrecioRechazo() != null) {
                        detalle.setPrecioRechazo(detalle.getEntrega().getPrecioRechazo());
                    }
                }

                String motivoDefecto = esDevuelto 
                        ? "Devuelto vivo a finca por dictamen sanitario/físico en planta" 
                        : "Dictamen desfavorable en báscula de matadero (comprado con penalización)";

                if (detalle.getRechazos() == null || detalle.getRechazos().isEmpty()) {
                    GanadoRechazado rechazo = new GanadoRechazado();
                    rechazo.setDetalleEntrega(detalle);
                    rechazo.setMotivoRechazo(motivoRechazo != null && !motivoRechazo.isBlank() 
                            ? motivoRechazo : motivoDefecto);
                    GanadoRechazado guardado = ganadoRechazadoRepository.save(rechazo);
                    if (detalle.getRechazos() != null) {
                        detalle.getRechazos().add(guardado);
                    }
                } else {
                    GanadoRechazado existente = detalle.getRechazos().get(0);
                    if (existente != null) {
                        existente.setMotivoRechazo(motivoRechazo != null && !motivoRechazo.isBlank() 
                                ? motivoRechazo : motivoDefecto);
                        ganadoRechazadoRepository.save(existente);
                    }
                }
            } else if ("Apto".equalsIgnoreCase(estadoDictamen)) {
                detalle.setPrecioRechazo(null);
                if (detalle.getRechazos() != null && !detalle.getRechazos().isEmpty()) {
                    ganadoRechazadoRepository.deleteAll(detalle.getRechazos());
                    detalle.getRechazos().clear();
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
