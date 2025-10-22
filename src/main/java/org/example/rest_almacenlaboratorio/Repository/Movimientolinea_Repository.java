package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Movimientolinea_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Movimientolinea_Repository extends JpaRepository<Movimientolinea_Entity, Integer> {
    List<Movimientolinea_Entity> findByIdMovimiento_Id(Integer idMovimiento);
    List<Movimientolinea_Entity> findByIdLote_Id(Integer idLote);
    List<Movimientolinea_Entity> findByIdAlmacenOrigen_Id(Integer idAlmacenOrigen);
    List<Movimientolinea_Entity> findByIdAlmacenDestino_Id(Integer idAlmacenDestino);
}

