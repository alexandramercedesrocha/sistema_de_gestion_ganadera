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

    boolean existsByGanadoArete(String arete);

    @Query("SELECT d FROM DetalleEntrega d " +
           "JOIN FETCH d.entrega e " +
           "LEFT JOIN FETCH e.matadero m " +
           "LEFT JOIN FETCH d.rechazos r " +
           "WHERE d.ganado.arete = :arete OR REPLACE(d.ganado.arete, ' ', '') = REPLACE(:arete, ' ', '') " +
           "ORDER BY e.fechaEntrega DESC, d.idDetalleEntrega DESC")
    List<DetalleEntrega> findHistorialPorArete(@Param("arete") String arete);

    @Query("SELECT d FROM DetalleEntrega d " +
           "JOIN FETCH d.entrega e " +
           "LEFT JOIN FETCH e.matadero m " +
           "LEFT JOIN FETCH d.rechazos r " +
           "WHERE d.ganado.arete IN :aretes " +
           "ORDER BY e.fechaEntrega DESC, d.idDetalleEntrega DESC")
    List<DetalleEntrega> findHistorialPorAretes(@Param("aretes") List<String> aretes);
}

