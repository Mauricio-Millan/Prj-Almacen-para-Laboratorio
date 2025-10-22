package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Departamento_Entity;
import org.example.rest_almacenlaboratorio.Repository.Departamento_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Departamento_Service {

    @Autowired
    private Departamento_Repository departamentoRepository;

    public List<Departamento_Entity> obtenerTodos() {
        return departamentoRepository.findAll();
    }

    public Optional<Departamento_Entity> obtenerPorId(Integer id) {
        return departamentoRepository.findById(id);
    }

    public Optional<Departamento_Entity> obtenerPorNombre(String nombre) {
        return departamentoRepository.findByNombre(nombre);
    }

    public Departamento_Entity crear(Departamento_Entity departamento) {
        return departamentoRepository.save(departamento);
    }

    public Departamento_Entity actualizar(Integer id, Departamento_Entity departamento) {
        return departamentoRepository.findById(id)
                .map(departamentoExistente -> {
                    departamentoExistente.setNombre(departamento.getNombre());
                    departamentoExistente.setResponsable(departamento.getResponsable());
                    departamentoExistente.setUbicacion(departamento.getUbicacion());
                    return departamentoRepository.save(departamentoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));
    }

    public void eliminar(Integer id) {
        departamentoRepository.deleteById(id);
    }

    public boolean existePorNombre(String nombre) {
        return departamentoRepository.existsByNombre(nombre);
    }
}

