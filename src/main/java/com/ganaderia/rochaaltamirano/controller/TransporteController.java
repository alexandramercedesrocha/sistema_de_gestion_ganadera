package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.model.Transporte;
import com.ganaderia.rochaaltamirano.service.TransporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * Controlador Web para el Módulo de Transporte y Fletes Ganaderos
 */
@Controller
@RequestMapping("/transporte")
public class TransporteController {

    private final TransporteService transporteService;

    public TransporteController(TransporteService transporteService) {
        this.transporteService = transporteService;
    }

    @GetMapping
    public String listarTransportes(Model model) {
        model.addAttribute("viajes", transporteService.listarTodos());
        model.addAttribute("totalCostos", transporteService.calcularTotalCostosTransporte());
        model.addAttribute("totalPendiente", transporteService.calcularTotalPendientePago());
        return "transporte/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevoViaje(Model model) {
        Transporte nuevoViaje = new Transporte();
        nuevoViaje.setFechaViaje(LocalDate.now());
        nuevoViaje.setEstadoPago("Pendiente");
        model.addAttribute("transporte", nuevoViaje);
        return "transporte/formulario";
    }

    @PostMapping("/guardar")
    public String guardarTransporte(@ModelAttribute("transporte") Transporte transporte, RedirectAttributes redirectAttributes) {
        transporteService.registrarTransporte(transporte);
        redirectAttributes.addFlashAttribute("mensajeExito", "Viaje de transporte registrado correctamente.");
        return "redirect:/transporte";
    }

    @PostMapping("/{id}/pagar")
    public String marcarComoPagado(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        boolean actualizado = transporteService.marcarComoPagado(id);
        if (actualizado) {
            redirectAttributes.addFlashAttribute("mensajeExito", "Flete #" + id + " marcado como PAGADO.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No se encontró el viaje #" + id);
        }
        return "redirect:/transporte";
    }
}
