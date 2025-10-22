package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Consumo_Entity;
import org.example.rest_almacenlaboratorio.Repository.Consumo_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class Consumo_Service {

    @Autowired
    private Consumo_Repository consumoRepository;

    public List<Consumo_Entity> obtenerTodos() {
        return consumoRepository.findAll();
    }

    public Optional<Consumo_Entity> obtenerPorId(Integer id) {
        return consumoRepository.findById(id);
    }

    public List<Consumo_Entity> obtenerPorFecha(LocalDate fecha) {
        return consumoRepository.findByFecha(fecha);
    }

    public List<Consumo_Entity> obtenerPorUsuario(Integer idUsuario) {
        return consumoRepository.findByIdUsuarioId(idUsuario);
    }

    public List<Consumo_Entity> obtenerPorDepartamento(Integer idDepartamento) {
        return consumoRepository.findByIdDepartamentoId(idDepartamento);
    }

    public Consumo_Entity crear(Consumo_Entity consumo) {
        return consumoRepository.save(consumo);
    }

    public Consumo_Entity actualizar(Integer id, Consumo_Entity consumo) {
        return consumoRepository.findById(id)
                .map(consumoExistente -> {
                    consumoExistente.setIdUsuario(consumo.getIdUsuario());
                    consumoExistente.setIdMovimiento(consumo.getIdMovimiento());
                    consumoExistente.setIdDepartamento(consumo.getIdDepartamento());
                    consumoExistente.setFecha(consumo.getFecha());
                    return consumoRepository.save(consumoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado"));
    }

    public void eliminar(Integer id) {
        consumoRepository.deleteById(id);
    }
}

