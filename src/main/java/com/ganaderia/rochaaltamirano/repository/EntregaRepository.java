package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Integer> {

    List<Entrega> findByMataderoIdMatadero(Integer idMatadero);

    default List<Entrega> listarTodas() {
        return findAll();
    }

    default Optional<Entrega> buscarPorId(Integer id) {
        return findById(id);
    }

    default Entrega guardar(Entrega entrega) {
        return save(entrega);
    }
}
