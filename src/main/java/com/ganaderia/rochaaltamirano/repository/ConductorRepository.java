package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, String> {
}

