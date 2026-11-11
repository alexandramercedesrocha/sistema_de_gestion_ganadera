package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.model.Viaje;
import com.ganaderia.rochaaltamirano.service.CatalogoService;
import com.ganaderia.rochaaltamirano.service.ViajeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador Web para el Módulo de Transporte, Viajes y Fletes Ganaderos
 */
@Controller
@RequestMapping("/transporte")
public class TransporteController {

    private final ViajeService viajeService;
    private final CatalogoService catalogoService;
    private final com.ganaderia.rochaaltamirano.service.EntregaService entregaService;

    public TransporteController(ViajeService viajeService,
                                CatalogoService catalogoService,
                                com.ganaderia.rochaaltamirano.service.EntregaService entregaService) {
        this.viajeService = viajeService;
        this.catalogoService = catalogoService;
        this.entregaService = entregaService;
    }

    @GetMapping
    public String listarTransportes(Model model) {
        List<Viaje> viajes = viajeService.listarTodos();
        model.addAttribute("viajes", viajes);
        model.addAttribute("totalViajes", viajes.size());
        model.addAttribute("totalPendiente", viajeService.contarViajesPendientes());
        return "transporte/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevoViaje(Model model) {
        Viaje nuevoViaje = new Viaje();
        nuevoViaje.setEstadoPago("Pendiente");
        model.addAttribute("viaje", nuevoViaje);
        model.addAttribute("camiones", catalogoService.listarCamiones());
        model.addAttribute("conductores", catalogoService.listarConductores());
        model.addAttribute("ubicaciones", catalogoService.listarUbicaciones());
        model.addAttribute("entregas", entregaService.listarTodas());
        return "transporte/formulario";
    }

    @PostMapping("/guardar")
    public String guardarTransporte(@ModelAttribute("viaje") Viaje viaje,
                                    @RequestParam(name = "idOrigen", required = false) Integer idOrigen,
                                    @RequestParam(name = "idDestino", required = false) Integer idDestino,
                                    @RequestParam(name = "cedulaConductor", required = false) String cedulaConductor,
                                    @RequestParam(name = "numPlaca", required = false) String numPlaca,
                                    @RequestParam(name = "idEntrega", required = false) Integer idEntrega,
                                    RedirectAttributes redirectAttributes) {
        viajeService.registrarViaje(viaje, idOrigen, idDestino, cedulaConductor, numPlaca, idEntrega);
        redirectAttributes.addFlashAttribute("mensajeExito", "Viaje de transporte registrado correctamente.");
        return "redirect:/transporte";
    }

    @PostMapping("/{id}/pagar")
    public String marcarComoPagado(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        boolean actualizado = viajeService.marcarComoPagado(id);
        if (actualizado) {
            redirectAttributes.addFlashAttribute("mensajeExito", "Flete #" + id + " marcado como PAGADO.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No se encontró el viaje #" + id);
        }
        return "redirect:/transporte";
    }
}
