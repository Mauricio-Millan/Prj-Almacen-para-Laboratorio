package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Usuario_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface Usuario_Repository extends JpaRepository<Usuario_Entity, Integer> {
    Optional<Usuario_Entity> findByNombre(String nombre);
    Optional<Usuario_Entity> findByDni(String dni);
    boolean existsByNombre(String nombre);
    boolean existsByDni(String dni);
}
