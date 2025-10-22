package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Tipoaccion_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Tipoaccion_Repository extends JpaRepository<Tipoaccion_Entity, Integer> {

    // Buscar tipo de acción por nombre
    Optional<Tipoaccion_Entity> findByNombre(String nombre);

    // Verificar si existe un tipo de acción con ese nombre
    boolean existsByNombre(String nombre);
}


