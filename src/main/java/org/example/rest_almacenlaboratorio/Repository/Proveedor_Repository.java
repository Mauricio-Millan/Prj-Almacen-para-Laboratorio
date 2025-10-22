package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Proveedor_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Proveedor_Repository extends JpaRepository<Proveedor_Entity, Integer> {
    Optional<Proveedor_Entity> findByNombre(String nombre);
    Optional<Proveedor_Entity> findByRuc(String ruc);
    boolean existsByRuc(String ruc);
}

