package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.model.DetalleEntrega;
import com.ganaderia.rochaaltamirano.model.Entrega;
import com.ganaderia.rochaaltamirano.service.CatalogoService;
import com.ganaderia.rochaaltamirano.service.EntregaService;
import com.ganaderia.rochaaltamirano.service.GanadoService;
import com.ganaderia.rochaaltamirano.service.ViajeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador Web para el Módulo de Entregas y Despachos a Matadero
 */
@Controller
@RequestMapping("/entregas")
public class EntregaController {

    private final EntregaService entregaService;
    private final CatalogoService catalogoService;
    private final GanadoService ganadoService;
    private final ViajeService viajeService;

    public EntregaController(EntregaService entregaService,
                             CatalogoService catalogoService,
                             GanadoService ganadoService,
                             ViajeService viajeService) {
        this.entregaService = entregaService;
        this.catalogoService = catalogoService;
        this.ganadoService = ganadoService;
        this.viajeService = viajeService;
    }

    @GetMapping
    public String listarEntregas(Model model) {
        List<Entrega> entregas = entregaService.listarTodas();
        model.addAttribute("entregas", entregas);
        model.addAttribute("totalCabezas", entregaService.contarTotalCabezasEntregadas());
        model.addAttribute("totalKilos", entregaService.calcularKilosTotalesEntregados());
        model.addAttribute("totalRechazos", entregaService.contarTotalRechazosMatadero());
        return "entregas/lista";
    }

    @GetMapping("/nueva")
    public String formularioNuevaEntrega(Model model) {
        Entrega entrega = new Entrega();
        entrega.setFechaEntrega(LocalDate.now());
        model.addAttribute("entrega", entrega);
        model.addAttribute("mataderos", catalogoService.listarMataderos());
        model.addAttribute("conductores", catalogoService.listarConductores());
        model.addAttribute("camiones", catalogoService.listarCamiones());
        model.addAttribute("ubicaciones", catalogoService.listarUbicaciones());
        model.addAttribute("ganadosDisponibles", ganadoService.listarGanadoEnFincaSinDespacho());
        return "entregas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarEntrega(@ModelAttribute("entrega") Entrega entrega,
                                 @RequestParam(name = "idMatadero", required = false) Integer idMatadero,
                                 @RequestParam(name = "cedulaConductor", required = false) List<String> cedulasConductor,
                                 @RequestParam(name = "numPlaca", required = false) List<String> numPlacas,
                                 @RequestParam(name = "idUbicacionOrigen", required = false) List<Integer> idsUbicacionOrigen,
                                 @RequestParam(name = "idUbicacionDestino", required = false) List<Integer> idsUbicacionDestino,
                                 @RequestParam(name = "costoFlete", required = false) List<BigDecimal> costosFlete,
                                 @RequestParam(name = "estadoPagoViaje", required = false) List<String> estadosPagoViaje,
                                 @RequestParam(name = "aretesSeleccionados", required = false) List<String> aretesSeleccionados,
                                 @RequestParam java.util.Map<String, String> allParams,
                                 RedirectAttributes redirectAttributes) {
        // El precio de rechazo general se elimina, ahora se asigna de forma individual opcional por res
        entrega.setPrecioRechazo(null);

        if (entrega.getPrecioAcordado() == null || entrega.getPrecioAcordado().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El precio acordado con el matadero debe ser un número positivo mayor que cero.");
            return "redirect:/entregas/nueva";
        }

        // Conformar la lista de viajes (Mínimo 1 viaje obligatorio)
        List<EntregaService.DatosViajeDto> listaViajes = new java.util.ArrayList<>();
        if (numPlacas != null && !numPlacas.isEmpty()) {
            for (int i = 0; i < numPlacas.size(); i++) {
                String placa = numPlacas.get(i);
                String cedula = (cedulasConductor != null && i < cedulasConductor.size()) ? cedulasConductor.get(i) : null;
                Integer idOrigen = (idsUbicacionOrigen != null && i < idsUbicacionOrigen.size()) ? idsUbicacionOrigen.get(i) : null;
                Integer idDestino = (idsUbicacionDestino != null && i < idsUbicacionDestino.size()) ? idsUbicacionDestino.get(i) : null;
                BigDecimal flete = (costosFlete != null && i < costosFlete.size()) ? costosFlete.get(i) : null;
                String estadoPago = (estadosPagoViaje != null && i < estadosPagoViaje.size()) ? estadosPagoViaje.get(i) : "Pendiente";

                if (flete != null && flete.compareTo(BigDecimal.ZERO) < 0) {
                    redirectAttributes.addFlashAttribute("mensajeError", "El costo del flete no puede ser un número negativo.");
                    return "redirect:/entregas/nueva";
                }

                if ((placa != null && !placa.isBlank()) || (cedula != null && !cedula.isBlank())) {
                    listaViajes.add(new EntregaService.DatosViajeDto(cedula, placa, idOrigen, idDestino, flete, estadoPago));
                }
            }
        }

        if (listaViajes.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error: La entrega requiere al menos un viaje de transporte asociado (camión y chofer).");
            return "redirect:/entregas/nueva";
        }

        Entrega guardada = entregaService.registrarEntregaConViajes(entrega, idMatadero, listaViajes);

        int totalViajes = guardada.getViajes() != null ? guardada.getViajes().size() : listaViajes.size();
        String txtViajes = totalViajes == 1 ? "1 viaje logístico asignado" : totalViajes + " viajes logísticos asignados";

        if (aretesSeleccionados != null && !aretesSeleccionados.isEmpty()) {
            java.util.Map<String, BigDecimal> pesosSalidaPorArete = new java.util.HashMap<>();
            for (String arete : aretesSeleccionados) {
                if (arete == null || arete.isBlank()) continue;
                String areteLimpio = arete.trim().toUpperCase();

                // Lectura de nuevo peso de salida por incremento de engorde antes de entregarse
                String keyPesoDirect = "pesoSalida_" + arete;
                String keyPesoUnderscore = "pesoSalida_" + arete.replace(" ", "_");
                String valPeso = allParams.get(keyPesoDirect);
                if (valPeso == null || valPeso.isBlank()) {
                    valPeso = allParams.get(keyPesoUnderscore);
                }
                if (valPeso != null && !valPeso.isBlank()) {
                    try {
                        BigDecimal pSalida = new BigDecimal(valPeso.trim());
                        if (pSalida.compareTo(BigDecimal.ZERO) > 0) {
                            pesosSalidaPorArete.put(areteLimpio, pSalida);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            entregaService.asociarGanadoAEntrega(guardada.getIdEntrega(), aretesSeleccionados, null, pesosSalidaPorArete);
            redirectAttributes.addFlashAttribute("mensajeExito", "Entrega #" + guardada.getIdEntrega() + " creada con " + aretesSeleccionados.size() + " reses cargadas al despacho y " + txtViajes + ".");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito", "Entrega #" + guardada.getIdEntrega() + " creada con " + txtViajes + " exitosamente.");
        }
        return "redirect:/entregas/" + guardada.getIdEntrega();
    }

    @GetMapping("/{id}")
    public String verDetalleEntrega(@PathVariable("id") Integer id, Model model) {
        Entrega entrega = entregaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada: " + id));

        model.addAttribute("entrega", entrega);
        model.addAttribute("detalles", entregaService.listarDetallesPorEntrega(id));
        model.addAttribute("ganadosDisponibles", ganadoService.listarGanadoEnFincaSinDespacho());
        return "entregas/detalle";
    }

    @PostMapping("/{id}/cargar-ganado-masivo")
    public String cargarGanadoMasivo(@PathVariable("id") Integer id,
                                     @RequestParam(name = "aretesSeleccionados", required = false) List<String> aretesSeleccionados,
                                     @RequestParam java.util.Map<String, String> allParams,
                                     RedirectAttributes redirectAttributes) {
        if (aretesSeleccionados == null || aretesSeleccionados.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Debe marcar al menos una res para cargar a la entrega.");
            return "redirect:/entregas/" + id;
        }
        java.util.Map<String, BigDecimal> pesosSalida = new java.util.HashMap<>();
        for (String arete : aretesSeleccionados) {
            if (arete == null || arete.isBlank()) continue;
            String keyPeso = "pesoSalidaDetalle_" + arete.replace(" ", "_");
            String valPeso = allParams.get(keyPeso);
            if (valPeso != null && !valPeso.isBlank()) {
                try {
                    BigDecimal p = new BigDecimal(valPeso.trim());
                    if (p.compareTo(BigDecimal.ZERO) > 0) pesosSalida.put(arete.trim().toUpperCase(), p);
                } catch (Exception ignored) {}
            }
        }
        entregaService.asociarGanadoAEntrega(id, aretesSeleccionados, null, pesosSalida);
        redirectAttributes.addFlashAttribute("mensajeExito", aretesSeleccionados.size() + " reses cargadas exitosamente a la entrega #" + id);
        return "redirect:/entregas/" + id;
    }

    @PostMapping("/{id}/actualizar-pesaje-res")
    public String actualizarPesajeRes(@PathVariable("id") Integer id,
                                      @RequestParam("idDetalle") Integer idDetalle,
                                      @RequestParam(name = "pesoMataderoKg", required = false) BigDecimal pesoMataderoKg,
                                      @RequestParam(name = "pesoFincaKg", required = false) BigDecimal pesoFincaKg,
                                      @RequestParam(name = "estadoDictamen", defaultValue = "Apto") String estadoDictamen,
                                      @RequestParam(name = "motivoRechazo", required = false) String motivoRechazo,
                                      @RequestParam(name = "precioRechazo", required = false) BigDecimal precioRechazo,
                                      RedirectAttributes redirectAttributes) {
        if (pesoMataderoKg != null && pesoMataderoKg.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El peso de venta debe ser un número positivo mayor que cero.");
            return "redirect:/entregas/" + id;
        }
        if (pesoFincaKg != null && pesoFincaKg.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El peso en finca debe ser un número positivo mayor que cero.");
            return "redirect:/entregas/" + id;
        }
        if (precioRechazo != null && precioRechazo.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El precio de rechazo debe ser un número positivo mayor que cero.");
            return "redirect:/entregas/" + id;
        }
        entregaService.actualizarPesajeYDictamen(idDetalle, pesoMataderoKg, pesoFincaKg, estadoDictamen, motivoRechazo, precioRechazo);
        redirectAttributes.addFlashAttribute("mensajeExito", "Pesajes (finca y matadero) y dictamen oficial actualizados.");
        return "redirect:/entregas/" + id;
    }

    @PostMapping("/{id}/actualizar-precio-rechazo")
    public String actualizarPrecioRechazoEntrega(@PathVariable("id") Integer id,
                                                 @RequestParam("precioRechazo") BigDecimal precioRechazo,
                                                 RedirectAttributes redirectAttributes) {
        if (precioRechazo == null || precioRechazo.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El precio de rechazo debe ser mayor a cero.");
            return "redirect:/entregas/" + id;
        }
        Entrega entrega = entregaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada: " + id));
        entrega.setPrecioRechazo(precioRechazo);

        if (entrega.getDetalles() != null) {
            for (DetalleEntrega det : entrega.getDetalles()) {
                if (det.isRechazadoComprado() || "Rechazado".equalsIgnoreCase(det.getEstadoDictamen())) {
                    det.setPrecioRechazo(precioRechazo);
                }
            }
        }
        entregaService.guardarEntrega(entrega);
        redirectAttributes.addFlashAttribute("mensajeExito", "Precio de rechazo de la Entrega #" + id + " establecido en C$ " + precioRechazo + " /kg exitosamente.");
        return "redirect:/entregas/" + id;
    }

    @PostMapping("/{id}/agregar-detalle")
    public String agregarDetallePesaje(@PathVariable("id") Integer id,
                                       @RequestParam("arete") String arete,
                                       @RequestParam("pesoMataderoKg") BigDecimal pesoMataderoKg,
                                       @RequestParam(name = "estadoDictamen", defaultValue = "Apto") String estadoDictamen,
                                       @RequestParam(name = "motivoRechazo", required = false) String motivoRechazo,
                                       RedirectAttributes redirectAttributes) {
        if (pesoMataderoKg != null && pesoMataderoKg.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El peso en matadero/venta debe ser un número positivo mayor que cero.");
            return "redirect:/entregas/" + id;
        }
        entregaService.registrarDetallePesaje(id, arete, pesoMataderoKg, estadoDictamen, motivoRechazo);
        redirectAttributes.addFlashAttribute("mensajeExito", "Pesaje y dictamen de res " + arete + " registrado.");
        return "redirect:/entregas/" + id;
    }
}
