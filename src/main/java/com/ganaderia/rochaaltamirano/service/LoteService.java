package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.model.Ganado;
import com.ganaderia.rochaaltamirano.model.Lote;
import com.ganaderia.rochaaltamirano.model.PagoLote;
import com.ganaderia.rochaaltamirano.model.Proveedor;
import com.ganaderia.rochaaltamirano.repository.DetalleEntregaRepository;
import com.ganaderia.rochaaltamirano.repository.GanadoRepository;
import com.ganaderia.rochaaltamirano.repository.LoteRepository;
import com.ganaderia.rochaaltamirano.repository.PagoLoteRepository;
import com.ganaderia.rochaaltamirano.repository.ProveedorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Lógica de Negocio para la Gestión de Lotes de Compra de Ganado
 */
@Service
@Transactional(readOnly = true)
public class LoteService {

    private final LoteRepository loteRepository;
    private final GanadoRepository ganadoRepository;
    private final PagoLoteRepository pagoLoteRepository;
    private final ProveedorRepository proveedorRepository;
    private final DetalleEntregaRepository detalleEntregaRepository;
    private final EntityManager entityManager;

    public LoteService(LoteRepository loteRepository,
                       GanadoRepository ganadoRepository,
                       PagoLoteRepository pagoLoteRepository,
                       ProveedorRepository proveedorRepository,
                       DetalleEntregaRepository detalleEntregaRepository,
                       EntityManager entityManager) {
        this.loteRepository = loteRepository;
        this.ganadoRepository = ganadoRepository;
        this.pagoLoteRepository = pagoLoteRepository;
        this.proveedorRepository = proveedorRepository;
        this.detalleEntregaRepository = detalleEntregaRepository;
        this.entityManager = entityManager;
    }

    public List<Lote> listarLotes() {
        return loteRepository.findAll();
    }

    
    public boolean existeAreteEnSistema(String arete) {
        if (arete == null || arete.isBlank()) return false;
        String original = arete.trim().toUpperCase();
        String limpio = original.replaceAll("\\s+", "");
        return ganadoRepository.existeAreteEnBaseDatos(limpio, original);
    }

    public List<Ganado> buscarGanadoPorAreteFlexible(String arete) {
        if (arete == null || arete.isBlank()) return List.of();
        String original = arete.trim().toUpperCase();
        String limpio = original.replaceAll("\\s+", "");
        return ganadoRepository.buscarPorAreteFlexible(limpio, original);
    }

    public Optional<Lote> buscarPorId(Integer idLote) {
        return loteRepository.findById(idLote);
    }

    @Transactional
    public Lote guardarLote(Lote lote) {
        return loteRepository.save(lote);
    }

    @Transactional
    public Lote registrarLote(Lote lote, Integer idProveedor) {
        if (lote.getPrecioEstandar() != null && lote.getPrecioEstandar().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio estándar del lote debe ser mayor a cero");
        }
        if (lote.getPrecioDeRechazo() != null && lote.getPrecioDeRechazo().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio de rechazo del lote no puede ser negativo");
        }
        if (idProveedor != null) {
            proveedorRepository.findById(idProveedor).ifPresent(lote::setProveedor);
        }
        return loteRepository.save(lote);
    }

    @Transactional
    public Ganado registrarAnimalEnLote(Integer idLote, Ganado animal, String areteAnteriorCodigo) {
        if (animal.getPesoKg() != null && animal.getPesoKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El peso de la res debe ser mayor a cero");
        }
        if (animal.getPrecioCompra() != null && animal.getPrecioCompra().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio de compra no puede ser negativo");
        }
        Lote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado: " + idLote));
        animal.setLote(lote);

        if (areteAnteriorCodigo != null && !areteAnteriorCodigo.isBlank()) {
            ganadoRepository.findById(areteAnteriorCodigo.trim().toUpperCase())
                    .ifPresent(animal::setAreteAnterior);
        }

        return ganadoRepository.save(animal);
    }

    @Transactional
    public PagoLote registrarPagoLote(Integer idLote, String comprobanteVoucher, String estadoPago) {
        Lote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado: " + idLote));

        PagoLote pago = new PagoLote();
        pago.setLote(lote);
        pago.setComprobanteVoucher(comprobanteVoucher);
        pago.setEstadoPago(estadoPago != null ? estadoPago : "Pagado");

        return pagoLoteRepository.save(pago);
    }

    public long contarTotalLotes() {
        return loteRepository.count();
    }

    public long contarTotalCabezasCompradas() {
        return ganadoRepository.count();
    }

    public BigDecimal calcularTotalInvertidoEstimado() {
        List<Lote> lotes = loteRepository.findAll();
        BigDecimal total = BigDecimal.ZERO;
        for (Lote lote : lotes) {
            try {
                if (lote.getAnimales() != null && !lote.getAnimales().isEmpty()) {
                    for (Ganado g : lote.getAnimales()) {
                        total = total.add(g.getSubtotalCompra());
                    }
                }
            } catch (Exception e) {
                // Fallback seguro si la sesión lazy estuviera cerrada
                if (lote.getIdLote() != null) {
                    List<Ganado> animales = ganadoRepository.findByLoteIdLote(lote.getIdLote());
                    for (Ganado g : animales) {
                        total = total.add(g.getSubtotalCompra());
                    }
                }
            }
        }
        return total;
    }

    @Transactional
    public Ganado actualizarAnimalEnLote(Integer idLote, String areteOriginal, Ganado datosNuevos, BigDecimal precioAnimalRechazo) {
        if (areteOriginal == null || areteOriginal.isBlank()) {
            throw new IllegalArgumentException("El arete original es obligatorio.");
        }
        String areteOrigLimpio = areteOriginal.trim().toUpperCase();
        Ganado animalExistente = ganadoRepository.findById(areteOrigLimpio)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el animal con arete: " + areteOriginal));

        if (animalExistente.getLote() == null || !idLote.equals(animalExistente.getLote().getIdLote())) {
            throw new IllegalArgumentException("El animal no pertenece al lote #" + idLote);
        }

        String nuevoArete = datosNuevos.getArete() != null ? datosNuevos.getArete().trim().toUpperCase() : areteOrigLimpio;
        if (nuevoArete.isBlank()) {
            throw new IllegalArgumentException("El nuevo número de arete no puede estar vacío.");
        }

        String nuevoAreteSinEspacios = nuevoArete.replaceAll("\\s+", "");
        String areteOrigSinEspacios = areteOrigLimpio.replaceAll("\\s+", "");
        boolean areteCambio = !nuevoAreteSinEspacios.equalsIgnoreCase(areteOrigSinEspacios);

        if (areteCambio) {
            // Verificar que no exista en ningún otro registro del sistema
            if (existeAreteEnSistema(nuevoArete)) {
                throw new IllegalArgumentException("¡Atención! El arete [" + nuevoArete + "] ya se encuentra registrado en el sistema. Ingrese un arete único.");
            }
        }

        // Asignación de precio según condición de compra
        if ("Rechazo".equalsIgnoreCase(datosNuevos.getCondicionCompra())) {
            if (precioAnimalRechazo == null || precioAnimalRechazo.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Debe ingresar un precio mayor a cero para condición de rechazo.");
            }
            datosNuevos.setPrecioCompra(precioAnimalRechazo);
            datosNuevos.setCondicionCompra("Rechazo");
        } else {
            datosNuevos.setCondicionCompra("Bueno");
            datosNuevos.setPrecioCompra(animalExistente.getLote().getPrecioEstandar());
        }

        if (!areteCambio) {
            animalExistente.setColor(datosNuevos.getColor());
            animalExistente.setPaletas(datosNuevos.getPaletas());
            animalExistente.setPesoKg(datosNuevos.getPesoKg());
            animalExistente.setCondicionCompra(datosNuevos.getCondicionCompra());
            animalExistente.setPrecioCompra(datosNuevos.getPrecioCompra());
            return ganadoRepository.save(animalExistente);
        } else {
            // El @Id cambió:
            // 1. Crear nuevo registro con nuevo arete
            Ganado nuevoAnimal = new Ganado();
            nuevoAnimal.setArete(nuevoArete);
            nuevoAnimal.setColor(datosNuevos.getColor());
            nuevoAnimal.setPaletas(datosNuevos.getPaletas());
            nuevoAnimal.setPesoKg(datosNuevos.getPesoKg());
            nuevoAnimal.setCondicionCompra(datosNuevos.getCondicionCompra());
            nuevoAnimal.setPrecioCompra(datosNuevos.getPrecioCompra());
            nuevoAnimal.setLote(animalExistente.getLote());
            nuevoAnimal.setAreteAnterior(animalExistente.getAreteAnterior());
            nuevoAnimal.setFechaIngreso(animalExistente.getFechaIngreso());
            nuevoAnimal.setEstado(animalExistente.getEstado());
            nuevoAnimal.setCategoria(animalExistente.getCategoria());

            Ganado guardado = ganadoRepository.save(nuevoAnimal);
            ganadoRepository.flush();

            // 2. Reasignar cualquier detalle_entrega que apuntara al viejo arete
            entityManager.createQuery("UPDATE DetalleEntrega d SET d.ganado = :nuevo WHERE d.ganado.arete = :viejo")
                    .setParameter("nuevo", guardado)
                    .setParameter("viejo", areteOrigLimpio)
                    .executeUpdate();

            // 3. Reasignar cualquier animal que tuviera el viejo arete como arete_anterior
            entityManager.createQuery("UPDATE Ganado g SET g.areteAnterior = :nuevo WHERE g.areteAnterior.arete = :viejo")
                    .setParameter("nuevo", guardado)
                    .setParameter("viejo", areteOrigLimpio)
                    .executeUpdate();

            // 4. Eliminar el viejo registro
            ganadoRepository.delete(animalExistente);
            ganadoRepository.flush();

            return guardado;
        }
    }

    @Transactional
    public void eliminarAnimalDeLote(Integer idLote, String arete) {
        if (arete == null || arete.isBlank()) {
            throw new IllegalArgumentException("El arete es obligatorio.");
        }
        String areteLimpio = arete.trim().toUpperCase();
        Ganado animal = ganadoRepository.findById(areteLimpio)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el animal con arete: " + arete));

        if (animal.getLote() == null || !idLote.equals(animal.getLote().getIdLote())) {
            throw new IllegalArgumentException("El animal no pertenece al lote #" + idLote);
        }

        if (detalleEntregaRepository.existsByGanadoArete(areteLimpio)) {
            throw new IllegalStateException("No se puede eliminar la res [" + arete + "] porque ya se encuentra vinculada a un despacho o entrega a matadero.");
        }

        // Desvincular autorreferencia si algún otro animal apunta a este como arete anterior
        entityManager.createQuery("UPDATE Ganado g SET g.areteAnterior = null WHERE g.areteAnterior.arete = :arete")
                .setParameter("arete", areteLimpio)
                .executeUpdate();

        ganadoRepository.delete(animal);
        ganadoRepository.flush();
    }
}

