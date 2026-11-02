package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.ArqueoMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArqueoMensualRepository extends JpaRepository<ArqueoMensual, Integer> {

    default List<ArqueoMensual> listarTodos() {
        return findAll();
    }

    default Optional<ArqueoMensual> buscarPorId(Integer id) {
        return findById(id);
    }

    default ArqueoMensual guardar(ArqueoMensual arqueo) {
        return save(arqueo);
    }
}
