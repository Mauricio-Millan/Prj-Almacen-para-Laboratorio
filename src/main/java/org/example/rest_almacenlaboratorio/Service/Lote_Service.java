package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Lote_Entity;
import org.example.rest_almacenlaboratorio.Repository.Lote_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class Lote_Service {

    @Autowired
    private Lote_Repository loteRepository;

    public List<Lote_Entity> obtenerTodos() {
        return loteRepository.findAll();
    }

    public Optional<Lote_Entity> obtenerPorId(Integer id) {
        return loteRepository.findById(id);
    }

    public List<Lote_Entity> obtenerPorEstado(Boolean estado) {
        return loteRepository.findByEstado(estado);
    }

    public List<Lote_Entity> obtenerPorReactivo(Integer idReactivo) {
        return loteRepository.findByIdReactivo_Id(idReactivo);
    }

    public List<Lote_Entity> obtenerPorCompra(Integer idCompra) {
        return loteRepository.findByIdCompra_Id(idCompra);
    }

    public List<Lote_Entity> obtenerLotesProximosAVencer(Date fecha) {
        return loteRepository.findByFechaExpiracionBefore(fecha);
    }

    public List<Lote_Entity> obtenerLotesPorRangoExpiracion(Date fechaInicio, Date fechaFin) {
        return loteRepository.findByFechaExpiracionBetween(fechaInicio, fechaFin);
    }

    public Lote_Entity crear(Lote_Entity lote) {
        // Si no se especifica estado, se asigna activo por defecto
        if (lote.getEstado() == null) {
            lote.setEstado(true);
        }
        return loteRepository.save(lote);
    }

    public Lote_Entity actualizar(Integer id, Lote_Entity lote) {
        return loteRepository.findById(id)
                .map(loteExistente -> {
                    loteExistente.setIdReactivo(lote.getIdReactivo());
                    loteExistente.setIdCompra(lote.getIdCompra());
                    loteExistente.setCantidadInicial(lote.getCantidadInicial());
                    loteExistente.setPrecioUnitario(lote.getPrecioUnitario());
                    loteExistente.setFechaExpiracion(lote.getFechaExpiracion());
                    loteExistente.setEstado(lote.getEstado());
                    return loteRepository.save(loteExistente);
                })
                .orElseThrow(() -> new RuntimeException("Lote no encontrado con ID: " + id));
    }

    public void eliminar(Integer id) {
        if (!loteRepository.existsById(id)) {
            throw new RuntimeException("Lote no encontrado con ID: " + id);
        }
        loteRepository.deleteById(id);
    }

    public Lote_Entity desactivarLote(Integer id) {
        return loteRepository.findById(id)
                .map(lote -> {
                    lote.setEstado(false);
                    return loteRepository.save(lote);
                })
                .orElseThrow(() -> new RuntimeException("Lote no encontrado con ID: " + id));
    }

    public boolean existeLote(Integer id) {
        return loteRepository.existsById(id);
    }
}

