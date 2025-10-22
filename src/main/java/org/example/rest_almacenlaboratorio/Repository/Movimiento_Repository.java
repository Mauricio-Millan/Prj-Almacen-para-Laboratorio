package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Movimiento_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface Movimiento_Repository extends JpaRepository<Movimiento_Entity, Integer> {
    List<Movimiento_Entity> findByIdUsuario_Id(Integer idUsuario);
    List<Movimiento_Entity> findByIdTipoAccion_Id(Integer idTipoAccion);
    List<Movimiento_Entity> findByFechaBetween(Instant fechaInicio, Instant fechaFin);
    List<Movimiento_Entity> findByReferencia(String referencia);
}

