package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Proveedor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio Simulado para Proveedores de Ganado
 */
@Repository
public class ProveedorRepository {

    private final Map<Long, Proveedor> proveedores = new ConcurrentHashMap<>();

    public ProveedorRepository() {
        proveedores.put(1L, new Proveedor(1L, "Don Chepe Miranda (Hacienda La Gloria)", "+505 8899-1122"));
        proveedores.put(2L, new Proveedor(2L, "Ganados del Norte S.A. (Matagalpa)", "+505 8765-4321"));
        proveedores.put(3L, new Proveedor(3L, "Finca Santa Rosa (Chontales)", "+505 8333-7788"));
        proveedores.put(4L, new Proveedor(4L, "Roberto Alvarado (Río Blanco)", "+505 8444-9900"));
    }

    public List<Proveedor> listarTodos() {
        return new ArrayList<>(proveedores.values());
    }

    public Optional<Proveedor> buscarPorId(Long id) {
        return Optional.ofNullable(proveedores.get(id));
    }

    public Proveedor guardar(Proveedor proveedor) {
        if (proveedor.getIdProveedor() == null) {
            long nuevoId = proveedores.keySet().stream().mapToLong(v -> v).max().orElse(0L) + 1L;
            proveedor.setIdProveedor(nuevoId);
        }
        proveedores.put(proveedor.getIdProveedor(), proveedor);
        return proveedor;
    }
}
