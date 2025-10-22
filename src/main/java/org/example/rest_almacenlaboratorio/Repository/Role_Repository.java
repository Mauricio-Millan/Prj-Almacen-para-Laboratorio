package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Role_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Role_Repository extends JpaRepository<Role_Entity, Integer> {

    // Buscar rol por nombre
    Optional<Role_Entity> findByNombre(String nombre);

    // Verificar si existe un rol con ese nombre
    boolean existsByNombre(String nombre);
}

