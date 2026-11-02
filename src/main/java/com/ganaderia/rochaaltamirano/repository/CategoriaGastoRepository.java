package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.CategoriaGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaGastoRepository extends JpaRepository<CategoriaGasto, Integer> {
}

