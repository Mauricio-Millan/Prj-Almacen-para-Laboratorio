package org.example.rest_almacenlaboratorio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rest_almacenlaboratorio.Controller.Lote_Controller;
import org.example.rest_almacenlaboratorio.Mapper.Lote_Entity;
import org.example.rest_almacenlaboratorio.Mapper.Reactivo_Entity;
import org.example.rest_almacenlaboratorio.Mapper.Compra_Entity;
import org.example.rest_almacenlaboratorio.Service.Lote_Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de controlador para Lote
 * Valida los endpoints HTTP, códigos de estado y operaciones de lotes
 */
@WebMvcTest(Lote_Controller.class)
class Lote_ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private Lote_Service loteService;

    private Lote_Entity lote1;
    private Lote_Entity lote2;
    private SimpleDateFormat dateFormatter;

    @BeforeEach
    void setUp() throws Exception {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Reactivo_Entity reactivo = new Reactivo_Entity();
        reactivo.setId(1);
        reactivo.setNombre("Ácido Sulfúrico");

        Compra_Entity compra = new Compra_Entity();
        compra.setId(1);

        lote1 = new Lote_Entity();
        lote1.setId(1);
        lote1.setIdReactivo(reactivo);
        lote1.setIdCompra(compra);
        lote1.setCantidadInicial(new BigDecimal("100"));
        lote1.setPrecioUnitario(new BigDecimal("25.50"));
        lote1.setFechaExpiracion(dateFormatter.parse("2026-12-31"));
        lote1.setEstado(true);

        lote2 = new Lote_Entity();
        lote2.setId(2);
        lote2.setIdReactivo(reactivo);
        lote2.setIdCompra(compra);
        lote2.setCantidadInicial(new BigDecimal("50"));
        lote2.setPrecioUnitario(new BigDecimal("30.00"));
        lote2.setFechaExpiracion(dateFormatter.parse("2027-06-30"));
        lote2.setEstado(true);
    }

    @Test
    void deberiaObtenerTodosLosLotes() throws Exception {
        // Arrange
        when(loteService.obtenerTodos()).thenReturn(Arrays.asList(lote1, lote2));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cantidadInicial").value(100))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].cantidadInicial").value(50));

        verify(loteService, times(1)).obtenerTodos();
    }

    @Test
    void deberiaObtenerLotePorId() throws Exception {
        // Arrange
        when(loteService.obtenerPorId(1)).thenReturn(Optional.of(lote1));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cantidadInicial").value(100));

        verify(loteService, times(1)).obtenerPorId(1);
    }

    @Test
    void deberiaRetornar404CuandoLoteNoExiste() throws Exception {
        // Arrange
        when(loteService.obtenerPorId(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(loteService, times(1)).obtenerPorId(999);
    }

    @Test
    void deberiaObtenerLotesPorEstado() throws Exception {
        // Arrange
        when(loteService.obtenerPorEstado(true)).thenReturn(Arrays.asList(lote1, lote2));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes/estado/true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].estado").value(true))
                .andExpect(jsonPath("$[1].estado").value(true));

        verify(loteService, times(1)).obtenerPorEstado(true);
    }

    @Test
    void deberiaObtenerLotesPorReactivo() throws Exception {
        // Arrange
        when(loteService.obtenerPorReactivo(1)).thenReturn(Arrays.asList(lote1, lote2));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes/reactivo/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(loteService, times(1)).obtenerPorReactivo(1);
    }

    @Test
    void deberiaObtenerLotesPorCompra() throws Exception {
        // Arrange
        when(loteService.obtenerPorCompra(1)).thenReturn(Arrays.asList(lote1, lote2));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes/compra/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(loteService, times(1)).obtenerPorCompra(1);
    }

    @Test
    void deberiaObtenerLotesProximosAVencer() throws Exception {
        // Arrange
        when(loteService.obtenerLotesProximosAVencer(any(Date.class))).thenReturn(Arrays.asList(lote1));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes/proximos-vencer")
                        .param("fecha", "2027-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(loteService, times(1)).obtenerLotesProximosAVencer(any(Date.class));
    }

    @Test
    void deberiaRetornar400CuandoFechaEsInvalida() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes/proximos-vencer")
                        .param("fecha", "fecha-invalida")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(loteService, never()).obtenerLotesProximosAVencer(any(Date.class));
    }

    @Test
    void deberiaObtenerLotesPorRangoExpiracion() throws Exception {
        // Arrange
        when(loteService.obtenerLotesPorRangoExpiracion(any(Date.class), any(Date.class)))
                .thenReturn(Arrays.asList(lote1, lote2));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes/rango-expiracion")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2027-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(loteService, times(1)).obtenerLotesPorRangoExpiracion(any(Date.class), any(Date.class));
    }

    @Test
    void deberiaCrearLote() throws Exception {
        // Arrange
        when(loteService.crear(any(Lote_Entity.class))).thenReturn(lote1);

        // Act & Assert
        mockMvc.perform(post("/Rest_AlmacenLaboratorio/api/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lote1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cantidadInicial").value(100));

        verify(loteService, times(1)).crear(any(Lote_Entity.class));
    }

    @Test
    void deberiaRetornar400CuandoFallaCreacion() throws Exception {
        // Arrange
        when(loteService.crear(any(Lote_Entity.class)))
                .thenThrow(new RuntimeException("Error al crear lote"));

        // Act & Assert
        mockMvc.perform(post("/Rest_AlmacenLaboratorio/api/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lote1)))
                .andExpect(status().isBadRequest());

        verify(loteService, times(1)).crear(any(Lote_Entity.class));
    }

    @Test
    void deberiaActualizarLote() throws Exception {
        // Arrange
        Lote_Entity loteActualizado = new Lote_Entity();
        loteActualizado.setId(1);
        loteActualizado.setCantidadInicial(new BigDecimal("150"));

        when(loteService.actualizar(eq(1), any(Lote_Entity.class))).thenReturn(loteActualizado);

        // Act & Assert
        mockMvc.perform(put("/Rest_AlmacenLaboratorio/api/lotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loteActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cantidadInicial").value(150));

        verify(loteService, times(1)).actualizar(eq(1), any(Lote_Entity.class));
    }

    @Test
    void deberiaRetornar404AlActualizarLoteInexistente() throws Exception {
        // Arrange
        when(loteService.actualizar(eq(999), any(Lote_Entity.class)))
                .thenThrow(new RuntimeException("Lote no encontrado"));

        // Act & Assert
        mockMvc.perform(put("/Rest_AlmacenLaboratorio/api/lotes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lote1)))
                .andExpect(status().isNotFound());

        verify(loteService, times(1)).actualizar(eq(999), any(Lote_Entity.class));
    }

    @Test
    void deberiaEliminarLote() throws Exception {
        // Arrange
        doNothing().when(loteService).eliminar(1);

        // Act & Assert
        mockMvc.perform(delete("/Rest_AlmacenLaboratorio/api/lotes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(loteService, times(1)).eliminar(1);
    }

    @Test
    void deberiaRetornar404AlEliminarLoteInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException()).when(loteService).eliminar(999);

        // Act & Assert
        mockMvc.perform(delete("/Rest_AlmacenLaboratorio/api/lotes/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(loteService, times(1)).eliminar(999);
    }

    @Test
    void deberiaDesactivarLote() throws Exception {
        // Arrange
        Lote_Entity loteDesactivado = new Lote_Entity();
        loteDesactivado.setId(1);
        loteDesactivado.setEstado(false);

        when(loteService.desactivarLote(1)).thenReturn(loteDesactivado);

        // Act & Assert
        mockMvc.perform(patch("/Rest_AlmacenLaboratorio/api/lotes/1/desactivar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value(false));

        verify(loteService, times(1)).desactivarLote(1);
    }

    @Test
    void deberiaRetornar404AlDesactivarLoteInexistente() throws Exception {
        // Arrange
        when(loteService.desactivarLote(999))
                .thenThrow(new RuntimeException("Lote no encontrado"));

        // Act & Assert
        mockMvc.perform(patch("/Rest_AlmacenLaboratorio/api/lotes/999/desactivar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(loteService, times(1)).desactivarLote(999);
    }

    @Test
    void deberiaVerificarSiExisteLote() throws Exception {
        // Arrange
        when(loteService.existeLote(1)).thenReturn(true);
        when(loteService.existeLote(999)).thenReturn(false);

        // Act & Assert - Lote existente
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes/1/existe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // Act & Assert - Lote no existente
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/lotes/999/existe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(loteService, times(1)).existeLote(1);
        verify(loteService, times(1)).existeLote(999);
    }
}

