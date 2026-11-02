package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.dto.BalanceSociedadDTO;
import com.ganaderia.rochaaltamirano.model.ArqueoMensual;
import com.ganaderia.rochaaltamirano.model.Gasto;
import com.ganaderia.rochaaltamirano.model.Liquidacion;
import com.ganaderia.rochaaltamirano.service.FinanzasService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * Controlador Web para el Módulo Financiero, Liquidaciones de Mataderos
 * y Balance 50-50 de la Sociedad Wilber Rocha & Yader Altamirano
 */
@Controller
@RequestMapping("/finanzas")
public class FinanzasController {

    private final FinanzasService finanzasService;

    public FinanzasController(FinanzasService finanzasService) {
        this.finanzasService = finanzasService;
    }

    @GetMapping("/balance")
    public String verBalanceSociedad(Model model) {
        BalanceSociedadDTO balance = finanzasService.generarBalanceConsolidado();
        model.addAttribute("balance", balance);
        model.addAttribute("socio1", "Wilber Rocha");
        model.addAttribute("socio2", "Yader Altamirano");
        return "finanzas/balance";
    }

    @GetMapping("/liquidaciones")
    public String listarLiquidaciones(Model model) {
        model.addAttribute("liquidaciones", finanzasService.listarLiquidaciones());
        model.addAttribute("totalLiquidado", finanzasService.calcularTotalIngresosLiquidaciones());
        
        Liquidacion nuevaLiq = new Liquidacion();
        nuevaLiq.setFechaLiquidacion(LocalDate.now());
        model.addAttribute("nuevaLiquidacion", nuevaLiq);
        
        return "finanzas/liquidaciones";
    }

    @PostMapping("/liquidaciones/guardar")
    public String guardarLiquidacion(@ModelAttribute("nuevaLiquidacion") Liquidacion liquidacion, RedirectAttributes redirectAttributes) {
        finanzasService.registrarLiquidacion(liquidacion);
        redirectAttributes.addFlashAttribute("mensajeExito", "Liquidación " + liquidacion.getIdNumComprobante() + " guardada exitosamente.");
        return "redirect:/finanzas/liquidaciones";
    }

    @GetMapping("/gastos")
    public String listarGastos(Model model) {
        model.addAttribute("gastos", finanzasService.listarGastos());
        model.addAttribute("totalGastos", finanzasService.calcularTotalGastosOperativos());
        
        Gasto nuevoGasto = new Gasto();
        nuevoGasto.setFechaGasto(LocalDate.now());
        model.addAttribute("nuevoGasto", nuevoGasto);
        
        return "finanzas/gastos";
    }

    @PostMapping("/gastos/guardar")
    public String guardarGasto(@ModelAttribute("nuevoGasto") Gasto gasto, RedirectAttributes redirectAttributes) {
        finanzasService.registrarGasto(gasto);
        redirectAttributes.addFlashAttribute("mensajeExito", "Gasto operativo registrado exitosamente.");
        return "redirect:/finanzas/gastos";
    }

    @GetMapping("/arqueos")
    public String listarArqueos(Model model) {
        model.addAttribute("arqueos", finanzasService.listarArqueos());
        
        ArqueoMensual nuevoArqueo = new ArqueoMensual();
        nuevoArqueo.setFecha(LocalDate.now());
        model.addAttribute("nuevoArqueo", nuevoArqueo);
        
        return "finanzas/arqueos";
    }

    @PostMapping("/arqueos/guardar")
    public String guardarArqueo(@ModelAttribute("nuevoArqueo") ArqueoMensual arqueo, RedirectAttributes redirectAttributes) {
        finanzasService.registrarArqueo(arqueo);
        redirectAttributes.addFlashAttribute("mensajeExito", "Cierre contable mensual registrado exitosamente.");
        return "redirect:/finanzas/arqueos";
    }
}
