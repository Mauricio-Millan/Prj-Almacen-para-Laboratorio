package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Marca_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Marca_Repository extends JpaRepository<Marca_Entity, Integer> {
    List<Marca_Entity> findByEstado(Boolean estado);
    Optional<Marca_Entity> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}

