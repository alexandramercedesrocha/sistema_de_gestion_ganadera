package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.dto.BalanceSociedadDTO;
import com.ganaderia.rochaaltamirano.model.ArqueoMensual;
import com.ganaderia.rochaaltamirano.model.Entrega;
import com.ganaderia.rochaaltamirano.model.Gasto;
import com.ganaderia.rochaaltamirano.model.Liquidacion;
import com.ganaderia.rochaaltamirano.service.CatalogoService;
import com.ganaderia.rochaaltamirano.service.EntregaService;
import com.ganaderia.rochaaltamirano.service.FinanzasService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Controlador Web para el Módulo Financiero, Liquidaciones de Mataderos
 * y Balance 50-50 de la Sociedad Wilber Rocha & Yader Altamirano
 */
@Controller
@RequestMapping("/finanzas")
public class FinanzasController {

    private final FinanzasService finanzasService;
    private final EntregaService entregaService;
    private final CatalogoService catalogoService;

    public FinanzasController(FinanzasService finanzasService,
                              EntregaService entregaService,
                              CatalogoService catalogoService) {
        this.finanzasService = finanzasService;
        this.entregaService = entregaService;
        this.catalogoService = catalogoService;
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
    public String listarLiquidaciones(@RequestParam(name = "verTodas", defaultValue = "false") boolean verTodas, Model model) {
        List<Liquidacion> liquidaciones = finanzasService.listarLiquidaciones();
        Set<Integer> idsLiquidadas = finanzasService.obtenerIdsEntregasLiquidadas();
        List<Entrega> todasEntregas = entregaService.listarTodas();

        List<Entrega> entregasPendientes = todasEntregas.stream()
                .filter(e -> !idsLiquidadas.contains(e.getIdEntrega()))
                .toList();

        model.addAttribute("liquidaciones", liquidaciones);
        model.addAttribute("totalLiquidado", finanzasService.calcularTotalIngresosLiquidaciones());
        model.addAttribute("idsEntregasLiquidadas", idsLiquidadas);
        model.addAttribute("entregasPendientes", entregasPendientes);
        model.addAttribute("todasEntregas", todasEntregas);
        model.addAttribute("entregas", verTodas ? todasEntregas : entregasPendientes);
        model.addAttribute("verTodas", verTodas);
        model.addAttribute("totalPendientes", entregasPendientes.size());
        model.addAttribute("totalEntregas", todasEntregas.size());
        model.addAttribute("mataderos", catalogoService.listarMataderos());

        Liquidacion nuevaLiq = new Liquidacion();
        nuevaLiq.setFechaLiquidacion(LocalDate.now());
        model.addAttribute("nuevaLiquidacion", nuevaLiq);

        return "finanzas/liquidaciones";
    }

    @PostMapping("/liquidaciones/guardar")
    public String guardarLiquidacion(@ModelAttribute("nuevaLiquidacion") Liquidacion liquidacion,
                                     @RequestParam(name = "idEntrega", required = false) Integer idEntrega,
                                     @RequestParam(name = "idMatadero", required = false) Integer idMatadero,
                                     RedirectAttributes redirectAttributes) {
        String comprobante = liquidacion.getIdNumComprobante() != null ? liquidacion.getIdNumComprobante().trim() : "";
        if (!comprobante.matches("^[0-9]{9,15}$")) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "El número de comprobante debe contener únicamente entre 9 y 15 dígitos numéricos (sin letras, texto extra, guiones ni números negativos).");
            return "redirect:/finanzas/liquidaciones";
        }
        liquidacion.setIdNumComprobante(comprobante);

        if (liquidacion.getMonto() == null || liquidacion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El monto de la liquidación debe ser un número positivo mayor que cero.");
            return "redirect:/finanzas/liquidaciones";
        }

        if (idEntrega != null) {
            if (finanzasService.existeLiquidacionParaEntrega(idEntrega)) {
                redirectAttributes.addFlashAttribute("mensajeError",
                    "Error: La Entrega #" + idEntrega + " ya ha sido liquidada previamente y cuenta con un comprobante registrado.");
                return "redirect:/finanzas/liquidaciones";
            }
            entregaService.buscarPorId(idEntrega).ifPresent(e -> {
                liquidacion.setEntrega(e);
                if (e.getMatadero() != null) {
                    liquidacion.setMatadero(e.getMatadero());
                }
            });
        }
        if (idMatadero != null && liquidacion.getMatadero() == null) {
            catalogoService.buscarMataderoPorId(idMatadero).ifPresent(liquidacion::setMatadero);
        }

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
        if (gasto.getMonto() == null || gasto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El monto del gasto debe ser un número positivo mayor que cero.");
            return "redirect:/finanzas/gastos";
        }
        finanzasService.registrarGasto(gasto);
        redirectAttributes.addFlashAttribute("mensajeExito", "Gasto registrado en el sistema contable.");
        return "redirect:/finanzas/gastos";
    }

    @GetMapping("/arqueos")
    public String listarArqueos(Model model) {
        model.addAttribute("arqueos", finanzasService.listarArqueos());
        
        ArqueoMensual nuevoArqueo = new ArqueoMensual();
        nuevoArqueo.setFechaCierre(LocalDate.now());
        model.addAttribute("nuevoArqueo", nuevoArqueo);
        
        return "finanzas/arqueos";
    }

    @PostMapping("/arqueos/guardar")
    public String guardarArqueo(@ModelAttribute("nuevoArqueo") ArqueoMensual arqueo, RedirectAttributes redirectAttributes) {
        finanzasService.registrarArqueo(arqueo);
        redirectAttributes.addFlashAttribute("mensajeExito", "Arqueo mensual del periodo cerrado exitosamente.");
        return "redirect:/finanzas/arqueos";
    }
}
