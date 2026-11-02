package com.ganaderia.rochaaltamirano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de inicio para el Sistema de Gestión y Comercialización 
 * de Ganado Bovino de la Sociedad de Wilber Rocha y Yader Altamirano.
 * 
 * @author Senior Software Engineer & Database Architect
 */
@SpringBootApplication
public class GanaderiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GanaderiaApplication.class, args);
        System.out.println("==================================================================");
        System.out.println(" SGC Ganadería Rocha & Altamirano iniciada correctamente en:");
        System.out.println(" URL local: http://localhost:8080");
        System.out.println(" Módulo: Gestión de Ganado, Transporte, Compras y Balance 50-50");
        System.out.println("==================================================================");
    }
}
