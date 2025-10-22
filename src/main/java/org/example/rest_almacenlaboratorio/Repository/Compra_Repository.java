package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Compra_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface Compra_Repository extends JpaRepository<Compra_Entity, Integer> {
    List<Compra_Entity> findByFecha(LocalDate fecha);
    List<Compra_Entity> findByIdUsuarioId(Integer idUsuario);
    List<Compra_Entity> findByIdProveedorId(Integer idProveedor);
}

