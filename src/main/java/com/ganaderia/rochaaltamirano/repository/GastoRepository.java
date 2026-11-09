package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Integer> {

    List<Gasto> findByCategoriaIdCategoria(Integer idCategoria);
    List<Gasto> findByArqueoIdArqueo(Integer idArqueo);
    List<Gasto> findByViajeIdViaje(Integer idViaje);

    default List<Gasto> listarTodos() {
        return findAll();
    }

    default Optional<Gasto> buscarPorId(Integer id) {
        return findById(id);
    }

    default Gasto guardar(Gasto gasto) {
        return save(gasto);
    }
}
