package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    default List<Proveedor> listarTodos() {
        return findAll();
    }

    default Optional<Proveedor> buscarPorId(Integer id) {
        return findById(id);
    }

    default Proveedor guardar(Proveedor proveedor) {
        return save(proveedor);
    }
}
