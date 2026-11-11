package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.DetalleEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleEntregaRepository extends JpaRepository<DetalleEntrega, Integer> {
    
    @Query("SELECT d FROM DetalleEntrega d LEFT JOIN FETCH d.ganado g WHERE d.entrega.idEntrega = :idEntrega ORDER BY d.idDetalleEntrega ASC")
    List<DetalleEntrega> findByEntregaIdEntrega(@Param("idEntrega") Integer idEntrega);

    List<DetalleEntrega> findByEstadoDictamen(String estadoDictamen);
}

