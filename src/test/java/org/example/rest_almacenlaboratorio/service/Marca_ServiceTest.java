package org.example.rest_almacenlaboratorio.service;

import org.example.rest_almacenlaboratorio.Mapper.Marca_Entity;
import org.example.rest_almacenlaboratorio.Repository.Marca_Repository;
import org.example.rest_almacenlaboratorio.Service.Marca_Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para el servicio de Marca
 * Valida la lógica de negocio sin depender de la base de datos
 */
@ExtendWith(MockitoExtension.class)
class Marca_ServiceTest {

    @Mock
    private Marca_Repository marcaRepository;

    @InjectMocks
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
    void deberiaObtenerTodasLasMarcas() {
        // Arrange
        when(marcaRepository.findAll()).thenReturn(Arrays.asList(marca1, marca2));

        // Act
        List<Marca_Entity> resultado = marcaService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Sigma-Aldrich", resultado.get(0).getNombre());
        assertEquals("Merck", resultado.get(1).getNombre());
        verify(marcaRepository, times(1)).findAll();
    }

    @Test
    void deberiaObtenerMarcaPorId() {
        // Arrange
        when(marcaRepository.findById(1)).thenReturn(Optional.of(marca1));

        // Act
        Optional<Marca_Entity> resultado = marcaService.obtenerPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Sigma-Aldrich", resultado.get().getNombre());
        assertEquals(1, resultado.get().getId());
        verify(marcaRepository, times(1)).findById(1);
    }

    @Test
    void deberiaRetornarVacioCuandoMarcaNoExiste() {
        // Arrange
        when(marcaRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Marca_Entity> resultado = marcaService.obtenerPorId(999);

        // Assert
        assertFalse(resultado.isPresent());
        verify(marcaRepository, times(1)).findById(999);
    }

    @Test
    void deberiaObtenerMarcasPorEstado() {
        // Arrange
        when(marcaRepository.findByEstado(true)).thenReturn(Arrays.asList(marca1));
        when(marcaRepository.findByEstado(false)).thenReturn(Arrays.asList(marca2));

        // Act
        List<Marca_Entity> marcasActivas = marcaService.obtenerPorEstado(true);
        List<Marca_Entity> marcasInactivas = marcaService.obtenerPorEstado(false);

        // Assert
        assertEquals(1, marcasActivas.size());
        assertTrue(marcasActivas.get(0).getEstado());

        assertEquals(1, marcasInactivas.size());
        assertFalse(marcasInactivas.get(0).getEstado());

        verify(marcaRepository, times(1)).findByEstado(true);
        verify(marcaRepository, times(1)).findByEstado(false);
    }

    @Test
    void deberiaObtenerMarcaPorNombre() {
        // Arrange
        when(marcaRepository.findByNombre("Sigma-Aldrich")).thenReturn(Optional.of(marca1));

        // Act
        Optional<Marca_Entity> resultado = marcaService.obtenerPorNombre("Sigma-Aldrich");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Sigma-Aldrich", resultado.get().getNombre());
        verify(marcaRepository, times(1)).findByNombre("Sigma-Aldrich");
    }

    @Test
    void deberiaCrearMarca() {
        // Arrange
        Marca_Entity nuevaMarca = new Marca_Entity();
        nuevaMarca.setNombre("Nueva Marca");
        nuevaMarca.setEstado(true);

        when(marcaRepository.save(any(Marca_Entity.class))).thenReturn(marca1);

        // Act
        Marca_Entity resultado = marcaService.crear(nuevaMarca);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        verify(marcaRepository, times(1)).save(nuevaMarca);
    }

    @Test
    void deberiaActualizarMarca() {
        // Arrange
        Marca_Entity marcaActualizada = new Marca_Entity();
        marcaActualizada.setNombre("Sigma Actualizado");
        marcaActualizada.setEstado(false);

        when(marcaRepository.findById(1)).thenReturn(Optional.of(marca1));
        when(marcaRepository.save(any(Marca_Entity.class))).thenReturn(marca1);

        // Act
        Marca_Entity resultado = marcaService.actualizar(1, marcaActualizada);

        // Assert
        assertNotNull(resultado);
        verify(marcaRepository, times(1)).findById(1);
        verify(marcaRepository, times(1)).save(any(Marca_Entity.class));
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarMarcaInexistente() {
        // Arrange
        Marca_Entity marcaActualizada = new Marca_Entity();
        marcaActualizada.setNombre("Actualizado");

        when(marcaRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            marcaService.actualizar(999, marcaActualizada);
        });

        assertEquals("Marca no encontrada", exception.getMessage());
        verify(marcaRepository, times(1)).findById(999);
        verify(marcaRepository, never()).save(any());
    }

    @Test
    void deberiaEliminarMarca() {
        // Arrange
        doNothing().when(marcaRepository).deleteById(1);

        // Act
        marcaService.eliminar(1);

        // Assert
        verify(marcaRepository, times(1)).deleteById(1);
    }

    @Test
    void deberiaVerificarSiExistePorNombre() {
        // Arrange
        when(marcaRepository.existsByNombre("Sigma-Aldrich")).thenReturn(true);
        when(marcaRepository.existsByNombre("Inexistente")).thenReturn(false);

        // Act
        boolean existe = marcaService.existePorNombre("Sigma-Aldrich");
        boolean noExiste = marcaService.existePorNombre("Inexistente");

        // Assert
        assertTrue(existe);
        assertFalse(noExiste);
        verify(marcaRepository, times(1)).existsByNombre("Sigma-Aldrich");
        verify(marcaRepository, times(1)).existsByNombre("Inexistente");
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayMarcas() {
        // Arrange
        when(marcaRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Marca_Entity> resultado = marcaService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(marcaRepository, times(1)).findAll();
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayMarcasConEstadoEspecifico() {
        // Arrange
        when(marcaRepository.findByEstado(true)).thenReturn(Arrays.asList());

        // Act
        List<Marca_Entity> resultado = marcaService.obtenerPorEstado(true);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(marcaRepository, times(1)).findByEstado(true);
    }

    @Test
    void deberiaActualizarSoloNombreDeMarca() {
        // Arrange
        Marca_Entity marcaConCambios = new Marca_Entity();
        marcaConCambios.setNombre("Nombre Actualizado");
        marcaConCambios.setEstado(marca1.getEstado());

        when(marcaRepository.findById(1)).thenReturn(Optional.of(marca1));
        when(marcaRepository.save(any(Marca_Entity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Marca_Entity resultado = marcaService.actualizar(1, marcaConCambios);

        // Assert
        assertEquals("Nombre Actualizado", resultado.getNombre());
        verify(marcaRepository, times(1)).save(any(Marca_Entity.class));
    }

    @Test
    void deberiaActualizarSoloEstadoDeMarca() {
        // Arrange
        Marca_Entity marcaConCambios = new Marca_Entity();
        marcaConCambios.setNombre(marca1.getNombre());
        marcaConCambios.setEstado(false);

        when(marcaRepository.findById(1)).thenReturn(Optional.of(marca1));
        when(marcaRepository.save(any(Marca_Entity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Marca_Entity resultado = marcaService.actualizar(1, marcaConCambios);

        // Assert
        assertFalse(resultado.getEstado());
        verify(marcaRepository, times(1)).save(any(Marca_Entity.class));
    }
}

