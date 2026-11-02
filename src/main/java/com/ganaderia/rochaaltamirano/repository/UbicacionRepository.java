package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Matadero;
import com.ganaderia.rochaaltamirano.model.Proveedor;
import com.ganaderia.rochaaltamirano.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    List<Ubicacion> findByProveedor(Proveedor proveedor);

    List<Ubicacion> findByMatadero(Matadero matadero);

    @Query("SELECT u FROM Ubicacion u WHERE u.proveedor.idProveedor = :idProveedor")
    List<Ubicacion> findByProveedorIdProveedor(@Param("idProveedor") Integer idProveedor);

    @Query("SELECT u FROM Ubicacion u WHERE u.matadero.idMatadero = :idMatadero")
    List<Ubicacion> findByMataderoIdMatadero(@Param("idMatadero") Integer idMatadero);
}
