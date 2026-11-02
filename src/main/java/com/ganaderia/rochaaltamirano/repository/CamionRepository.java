package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CamionRepository extends JpaRepository<Camion, String> {
}

