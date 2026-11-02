package com.ganaderia.rochaaltamirano.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

/**
 * Servicio para almacenamiento y gestión de imágenes y documentos subidos
 * (Fotos de Circulación vehicular y Licencias de Conducir)
 */
@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
            Files.createDirectories(rootLocation.resolve("circulacion"));
            Files.createDirectories(rootLocation.resolve("licencias"));
            Files.createDirectories(rootLocation.resolve("comprobantes"));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de almacenamiento de archivos", e);
        }
    }

    /**
     * Guarda un archivo MultipartFile y retorna su URL web relativa accesible (/uploads/...)
     */
    public String almacenarArchivo(MultipartFile file, String subcarpeta) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path destinoCarpeta = this.rootLocation.resolve(subcarpeta);
            Files.createDirectories(destinoCarpeta);

            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            } else {
                extension = ".jpg";
            }

            // Generar nombre de archivo único
            String nombreArchivo = UUID.randomUUID().toString() + extension;
            Path destinoArchivo = destinoCarpeta.resolve(nombreArchivo);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinoArchivo, StandardCopyOption.REPLACE_EXISTING);
            }

            // Retornar la ruta relativa servida por Spring Boot
            return "/uploads/" + subcarpeta + "/" + nombreArchivo;

        } catch (IOException e) {
            throw new RuntimeException("Error al almacenar el archivo en disco: " + file.getOriginalFilename(), e);
        }
    }
}

