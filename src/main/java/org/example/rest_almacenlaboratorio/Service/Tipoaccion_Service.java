package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Tipoaccion_Entity;
import org.example.rest_almacenlaboratorio.Repository.Tipoaccion_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Tipoaccion_Service {

    @Autowired
    private Tipoaccion_Repository tipoaccionRepository;

    public List<Tipoaccion_Entity> obtenerTodos() {
        return tipoaccionRepository.findAll();
    }

    public Optional<Tipoaccion_Entity> obtenerPorId(Integer id) {
        return tipoaccionRepository.findById(id);
    }

    public Optional<Tipoaccion_Entity> obtenerPorNombre(String nombre) {
        return tipoaccionRepository.findByNombre(nombre);
    }

    public boolean existeTipoaccion(Integer id) {
        return tipoaccionRepository.existsById(id);
    }

    public boolean existeTipoaccionPorNombre(String nombre) {
        return tipoaccionRepository.existsByNombre(nombre);
    }
}

