package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.Lote;
import com.ganaderia.rochaaltamirano.model.PagoLote;
import com.ganaderia.rochaaltamirano.service.CatalogoService;
import com.ganaderia.rochaaltamirano.service.LoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador Web para el Módulo de Compras de Ganado y Lotes
 */
@Controller
@RequestMapping("/compras")
public class CompraController {

    private final LoteService loteService;
    private final CatalogoService catalogoService;

    public CompraController(LoteService loteService, CatalogoService catalogoService) {
        this.loteService = loteService;
        this.catalogoService = catalogoService;
    }

    @GetMapping
    public String listarCompras(@RequestParam(name = "idProveedor", required = false) Integer idProveedor,
                                @RequestParam(name = "fecha", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fecha,
                                Model model) {
        List<Lote> lotes = loteService.listarLotes();
        if (idProveedor != null && idProveedor > 0) {
            lotes = lotes.stream()
                    .filter(l -> l.getProveedor() != null && idProveedor.equals(l.getProveedor().getIdProveedor()))
                    .toList();
            model.addAttribute("idProveedorSeleccionado", idProveedor);
        }
        if (fecha != null) {
            lotes = lotes.stream()
                    .filter(l -> fecha.equals(l.getFechaCompra()))
                    .toList();
            model.addAttribute("fechaSeleccionada", fecha);
        }
        model.addAttribute("lotes", lotes);
        model.addAttribute("proveedores", catalogoService.listarProveedores());
        model.addAttribute("totalCabezasCompradas", loteService.contarTotalCabezasCompradas());
        model.addAttribute("totalInvertido", loteService.calcularTotalInvertidoEstimado());
        return "compras/lista";
    }

    @GetMapping("/nueva")
    public String formularioNuevoLote(Model model) {
        Lote nuevoLote = new Lote();
        nuevoLote.setFechaCompra(LocalDate.now());
        model.addAttribute("lote", nuevoLote);
        model.addAttribute("proveedores", catalogoService.listarProveedores());
        return "compras/formulario";
    }

    @PostMapping("/guardar")
    public String guardarLote(@ModelAttribute("lote") Lote lote,
                              @RequestParam(name = "idProveedor", required = false) Integer idProveedor,
                              RedirectAttributes redirectAttributes) {
        if (lote.getPrecioEstandar() != null && lote.getPrecioEstandar().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El precio estándar debe ser un número positivo mayor que cero.");
            return "redirect:/compras/nueva";
        }
        Lote guardado = loteService.registrarLote(lote, idProveedor);
        redirectAttributes.addFlashAttribute("mensajeExito", "Lote de compra #" + guardado.getIdLote() + " registrado. Ahora puedes registrar los aretes que vienen en este lote.");
        return "redirect:/compras/lote/" + guardado.getIdLote();
    }

    @GetMapping("/lote/{id}")
    public String verDetalleLote(@PathVariable("id") Integer id, Model model) {
        Lote lote = loteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado: " + id));

        model.addAttribute("lote", lote);
        model.addAttribute("nuevoAnimal", new Ganado());
        model.addAttribute("nuevoPago", new PagoLote());
        return "compras/detalle-lote";
    }

    @PostMapping("/lote/{id}/agregar-animal")
    public String agregarAnimalALote(@PathVariable("id") Integer id,
                                     @ModelAttribute("nuevoAnimal") Ganado animal,
                                     @RequestParam(name = "precioAnimalRechazo", required = false) BigDecimal precioAnimalRechazo,
                                     RedirectAttributes redirectAttributes) {
        String arete = animal.getArete() != null ? animal.getArete().trim() : "";
        if (arete.isBlank()) {
            redirectAttributes.addFlashAttribute("mensajeError", "El número de arete es obligatorio.");
            return "redirect:/compras/lote/" + id;
        }

        if (animal.getPesoKg() == null || animal.getPesoKg().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "El peso de la báscula debe ser mayor que cero.");
            return "redirect:/compras/lote/" + id;
        }

        Lote lote = loteService.buscarPorId(id).orElse(null);
        if (lote == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "El lote especificado no existe.");
            return "redirect:/compras";
        }

        // Asignación de precio de compra individual:
        if ("Rechazo".equalsIgnoreCase(animal.getCondicionCompra())) {
            if (precioAnimalRechazo == null || precioAnimalRechazo.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("mensajeError", 
                    "Debe indicar el precio de compra específico (C$/kg) para este animal con condición de rechazo.");
                return "redirect:/compras/lote/" + id;
            }
            animal.setPrecioCompra(precioAnimalRechazo);
        } else {
            // Condición Bueno (Apto): utiliza el precio estándar base del lote
            animal.setPrecioCompra(lote.getPrecioEstandar());
        }

        if (loteService.existeAreteEnSistema(arete)) {
            List<Ganado> existentes = loteService.buscarGanadoPorAreteFlexible(arete);
            String detalleLote = "";
            if (!existentes.isEmpty() && existentes.get(0).getLote() != null) {
                detalleLote = " (Registrado en Lote #" + existentes.get(0).getLote().getIdLote();
                if (existentes.get(0).getLote().getProveedor() != null) {
                    detalleLote += " - " + existentes.get(0).getLote().getProveedor().getNombre();
                }
                detalleLote += ")";
            }
            redirectAttributes.addFlashAttribute("mensajeError", 
                "¡Atención! El arete [" + arete + "] ya se encuentra registrado en la base de datos" + detalleLote + ". No se permiten aretes duplicados en el sistema.");
            return "redirect:/compras/lote/" + id;
        }

        loteService.registrarAnimalEnLote(id, animal, null);
        redirectAttributes.addFlashAttribute("mensajeExito", "Animal con arete " + arete + " añadido exitosamente al lote #" + id);
        return "redirect:/compras/lote/" + id;
    }

    @GetMapping("/api/verificar-arete")
    @ResponseBody
    public java.util.Map<String, Object> verificarAreteEnBaseDatos(@RequestParam("arete") String arete) {
        java.util.Map<String, Object> respuesta = new java.util.HashMap<>();
        if (arete == null || arete.trim().replaceAll("\\s+", "").length() < 9) {
            respuesta.put("existe", false);
            return respuesta;
        }

        List<Ganado> encontrados = loteService.buscarGanadoPorAreteFlexible(arete);
        if (!encontrados.isEmpty()) {
            Ganado g = encontrados.get(0);
            respuesta.put("existe", true);
            String info = "El arete [" + arete.trim() + "] ya existe en la base de datos";
            if (g.getLote() != null) {
                info += " (Lote #" + g.getLote().getIdLote();
                if (g.getLote().getProveedor() != null) {
                    info += " - " + g.getLote().getProveedor().getNombre();
                }
                info += ")";
            }
            if (g.getCondicionCompra() != null) {
                info += " | Condición: " + g.getCondicionCompra();
            }
            respuesta.put("mensaje", info);
            respuesta.put("loteId", g.getLote() != null ? g.getLote().getIdLote() : null);
            return respuesta;
        }

        respuesta.put("existe", false);
        return respuesta;
    }

    @PostMapping("/lote/{id}/actualizar-precio-rechazo")
    public String actualizarPrecioRechazo(@PathVariable("id") Integer id,
                                          @RequestParam("precioDeRechazo") java.math.BigDecimal precioDeRechazo,
                                          RedirectAttributes redirectAttributes) {
        Lote lote = loteService.buscarPorId(id).orElse(null);
        if (lote != null && precioDeRechazo != null && precioDeRechazo.compareTo(java.math.BigDecimal.ZERO) > 0) {
            lote.setPrecioDeRechazo(precioDeRechazo);
            loteService.guardarLote(lote);
            redirectAttributes.addFlashAttribute("mensajeExito", "Precio de rechazo actualizado a C$ " + precioDeRechazo + " /kg para el lote #" + id);
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "Ingrese un precio de rechazo válido mayor que cero.");
        }
        return "redirect:/compras/lote/" + id;
    }

    @PostMapping("/lote/{id}/agregar-pago")
    public String agregarPagoALote(@PathVariable("id") Integer id,
                                   @RequestParam(name = "comprobanteVoucher", required = false) String comprobanteVoucher,
                                   @RequestParam(name = "estadoPago", defaultValue = "Pagado") String estadoPago,
                                   RedirectAttributes redirectAttributes) {
        if (comprobanteVoucher == null || comprobanteVoucher.isBlank()) {
            if ("Pendiente".equalsIgnoreCase(estadoPago)) {
                comprobanteVoucher = "PENDIENTE";
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "Para pagos totales o parciales debe ingresar el número de comprobante bancario (9 a 11 dígitos).");
                return "redirect:/compras/lote/" + id;
            }
        }
        loteService.registrarPagoLote(id, comprobanteVoucher.trim(), estadoPago);
        redirectAttributes.addFlashAttribute("mensajeExito", "Registro de pago (" + estadoPago + ") actualizado para el lote #" + id);
        return "redirect:/compras/lote/" + id;
    }
}
