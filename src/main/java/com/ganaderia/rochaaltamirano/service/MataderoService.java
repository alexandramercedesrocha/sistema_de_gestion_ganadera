package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.Matadero;
import com.ganaderia.rochaaltamirano.repository.MataderoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Catálogo para Mataderos Industriales
 */
@Service
public class MataderoService {

    private final MataderoRepository mataderoRepository;

    public MataderoService(MataderoRepository mataderoRepository) {
        this.mataderoRepository = mataderoRepository;
    }

    public List<Matadero> listarTodos() {
        return mataderoRepository.listarTodos();
    }

    public Matadero registrar(Matadero matadero) {
        return mataderoRepository.guardar(matadero);
    }
}
