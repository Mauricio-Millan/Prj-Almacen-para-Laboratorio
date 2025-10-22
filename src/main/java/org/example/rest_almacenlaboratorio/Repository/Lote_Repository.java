package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Lote_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface Lote_Repository extends JpaRepository<Lote_Entity, Integer> {
    List<Lote_Entity> findByEstado(Boolean estado);
    List<Lote_Entity> findByIdReactivo_Id(Integer idReactivo);
    List<Lote_Entity> findByIdCompra_Id(Integer idCompra);
    List<Lote_Entity> findByFechaExpiracionBefore(Date fecha);
    List<Lote_Entity> findByFechaExpiracionBetween(Date fechaInicio, Date fechaFin);
}

