package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.*;
import com.ganaderia.rochaaltamirano.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio Central para Administración de Catálogos Maestros
 * (Proveedores, Mataderos, Camiones, Conductores, Ubicaciones y Teléfonos)
 */
@Service
@Transactional(readOnly = true)
public class CatalogoService {

    private final ProveedorRepository proveedorRepository;
    private final MataderoRepository mataderoRepository;
    private final CamionRepository camionRepository;
    private final ConductorRepository conductorRepository;
    private final UbicacionRepository ubicacionRepository;
    private final TelefonoRepository telefonoRepository;
    private final CategoriaGastoRepository categoriaGastoRepository;

    public CatalogoService(ProveedorRepository proveedorRepository,
                           MataderoRepository mataderoRepository,
                           CamionRepository camionRepository,
                           ConductorRepository conductorRepository,
                           UbicacionRepository ubicacionRepository,
                           TelefonoRepository telefonoRepository,
                           CategoriaGastoRepository categoriaGastoRepository) {
        this.proveedorRepository = proveedorRepository;
        this.mataderoRepository = mataderoRepository;
        this.camionRepository = camionRepository;
        this.conductorRepository = conductorRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.telefonoRepository = telefonoRepository;
        this.categoriaGastoRepository = categoriaGastoRepository;
    }

    // --- PROVEEDORES ---
    public List<Proveedor> listarProveedores() {
        return proveedorRepository.findAll();
    }

    @Transactional
    public Proveedor guardarProveedor(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Transactional
    public Proveedor registrarProveedorCompleto(String nombre, String telefono, String compania, String nombreLugar, String urlMaps) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(nombre.trim());
        Proveedor guardado = proveedorRepository.save(proveedor);

        if (telefono != null && !telefono.isBlank()) {
            Telefono tel = new Telefono();
            tel.setProveedor(guardado);
            tel.setTelefono(telefono.trim());
            tel.setCompania(compania != null && !compania.isBlank() ? compania : "Claro");
            telefonoRepository.save(tel);
        }

        if (nombreLugar != null && !nombreLugar.isBlank()) {
            Ubicacion ubi = new Ubicacion();
            ubi.setProveedor(guardado);
            ubi.setNombreLugar(nombreLugar.trim());
            ubi.setUrl(urlMaps != null ? urlMaps.trim() : null);
            ubi.setTipoLugar("Finca Ganadera");
            ubicacionRepository.save(ubi);
        }

        return guardado;
    }

    @Transactional
    public void eliminarProveedor(Integer id) {
        proveedorRepository.deleteById(id);
    }

    // --- MATADEROS ---
    public List<Matadero> listarMataderos() {
        return mataderoRepository.findAll();
    }

    @Transactional
    public Matadero guardarMatadero(Matadero matadero) {
        return mataderoRepository.save(matadero);
    }

    @Transactional
    public Matadero registrarMataderoCompleto(String nombre, String telefono, String compania, String nombreLugar, String urlMaps) {
        Matadero matadero = new Matadero();
        matadero.setNombreMatadero(nombre.trim());
        Matadero guardado = mataderoRepository.save(matadero);

        if (telefono != null && !telefono.isBlank()) {
            Telefono tel = new Telefono();
            tel.setMatadero(guardado);
            tel.setTelefono(telefono.trim());
            tel.setCompania(compania != null && !compania.isBlank() ? compania : "Claro");
            telefonoRepository.save(tel);
        }

        if (nombreLugar != null && !nombreLugar.isBlank()) {
            Ubicacion ubi = new Ubicacion();
            ubi.setMatadero(guardado);
            ubi.setNombreLugar(nombreLugar.trim());
            ubi.setUrl(urlMaps != null ? urlMaps.trim() : null);
            ubi.setTipoLugar("Planta Matadero");
            ubicacionRepository.save(ubi);
        }

        return guardado;
    }

    @Transactional
    public void eliminarMatadero(Integer id) {
        mataderoRepository.deleteById(id);
    }

    // --- CAMIONES ---
    public List<Camion> listarCamiones() {
        return camionRepository.findAll();
    }

    @Transactional
    public Camion guardarCamion(Camion camion) {
        return camionRepository.save(camion);
    }

    @Transactional
    public void eliminarCamion(String placa) {
        camionRepository.deleteById(placa);
    }

    // --- CONDUCTORES ---
    public List<Conductor> listarConductores() {
        return conductorRepository.findAll();
    }

    @Transactional
    public Conductor guardarConductor(Conductor conductor) {
        return conductorRepository.save(conductor);
    }

    @Transactional
    public Conductor registrarConductorCompleto(String cedula, String nombre, String telefono, String compania, String urlFotoLicencia) {
        Conductor conductor = new Conductor();
        conductor.setCedula(cedula.trim().toUpperCase());
        conductor.setNombre(nombre.trim());
        conductor.setUrlFotoLicencia(urlFotoLicencia);
        Conductor guardado = conductorRepository.save(conductor);

        if (telefono != null && !telefono.isBlank()) {
            Telefono tel = new Telefono();
            tel.setConductor(guardado);
            tel.setTelefono(telefono.trim());
            tel.setCompania(compania != null && !compania.isBlank() ? compania : "Claro");
            telefonoRepository.save(tel);
        }

        return guardado;
    }

    @Transactional
    public void eliminarConductor(String cedula) {
        conductorRepository.deleteById(cedula);
    }

    // --- UBICACIONES ---
    public List<Ubicacion> listarUbicaciones() {
        return ubicacionRepository.findAll();
    }

    @Transactional
    public Ubicacion guardarUbicacion(Ubicacion ubicacion, Integer idProveedor, Integer idMatadero) {
        if (idProveedor != null) {
            proveedorRepository.findById(idProveedor).ifPresent(ubicacion::setProveedor);
        }
        if (idMatadero != null) {
            mataderoRepository.findById(idMatadero).ifPresent(ubicacion::setMatadero);
        }
        return ubicacionRepository.save(ubicacion);
    }

    public void eliminarUbicacion(Integer id) {
        ubicacionRepository.deleteById(id);
    }

    // --- TELÉFONOS ---
    public List<Telefono> listarTelefonos() {
        return telefonoRepository.findAll();
    }

    @Transactional
    public Telefono guardarTelefono(Telefono telefono, Integer idProveedor, Integer idMatadero, String cedulaConductor) {
        if (idProveedor != null) {
            proveedorRepository.findById(idProveedor).ifPresent(telefono::setProveedor);
        }
        if (idMatadero != null) {
            mataderoRepository.findById(idMatadero).ifPresent(telefono::setMatadero);
        }
        if (cedulaConductor != null && !cedulaConductor.isBlank()) {
            conductorRepository.findById(cedulaConductor).ifPresent(telefono::setConductor);
        }
        return telefonoRepository.save(telefono);
    }

    public void eliminarTelefono(Integer id) {
        telefonoRepository.deleteById(id);
    }

    // --- CATEGORÍAS DE GASTO ---
    public List<CategoriaGasto> listarCategoriasGasto() {
        return categoriaGastoRepository.findAll();
    }

    public CategoriaGasto guardarCategoriaGasto(CategoriaGasto categoria) {
        return categoriaGastoRepository.save(categoria);
    }
}

