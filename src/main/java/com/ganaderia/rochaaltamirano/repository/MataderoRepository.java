package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Matadero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MataderoRepository extends JpaRepository<Matadero, Integer> {

    default List<Matadero> listarTodos() {
        return findAll();
    }

    default Optional<Matadero> buscarPorId(Integer id) {
        return findById(id);
    }

    default Matadero guardar(Matadero matadero) {
        return save(matadero);
    }
}
