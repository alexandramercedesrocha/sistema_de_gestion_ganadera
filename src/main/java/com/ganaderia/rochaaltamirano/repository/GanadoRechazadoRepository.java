package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.GanadoRechazado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GanadoRechazadoRepository extends JpaRepository<GanadoRechazado, Integer> {
    List<GanadoRechazado> findByDetalleEntregaEntregaIdEntrega(Integer idEntrega);
}

