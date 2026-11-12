package com.ganaderia.rochaaltamirano.service;

import com.ganaderia.rochaaltamirano.dto.HistorialBovinoDTO;
import com.ganaderia.rochaaltamirano.model.*;
import com.ganaderia.rochaaltamirano.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GanadoTrazabilidadTest {

    @Mock
    private GanadoRepository ganadoRepository;

    @Mock
    private GanadoRechazadoRepository ganadoRechazadoRepository;

    @Mock
    private DetalleEntregaRepository detalleEntregaRepository;

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private GanadoService ganadoService;

    private Ganado resOriginal;
    private Lote lote;

    @BeforeEach
    void setUp() {
        lote = new Lote();
        lote.setIdLote(1);
        lote.setFechaCompra(LocalDate.of(2026, 1, 10));

        resOriginal = new Ganado();
        resOriginal.setArete("01213 3778");
        resOriginal.setColor("Hosco");
        resOriginal.setPaletas("2");
        resOriginal.setPesoKg(new BigDecimal("380.00"));
        resOriginal.setCondicionCompra("Bueno");
        resOriginal.setLote(lote);
        resOriginal.setEstado("En Finca");
        resOriginal.setCategoria("Rechazo");
    }

    @Test
    @DisplayName("Debe re-aretar exitosamente una res rechazada vinculando arete_anterior")
    void testRearetarGanadoExitoso() {
        String nuevoArete = "01213 9999";
        BigDecimal nuevoPeso = new BigDecimal("395.50");

        when(ganadoRepository.existeAreteEnBaseDatos(anyString(), anyString())).thenReturn(false);
        when(ganadoRepository.findById("01213 3778")).thenReturn(Optional.of(resOriginal));
        when(ganadoRepository.save(any(Ganado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ganado rearetada = ganadoService.rearetarGanado("01213 3778", nuevoArete, nuevoPeso, 1);

        assertNotNull(rearetada);
        assertEquals("01213 9999", rearetada.getArete());
        assertEquals(nuevoPeso, rearetada.getPesoKg());
        assertEquals("Bueno", rearetada.getCondicionCompra());
        assertEquals("Apto", rearetada.getCategoria());
        assertEquals("En Finca", rearetada.getEstado());

        // Verificar el enlace de trazabilidad: arete_anterior apunta a la res original
        assertNotNull(rearetada.getAreteAnterior());
        assertEquals("01213 3778", rearetada.getAreteAnterior().getArete());

        // Verificar que la res original queda marcada como Histórico
        assertEquals("Re-aretado (Histórico)", resOriginal.getEstado());
        assertEquals("Re-aretado", resOriginal.getCategoria());

        verify(ganadoRepository, times(2)).save(any(Ganado.class));
    }

    @Test
    @DisplayName("Debe rechazar re-aretado si el nuevo arete ya existe en el sistema")
    void testRearetarAreteDuplicadoFalla() {
        when(ganadoRepository.existeAreteEnBaseDatos(anyString(), anyString())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            ganadoService.rearetarGanado("01213 3778", "01213 9999", new BigDecimal("400.00"), 1);
        });

        assertTrue(ex.getMessage().contains("ya existe registrado"));
        verify(ganadoRepository, never()).save(any(Ganado.class));
    }

    @Test
    @DisplayName("Debe obtener el historial completo del bovino reconstruyendo la cadena de aretes y entregas")
    void testObtenerHistorialBovino() {
        Ganado nuevaRes = new Ganado();
        nuevaRes.setArete("01213 9999");
        nuevaRes.setAreteAnterior(resOriginal);
        nuevaRes.setPesoKg(new BigDecimal("395.00"));
        nuevaRes.setColor("Hosco");
        nuevaRes.setPaletas("2");
        nuevaRes.setCondicionCompra("Bueno");
        nuevaRes.setLote(lote);

        Entrega entrega = new Entrega();
        entrega.setIdEntrega(2);
        entrega.setFechaEntrega(LocalDate.of(2026, 2, 1));
        Matadero matadero = new Matadero();
        matadero.setNombreMatadero("Matadero San Martín");
        entrega.setMatadero(matadero);

        DetalleEntrega detalle = new DetalleEntrega();
        detalle.setIdDetalleEntrega(104);
        detalle.setEntrega(entrega);
        detalle.setGanado(resOriginal);
        detalle.setPesoMataderoKg(new BigDecimal("370.00"));
        detalle.setEstadoDictamen("Rechazado - Devuelto");

        GanadoRechazado gr = new GanadoRechazado();
        gr.setMotivoRechazo("Golpe severo en lomo");
        detalle.getRechazos().add(gr);

        when(ganadoRepository.findById("01213 9999")).thenReturn(Optional.of(nuevaRes));
        when(ganadoRepository.findFirstByAreteAnteriorArete("01213 9999")).thenReturn(Optional.empty());
        when(detalleEntregaRepository.findHistorialPorAretes(anyList())).thenReturn(List.of(detalle));

        HistorialBovinoDTO historial = ganadoService.obtenerHistorialBovino("01213 9999");

        assertNotNull(historial);
        assertEquals("01213 9999", historial.getArete());
        assertTrue(historial.isTieneAreteAnterior());
        assertEquals("01213 3778", historial.getAreteAnterior());

        // Cadena de trazabilidad
        assertNotNull(historial.getCadenaTrazabilidad());
        assertTrue(historial.getCadenaTrazabilidad().size() >= 2);

        // Despachos registrados en matadero
        assertEquals(1, historial.getEntregas().size());
        assertEquals("Matadero San Martín", historial.getEntregas().get(0).getNombreMatadero());
        assertEquals("Rechazado - Devuelto", historial.getEntregas().get(0).getDictamen());
        assertTrue(historial.getEntregas().get(0).isEsDevuelto());
        assertEquals("Golpe severo en lomo", historial.getEntregas().get(0).getMotivoRechazo());
    }
}

