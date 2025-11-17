package org.example.rest_almacenlaboratorio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rest_almacenlaboratorio.Controller.Movimiento_Controller;
import org.example.rest_almacenlaboratorio.DTOs.Movimiento.*;
import org.example.rest_almacenlaboratorio.Mapper.Movimiento_Entity;
import org.example.rest_almacenlaboratorio.Mapper.Tipoaccion_Entity;
import org.example.rest_almacenlaboratorio.Mapper.Usuario_Entity;
import org.example.rest_almacenlaboratorio.Service.Movimiento_Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de controlador para Movimiento
 * Valida los endpoints HTTP, códigos de estado y procedimientos almacenados
 */
@WebMvcTest(Movimiento_Controller.class)
class Movimiento_ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private Movimiento_Service movimientoService;

    private Movimiento_Entity movimiento1;
    private Movimiento_Entity movimiento2;

    @BeforeEach
    void setUp() {
        Usuario_Entity usuario = new Usuario_Entity();
        usuario.setId(1);
        usuario.setNombre("Juan Pérez");

        Tipoaccion_Entity tipoAccion = new Tipoaccion_Entity();
        tipoAccion.setId(1);
        tipoAccion.setNombre("INGRESO");

        movimiento1 = new Movimiento_Entity();
        movimiento1.setId(1);
        movimiento1.setIdUsuario(usuario);
        movimiento1.setIdTipoAccion(tipoAccion);
        movimiento1.setReferencia("REF-001");
        movimiento1.setFecha(Instant.now());
        movimiento1.setComentario("Comentario 1");

        movimiento2 = new Movimiento_Entity();
        movimiento2.setId(2);
        movimiento2.setIdUsuario(usuario);
        movimiento2.setIdTipoAccion(tipoAccion);
        movimiento2.setReferencia("REF-002");
        movimiento2.setFecha(Instant.now());
        movimiento2.setComentario("Comentario 2");
    }

    // ========================================================================
    // TESTS CRUD BÁSICOS
    // ========================================================================

    @Test
    void deberiaObtenerTodosLosMovimientos() throws Exception {
        // Arrange
        when(movimientoService.obtenerTodos()).thenReturn(Arrays.asList(movimiento1, movimiento2));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].referencia").value("REF-001"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].referencia").value("REF-002"));

        verify(movimientoService, times(1)).obtenerTodos();
    }

    @Test
    void deberiaObtenerMovimientoPorId() throws Exception {
        // Arrange
        when(movimientoService.obtenerPorId(1)).thenReturn(Optional.of(movimiento1));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/movimientos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.referencia").value("REF-001"));

        verify(movimientoService, times(1)).obtenerPorId(1);
    }

    @Test
    void deberiaRetornar404CuandoMovimientoNoExiste() throws Exception {
        // Arrange
        when(movimientoService.obtenerPorId(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/movimientos/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(movimientoService, times(1)).obtenerPorId(999);
    }

    @Test
    void deberiaObtenerMovimientosPorUsuario() throws Exception {
        // Arrange
        when(movimientoService.obtenerPorUsuario(1)).thenReturn(Arrays.asList(movimiento1, movimiento2));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/movimientos/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(movimientoService, times(1)).obtenerPorUsuario(1);
    }

    @Test
    void deberiaObtenerMovimientosPorTipoAccion() throws Exception {
        // Arrange
        when(movimientoService.obtenerPorTipoAccion(1)).thenReturn(Arrays.asList(movimiento1));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/movimientos/tipo-accion/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(movimientoService, times(1)).obtenerPorTipoAccion(1);
    }

    @Test
    void deberiaObtenerMovimientosPorReferencia() throws Exception {
        // Arrange
        when(movimientoService.obtenerPorReferencia("REF-001")).thenReturn(Arrays.asList(movimiento1));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/movimientos/referencia/REF-001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].referencia").value("REF-001"));

        verify(movimientoService, times(1)).obtenerPorReferencia("REF-001");
    }

    @Test
    void deberiaCrearMovimiento() throws Exception {
        // Arrange
        when(movimientoService.crear(any(Movimiento_Entity.class))).thenReturn(movimiento1);

        // Act & Assert
        mockMvc.perform(post("/Rest_AlmacenLaboratorio/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimiento1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.referencia").value("REF-001"));

        verify(movimientoService, times(1)).crear(any(Movimiento_Entity.class));
    }

    @Test
    void deberiaActualizarMovimiento() throws Exception {
        // Arrange
        when(movimientoService.actualizar(eq(1), any(Movimiento_Entity.class))).thenReturn(movimiento1);

        // Act & Assert
        mockMvc.perform(put("/Rest_AlmacenLaboratorio/api/movimientos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimiento1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(movimientoService, times(1)).actualizar(eq(1), any(Movimiento_Entity.class));
    }

    @Test
    void deberiaRetornar404AlActualizarMovimientoInexistente() throws Exception {
        // Arrange
        when(movimientoService.actualizar(eq(999), any(Movimiento_Entity.class)))
                .thenThrow(new RuntimeException("Movimiento no encontrado"));

        // Act & Assert
        mockMvc.perform(put("/Rest_AlmacenLaboratorio/api/movimientos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimiento1)))
                .andExpect(status().isNotFound());

        verify(movimientoService, times(1)).actualizar(eq(999), any(Movimiento_Entity.class));
    }

    @Test
    void deberiaEliminarMovimiento() throws Exception {
        // Arrange
        doNothing().when(movimientoService).eliminar(1);

        // Act & Assert
        mockMvc.perform(delete("/Rest_AlmacenLaboratorio/api/movimientos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(movimientoService, times(1)).eliminar(1);
    }

    @Test
    void deberiaRetornar404AlEliminarMovimientoInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException()).when(movimientoService).eliminar(999);

        // Act & Assert
        mockMvc.perform(delete("/Rest_AlmacenLaboratorio/api/movimientos/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(movimientoService, times(1)).eliminar(999);
    }

    // ========================================================================
    // TESTS PROCEDIMIENTOS ALMACENADOS
    // ========================================================================

    @Test
    void deberiaRegistrarIngresoMultiple() throws Exception {
        // Arrange
        IngresoMultipleRequestDTO request = new IngresoMultipleRequestDTO();
        request.setIdUsuario(1);
        request.setIdProveedor(1);
        request.setIdAlmacenDestino(1);
        request.setReferencia("COMPRA-2025-001");
        request.setComentario("Compra mensual");

        LoteIngresoDTO lote1 = new LoteIngresoDTO();
        lote1.setIdReactivo(1);
        lote1.setCantidad(new BigDecimal("50"));
        lote1.setPrecioUnitario(new BigDecimal("45.5"));
        lote1.setFechaExpiracion(LocalDate.of(2026, 12, 31));

        request.setLotes(Arrays.asList(lote1));

        IngresoMultipleResponseDTO response = new IngresoMultipleResponseDTO();
        response.setIdMovimiento(15);
        response.setIdCompra(8);
        response.setTotalLotesRegistrados(1);
        response.setTotalUnidades(new BigDecimal("50"));
        response.setValorTotal(new BigDecimal("2275.00"));

        when(movimientoService.registrarIngresoMultiple(any(IngresoMultipleRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/Rest_AlmacenLaboratorio/api/movimientos/ingreso-multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMovimiento").value(15))
                .andExpect(jsonPath("$.idCompra").value(8))
                .andExpect(jsonPath("$.totalLotesRegistrados").value(1))
                .andExpect(jsonPath("$.totalUnidades").value(50));

        verify(movimientoService, times(1)).registrarIngresoMultiple(any(IngresoMultipleRequestDTO.class));
    }

    @Test
    void deberiaRetornar500CuandoFallaIngresoMultiple() throws Exception {
        // Arrange
        IngresoMultipleRequestDTO request = new IngresoMultipleRequestDTO();
        request.setIdUsuario(1);
        request.setIdProveedor(1);
        request.setIdAlmacenDestino(1);

        when(movimientoService.registrarIngresoMultiple(any(IngresoMultipleRequestDTO.class)))
                .thenThrow(new RuntimeException("Error en la base de datos"));

        // Act & Assert
        mockMvc.perform(post("/Rest_AlmacenLaboratorio/api/movimientos/ingreso-multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error al registrar ingreso múltiple"));

        verify(movimientoService, times(1)).registrarIngresoMultiple(any(IngresoMultipleRequestDTO.class));
    }

    @Test
    void deberiaRegistrarConsumoMultiple() throws Exception {
        // Arrange
        ConsumoMultipleRequestDTO request = new ConsumoMultipleRequestDTO();
        request.setIdUsuario(1);
        request.setIdDepartamento(3);
        request.setIdAlmacenOrigen(1);
        request.setReferencia("CONSUMO-2025-001");
        request.setComentario("Consumo para experimento");

        ConsumoItemDTO consumo1 = new ConsumoItemDTO();
        consumo1.setIdLote(5);
        consumo1.setCantidad(new BigDecimal("10"));

        request.setConsumos(Arrays.asList(consumo1));

        ConsumoMultipleResponseDTO response = new ConsumoMultipleResponseDTO();
        response.setIdMovimiento(16);
        response.setTotalLotesConsumidos(1);
        response.setTotalUnidadesConsumidas(new BigDecimal("10"));

        when(movimientoService.registrarConsumoMultiple(any(ConsumoMultipleRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/Rest_AlmacenLaboratorio/api/movimientos/consumo-multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMovimiento").value(16))
                .andExpect(jsonPath("$.totalLotesConsumidos").value(1))
                .andExpect(jsonPath("$.totalUnidadesConsumidas").value(10));

        verify(movimientoService, times(1)).registrarConsumoMultiple(any(ConsumoMultipleRequestDTO.class));
    }

    @Test
    void deberiaRegistrarTrasladoMultiple() throws Exception {
        // Arrange
        TrasladoMultipleRequestDTO request = new TrasladoMultipleRequestDTO();
        request.setIdUsuario(1);
        request.setIdAlmacenOrigen(1);
        request.setIdAlmacenDestino(2);
        request.setReferencia("TRASLADO-2025-001");
        request.setComentario("Reorganización");

        TrasladoItemDTO traslado1 = new TrasladoItemDTO();
        traslado1.setIdLote(3);
        traslado1.setCantidad(new BigDecimal("15"));

        request.setTraslados(Arrays.asList(traslado1));

        TrasladoMultipleResponseDTO response = new TrasladoMultipleResponseDTO();
        response.setIdMovimiento(17);
        response.setTotalLotesTrasladados(1);
        response.setTotalUnidades(new BigDecimal("15"));

        when(movimientoService.registrarTrasladoMultiple(any(TrasladoMultipleRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/Rest_AlmacenLaboratorio/api/movimientos/traslado-multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMovimiento").value(17))
                .andExpect(jsonPath("$.totalLotesTrasladados").value(1))
                .andExpect(jsonPath("$.totalUnidades").value(15));

        verify(movimientoService, times(1)).registrarTrasladoMultiple(any(TrasladoMultipleRequestDTO.class));
    }
}

