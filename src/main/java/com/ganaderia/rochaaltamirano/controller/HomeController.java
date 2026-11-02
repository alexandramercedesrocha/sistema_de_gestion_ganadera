package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.dto.BalanceSociedadDTO;
import com.ganaderia.rochaaltamirano.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador Principal del Sistema Ganadero Rocha & Altamirano
 * Presenta el Dashboard de control general con KPIs ejecutivos.
 */
@Controller
public class HomeController {

    private final GanadoService ganadoService;
    private final LoteService loteService;
    private final ViajeService viajeService;
    private final EntregaService entregaService;
    private final FinanzasService finanzasService;

    public HomeController(GanadoService ganadoService,
                          LoteService loteService,
                          ViajeService viajeService,
                          EntregaService entregaService,
                          FinanzasService finanzasService) {
        this.ganadoService = ganadoService;
        this.loteService = loteService;
        this.viajeService = viajeService;
        this.entregaService = entregaService;
        this.finanzasService = finanzasService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        BalanceSociedadDTO balance = finanzasService.generarBalanceConsolidado();
        
        model.addAttribute("balance", balance);
        model.addAttribute("totalGanado", ganadoService.listarInventarioCompleto().size());
        model.addAttribute("ganadoEnFinca", ganadoService.contarCabezasEnFinca());
        model.addAttribute("ganadoEntregado", entregaService.contarTotalCabezasEntregadas());
        model.addAttribute("ganadoRechazo", entregaService.contarTotalRechazosMatadero());
        
        model.addAttribute("ultimosLotes", loteService.listarLotes());
        model.addAttribute("ultimosViajes", viajeService.listarTodos());
        model.addAttribute("ultimasLiquidaciones", finanzasService.listarLiquidaciones());
        model.addAttribute("ultimasEntregas", entregaService.listarTodas());

        return "index";
    }
}
