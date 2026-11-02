package com.ganaderia.rochaaltamirano.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Manejador Global de Excepciones para el Sistema Ganadero
 * Previene el pantallazo blanco (Whitelabel Error 500) y ofrece navegación segura y amigable.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String manejarViolacionIntegridad(DataIntegrityViolationException ex,
                                            HttpServletRequest request,
                                            RedirectAttributes redirectAttributes,
                                            Model model) {
        log.error("Violación de integridad referencial en BD: {}", ex.getMessage());

        String mensaje = "No se pudo completar la operación en la base de datos. Es posible que el registro (arete, placa o código) ya exista, o esté vinculado con otros registros activos.";

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            redirectAttributes.addFlashAttribute("mensajeError", mensaje);
            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/");
        }

        model.addAttribute("errorMessage", mensaje);
        model.addAttribute("status", 409);
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String manejarExcepcionGeneral(Exception ex,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        log.error("Excepción no controlada en ruta {}: ", request.getRequestURI(), ex);

        String mensaje = "Se presentó una eventualidad al consultar la base de datos: " + (ex.getMessage() != null ? ex.getMessage() : "Error interno");

        // Si fue un formulario POST, intentamos retornar a la página previa con un flash alert
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            redirectAttributes.addFlashAttribute("mensajeError", mensaje);
            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/");
        }

        model.addAttribute("errorMessage", mensaje);
        model.addAttribute("status", 500);
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("exception", ex.getClass().getSimpleName());
        return "error";
    }
}
