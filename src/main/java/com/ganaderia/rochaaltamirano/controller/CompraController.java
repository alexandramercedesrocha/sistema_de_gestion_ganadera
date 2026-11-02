package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.model.Compra;
import com.ganaderia.rochaaltamirano.service.CompraService;
import com.ganaderia.rochaaltamirano.service.ProveedorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * Controlador Web para el Módulo de Compras de Ganado Bovino
 */
@Controller
@RequestMapping("/compras")
public class CompraController {

    private final CompraService compraService;
    private final ProveedorService proveedorService;

    public CompraController(CompraService compraService, ProveedorService proveedorService) {
        this.compraService = compraService;
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public String listarCompras(Model model) {
        model.addAttribute("compras", compraService.listarCompras());
        model.addAttribute("totalInvertido", compraService.calcularTotalInvertido());
        model.addAttribute("totalCabezasCompradas", compraService.calcularTotalCabezasCompradas());
        return "compras/lista";
    }

    @GetMapping("/nueva")
    public String formularioNuevaCompra(Model model) {
        Compra nuevaCompra = new Compra();
        nuevaCompra.setFechaCompra(LocalDate.now());
        model.addAttribute("compra", nuevaCompra);
        model.addAttribute("proveedores", proveedorService.listarTodos());
        return "compras/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCompra(@ModelAttribute("compra") Compra compra, RedirectAttributes redirectAttributes) {
        compraService.registrarCompra(compra);
        redirectAttributes.addFlashAttribute("mensajeExito", "Compra registrada exitosamente por un total de $" + compra.getMontoTotal());
        return "redirect:/compras";
    }
}
