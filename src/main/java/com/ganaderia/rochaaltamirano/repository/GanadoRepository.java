package com.ganaderia.rochaaltamirano.repository;

import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.GanadoRechazo;
import com.ganaderia.rochaaltamirano.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface GanadoRepository extends JpaRepository<Ganado, String> {

    List<Ganado> findByLote(Lote lote);

    @Query("SELECT g FROM Ganado g WHERE g.lote.idLote = :idLote")
    List<Ganado> findByLoteIdLote(@Param("idLote") Integer idLote);

    @Query("SELECT g FROM Ganado g WHERE REPLACE(g.arete, ' ', '') = :areteLimpio OR g.arete = :areteOriginal")
    List<Ganado> buscarPorAreteFlexible(@Param("areteLimpio") String areteLimpio, @Param("areteOriginal") String areteOriginal);

    @Query("SELECT COUNT(g) > 0 FROM Ganado g WHERE REPLACE(g.arete, ' ', '') = :areteLimpio OR g.arete = :areteOriginal")
    boolean existeAreteEnBaseDatos(@Param("areteLimpio") String areteLimpio, @Param("areteOriginal") String areteOriginal);

    Optional<Ganado> findFirstByAreteAnterior(Ganado areteAnterior);

    Optional<Ganado> findFirstByAreteAnteriorArete(String arete);

    boolean existsByAreteAnteriorArete(String arete);

    /**
     * Regla de Negocio: Animales activos en inventario.
     * Incluye todo el ganado que NO haya sido comprado/aceptado por matadero
     * y que NO haya sido ya reemplazado por re-aretado.
     */
    @Query("SELECT g FROM Ganado g WHERE " +
           "NOT EXISTS (SELECT nuevo FROM Ganado nuevo WHERE nuevo.areteAnterior = g) AND " +
           "NOT EXISTS (" +
           "  SELECT d FROM DetalleEntrega d WHERE d.ganado = g AND (" +
           "    LOWER(d.estadoDictamen) = 'apto' OR " +
           "    LOWER(d.estadoDictamen) LIKE '%comprado%' OR " +
           "    (LOWER(d.estadoDictamen) = 'rechazado' AND d.precioRechazo IS NOT NULL)" +
           "  )" +
           ")")
    List<Ganado> findGanadoActivoInventario();

    /**
     * Reses que ya fueron entregadas y compradas/aceptadas por la planta matadero (venta consumada).
     */
    @Query("SELECT DISTINCT g FROM Ganado g WHERE EXISTS (" +
           "  SELECT d FROM DetalleEntrega d WHERE d.ganado = g AND (" +
           "    LOWER(d.estadoDictamen) = 'apto' OR " +
           "    LOWER(d.estadoDictamen) LIKE '%comprado%' OR " +
           "    (LOWER(d.estadoDictamen) = 'rechazado' AND d.precioRechazo IS NOT NULL)" +
           "  )" +
           ")")
    List<Ganado> findGanadoVendidosAceptados();

    /**
     * Reses que fueron despachadas a matadero pero sufrieron rechazo técnico/sanitario
     * y fueron devueltas a la finca, pendientes de ser re-aretadas / recuperadas.
     */
    @Query("SELECT DISTINCT g FROM Ganado g WHERE " +
           "NOT EXISTS (SELECT nuevo FROM Ganado nuevo WHERE nuevo.areteAnterior = g) AND " +
           "EXISTS (" +
           "  SELECT d FROM DetalleEntrega d WHERE d.ganado = g AND (" +
           "    LOWER(d.estadoDictamen) LIKE '%devuelto%' OR " +
           "    (LOWER(d.estadoDictamen) = 'rechazado' AND d.precioRechazo IS NULL)" +
           "  )" +
           ") AND NOT EXISTS (" +
           "  SELECT d2 FROM DetalleEntrega d2 WHERE d2.ganado = g AND (" +
           "    LOWER(d2.estadoDictamen) = 'apto' OR LOWER(d2.estadoDictamen) LIKE '%comprado%'" +
           "  )" +
           ")")
    List<Ganado> findGanadoRechazosMatadero();

    /**
     * Reses en finca que nunca han tenido despacho a matadero y no han sido reemplazadas por re-aretado.
     */
    @Query("SELECT g FROM Ganado g WHERE " +
           "NOT EXISTS (SELECT nuevo FROM Ganado nuevo WHERE nuevo.areteAnterior = g) AND " +
           "NOT EXISTS (" +
           "  SELECT d FROM DetalleEntrega d WHERE d.ganado = g" +
           ")")
    List<Ganado> findGanadoEnFincaSinDespacho();

    default List<Ganado> listarTodos() {
        return findAll();
    }

    default Optional<Ganado> buscarPorArete(String arete) {
        if (arete == null) return Optional.empty();
        return findById(arete.trim().toUpperCase());
    }

    default Ganado guardar(Ganado ganado) {
        if (ganado != null && ganado.getArete() != null) {
            ganado.setArete(ganado.getArete().trim().toUpperCase());
            return save(ganado);
        }
        return ganado;
    }

    default boolean eliminarPorArete(String arete) {
        if (arete == null) return false;
        String clave = arete.trim().toUpperCase();
        if (existsById(clave)) {
            deleteById(clave);
            return true;
        }
        return false;
    }

    default List<Ganado> filtrarPorEstado(String estado) {
        return findAll().stream()
                .filter(g -> g.getEstado() != null && g.getEstado().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

    default List<Ganado> filtrarPorCategoria(String categoria) {
        return findAll().stream()
                .filter(g -> g.getCategoria() != null && g.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    default List<GanadoRechazo> listarSoloRechazos() {
        return new ArrayList<>();
    }
}
