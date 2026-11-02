package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.GanadoRechazo;
import com.ganaderia.rochaaltamirano.service.GanadoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador Web para el Módulo de Inventario de Ganado y Ganado de Rechazo
 */
@Controller
@RequestMapping("/ganado")
public class GanadoController {

    private final GanadoService ganadoService;

    public GanadoController(GanadoService ganadoService) {
        this.ganadoService = ganadoService;
    }

    @GetMapping
    public String listarGanado(@RequestParam(name = "estado", required = false) String estado,
                               @RequestParam(name = "categoria", required = false) String categoria,
                               Model model) {
        List<Ganado> inventario;

        if (estado != null && !estado.isBlank()) {
            inventario = ganadoService.listarPorEstado(estado);
            model.addAttribute("filtroActivo", "Estado: " + estado);
        } else if (categoria != null && !categoria.isBlank()) {
            inventario = ganadoService.listarPorCategoria(categoria);
            model.addAttribute("filtroActivo", "Categoría: " + categoria);
        } else {
            inventario = ganadoService.listarInventarioCompleto();
            model.addAttribute("filtroActivo", "Todos los registros");
        }

        model.addAttribute("ganados", inventario);
        model.addAttribute("totalCabezas", inventario.size());
        model.addAttribute("totalEnFinca", ganadoService.contarCabezasEnFinca());
        model.addAttribute("totalRechazos", ganadoService.contarRechazos());
        model.addAttribute("kilosTotalesFinca", ganadoService.calcularTotalKilosEnFinca());

        return "ganado/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevoGanado(Model model) {
        model.addAttribute("ganado", new Ganado());
        return "ganado/formulario";
    }

    @PostMapping("/guardar")
    public String guardarGanado(@ModelAttribute("ganado") Ganado ganado, RedirectAttributes redirectAttributes) {
        ganadoService.registrarGanado(ganado);
        redirectAttributes.addFlashAttribute("mensajeExito", "Animal con arete " + ganado.getArete() + " registrado exitosamente en el inventario.");
        return "redirect:/ganado";
    }

    @GetMapping("/rechazos")
    public String listarGanadoRechazo(Model model) {
        List<GanadoRechazo> rechazos = ganadoService.listarRechazos();
        model.addAttribute("rechazos", rechazos);
        model.addAttribute("totalRechazos", rechazos.size());
        return "ganado/detalle-rechazo";
    }

    @GetMapping("/nuevo-rechazo")
    public String formularioNuevoRechazo(Model model) {
        model.addAttribute("ganadoRechazo", new GanadoRechazo());
        return "ganado/formulario-rechazo";
    }

    @PostMapping("/guardar-rechazo")
    public String guardarRechazo(@ModelAttribute("ganadoRechazo") GanadoRechazo rechazo, RedirectAttributes redirectAttributes) {
        ganadoService.registrarRechazo(rechazo);
        redirectAttributes.addFlashAttribute("mensajeExito", "Registro de ganado de rechazo " + rechazo.getArete() + " guardado con su dictamen.");
        return "redirect:/ganado/rechazos";
    }

    @PostMapping("/eliminar/{arete}")
    public String eliminarGanado(@PathVariable("arete") String arete, RedirectAttributes redirectAttributes) {
        ganadoService.eliminarGanado(arete);
        redirectAttributes.addFlashAttribute("mensajeExito", "Arete " + arete + " retirado del inventario.");
        return "redirect:/ganado";
    }
}
