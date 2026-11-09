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
        model.addAttribute("viajes", viajeService.listarTodos());
        model.addAttribute("ganadosDisponibles", ganadoService.listarGanadoEnFincaSinDespacho());
        return "entregas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarEntrega(@ModelAttribute("entrega") Entrega entrega,
                                 @RequestParam(name = "idMatadero", required = false) Integer idMatadero,
                                 @RequestParam(name = "idViaje", required = false) Integer idViaje,
                                 @RequestParam(name = "aretesSeleccionados", required = false) List<String> aretesSeleccionados,
                                 RedirectAttributes redirectAttributes) {
        Entrega guardada = entregaService.registrarEntrega(entrega, idMatadero, idViaje);
        if (aretesSeleccionados != null && !aretesSeleccionados.isEmpty()) {
            entregaService.asociarGanadoAEntrega(guardada.getIdEntrega(), aretesSeleccionados);
            redirectAttributes.addFlashAttribute("mensajeExito", "Entrega #" + guardada.getIdEntrega() + " creada con " + aretesSeleccionados.size() + " reses cargadas al despacho exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito", "Entrega #" + guardada.getIdEntrega() + " creada exitosamente.");
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
