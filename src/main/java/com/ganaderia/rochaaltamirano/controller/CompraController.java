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
    public String listarCompras(Model model) {
        List<Lote> lotes = loteService.listarLotes();
        model.addAttribute("lotes", lotes);
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
                                     RedirectAttributes redirectAttributes) {
        String arete = animal.getArete() != null ? animal.getArete().trim() : "";
        if (arete.isBlank()) {
            redirectAttributes.addFlashAttribute("mensajeError", "El número de arete es obligatorio.");
            return "redirect:/compras/lote/" + id;
        }

        if (loteService.existeAreteEnSistema(arete)) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "El arete [" + arete + "] ya se encuentra registrado en el sistema o en el inventario actual. Ingrese un arete único.");
            return "redirect:/compras/lote/" + id;
        }

        loteService.registrarAnimalEnLote(id, animal, null);
        redirectAttributes.addFlashAttribute("mensajeExito", "Animal con arete " + arete + " añadido exitosamente al lote #" + id);
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
