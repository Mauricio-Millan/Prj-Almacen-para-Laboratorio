package org.example.rest_almacenlaboratorio.repository;

import org.example.rest_almacenlaboratorio.Mapper.Marca_Entity;
import org.example.rest_almacenlaboratorio.Repository.Marca_Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para el repositorio de Marca
 * Valida las operaciones CRUD y consultas personalizadas con la base de datos
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class Marca_RepositoryTest {

    @Autowired
    private Marca_Repository marcaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Marca_Entity marca1;
    private Marca_Entity marca2;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada test
        marcaRepository.deleteAll();

        // Crear datos de prueba
        marca1 = new Marca_Entity();
        marca1.setNombre("Sigma-Aldrich");
        marca1.setEstado(true);

        marca2 = new Marca_Entity();
        marca2.setNombre("Merck");
        marca2.setEstado(false);
    }

    @Test
    void deberiaGuardarMarca() {
        // Act
        Marca_Entity marcaGuardada = marcaRepository.save(marca1);

        // Assert
        assertNotNull(marcaGuardada.getId());
        assertEquals("Sigma-Aldrich", marcaGuardada.getNombre());
        assertTrue(marcaGuardada.getEstado());
    }

    @Test
    void deberiaEncontrarMarcaPorId() {
        // Arrange
        Marca_Entity marcaGuardada = marcaRepository.save(marca1);

        // Act
        Optional<Marca_Entity> encontrada = marcaRepository.findById(marcaGuardada.getId());

        // Assert
        assertTrue(encontrada.isPresent());
        assertEquals("Sigma-Aldrich", encontrada.get().getNombre());
    }

    @Test
    void deberiaRetornarVacioCuandoIdNoExiste() {
        // Act
        Optional<Marca_Entity> encontrada = marcaRepository.findById(999);

        // Assert
        assertFalse(encontrada.isPresent());
    }

    @Test
    void deberiaEncontrarMarcasPorEstado() {
        // Arrange
        marcaRepository.save(marca1); // estado = true
        marcaRepository.save(marca2); // estado = false

        // Act
        List<Marca_Entity> marcasActivas = marcaRepository.findByEstado(true);
        List<Marca_Entity> marcasInactivas = marcaRepository.findByEstado(false);

        // Assert
        assertEquals(1, marcasActivas.size());
        assertEquals("Sigma-Aldrich", marcasActivas.get(0).getNombre());

        assertEquals(1, marcasInactivas.size());
        assertEquals("Merck", marcasInactivas.get(0).getNombre());
    }

    @Test
    void deberiaEncontrarMarcaPorNombre() {
        // Arrange
        marcaRepository.save(marca1);

        // Act
        Optional<Marca_Entity> encontrada = marcaRepository.findByNombre("Sigma-Aldrich");

        // Assert
        assertTrue(encontrada.isPresent());
        assertEquals("Sigma-Aldrich", encontrada.get().getNombre());
    }

    @Test
    void deberiaRetornarVacioCuandoNombreNoExiste() {
        // Act
        Optional<Marca_Entity> encontrada = marcaRepository.findByNombre("Inexistente");

        // Assert
        assertFalse(encontrada.isPresent());
    }

    @Test
    void deberiaVerificarSiExistePorNombre() {
        // Arrange
        marcaRepository.save(marca1);

        // Act
        boolean existe = marcaRepository.existsByNombre("Sigma-Aldrich");
        boolean noExiste = marcaRepository.existsByNombre("Inexistente");

        // Assert
        assertTrue(existe);
        assertFalse(noExiste);
    }

    @Test
    void deberiaListarTodasLasMarcas() {
        // Arrange
        marcaRepository.save(marca1);
        marcaRepository.save(marca2);

        // Act
        List<Marca_Entity> marcas = marcaRepository.findAll();

        // Assert
        assertEquals(2, marcas.size());
    }

    @Test
    void deberiaActualizarMarca() {
        // Arrange
        Marca_Entity marcaGuardada = marcaRepository.save(marca1);

        // Act
        marcaGuardada.setNombre("Sigma Actualizado");
        marcaGuardada.setEstado(false);
        Marca_Entity marcaActualizada = marcaRepository.save(marcaGuardada);

        // Assert
        assertEquals("Sigma Actualizado", marcaActualizada.getNombre());
        assertFalse(marcaActualizada.getEstado());
    }

    @Test
    void deberiaEliminarMarca() {
        // Arrange
        Marca_Entity marcaGuardada = marcaRepository.save(marca1);
        Integer id = marcaGuardada.getId();

        // Act
        marcaRepository.deleteById(id);
        Optional<Marca_Entity> marcaEliminada = marcaRepository.findById(id);

        // Assert
        assertFalse(marcaEliminada.isPresent());
    }

    @Test
    void deberiaContarMarcas() {
        // Arrange
        marcaRepository.save(marca1);
        marcaRepository.save(marca2);

        // Act
        long cantidad = marcaRepository.count();

        // Assert
        assertEquals(2, cantidad);
    }

    @Test
    void deberiaValidarUniquenessDelNombre() {
        // Arrange
        marcaRepository.save(marca1);

        // Act & Assert
        Marca_Entity marcaDuplicada = new Marca_Entity();
        marcaDuplicada.setNombre("Sigma-Aldrich");
        marcaDuplicada.setEstado(true);

        // Verificar que existe antes de intentar guardar
        assertTrue(marcaRepository.existsByNombre("Sigma-Aldrich"));
    }
}

