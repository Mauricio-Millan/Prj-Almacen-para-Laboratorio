package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Consumo_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface Consumo_Repository extends JpaRepository<Consumo_Entity, Integer> {
    List<Consumo_Entity> findByFecha(LocalDate fecha);
    List<Consumo_Entity> findByIdUsuarioId(Integer idUsuario);
    List<Consumo_Entity> findByIdDepartamentoId(Integer idDepartamento);
}

