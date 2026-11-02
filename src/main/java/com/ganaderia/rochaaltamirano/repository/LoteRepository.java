package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Lote;
import com.ganaderia.rochaaltamirano.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {

    List<Lote> findByProveedor(Proveedor proveedor);

    @Query("SELECT l FROM Lote l WHERE l.proveedor.idProveedor = :idProveedor")
    List<Lote> findByProveedorIdProveedor(@Param("idProveedor") Integer idProveedor);
}
