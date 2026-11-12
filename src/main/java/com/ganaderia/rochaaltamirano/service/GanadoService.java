package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.dto.HistorialBovinoDTO;
import com.ganaderia.rochaaltamirano.dto.HistorialEntregaDTO;
import com.ganaderia.rochaaltamirano.model.DetalleEntrega;
import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.GanadoRechazado;
import com.ganaderia.rochaaltamirano.model.GanadoRechazo;
import com.ganaderia.rochaaltamirano.model.Matadero;
import com.ganaderia.rochaaltamirano.repository.DetalleEntregaRepository;
import com.ganaderia.rochaaltamirano.repository.GanadoRechazadoRepository;
import com.ganaderia.rochaaltamirano.repository.GanadoRepository;
import com.ganaderia.rochaaltamirano.repository.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Servicio de Lógica de Negocio para la Administración de Ganado Bovino
 */
@Service
@Transactional(readOnly = true)
public class GanadoService {

    private final GanadoRepository ganadoRepository;
    private final GanadoRechazadoRepository ganadoRechazadoRepository;
    private final DetalleEntregaRepository detalleEntregaRepository;
    private final LoteRepository loteRepository;

    public GanadoService(GanadoRepository ganadoRepository, 
                         GanadoRechazadoRepository ganadoRechazadoRepository,
                         DetalleEntregaRepository detalleEntregaRepository,
                         LoteRepository loteRepository) {
        this.ganadoRepository = ganadoRepository;
        this.ganadoRechazadoRepository = ganadoRechazadoRepository;
        this.detalleEntregaRepository = detalleEntregaRepository;
        this.loteRepository = loteRepository;
    }

    public List<Ganado> listarInventarioCompleto() {
        return ganadoRepository.listarTodos();
    }

    public Optional<Ganado> buscarPorArete(String arete) {
        return ganadoRepository.buscarPorArete(arete);
    }

    @Transactional
    public Ganado registrarGanado(Ganado ganado) {
        if (ganado.getPesoKg() != null && ganado.getPesoKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El peso de la res debe ser mayor a cero");
        }
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
        if (rechazo.getPesoEntradaKg() != null && rechazo.getPesoEntradaKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El peso de entrada debe ser mayor a cero");
        }
        if (rechazo.getPesoMatadero() != null && rechazo.getPesoMatadero().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El peso de matadero debe ser mayor a cero");
        }
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
        java.util.Set<String> aretesAgregados = new java.util.HashSet<>();
        
        try {
            List<GanadoRechazado> rechazosBD = ganadoRechazadoRepository.findAll();
            for (GanadoRechazado gr : rechazosBD) {
                DetalleEntrega det = gr.getDetalleEntrega();
                if (det != null && det.getGanado() != null) {
                    Ganado g = det.getGanado();
                    String areteLimpio = g.getArete() != null ? g.getArete().trim().toUpperCase() : "";
                    if (!areteLimpio.isEmpty() && !aretesAgregados.contains(areteLimpio)) {
                        LocalDate fecha = (det.getEntrega() != null && det.getEntrega().getFechaEntrega() != null)
                                ? det.getEntrega().getFechaEntrega() : LocalDate.now();
                        BigDecimal pesoEntrada = g.getPesoKg() != null ? g.getPesoKg() : BigDecimal.ZERO;
                        BigDecimal pesoMat = det.getPesoMataderoKg() != null ? det.getPesoMataderoKg() : pesoEntrada;

                        String estadoRes;
                        if ("Re-aretado (Histórico)".equalsIgnoreCase(g.getEstado()) || fueRearetado(g)) {
                            estadoRes = "Re-aretado (Histórico)";
                        } else if (det.isRechazadoComprado() || (det.getPrecioRechazo() != null && det.getPrecioRechazo().compareTo(BigDecimal.ZERO) > 0)) {
                            estadoRes = "Entregado a Matadero";
                        } else {
                            estadoRes = "En Finca";
                        }

                        GanadoRechazo dto = new GanadoRechazo(
                                g.getArete(),
                                g.getPaletas(),
                                g.getColor(),
                                estadoRes,
                                pesoEntrada,
                                gr.getMotivoRechazo() != null ? gr.getMotivoRechazo() : "Rechazo en planta matadero",
                                fecha,
                                pesoMat
                        );
                        dto.setLote(g.getLote());
                        lista.add(dto);
                        aretesAgregados.add(areteLimpio);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        // Buscar reses en la tabla de ganado que estén marcadas como rechazo y no se hayan incluido aún
        try {
            List<Ganado> rechazados = ganadoRepository.findGanadoRechazosMatadero();
            for (Ganado g : rechazados) {
                String areteLimpio = g.getArete() != null ? g.getArete().trim().toUpperCase() : "";
                if (!areteLimpio.isEmpty() && !aretesAgregados.contains(areteLimpio)) {
                    String est = ("Re-aretado (Histórico)".equalsIgnoreCase(g.getEstado()) || fueRearetado(g))
                            ? "Re-aretado (Histórico)"
                            : "En Finca";
                    GanadoRechazo dto = new GanadoRechazo(
                            g.getArete(),
                            g.getPaletas(),
                            g.getColor(),
                            est,
                            g.getPesoKg() != null ? g.getPesoKg() : BigDecimal.ZERO,
                            "Dictamen desfavorable en báscula de matadero",
                            LocalDate.now(),
                            g.getPesoKg() != null ? g.getPesoKg() : BigDecimal.ZERO
                    );
                    dto.setLote(g.getLote());
                    lista.add(dto);
                    aretesAgregados.add(areteLimpio);
                }
            }
        } catch (Exception ignored) {
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

    public boolean fueRearetado(Ganado g) {
        if (g == null || g.getArete() == null) return false;
        return ganadoRepository.existsByAreteAnteriorArete(g.getArete());
    }

    public boolean validarDisponibilidadArete(String arete) {
        if (arete == null || arete.isBlank()) return false;
        String soloDigitos = arete.replaceAll("\\D", "");
        if (soloDigitos.length() != 9) return true;
        String form = soloDigitos.substring(0, 5) + " " + soloDigitos.substring(5);
        return !ganadoRepository.existeAreteEnBaseDatos(soloDigitos, form);
    }

    /**
     * Proceso de Re-aretado y Recuperación:
     * Para reses rechazadas en matadero y devueltas vivas a finca.
     * Crea un nuevo registro de Ganado con el nuevo arete y condición Apto,
     * enlazando el arete anterior mediante clave foránea para preservar trazabilidad.
     */
    @Transactional
    public Ganado rearetarGanado(String areteOriginal, String nuevoArete, BigDecimal nuevoPesoKg, Integer idLote) {
        if (areteOriginal == null || areteOriginal.isBlank()) {
            throw new IllegalArgumentException("Debe especificar el arete original a re-aretar.");
        }
        if (nuevoArete == null || nuevoArete.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar el nuevo número de arete oficial.");
        }
        if (nuevoPesoKg == null || nuevoPesoKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El nuevo peso en báscula debe ser mayor a cero.");
        }

        String soloDigitos = nuevoArete.replaceAll("\\D", "");
        if (soloDigitos.length() != 9) {
            throw new IllegalArgumentException("El nuevo arete debe contener exactamente 9 dígitos numéricos (Formato: XXXXX XXXX). No se permiten letras ni números negativos.");
        }

        String nuevoAreteFormateado = soloDigitos.substring(0, 5) + " " + soloDigitos.substring(5);
        String nuevoAreteSinEspacios = soloDigitos;
        String areteOrigLimpio = areteOriginal.trim().toUpperCase();

        if (nuevoAreteSinEspacios.equalsIgnoreCase(areteOrigLimpio.replaceAll("\\D", ""))) {
            throw new IllegalArgumentException("El nuevo arete no puede ser idéntico al arete original.");
        }

        // 1. Validar que el nuevo arete sea único
        if (ganadoRepository.existeAreteEnBaseDatos(nuevoAreteSinEspacios, nuevoAreteFormateado)) {
            throw new IllegalArgumentException("El nuevo arete '" + nuevoAreteFormateado + "' ya existe registrado en el sistema. Ingrese un arete único.");
        }

        // 2. Buscar la res original
        Ganado resOriginal = ganadoRepository.findById(areteOrigLimpio)
                .orElseGet(() -> {
                    List<Ganado> flex = ganadoRepository.buscarPorAreteFlexible(areteOrigLimpio.replaceAll("\\s+", ""), areteOrigLimpio);
                    if (!flex.isEmpty()) return flex.get(0);
                    throw new IllegalArgumentException("No se encontró la res con arete original: " + areteOrigLimpio);
                });

        // 3. Crear la nueva res con el nuevo arete formateado XXXXX XXXX
        Ganado nuevaRes = new Ganado();
        nuevaRes.setArete(nuevoAreteFormateado);
        nuevaRes.setAreteAnterior(resOriginal); // FK a la res original
        nuevaRes.setPesoKg(nuevoPesoKg);
        nuevaRes.setColor(resOriginal.getColor() != null ? resOriginal.getColor() : "No especificado");
        nuevaRes.setPaletas(resOriginal.getPaletas() != null ? resOriginal.getPaletas() : "2 Paletas");
        nuevaRes.setCondicionCompra("Bueno"); // Vuelve a condición Apto
        nuevaRes.setPrecioCompra(resOriginal.getPrecioCompra());

        if (idLote != null && idLote > 0) {
            loteRepository.findById(idLote).ifPresent(nuevaRes::setLote);
        }
        if (nuevaRes.getLote() == null) {
            nuevaRes.setLote(resOriginal.getLote());
        }

        nuevaRes.setEstado("En Finca");
        nuevaRes.setCategoria("Apto");
        nuevaRes.setFechaIngreso(java.time.LocalDateTime.now());

        Ganado guardada = ganadoRepository.save(nuevaRes);

        // 4. Actualizar estado de la res original a histórico
        resOriginal.setEstado("Re-aretado (Histórico)");
        resOriginal.setCategoria("Re-aretado");
        ganadoRepository.save(resOriginal);

        return guardada;
    }

    /**
     * Aparato de Trazabilidad e Historial del Bovino:
     * Reconstruye la cronología completa de un animal, incluyendo sus antecedentes genealógicos
     * de aretes anteriores, pesajes, lotes, viajes, mataderos a los que fue enviado y dictámenes de rechazo.
     */
    public HistorialBovinoDTO obtenerHistorialBovino(String arete) {
        if (arete == null || arete.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar un arete para consultar su historial.");
        }
        String areteLimpio = arete.trim().toUpperCase();
        String areteSinEspacios = areteLimpio.replaceAll("\\s+", "");

        Ganado res = ganadoRepository.findById(areteLimpio)
                .orElseGet(() -> {
                    List<Ganado> flex = ganadoRepository.buscarPorAreteFlexible(areteSinEspacios, areteLimpio);
                    if (!flex.isEmpty()) return flex.get(0);
                    throw new IllegalArgumentException("Bovino no encontrado con arete: " + areteLimpio);
                });

        HistorialBovinoDTO dto = new HistorialBovinoDTO();
        dto.setArete(res.getArete());
        dto.setAreteFormateado(res.getAreteFormateado());
        dto.setColor(res.getColor() != null ? res.getColor() : "No especificado");
        dto.setPaletas(res.getPaletas() != null ? res.getPaletas() : "2 Paletas");
        dto.setPesoActualKg(res.getPesoKg() != null ? res.getPesoKg() : BigDecimal.ZERO);
        dto.setCondicion(res.getCondicionCompra() != null ? res.getCondicionCompra() : "Bueno");
        dto.setCategoria(res.getCategoria() != null ? res.getCategoria() : "Apto");
        dto.setFechaIngreso(res.getFechaIngreso());

        if (res.getLote() != null) {
            dto.setIdLote(res.getLote().getIdLote());
            dto.setFechaCompra(res.getLote().getFechaCompra());
            if (res.getLote().getProveedor() != null) {
                dto.setProveedorNombre(res.getLote().getProveedor().getNombre());
            }
        }
        dto.setPrecioCompraKg(res.getPrecioPorKilo());
        dto.setTotalCompra(res.getSubtotalCompra());

        // Cadena de trazabilidad (ancestros y descendientes)
        List<String> cadena = new ArrayList<>();
        Set<String> todosAretesRelacionados = new LinkedHashSet<>();

        // Rastrear ancestros hacia atrás
        Ganado cursor = res.getAreteAnterior();
        List<String> ancestros = new ArrayList<>();
        while (cursor != null) {
            String form = cursor.getAreteFormateado() != null ? cursor.getAreteFormateado() : cursor.getArete();
            ancestros.add(0, form + " (Arete Original)");
            todosAretesRelacionados.add(cursor.getArete());
            cursor = cursor.getAreteAnterior();
        }
        cadena.addAll(ancestros);

        // Arete actual
        String actualForm = res.getAreteFormateado() != null ? res.getAreteFormateado() : res.getArete();
        cadena.add(actualForm + " (Consultado)");
        todosAretesRelacionados.add(res.getArete());

        // Rastrear si este animal fue posteriormente re-aretado hacia adelante
        Optional<Ganado> hijoOpt = ganadoRepository.findFirstByAreteAnteriorArete(res.getArete());
        if (hijoOpt.isPresent()) {
            Ganado hijo = hijoOpt.get();
            dto.setFueRearetado(true);
            dto.setNuevoArete(hijo.getArete());
            dto.setNuevoAreteFormateado(hijo.getAreteFormateado());
            String hijoForm = hijo.getAreteFormateado() != null ? hijo.getAreteFormateado() : hijo.getArete();
            cadena.add(hijoForm + " (Re-aretado Posterior Activo)");
            todosAretesRelacionados.add(hijo.getArete());
        }

        if (res.getAreteAnterior() != null) {
            dto.setTieneAreteAnterior(true);
            dto.setAreteAnterior(res.getAreteAnterior().getArete());
            dto.setAreteAnteriorFormateado(res.getAreteAnterior().getAreteFormateado());
        }

        dto.setCadenaTrazabilidad(cadena);

        // Estado calculado para visualización
        if (dto.isFueRearetado()) {
            dto.setEstado("Re-aretado (Histórico)");
        } else if (ganadoRepository.findGanadoVendidosAceptados().contains(res)) {
            dto.setEstado("Vendido en Planta Matadero");
        } else if (ganadoRepository.findGanadoRechazosMatadero().contains(res)) {
            dto.setEstado("Rechazo en Matadero (En Finca)");
        } else {
            dto.setEstado("En Finca (Apto)");
        }

        // Consultar despachos a matadero de todos los aretes de la cadena
        List<DetalleEntrega> detalles = detalleEntregaRepository.findHistorialPorAretes(new ArrayList<>(todosAretesRelacionados));
        List<HistorialEntregaDTO> eventos = new ArrayList<>();

        for (DetalleEntrega det : detalles) {
            HistorialEntregaDTO evt = new HistorialEntregaDTO();
            if (det.getEntrega() != null) {
                evt.setIdEntrega(det.getEntrega().getIdEntrega());
                evt.setFechaEntrega(det.getEntrega().getFechaEntrega());
                if (det.getEntrega().getMatadero() != null) {
                    Matadero m = det.getEntrega().getMatadero();
                    evt.setNombreMatadero(m.getNombreMatadero());
                    if (m.getUbicaciones() != null && !m.getUbicaciones().isEmpty() && m.getUbicaciones().get(0) != null) {
                        evt.setUbicacionMatadero(m.getUbicaciones().get(0).getNombreLugar());
                    } else {
                        evt.setUbicacionMatadero("Instalaciones de Planta");
                    }
                }
            }
            evt.setAreteUsado(det.getAreteDisplay());
            evt.setPesoFincaKg(det.getGanado() != null ? det.getGanado().getPesoKg() : null);
            evt.setPesoMataderoKg(det.getPesoMataderoKg());
            evt.setKilosMerma(det.getKilosMerma());
            evt.setPorcentajeMerma(det.getPorcentajeMerma());
            evt.setDictamen(det.getEstadoDictamen() != null ? det.getEstadoDictamen() : "Apto");
            evt.setPrecioRechazo(det.getPrecioRechazo());
            evt.setMotivoRechazo(det.getPrimerMotivoRechazo());
            evt.setEsRechazo(det.isRechazado());
            evt.setEsDevuelto(det.isRechazadoDevuelto());
            evt.setEsComprado(det.isRechazadoComprado());
            evt.setEsApto(det.isApto());

            if (evt.isEsRechazo()) {
                dto.setTieneHistorialRechazo(true);
                if (dto.getUltimoMotivoRechazo() == null && evt.getMotivoRechazo() != null && !evt.getMotivoRechazo().isBlank()) {
                    dto.setUltimoMotivoRechazo(evt.getMotivoRechazo());
                }
                if (dto.getUltimoMataderoRechazo() == null && evt.getNombreMatadero() != null) {
                    dto.setUltimoMataderoRechazo(evt.getNombreMatadero());
                }
            }

            eventos.add(evt);
        }

        dto.setEntregas(eventos);
        return dto;
    }
}
