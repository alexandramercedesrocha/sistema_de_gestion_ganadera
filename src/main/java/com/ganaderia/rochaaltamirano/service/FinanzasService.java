package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.dto.BalanceSociedadDTO;
import com.ganaderia.rochaaltamirano.model.ArqueoMensual;
import com.ganaderia.rochaaltamirano.model.Gasto;
import com.ganaderia.rochaaltamirano.model.Liquidacion;
import com.ganaderia.rochaaltamirano.repository.ArqueoMensualRepository;
import com.ganaderia.rochaaltamirano.repository.GastoRepository;
import com.ganaderia.rochaaltamirano.repository.LiquidacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Servicio Central para el Módulo de Finanzas, Liquidaciones de Mataderos,
 * Gastos Operativos y Cálculo del Balance de la Sociedad 50-50 directamente de MySQL.
 */
@Service
@Transactional(readOnly = true)
public class FinanzasService {

    private final LiquidacionRepository liquidacionRepository;
    private final GastoRepository gastoRepository;
    private final ArqueoMensualRepository arqueoMensualRepository;
    private final LoteService loteService;
    private final GanadoService ganadoService;

    public FinanzasService(LiquidacionRepository liquidacionRepository,
                           GastoRepository gastoRepository,
                           ArqueoMensualRepository arqueoMensualRepository,
                           LoteService loteService,
                           GanadoService ganadoService) {
        this.liquidacionRepository = liquidacionRepository;
        this.gastoRepository = gastoRepository;
        this.arqueoMensualRepository = arqueoMensualRepository;
        this.loteService = loteService;
        this.ganadoService = ganadoService;
    }

    // --- LIQUIDACIONES (Desde MySQL) ---
    public List<Liquidacion> listarLiquidaciones() {
        return liquidacionRepository.findAll();
    }

    @Transactional
    public Liquidacion registrarLiquidacion(Liquidacion liquidacion) {
        return liquidacionRepository.save(liquidacion);
    }

    public BigDecimal calcularTotalIngresosLiquidaciones() {
        return liquidacionRepository.findAll().stream()
                .map(l -> l.getMonto() != null ? l.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- GASTOS OPERATIVOS (Desde MySQL) ---
    public List<Gasto> listarGastos() {
        return gastoRepository.findAll();
    }

    @Transactional
    public Gasto registrarGasto(Gasto gasto) {
        return gastoRepository.save(gasto);
    }

    public BigDecimal calcularTotalGastosOperativos() {
        return gastoRepository.findAll().stream()
                .map(g -> g.getMonto() != null ? g.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- ARQUEOS MENSUALES (Desde MySQL) ---
    public List<ArqueoMensual> listarArqueos() {
        return arqueoMensualRepository.findAll();
    }

    @Transactional
    public ArqueoMensual registrarArqueo(ArqueoMensual arqueo) {
        return arqueoMensualRepository.save(arqueo);
    }

    public Optional<ArqueoMensual> buscarArqueoPorId(Integer id) {
        return arqueoMensualRepository.findById(id);
    }

    public Optional<ArqueoMensual> buscarArqueoPorId(Long id) {
        return id != null ? arqueoMensualRepository.findById(id.intValue()) : Optional.empty();
    }

    // --- BALANCE CONSOLIDADO SOCIEDAD (50% WILBER ROCHA - 50% YADER ALTAMIRANO) ---
    public BalanceSociedadDTO generarBalanceConsolidado() {
        BalanceSociedadDTO balance = new BalanceSociedadDTO();

        // 1. Ingresos Reales desde MySQL (Liquidaciones)
        BigDecimal ingresos = calcularTotalIngresosLiquidaciones();
        balance.setTotalLiquidaciones(ingresos);

        // 2. Egresos Reales desde MySQL (Lotes de Compra + Gastos Operativos)
        BigDecimal compras = loteService.calcularTotalInvertidoEstimado();
        BigDecimal fletes = BigDecimal.ZERO; // Los fletes registrados en gastos se computan en gastos
        BigDecimal gastos = calcularTotalGastosOperativos();

        balance.setTotalCompras(compras);
        balance.setTotalTransporte(fletes);
        balance.setTotalGastos(gastos);

        // Egresos Totales
        BigDecimal totalEgresos = compras.add(fletes).add(gastos);
        balance.setTotalEgresos(totalEgresos);

        // 3. Utilidad Neta (Ingresos - Egresos)
        BigDecimal utilidadNeta = ingresos.subtract(totalEgresos);
        balance.setUtilidadNeta(utilidadNeta);

        // 4. Distribución Societaria 50-50
        BigDecimal mitad = utilidadNeta.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        balance.setCuotaWilberRocha(mitad);
        balance.setCuotaYaderAltamirano(mitad);

        // 5. Estadísticas de inventario desde MySQL
        balance.setTotalCabezasEnFinca(ganadoService.contarCabezasEnFinca());
        balance.setTotalKilosEnFinca(ganadoService.calcularTotalKilosEnFinca());

        return balance;
    }
}
