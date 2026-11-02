package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.dto.BalanceSociedadDTO;
import com.ganaderia.rochaaltamirano.model.ArqueoMensual;
import com.ganaderia.rochaaltamirano.model.Gasto;
import com.ganaderia.rochaaltamirano.model.Liquidacion;
import com.ganaderia.rochaaltamirano.repository.ArqueoMensualRepository;
import com.ganaderia.rochaaltamirano.repository.GastoRepository;
import com.ganaderia.rochaaltamirano.repository.LiquidacionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio Financiero y de Liquidaciones de la Sociedad Rocha & Altamirano
 * 
 * Gestiona ingresos por matadero, gastos de finca, arqueos y 
 * la división proporcional estricta del 50% para Wilber Rocha y 50% para Yader Altamirano.
 */
@Service
public class FinanzasService {

    private final LiquidacionRepository liquidacionRepository;
    private final GastoRepository gastoRepository;
    private final ArqueoMensualRepository arqueoMensualRepository;
    private final CompraService compraService;
    private final TransporteService transporteService;
    private final GanadoService ganadoService;

    public FinanzasService(LiquidacionRepository liquidacionRepository,
                           GastoRepository gastoRepository,
                           ArqueoMensualRepository arqueoMensualRepository,
                           CompraService compraService,
                           TransporteService transporteService,
                           GanadoService ganadoService) {
        this.liquidacionRepository = liquidacionRepository;
        this.gastoRepository = gastoRepository;
        this.arqueoMensualRepository = arqueoMensualRepository;
        this.compraService = compraService;
        this.transporteService = transporteService;
        this.ganadoService = ganadoService;
    }

    // --- LIQUIDACIONES ---
    public List<Liquidacion> listarLiquidaciones() {
        return liquidacionRepository.listarTodas();
    }

    public Liquidacion registrarLiquidacion(Liquidacion liquidacion) {
        return liquidacionRepository.guardar(liquidacion);
    }

    public BigDecimal calcularTotalIngresosLiquidaciones() {
        return liquidacionRepository.listarTodas().stream()
                .map(l -> l.getMonto() != null ? l.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- GASTOS ---
    public List<Gasto> listarGastos() {
        return gastoRepository.listarTodos();
    }

    public Gasto registrarGasto(Gasto gasto) {
        return gastoRepository.guardar(gasto);
    }

    public BigDecimal calcularTotalGastosOperativos() {
        return gastoRepository.listarTodos().stream()
                .map(g -> g.getMonto() != null ? g.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- ARQUEOS MENSUALES ---
    public List<ArqueoMensual> listarArqueos() {
        return arqueoMensualRepository.listarTodos();
    }

    public ArqueoMensual registrarArqueo(ArqueoMensual arqueo) {
        return arqueoMensualRepository.guardar(arqueo);
    }

    public Optional<ArqueoMensual> buscarArqueoPorId(Long id) {
        return arqueoMensualRepository.buscarPorId(id);
    }

    // --- BALANCE CONSOLIDADO SOCIEDAD (50% WILBER ROCHA - 50% YADER ALTAMIRANO) ---
    public BalanceSociedadDTO generarBalanceConsolidado() {
        BalanceSociedadDTO balance = new BalanceSociedadDTO();

        // 1. Ingresos
        BigDecimal ingresos = calcularTotalIngresosLiquidaciones();
        balance.setTotalLiquidaciones(ingresos);

        // 2. Egresos
        BigDecimal compras = compraService.calcularTotalInvertido();
        BigDecimal fletes = transporteService.calcularTotalCostosTransporte();
        BigDecimal gastosVarios = calcularTotalGastosOperativos();

        balance.setTotalCompras(compras);
        balance.setTotalTransporte(fletes);
        balance.setTotalGastosVarios(gastosVarios);

        // 3. Totales y reparto 50-50
        balance.calcularTotales();

        // 4. Métricas de Ganado
        balance.setTotalCabezasActivas(ganadoService.listarInventarioCompleto().size());
        balance.setCabezasEnFinca((int) ganadoService.contarCabezasEnFinca());
        balance.setCabezasEntregadas((int) ganadoService.contarCabezasEntregadas());
        balance.setCabezasRechazo((int) ganadoService.contarRechazos());
        balance.setTotalKilosEnFinca(ganadoService.calcularTotalKilosEnFinca());

        return balance;
    }
}
