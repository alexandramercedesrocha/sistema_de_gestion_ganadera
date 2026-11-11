package com.ganaderia.rochaaltamirano.controller;

import com.ganaderia.rochaaltamirano.model.*;
import com.ganaderia.rochaaltamirano.service.CatalogoService;
import com.ganaderia.rochaaltamirano.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Web Integral para la Gestión de Catálogos Maestros:
 * Proveedores, Mataderos, Flota Vehicular, Conductores, Ubicaciones y Directorio Telefónico.
 */
@Controller
@RequestMapping("/catalogos")
public class CatalogoController {

    private final CatalogoService catalogoService;
    private final FileStorageService fileStorageService;

    public CatalogoController(CatalogoService catalogoService, FileStorageService fileStorageService) {
        this.catalogoService = catalogoService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String indexCatalogos(@RequestParam(name = "tab", defaultValue = "proveedores") String tab, Model model) {
        model.addAttribute("tabActiva", tab);
        model.addAttribute("proveedores", catalogoService.listarProveedores());
        model.addAttribute("mataderos", catalogoService.listarMataderos());
        model.addAttribute("camiones", catalogoService.listarCamiones());
        model.addAttribute("conductores", catalogoService.listarConductores());
        model.addAttribute("ubicaciones", catalogoService.listarUbicaciones());
        model.addAttribute("telefonos", catalogoService.listarTelefonos());
        model.addAttribute("categoriasGasto", catalogoService.listarCategoriasGasto());
        return "catalogos/index";
    }

    // --- PROVEEDORES ---
    @PostMapping("/proveedores/guardar")
    public String guardarProveedor(@RequestParam("nombre") String nombre,
                                   @RequestParam(name = "telefono", required = false) String telefono,
                                   @RequestParam(name = "compania", required = false, defaultValue = "Claro") String compania,
                                   @RequestParam(name = "nombreLugar", required = false) String nombreLugar,
                                   RedirectAttributes ra) {
        catalogoService.registrarProveedorCompleto(nombre, telefono, compania, nombreLugar);
        ra.addFlashAttribute("mensajeExito", "Proveedor '" + nombre + "' registrado con su información de contacto y ubicación.");
        return "redirect:/catalogos?tab=proveedores";
    }

    @PostMapping("/proveedores/{id}/eliminar")
    public String eliminarProveedor(@PathVariable("id") Integer id, RedirectAttributes ra) {
        catalogoService.eliminarProveedor(id);
        ra.addFlashAttribute("mensajeExito", "Proveedor eliminado del catálogo.");
        return "redirect:/catalogos?tab=proveedores";
    }

    // --- MATADEROS ---
    @PostMapping("/mataderos/guardar")
    public String guardarMatadero(@RequestParam("nombreMatadero") String nombreMatadero,
                                  @RequestParam(name = "telefono", required = false) String telefono,
                                  @RequestParam(name = "compania", required = false, defaultValue = "Claro") String compania,
                                  @RequestParam(name = "nombreLugar", required = false) String nombreLugar,
                                  RedirectAttributes ra) {
        catalogoService.registrarMataderoCompleto(nombreMatadero, telefono, compania, nombreLugar);
        ra.addFlashAttribute("mensajeExito", "Planta Matadero '" + nombreMatadero + "' registrada correctamente.");
        return "redirect:/catalogos?tab=mataderos";
    }

    @PostMapping("/mataderos/{id}/eliminar")
    public String eliminarMatadero(@PathVariable("id") Integer id, RedirectAttributes ra) {
        catalogoService.eliminarMatadero(id);
        ra.addFlashAttribute("mensajeExito", "Matadero eliminado del catálogo.");
        return "redirect:/catalogos?tab=mataderos";
    }

    // --- CAMIONES ---
    @PostMapping("/camiones/guardar")
    public String guardarCamion(@RequestParam("numPlaca") String numPlaca,
                                @RequestParam(name = "archivoCirculacion", required = false) MultipartFile archivoCirculacion,
                                @RequestParam(name = "urlFotoCirculacion", required = false) String urlFotoCirculacion,
                                RedirectAttributes ra) {
        Camion camion = new Camion();
        camion.setNumPlaca(numPlaca.trim().toUpperCase());

        String urlFinal = urlFotoCirculacion;
        if (archivoCirculacion != null && !archivoCirculacion.isEmpty()) {
            urlFinal = fileStorageService.almacenarArchivo(archivoCirculacion, "circulacion");
        }
        camion.setUrlFotoCirculacion(urlFinal);

        catalogoService.guardarCamion(camion);
        ra.addFlashAttribute("mensajeExito", "Camión con placa '" + camion.getNumPlaca() + "' registrado correctamente con su documento.");
        return "redirect:/catalogos?tab=camiones";
    }

    @PostMapping("/camiones/{placa}/eliminar")
    public String eliminarCamion(@PathVariable("placa") String placa, RedirectAttributes ra) {
        catalogoService.eliminarCamion(placa);
        ra.addFlashAttribute("mensajeExito", "Camión con placa '" + placa + "' eliminado.");
        return "redirect:/catalogos?tab=camiones";
    }

    // --- CONDUCTORES ---
    @PostMapping("/conductores/guardar")
    public String guardarConductor(@RequestParam("cedula") String cedula,
                                   @RequestParam("nombre") String nombre,
                                   @RequestParam(name = "telefono", required = false) String telefono,
                                   @RequestParam(name = "compania", required = false, defaultValue = "Claro") String compania,
                                   @RequestParam(name = "archivoLicencia", required = false) MultipartFile archivoLicencia,
                                   @RequestParam(name = "urlFotoLicencia", required = false) String urlFotoLicencia,
                                   RedirectAttributes ra) {
        String urlFinal = urlFotoLicencia;
        if (archivoLicencia != null && !archivoLicencia.isEmpty()) {
            urlFinal = fileStorageService.almacenarArchivo(archivoLicencia, "licencias");
        }

        catalogoService.registrarConductorCompleto(cedula, nombre, telefono, compania, urlFinal);
        ra.addFlashAttribute("mensajeExito", "Conductor '" + nombre + "' registrado con su licencia de conducir y teléfono.");
        return "redirect:/catalogos?tab=conductores";
    }

    @PostMapping("/conductores/{cedula}/eliminar")
    public String eliminarConductor(@PathVariable("cedula") String cedula, RedirectAttributes ra) {
        catalogoService.eliminarConductor(cedula);
        ra.addFlashAttribute("mensajeExito", "Conductor con cédula '" + cedula + "' eliminado.");
        return "redirect:/catalogos?tab=conductores";
    }

    // --- UBICACIONES ---
    @PostMapping("/ubicaciones/guardar")
    public String guardarUbicacion(@ModelAttribute("ubicacion") Ubicacion ubicacion,
                                   @RequestParam(name = "idProveedor", required = false) Integer idProveedor,
                                   @RequestParam(name = "idMatadero", required = false) Integer idMatadero,
                                   RedirectAttributes ra) {
        catalogoService.guardarUbicacion(ubicacion, idProveedor, idMatadero);
        ra.addFlashAttribute("mensajeExito", "Ubicación geográfica guardada con su enlace a mapa.");
        return "redirect:/catalogos?tab=ubicaciones";
    }

    @PostMapping("/ubicaciones/{id}/eliminar")
    public String eliminarUbicacion(@PathVariable("id") Integer id, RedirectAttributes ra) {
        catalogoService.eliminarUbicacion(id);
        ra.addFlashAttribute("mensajeExito", "Ubicación eliminada.");
        return "redirect:/catalogos?tab=ubicaciones";
    }

    // --- TELÉFONOS ---
    @PostMapping("/telefonos/guardar")
    public String guardarTelefono(@ModelAttribute("telefono") Telefono telefono,
                                  @RequestParam(name = "idProveedor", required = false) Integer idProveedor,
                                  @RequestParam(name = "idMatadero", required = false) Integer idMatadero,
                                  @RequestParam(name = "cedulaConductor", required = false) String cedulaConductor,
                                  RedirectAttributes ra) {
        catalogoService.guardarTelefono(telefono, idProveedor, idMatadero, cedulaConductor);
        ra.addFlashAttribute("mensajeExito", "Contacto telefónico registrado correctamente.");
        return "redirect:/catalogos?tab=telefonos";
    }

    @PostMapping("/telefonos/{id}/eliminar")
    public String eliminarTelefono(@PathVariable("id") Integer id, RedirectAttributes ra) {
        catalogoService.eliminarTelefono(id);
        ra.addFlashAttribute("mensajeExito", "Teléfono retirado del directorio.");
        return "redirect:/catalogos?tab=telefonos";
    }
}
