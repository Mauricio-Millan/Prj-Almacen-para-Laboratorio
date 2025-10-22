package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Departamento_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Departamento_Repository extends JpaRepository<Departamento_Entity, Integer> {
    Optional<Departamento_Entity> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}

