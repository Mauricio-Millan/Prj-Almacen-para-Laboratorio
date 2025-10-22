package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Reactivo_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Reactivo_Repository extends JpaRepository<Reactivo_Entity, Integer> {

    // Buscar reactivo por nombre
    Optional<Reactivo_Entity> findByNombre(String nombre);

    // Buscar reactivos por marca
    List<Reactivo_Entity> findByIdMarca_Id(Integer idMarca);

    // Verificar si existe un reactivo con ese nombre
    boolean existsByNombre(String nombre);
}