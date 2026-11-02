package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Lote;
import com.ganaderia.rochaaltamirano.model.PagoLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoLoteRepository extends JpaRepository<PagoLote, Integer> {

    List<PagoLote> findByLote(Lote lote);

    List<PagoLote> findByEstadoPago(String estadoPago);

    @Query("SELECT p FROM PagoLote p WHERE p.lote.idLote = :idLote")
    List<PagoLote> findByLoteIdLote(@Param("idLote") Integer idLote);
}
