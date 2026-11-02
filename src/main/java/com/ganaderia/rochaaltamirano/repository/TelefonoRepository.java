package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Conductor;
import com.ganaderia.rochaaltamirano.model.Matadero;
import com.ganaderia.rochaaltamirano.model.Proveedor;
import com.ganaderia.rochaaltamirano.model.Telefono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelefonoRepository extends JpaRepository<Telefono, Integer> {

    List<Telefono> findByProveedor(Proveedor proveedor);

    List<Telefono> findByMatadero(Matadero matadero);

    List<Telefono> findByConductor(Conductor conductor);

    @Query("SELECT t FROM Telefono t WHERE t.proveedor.idProveedor = :idProveedor")
    List<Telefono> findByProveedorIdProveedor(@Param("idProveedor") Integer idProveedor);

    @Query("SELECT t FROM Telefono t WHERE t.matadero.idMatadero = :idMatadero")
    List<Telefono> findByMataderoIdMatadero(@Param("idMatadero") Integer idMatadero);

    @Query("SELECT t FROM Telefono t WHERE t.conductor.cedula = :cedula")
    List<Telefono> findByConductorCedula(@Param("cedula") String cedula);
}
