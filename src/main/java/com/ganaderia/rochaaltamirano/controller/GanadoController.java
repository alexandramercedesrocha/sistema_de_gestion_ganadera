package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.GanadoRechazo;
import com.ganaderia.rochaaltamirano.service.CatalogoService;
import com.ganaderia.rochaaltamirano.service.GanadoService;
import com.ganaderia.rochaaltamirano.service.LoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador Web para el Módulo de Inventario de Ganado y Ganado de Rechazo
 */
@Controller
@RequestMapping("/ganado")
public class GanadoController {

    private final GanadoService ganadoService;
    private final LoteService loteService;
    private final CatalogoService catalogoService;

    public GanadoController(GanadoService ganadoService, LoteService loteService, CatalogoService catalogoService) {
        this.ganadoService = ganadoService;
        this.loteService = loteService;
        this.catalogoService = catalogoService;
    }

    @GetMapping
    public String listarGanado(@RequestParam(name = "vista", required = false, defaultValue = "activos") String vista,
                               @RequestParam(name = "idProveedor", required = false) Integer idProveedor,
                               @RequestParam(name = "fecha", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fecha,
                               Model model) {
        List<Ganado> inventario;

        if ("en-finca".equalsIgnoreCase(vista)) {
            inventario = ganadoService.listarGanadoEnFincaSinDespacho();
            model.addAttribute("filtroActivo", "Solo en Finca (Sin despachar a planta)");
        } else if ("rechazos".equalsIgnoreCase(vista)) {
            inventario = ganadoService.listarGanadoRechazosMatadero();
            model.addAttribute("filtroActivo", "Rechazos de Matadero (Disponibles en Finca)");
        } else if ("vendidos".equalsIgnoreCase(vista)) {
            inventario = ganadoService.listarGanadoVendidosAceptados();
            model.addAttribute("filtroActivo", "Entregados y Aceptados en Canal (Ventas cerradas)");
        } else if ("todos".equalsIgnoreCase(vista)) {
            inventario = ganadoService.listarInventarioCompleto();
            model.addAttribute("filtroActivo", "Historial Completo de Aretes");
        } else {
            // Regla principal: Todo el ganado comprado que NO tenga entrega aceptada (incluye rechazos)
            inventario = ganadoService.listarGanadoActivoInventario();
            model.addAttribute("filtroActivo", "Ganado Activo en Inventario (En Finca y Rechazos disponibles)");
        }

        // Asignar etiquetas visuales según el historial de cada animal
        List<Ganado> vendidos = ganadoService.listarGanadoVendidosAceptados();
        List<Ganado> rechazos = ganadoService.listarGanadoRechazosMatadero();

        for (Ganado g : inventario) {
            if (ganadoService.fueRearetado(g)) {
                g.setEstado("Re-aretado (Histórico)");
                g.setCategoria("Re-aretado");
            } else if (vendidos.contains(g)) {
                g.setEstado("Entregado a Matadero");
                g.setCategoria("Apto / Canal Aceptada");
            } else if (rechazos.contains(g)) {
                g.setEstado("Rechazo en Matadero");
                g.setCategoria("Rechazo");
            } else {
                g.setEstado("En Finca");
                g.setCategoria("Apto");
            }
        }

        // Filtro por Proveedor en todas las opciones de inventario
        if (idProveedor != null && idProveedor > 0) {
            inventario = inventario.stream()
                    .filter(g -> g.getLote() != null && g.getLote().getProveedor() != null && idProveedor.equals(g.getLote().getProveedor().getIdProveedor()))
                    .toList();
            model.addAttribute("idProveedorSeleccionado", idProveedor);
        }

        // Filtro por Fecha de compra en todas las opciones de inventario
        if (fecha != null) {
            inventario = inventario.stream()
                    .filter(g -> g.getLote() != null && fecha.equals(g.getLote().getFechaCompra()))
                    .toList();
            model.addAttribute("fechaSeleccionada", fecha);
        }

        model.addAttribute("vistaSeleccionada", vista);
        model.addAttribute("ganados", inventario);
        model.addAttribute("proveedores", catalogoService.listarProveedores());
        model.addAttribute("lotes", loteService.listarLotes());
        model.addAttribute("totalCabezas", inventario.size());
        model.addAttribute("totalEnFinca", ganadoService.contarCabezasEnFinca());
        model.addAttribute("totalRechazos", ganadoService.contarRechazos());
        model.addAttribute("kilosTotalesFinca", ganadoService.calcularTotalKilosEnFinca());

        return "ganado/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevoGanado(Model model) {
        model.addAttribute("ganado", new Ganado());
        model.addAttribute("lotes", loteService.listarLotes());
        return "ganado/formulario";
    }

    @PostMapping("/guardar")
    public String guardarGanado(@ModelAttribute("ganado") Ganado ganado,
                                @RequestParam(name = "idLote", required = false) Integer idLote,
                                RedirectAttributes redirectAttributes) {
        if (ganado.getPesoKg() == null || ganado.getPesoKg().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El peso de la res debe ser un número positivo mayor que cero.");
            return "redirect:/ganado/nuevo";
        }
        if (idLote != null) {
            loteService.buscarPorId(idLote).ifPresent(ganado::setLote);
        }
        ganadoService.registrarGanado(ganado);
        redirectAttributes.addFlashAttribute("mensajeExito", "Animal con arete " + ganado.getArete() + " registrado exitosamente en el inventario.");
        return "redirect:/ganado";
    }

    @GetMapping("/rechazos")
    public String listarGanadoRechazo(Model model) {
        List<GanadoRechazo> rechazos = ganadoService.listarRechazos();
        model.addAttribute("rechazos", rechazos);
        model.addAttribute("totalRechazos", rechazos.size());
        model.addAttribute("lotes", loteService.listarLotes());
        return "ganado/detalle-rechazo";
    }

    @GetMapping("/nuevo-rechazo")
    public String formularioNuevoRechazo(Model model) {
        model.addAttribute("ganadoRechazo", new GanadoRechazo());
        return "ganado/formulario-rechazo";
    }

    @PostMapping("/guardar-rechazo")
    public String guardarRechazo(@ModelAttribute("ganadoRechazo") GanadoRechazo rechazo, RedirectAttributes redirectAttributes) {
        if (rechazo.getPesoEntradaKg() == null || rechazo.getPesoEntradaKg().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El peso de entrada debe ser un número positivo mayor que cero.");
            return "redirect:/ganado/nuevo-rechazo";
        }
        if (rechazo.getPesoMatadero() != null && rechazo.getPesoMatadero().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El peso de báscula de matadero debe ser un número positivo mayor que cero.");
            return "redirect:/ganado/nuevo-rechazo";
        }
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

    @PostMapping("/rearetar")
    public String rearetarGanado(@RequestParam("areteOriginal") String areteOriginal,
                                 @RequestParam("nuevoArete") String nuevoArete,
                                 @RequestParam("nuevoPesoKg") BigDecimal nuevoPesoKg,
                                 @RequestParam(name = "idLote", required = false) Integer idLote,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (nuevoPesoKg == null || nuevoPesoKg.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("mensajeError", "El peso recuperado debe ser un número positivo mayor que cero.");
                return "redirect:/ganado";
            }
            Ganado nuevo = ganadoService.rearetarGanado(areteOriginal, nuevoArete, nuevoPesoKg, idLote);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "¡Bovino re-aretado y recuperado exitosamente! Arete anterior " + areteOriginal + 
                    " vinculado al nuevo arete oficial " + nuevo.getAreteFormateado() + 
                    " con condición Apto (" + nuevoPesoKg + " kg) listo para ser despachado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al re-aretar: " + e.getMessage());
        }
        return "redirect:/ganado";
    }

    @GetMapping("/api/historial/{arete}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> obtenerHistorialApi(@PathVariable("arete") String arete) {
        try {
            com.ganaderia.rochaaltamirano.dto.HistorialBovinoDTO dto = ganadoService.obtenerHistorialBovino(arete);
            return org.springframework.http.ResponseEntity.ok(dto);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/validar-arete")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> validarAreteApi(@RequestParam("arete") String arete) {
        String soloDigitos = arete != null ? arete.replaceAll("\\D", "") : "";
        if (soloDigitos.length() < 9) {
            return org.springframework.http.ResponseEntity.ok(java.util.Map.of(
                    "completo", false,
                    "disponible", true,
                    "existe", false,
                    "mensaje", "Ingrese los 9 dígitos numéricos (XXXXX XXXX)"
            ));
        }
        boolean disponible = ganadoService.validarDisponibilidadArete(arete);
        return org.springframework.http.ResponseEntity.ok(java.util.Map.of(
                "completo", true,
                "disponible", disponible,
                "existe", !disponible,
                "mensaje", disponible ? "¡Arete disponible para registro!" : "¡Atención! Este arete ya existe registrado en la base de datos del sistema"
        ));
    }
}
