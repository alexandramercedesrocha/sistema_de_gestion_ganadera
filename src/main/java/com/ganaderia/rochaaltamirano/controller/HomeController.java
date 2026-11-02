package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.dto.BalanceSociedadDTO;
import com.ganaderia.rochaaltamirano.service.CompraService;
import com.ganaderia.rochaaltamirano.service.FinanzasService;
import com.ganaderia.rochaaltamirano.service.GanadoService;
import com.ganaderia.rochaaltamirano.service.TransporteService;
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
    private final CompraService compraService;
    private final TransporteService transporteService;
    private final FinanzasService finanzasService;

    public HomeController(GanadoService ganadoService,
                          CompraService compraService,
                          TransporteService transporteService,
                          FinanzasService finanzasService) {
        this.ganadoService = ganadoService;
        this.compraService = compraService;
        this.transporteService = transporteService;
        this.finanzasService = finanzasService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        BalanceSociedadDTO balance = finanzasService.generarBalanceConsolidado();
        
        model.addAttribute("balance", balance);
        model.addAttribute("totalGanado", ganadoService.listarInventarioCompleto().size());
        model.addAttribute("ganadoEnFinca", ganadoService.contarCabezasEnFinca());
        model.addAttribute("ganadoEntregado", ganadoService.contarCabezasEntregadas());
        model.addAttribute("ganadoRechazo", ganadoService.contarRechazos());
        
        model.addAttribute("ultimasCompras", compraService.listarCompras());
        model.addAttribute("ultimosViajes", transporteService.listarTodos());
        model.addAttribute("ultimasLiquidaciones", finanzasService.listarLiquidaciones());

        return "index";
    }
}
