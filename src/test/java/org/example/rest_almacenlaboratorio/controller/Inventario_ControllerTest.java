package org.example.rest_almacenlaboratorio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rest_almacenlaboratorio.Controller.Inventario_Controller;
import org.example.rest_almacenlaboratorio.DTOs.Inventario.*;
import org.example.rest_almacenlaboratorio.Service.Inventario_Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de controlador para Inventario
 * Valida los endpoints HTTP del procedimiento almacenado de inventario
 */
@WebMvcTest(Inventario_Controller.class)
class Inventario_ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private Inventario_Service inventarioService;

    private InventarioAlmacenCompletoDTO inventarioCompleto;
    private AlmacenInfoDTO almacenInfo;
    private InventarioDetalladoDTO inventarioDetallado1;
    private InventarioDetalladoDTO inventarioDetallado2;
    private ResumenAlmacenDTO resumenAlmacen;

    @BeforeEach
    void setUp() {
        // Configurar información del almacén
        almacenInfo = new AlmacenInfoDTO();
        almacenInfo.setIdAlmacen(1);
        almacenInfo.setNombreAlmacen("Almacén Central");
        almacenInfo.setDireccion("Calle Principal 123");
        almacenInfo.setTelefono("555-1234");

        // Configurar inventario detallado 1
        inventarioDetallado1 = new InventarioDetalladoDTO();
        inventarioDetallado1.setIdReactivo(1);
        inventarioDetallado1.setNombreReactivo("Ácido Sulfúrico");
        inventarioDetallado1.setMarca("Sigma-Aldrich");
        inventarioDetallado1.setNumeroLote(1);
        inventarioDetallado1.setCantidadInicialLote(100);
        inventarioDetallado1.setStockActual(150);

        // Configurar inventario detallado 2
        inventarioDetallado2 = new InventarioDetalladoDTO();
        inventarioDetallado2.setIdReactivo(2);
        inventarioDetallado2.setNombreReactivo("Ácido Clorhídrico");
        inventarioDetallado2.setMarca("Merck");
        inventarioDetallado2.setNumeroLote(2);
        inventarioDetallado2.setCantidadInicialLote(80);
        inventarioDetallado2.setStockActual(100);

        // Configurar resumen del almacén
        resumenAlmacen = new ResumenAlmacenDTO();
        resumenAlmacen.setTotalReactivosDistintos(2);
        resumenAlmacen.setTotalLotes(3);
        resumenAlmacen.setTotalUnidades(250);
        resumenAlmacen.setValorTotalInventario(5000.00);

        // Configurar inventario completo
        inventarioCompleto = new InventarioAlmacenCompletoDTO();
        inventarioCompleto.setAlmacenInfo(almacenInfo);
        inventarioCompleto.setInventarioDetallado(Arrays.asList(inventarioDetallado1, inventarioDetallado2));
        inventarioCompleto.setResumen(resumenAlmacen);
    }

    @Test
    void deberiaConsultarInventarioDetalladoSinFiltro() throws Exception {
        // Arrange
        when(inventarioService.consultarInventarioDetallado(eq(1), eq(null)))
                .thenReturn(inventarioCompleto);

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/inventario/almacen/1/detallado")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.almacenInfo.idAlmacen").value(1))
                .andExpect(jsonPath("$.almacenInfo.nombreAlmacen").value("Almacén Central"))
                .andExpect(jsonPath("$.almacenInfo.direccion").value("Calle Principal 123"))
                .andExpect(jsonPath("$.almacenInfo.telefono").value("555-1234"))
                .andExpect(jsonPath("$.inventarioDetallado", hasSize(2)))
                .andExpect(jsonPath("$.inventarioDetallado[0].idReactivo").value(1))
                .andExpect(jsonPath("$.inventarioDetallado[0].nombreReactivo").value("Ácido Sulfúrico"))
                .andExpect(jsonPath("$.inventarioDetallado[0].stockActual").value(150))
                .andExpect(jsonPath("$.inventarioDetallado[1].idReactivo").value(2))
                .andExpect(jsonPath("$.inventarioDetallado[1].nombreReactivo").value("Ácido Clorhídrico"))
                .andExpect(jsonPath("$.resumen.totalReactivosDistintos").value(2))
                .andExpect(jsonPath("$.resumen.totalLotes").value(3))
                .andExpect(jsonPath("$.resumen.totalUnidades").value(250));

        verify(inventarioService, times(1)).consultarInventarioDetallado(eq(1), eq(null));
    }

    @Test
    void deberiaConsultarInventarioDetalladoConFiltroNombre() throws Exception {
        // Arrange
        InventarioAlmacenCompletoDTO inventarioFiltrado = new InventarioAlmacenCompletoDTO();
        inventarioFiltrado.setAlmacenInfo(almacenInfo);
        inventarioFiltrado.setInventarioDetallado(Collections.singletonList(inventarioDetallado1));

        ResumenAlmacenDTO resumenFiltrado = new ResumenAlmacenDTO();
        resumenFiltrado.setTotalReactivosDistintos(1);
        resumenFiltrado.setTotalLotes(2);
        resumenFiltrado.setTotalUnidades(150);
        resumenFiltrado.setValorTotalInventario(3000.00);
        inventarioFiltrado.setResumen(resumenFiltrado);

        when(inventarioService.consultarInventarioDetallado(eq(1), eq("Ácido Sulfúrico")))
                .thenReturn(inventarioFiltrado);

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/inventario/almacen/1/detallado")
                        .param("nombreReactivo", "Ácido Sulfúrico")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.almacenInfo.idAlmacen").value(1))
                .andExpect(jsonPath("$.inventarioDetallado", hasSize(1)))
                .andExpect(jsonPath("$.inventarioDetallado[0].nombreReactivo").value("Ácido Sulfúrico"))
                .andExpect(jsonPath("$.resumen.totalReactivosDistintos").value(1));

        verify(inventarioService, times(1)).consultarInventarioDetallado(eq(1), eq("Ácido Sulfúrico"));
    }

    @Test
    void deberiaRetornar404CuandoAlmacenNoExiste() throws Exception {
        // Arrange
        InventarioAlmacenCompletoDTO inventarioVacio = new InventarioAlmacenCompletoDTO();
        inventarioVacio.setAlmacenInfo(null);
        inventarioVacio.setInventarioDetallado(Collections.emptyList());
        inventarioVacio.setResumen(new ResumenAlmacenDTO());

        when(inventarioService.consultarInventarioDetallado(eq(999), eq(null)))
                .thenReturn(inventarioVacio);

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/inventario/almacen/999/detallado")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("El almacén con ID 999 no existe."));

        verify(inventarioService, times(1)).consultarInventarioDetallado(eq(999), eq(null));
    }

    @Test
    void deberiaRetornar404CuandoServicioLanzaExcepcionNoExiste() throws Exception {
        // Arrange
        when(inventarioService.consultarInventarioDetallado(eq(888), eq(null)))
                .thenThrow(new RuntimeException("El almacén con ID 888 no existe"));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/inventario/almacen/888/detallado")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("El almacén con ID 888 no existe"));

        verify(inventarioService, times(1)).consultarInventarioDetallado(eq(888), eq(null));
    }

    @Test
    void deberiaRetornar500CuandoHayErrorInterno() throws Exception {
        // Arrange - Lanzar RuntimeException sin "no existe" en el mensaje
        when(inventarioService.consultarInventarioDetallado(eq(1), eq(null)))
                .thenThrow(new RuntimeException("Error de conexión a la base de datos"));

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/inventario/almacen/1/detallado")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("Error al consultar el inventario: Error de conexión a la base de datos"));


        verify(inventarioService, times(1)).consultarInventarioDetallado(eq(1), eq(null));
    }

    @Test
    void deberiaConsultarInventarioSinReactivos() throws Exception {
        // Arrange
        InventarioAlmacenCompletoDTO inventarioVacio = new InventarioAlmacenCompletoDTO();
        inventarioVacio.setAlmacenInfo(almacenInfo);
        inventarioVacio.setInventarioDetallado(Collections.emptyList());

        ResumenAlmacenDTO resumenVacio = new ResumenAlmacenDTO();
        resumenVacio.setTotalReactivosDistintos(0);
        resumenVacio.setTotalLotes(0);
        resumenVacio.setTotalUnidades(0);
        resumenVacio.setValorTotalInventario(0.00);
        inventarioVacio.setResumen(resumenVacio);

        when(inventarioService.consultarInventarioDetallado(eq(2), eq(null)))
                .thenReturn(inventarioVacio);

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/inventario/almacen/2/detallado")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.almacenInfo.idAlmacen").value(1))
                .andExpect(jsonPath("$.inventarioDetallado", hasSize(0)))
                .andExpect(jsonPath("$.resumen.totalReactivosDistintos").value(0))
                .andExpect(jsonPath("$.resumen.totalLotes").value(0));

        verify(inventarioService, times(1)).consultarInventarioDetallado(eq(2), eq(null));
    }

    @Test
    void deberiaConsultarConParametroNombreVacio() throws Exception {
        // Arrange
        when(inventarioService.consultarInventarioDetallado(eq(1), eq("")))
                .thenReturn(inventarioCompleto);

        // Act & Assert
        mockMvc.perform(get("/Rest_AlmacenLaboratorio/api/inventario/almacen/1/detallado")
                        .param("nombreReactivo", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventarioDetallado", hasSize(2)));

        verify(inventarioService, times(1)).consultarInventarioDetallado(eq(1), eq(""));
    }
}

