package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Liquidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiquidacionRepository extends JpaRepository<Liquidacion, String> {

    List<Liquidacion> findByMataderoIdMatadero(Integer idMatadero);
    List<Liquidacion> findByArqueoIdArqueo(Integer idArqueo);
    boolean existsByEntregaIdEntrega(Integer idEntrega);
    Optional<Liquidacion> findByEntregaIdEntrega(Integer idEntrega);

    default List<Liquidacion> listarTodas() {
        return findAll();
    }

    default Optional<Liquidacion> buscarPorComprobante(String idNumComprobante) {
        if (idNumComprobante == null) return Optional.empty();
        return findById(idNumComprobante.trim().toUpperCase());
    }

    default Liquidacion guardar(Liquidacion liquidacion) {
        if (liquidacion != null && liquidacion.getIdNumComprobante() != null) {
            liquidacion.setIdNumComprobante(liquidacion.getIdNumComprobante().trim().toUpperCase());
            return save(liquidacion);
        }
        return liquidacion;
    }
}
