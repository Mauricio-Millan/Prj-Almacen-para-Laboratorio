package org.example.rest_almacenlaboratorio.Repository;

import org.example.rest_almacenlaboratorio.Mapper.Lote_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Obtener todos los lotes ordenados por fecha de vencimiento (ascendente - los próximos a vencer primero)
    List<Lote_Entity> findAllByOrderByFechaExpiracionAsc();

    // Obtener lotes de un almacén específico a través del inventario
    @Query("SELECT l FROM Lote_Entity l " +
           "INNER JOIN InventarioAlmacen_Entity i ON i.idLote.id = l.id " +
           "WHERE i.idAlmacen.id = :idAlmacen " +
           "ORDER BY l.fechaExpiracion ASC")
    List<Lote_Entity> findLotesByAlmacenIdOrderByFechaExpiracion(@Param("idAlmacen") Integer idAlmacen);
}

