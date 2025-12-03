package org.example.rest_almacenlaboratorio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rest_almacenlaboratorio.Controller.Marca_Controller;
import org.example.rest_almacenlaboratorio.Mapper.Marca_Entity;
import org.example.rest_almacenlaboratorio.Service.Marca_Service;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.jupiter.api.Test;

/**
 * Tests de controlador para Marca
 * Valida los endpoints HTTP, códigos de estado y serialización JSON
 */
@WebMvcTest(Marca_Controller.class)
class Marca_ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private Marca_Service marcaService;

    private Marca_Entity marca1;
    private Marca_Entity marca2;

    @BeforeEach
    void setUp() {
        marca1 = new Marca_Entity();
        marca1.setId(1);
        marca1.setNombre("Sigma-Aldrich");
        marca1.setEstado(true);

        marca2 = new Marca_Entity();
        marca2.setId(2);
        marca2.setNombre("Merck");
        marca2.setEstado(false);
    }

    @Test
    void deberiaObtenerTodasLasMarcas() throws Exception {
        // Arrange
        when(marcaService.obtenerTodos()).thenReturn(Arrays.asList(marca1, marca2));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/marcas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Sigma-Aldrich"))
                .andExpect(jsonPath("$[0].estado").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Merck"))
                .andExpect(jsonPath("$[1].estado").value(false));

        verify(marcaService, times(1)).obtenerTodos();
    }

    @Test
    void deberiaObtenerMarcaPorId() throws Exception {
        // Arrange
        when(marcaService.obtenerPorId(1)).thenReturn(Optional.of(marca1));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/marcas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Sigma-Aldrich"))
                .andExpect(jsonPath("$.estado").value(true));

        verify(marcaService, times(1)).obtenerPorId(1);
    }

    @Test
    void deberiaRetornar404CuandoMarcaNoExiste() throws Exception {
        // Arrange
        when(marcaService.obtenerPorId(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/marcas/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(marcaService, times(1)).obtenerPorId(999);
    }

    @Test
    void deberiaObtenerMarcasPorEstado() throws Exception {
        // Arrange
        when(marcaService.obtenerPorEstado(true)).thenReturn(Arrays.asList(marca1));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/marcas/estado/true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value("Sigma-Aldrich"))
                .andExpect(jsonPath("$[0].estado").value(true));

        verify(marcaService, times(1)).obtenerPorEstado(true);
    }

    @Test
    void deberiaObtenerMarcaPorNombre() throws Exception {
        // Arrange
        when(marcaService.obtenerPorNombre("Sigma-Aldrich")).thenReturn(Optional.of(marca1));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/marcas/nombre/Sigma-Aldrich")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Sigma-Aldrich"));

        verify(marcaService, times(1)).obtenerPorNombre("Sigma-Aldrich");
    }

    @Test
    void deberiaRetornar404CuandoNombreNoExiste() throws Exception {
        // Arrange
        when(marcaService.obtenerPorNombre("Inexistente")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/marcas/nombre/Inexistente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(marcaService, times(1)).obtenerPorNombre("Inexistente");
    }

    @Test
    void deberiaCrearMarca() throws Exception {
        // Arrange
        Marca_Entity nuevaMarca = new Marca_Entity();
        nuevaMarca.setNombre("Nueva Marca");
        nuevaMarca.setEstado(true);

        when(marcaService.existePorNombre("Nueva Marca")).thenReturn(false);
        when(marcaService.crear(any(Marca_Entity.class))).thenReturn(marca1);

        // Act & Assert
        mockMvc.perform(post("/Rest_AlmacenLaboratorio/api/marcas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaMarca)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Sigma-Aldrich"));

        verify(marcaService, times(1)).existePorNombre("Nueva Marca");
        verify(marcaService, times(1)).crear(any(Marca_Entity.class));
    }

    @Test
    void deberiaRetornar409CuandoMarcaYaExiste() throws Exception {
        // Arrange
        Marca_Entity marcaDuplicada = new Marca_Entity();
        marcaDuplicada.setNombre("Sigma-Aldrich");
        marcaDuplicada.setEstado(true);

        when(marcaService.existePorNombre("Sigma-Aldrich")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/Rest_AlmacenLaboratorio/api/marcas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(marcaDuplicada)))
                .andExpect(status().isConflict());

        verify(marcaService, times(1)).existePorNombre("Sigma-Aldrich");
        verify(marcaService, never()).crear(any());
    }

    @Test
    void deberiaActualizarMarca() throws Exception {
        // Arrange
        Marca_Entity marcaActualizada = new Marca_Entity();
        marcaActualizada.setId(1);
        marcaActualizada.setNombre("Sigma Actualizado");
        marcaActualizada.setEstado(false);

        when(marcaService.actualizar(eq(1), any(Marca_Entity.class))).thenReturn(marcaActualizada);

        // Act & Assert
        mockMvc.perform(put("/Rest_AlmacenLaboratorio/api/marcas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(marcaActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Sigma Actualizado"))
                .andExpect(jsonPath("$.estado").value(false));

        verify(marcaService, times(1)).actualizar(eq(1), any(Marca_Entity.class));
    }

    @Test
    void deberiaRetornar404AlActualizarMarcaInexistente() throws Exception {
        // Arrange
        Marca_Entity marcaActualizada = new Marca_Entity();
        marcaActualizada.setNombre("Actualizado");

        when(marcaService.actualizar(eq(999), any(Marca_Entity.class)))
                .thenThrow(new RuntimeException("Marca no encontrada"));

        // Act & Assert
        mockMvc.perform(put("/Rest_AlmacenLaboratorio/api/marcas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(marcaActualizada)))
                .andExpect(status().isNotFound());

        verify(marcaService, times(1)).actualizar(eq(999), any(Marca_Entity.class));
    }

    @Test
    void deberiaEliminarMarca() throws Exception {
        // Arrange
        doNothing().when(marcaService).eliminar(1);

        // Act & Assert
        mockMvc.perform(delete("/Rest_AlmacenLaboratorio/api/marcas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(marcaService, times(1)).eliminar(1);
    }

    @Test
    void deberiaRetornar404AlEliminarMarcaInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException()).when(marcaService).eliminar(999);

        // Act & Assert
        mockMvc.perform(delete("/Rest_AlmacenLaboratorio/api/marcas/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(marcaService, times(1)).eliminar(999);
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayMarcas() throws Exception {
        // Arrange
        when(marcaService.obtenerTodos()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/marcas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(marcaService, times(1)).obtenerTodos();
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayMarcasConEstado() throws Exception {
        // Arrange
        when(marcaService.obtenerPorEstado(false)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/marcas/estado/false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(marcaService, times(1)).obtenerPorEstado(false);
    }

    @Test
    void deberiaRetornar400CuandoHayErrorAlCrear() throws Exception {
        // Arrange
        Marca_Entity marcaInvalida = new Marca_Entity();
        marcaInvalida.setNombre("Test");

        when(marcaService.existePorNombre("Test")).thenReturn(false);
        when(marcaService.crear(any(Marca_Entity.class))).thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        mockMvc.perform(post("/Rest_AlmacenLaboratorio/api/marcas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(marcaInvalida)))
                .andExpect(status().isBadRequest());

        verify(marcaService, times(1)).crear(any(Marca_Entity.class));
    }
}

