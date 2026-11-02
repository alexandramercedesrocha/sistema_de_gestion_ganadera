package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.DetalleEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleEntregaRepository extends JpaRepository<DetalleEntrega, Integer> {
    List<DetalleEntrega> findByEntregaIdEntrega(Integer idEntrega);
    List<DetalleEntrega> findByEstadoDictamen(String estadoDictamen);
}

