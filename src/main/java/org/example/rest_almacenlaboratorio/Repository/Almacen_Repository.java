package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Almacen_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Almacen_Repository extends JpaRepository<Almacen_Entity, Integer> {
    Optional<Almacen_Entity> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}

