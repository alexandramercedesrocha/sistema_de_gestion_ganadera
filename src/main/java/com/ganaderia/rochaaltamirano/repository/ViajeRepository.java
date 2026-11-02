package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Integer> {
    List<Viaje> findByEstadoPago(String estadoPago);
    List<Viaje> findByConductorCedula(String cedula);
    List<Viaje> findByCamionNumPlaca(String numPlaca);
}

