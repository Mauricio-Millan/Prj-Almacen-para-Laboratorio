package org.example.rest_almacenlaboratorio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rest_almacenlaboratorio.Controller.Reactivo_Controller;
import org.example.rest_almacenlaboratorio.Mapper.Reactivo_Entity;
import org.example.rest_almacenlaboratorio.Mapper.Marca_Entity;
import org.example.rest_almacenlaboratorio.Service.Reactivo_Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de controlador para Reactivo
 * Valida los endpoints HTTP, códigos de estado y operaciones de reactivos
 */
@WebMvcTest(Reactivo_Controller.class)
class Reactivo_ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private Reactivo_Service reactivoService;

    private Reactivo_Entity reactivo1;
    private Reactivo_Entity reactivo2;

    @BeforeEach
    void setUp() {
        Marca_Entity marca = new Marca_Entity();
        marca.setId(1);
        marca.setNombre("Sigma-Aldrich");

        reactivo1 = new Reactivo_Entity();
        reactivo1.setId(1);
        reactivo1.setNombre("Ácido Sulfúrico");
        reactivo1.setIdMarca(marca);

        reactivo2 = new Reactivo_Entity();
        reactivo2.setId(2);
        reactivo2.setNombre("Ácido Clorhídrico");
        reactivo2.setIdMarca(marca);
    }

    @Test
    void deberiaObtenerTodosLosReactivos() throws Exception {
        // Arrange
        when(reactivoService.obtenerTodos()).thenReturn(Arrays.asList(reactivo1, reactivo2));

        // Act & Assert
        mockMvc.perform(get("/api/reactivos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ácido Sulfúrico"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Ácido Clorhídrico"));

        verify(reactivoService, times(1)).obtenerTodos();
    }

    @Test
    void deberiaRetornar500CuandoFallaObtenerTodos() throws Exception {
        // Arrange
        when(reactivoService.obtenerTodos()).thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        mockMvc.perform(get("/api/reactivos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(reactivoService, times(1)).obtenerTodos();
    }

    @Test
    void deberiaObtenerReactivoPorId() throws Exception {
        // Arrange
        when(reactivoService.obtenerPorId(1)).thenReturn(Optional.of(reactivo1));

        // Act & Assert
        mockMvc.perform(get("/api/reactivos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ácido Sulfúrico"));

        verify(reactivoService, times(1)).obtenerPorId(1);
    }

    @Test
    void deberiaRetornar404CuandoReactivoNoExiste() throws Exception {
        // Arrange
        when(reactivoService.obtenerPorId(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/reactivos/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(reactivoService, times(1)).obtenerPorId(999);
    }

    @Test
    void deberiaObtenerReactivoPorNombre() throws Exception {
        // Arrange
        when(reactivoService.obtenerPorNombre("Ácido Sulfúrico")).thenReturn(Optional.of(reactivo1));

        // Act & Assert
        mockMvc.perform(get("/api/reactivos/nombre/Ácido Sulfúrico")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ácido Sulfúrico"));

        verify(reactivoService, times(1)).obtenerPorNombre("Ácido Sulfúrico");
    }

    @Test
    void deberiaRetornar404CuandoNombreNoExiste() throws Exception {
        // Arrange
        when(reactivoService.obtenerPorNombre("Inexistente")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/reactivos/nombre/Inexistente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(reactivoService, times(1)).obtenerPorNombre("Inexistente");
    }

    @Test
    void deberiaObtenerReactivosPorMarca() throws Exception {
        // Arrange
        when(reactivoService.obtenerPorMarca(1)).thenReturn(Arrays.asList(reactivo1, reactivo2));

        // Act & Assert
        mockMvc.perform(get("/api/reactivos/marca/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(reactivoService, times(1)).obtenerPorMarca(1);
    }

    @Test
    void deberiaCrearReactivo() throws Exception {
        // Arrange
        when(reactivoService.crear(any(Reactivo_Entity.class))).thenReturn(reactivo1);

        // Act & Assert
        mockMvc.perform(post("/api/reactivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactivo1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ácido Sulfúrico"));

        verify(reactivoService, times(1)).crear(any(Reactivo_Entity.class));
    }

    @Test
    void deberiaRetornar400CuandoFallaCreacion() throws Exception {
        // Arrange
        when(reactivoService.crear(any(Reactivo_Entity.class)))
                .thenThrow(new RuntimeException("El reactivo ya existe"));

        // Act & Assert
        mockMvc.perform(post("/api/reactivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactivo1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("El reactivo ya existe"));

        verify(reactivoService, times(1)).crear(any(Reactivo_Entity.class));
    }


    @Test
    void deberiaActualizarReactivo() throws Exception {
        // Arrange
        Reactivo_Entity reactivoActualizado = new Reactivo_Entity();
        reactivoActualizado.setId(1);
        reactivoActualizado.setNombre("Ácido Sulfúrico Actualizado");

        when(reactivoService.actualizar(eq(1), any(Reactivo_Entity.class))).thenReturn(reactivoActualizado);

        // Act & Assert
        mockMvc.perform(put("/api/reactivos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactivoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ácido Sulfúrico Actualizado"));

        verify(reactivoService, times(1)).actualizar(eq(1), any(Reactivo_Entity.class));
    }

    @Test
    void deberiaRetornar400AlActualizarConError() throws Exception {
        // Arrange
        when(reactivoService.actualizar(eq(1), any(Reactivo_Entity.class)))
                .thenThrow(new RuntimeException("Error de validación"));

        // Act & Assert
        mockMvc.perform(put("/api/reactivos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactivo1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Error de validación"));

        verify(reactivoService, times(1)).actualizar(eq(1), any(Reactivo_Entity.class));
    }

    @Test
    void deberiaEliminarReactivo() throws Exception {
        // Arrange
        doNothing().when(reactivoService).eliminar(1);

        // Act & Assert
        mockMvc.perform(delete("/api/reactivos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Reactivo eliminado exitosamente"));

        verify(reactivoService, times(1)).eliminar(1);
    }

    @Test
    void deberiaRetornar404AlEliminarReactivoInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Reactivo no encontrado")).when(reactivoService).eliminar(999);

        // Act & Assert
        mockMvc.perform(delete("/api/reactivos/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Reactivo no encontrado"));

        verify(reactivoService, times(1)).eliminar(999);
    }


    @Test
    void deberiaVerificarSiExisteReactivo() throws Exception {
        // Arrange
        when(reactivoService.existeReactivo(1)).thenReturn(true);
        when(reactivoService.existeReactivo(999)).thenReturn(false);

        // Act & Assert - Reactivo existente
        mockMvc.perform(get("/api/reactivos/existe/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // Act & Assert - Reactivo no existente
        mockMvc.perform(get("/api/reactivos/existe/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(reactivoService, times(1)).existeReactivo(1);
        verify(reactivoService, times(1)).existeReactivo(999);
    }

    @Test
    void deberiaRetornar500AlVerificarExistenciaConError() throws Exception {
        // Arrange
        when(reactivoService.existeReactivo(1)).thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        mockMvc.perform(get("/api/reactivos/existe/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(reactivoService, times(1)).existeReactivo(1);
    }
}

