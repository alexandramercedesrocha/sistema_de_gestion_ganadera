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
                                 @RequestParam(name = "cedulaConductor", required = false) String cedulaConductor,
                                 @RequestParam(name = "numPlaca", required = false) String numPlaca,
                                 @RequestParam(name = "idUbicacionOrigen", required = false) Integer idUbicacionOrigen,
                                 @RequestParam(name = "idUbicacionDestino", required = false) Integer idUbicacionDestino,
                                 @RequestParam(name = "costoFlete", required = false) BigDecimal costoFlete,
                                 @RequestParam(name = "estadoPagoViaje", required = false, defaultValue = "Pendiente") String estadoPagoViaje,
                                 @RequestParam(name = "aretesSeleccionados", required = false) List<String> aretesSeleccionados,
                                 @RequestParam java.util.Map<String, String> allParams,
                                 RedirectAttributes redirectAttributes) {
        // El precio de rechazo general se elimina, ahora se asigna de forma individual opcional por res
        entrega.setPrecioRechazo(null);

        Entrega guardada = entregaService.registrarEntregaConViaje(
                entrega, idMatadero, cedulaConductor, numPlaca,
                idUbicacionOrigen, idUbicacionDestino, costoFlete, estadoPagoViaje);

        if (aretesSeleccionados != null && !aretesSeleccionados.isEmpty()) {
            java.util.Map<String, BigDecimal> preciosRechazoPorArete = new java.util.HashMap<>();
            for (String arete : aretesSeleccionados) {
                if (arete == null || arete.isBlank()) continue;
                String areteLimpio = arete.trim().toUpperCase();
                String keyDirect = "precioRechazo_" + arete;
                String keyUnderscore = "precioRechazo_" + arete.replace(" ", "_");
                String val = allParams.get(keyDirect);
                if (val == null || val.isBlank()) {
                    val = allParams.get(keyUnderscore);
                }
                if (val != null && !val.isBlank()) {
                    try {
                        BigDecimal pRech = new BigDecimal(val.trim());
                        if (pRech.compareTo(BigDecimal.ZERO) >= 0) {
                            preciosRechazoPorArete.put(areteLimpio, pRech);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            entregaService.asociarGanadoAEntrega(guardada.getIdEntrega(), aretesSeleccionados, preciosRechazoPorArete);
            redirectAttributes.addFlashAttribute("mensajeExito", "Entrega #" + guardada.getIdEntrega() + " creada con " + aretesSeleccionados.size() + " reses cargadas al despacho y viaje logístico asignado.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito", "Entrega #" + guardada.getIdEntrega() + " creada con viaje logístico asignado exitosamente.");
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
                                     RedirectAttributes redirectAttributes) {
        if (aretesSeleccionados == null || aretesSeleccionados.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Debe marcar al menos una res para cargar a la entrega.");
            return "redirect:/entregas/" + id;
        }
        entregaService.asociarGanadoAEntrega(id, aretesSeleccionados);
        redirectAttributes.addFlashAttribute("mensajeExito", aretesSeleccionados.size() + " reses cargadas exitosamente a la entrega #" + id);
        return "redirect:/entregas/" + id;
    }

    @PostMapping("/{id}/actualizar-pesaje-res")
    public String actualizarPesajeRes(@PathVariable("id") Integer id,
                                      @RequestParam("idDetalle") Integer idDetalle,
                                      @RequestParam(name = "pesoMataderoKg", required = false) BigDecimal pesoMataderoKg,
                                      @RequestParam(name = "estadoDictamen", defaultValue = "Apto") String estadoDictamen,
                                      @RequestParam(name = "motivoRechazo", required = false) String motivoRechazo,
                                      RedirectAttributes redirectAttributes) {
        entregaService.actualizarPesajeYDictamen(idDetalle, pesoMataderoKg, estadoDictamen, motivoRechazo);
        redirectAttributes.addFlashAttribute("mensajeExito", "Pesaje y dictamen oficial de matadero actualizados.");
        return "redirect:/entregas/" + id;
    }

    @PostMapping("/{id}/agregar-detalle")
    public String agregarDetallePesaje(@PathVariable("id") Integer id,
                                       @RequestParam("arete") String arete,
                                       @RequestParam("pesoMataderoKg") BigDecimal pesoMataderoKg,
                                       @RequestParam(name = "estadoDictamen", defaultValue = "Apto") String estadoDictamen,
                                       @RequestParam(name = "motivoRechazo", required = false) String motivoRechazo,
                                       RedirectAttributes redirectAttributes) {
        entregaService.registrarDetallePesaje(id, arete, pesoMataderoKg, estadoDictamen, motivoRechazo);
        redirectAttributes.addFlashAttribute("mensajeExito", "Pesaje y dictamen de res " + arete + " registrado.");
        return "redirect:/entregas/" + id;
    }
}
