package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.Matadero;
import com.ganaderia.rochaaltamirano.model.Proveedor;
import com.ganaderia.rochaaltamirano.repository.MataderoRepository;
import com.ganaderia.rochaaltamirano.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicios de Catálogo para Proveedores y Mataderos
 */
@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> listarTodos() {
        return proveedorRepository.listarTodos();
    }

    public Proveedor registrar(Proveedor proveedor) {
        return proveedorRepository.guardar(proveedor);
    }
}
