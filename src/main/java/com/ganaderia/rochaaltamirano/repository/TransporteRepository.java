package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Transporte;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio de Transporte (Sin datos precargados)
 */
@Repository
public class TransporteRepository {

    private final Map<Long, Transporte> transportes = new ConcurrentHashMap<>();

    public TransporteRepository() {
        // Inicialmente vacío: se alimenta de la base de datos o registros manuales
    }

    public List<Transporte> listarTodos() {
        List<Transporte> lista = new ArrayList<>(transportes.values());
        lista.sort((t1, t2) -> t2.getFechaViaje().compareTo(t1.getFechaViaje()));
        return lista;
    }

    public Optional<Transporte> buscarPorId(Long id) {
        return Optional.ofNullable(transportes.get(id));
    }

    public Transporte guardar(Transporte transporte) {
        if (transporte.getIdTransporte() == null) {
            long nuevoId = transportes.keySet().stream().mapToLong(v -> v).max().orElse(300L) + 1L;
            transporte.setIdTransporte(nuevoId);
        }
        transportes.put(transporte.getIdTransporte(), transporte);
        return transporte;
    }

    public boolean actualizarEstadoPago(Long id, String estado) {
        Transporte transporte = transportes.get(id);
        if (transporte != null) {
            transporte.setEstadoPago(estado);
            return true;
        }
        return false;
    }

    public List<Transporte> filtrarPorEstadoPago(String estado) {
        List<Transporte> resultado = new ArrayList<>();
        for (Transporte t : transportes.values()) {
            if (t.getEstadoPago() != null && t.getEstadoPago().equalsIgnoreCase(estado)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    public boolean marcarComoPagado(Long id) {
        return actualizarEstadoPago(id, "Pagado");
    }
}
