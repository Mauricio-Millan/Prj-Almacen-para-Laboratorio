package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Movimientolinea_Entity;
import org.example.rest_almacenlaboratorio.Repository.Movimientolinea_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Movimientolinea_Service {

    @Autowired
    private Movimientolinea_Repository movimientolineaRepository;

    public List<Movimientolinea_Entity> obtenerTodos() {
        return movimientolineaRepository.findAll();
    }

    public Optional<Movimientolinea_Entity> obtenerPorId(Integer id) {
        return movimientolineaRepository.findById(id);
    }

    public List<Movimientolinea_Entity> obtenerPorMovimiento(Integer idMovimiento) {
        return movimientolineaRepository.findByIdMovimiento_Id(idMovimiento);
    }

    public List<Movimientolinea_Entity> obtenerPorLote(Integer idLote) {
        return movimientolineaRepository.findByIdLote_Id(idLote);
    }

    public List<Movimientolinea_Entity> obtenerPorAlmacenOrigen(Integer idAlmacenOrigen) {
        return movimientolineaRepository.findByIdAlmacenOrigen_Id(idAlmacenOrigen);
    }

    public List<Movimientolinea_Entity> obtenerPorAlmacenDestino(Integer idAlmacenDestino) {
        return movimientolineaRepository.findByIdAlmacenDestino_Id(idAlmacenDestino);
    }

    public Movimientolinea_Entity crear(Movimientolinea_Entity movimientolinea) {
        return movimientolineaRepository.save(movimientolinea);
    }

    public Movimientolinea_Entity actualizar(Integer id, Movimientolinea_Entity movimientolinea) {
        return movimientolineaRepository.findById(id)
                .map(movimientolineaExistente -> {
                    movimientolineaExistente.setIdMovimiento(movimientolinea.getIdMovimiento());
                    movimientolineaExistente.setIdAlmacenOrigen(movimientolinea.getIdAlmacenOrigen());
                    movimientolineaExistente.setIdAlmacenDestino(movimientolinea.getIdAlmacenDestino());
                    movimientolineaExistente.setIdLote(movimientolinea.getIdLote());
                    movimientolineaExistente.setCantidadDelta(movimientolinea.getCantidadDelta());
                    movimientolineaExistente.setPrecioVenta(movimientolinea.getPrecioVenta());
                    return movimientolineaRepository.save(movimientolineaExistente);
                })
                .orElseThrow(() -> new RuntimeException("Movimiento línea no encontrado"));
    }

    public void eliminar(Integer id) {
        movimientolineaRepository.deleteById(id);
    }
}

