package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Marca_Entity;
import org.example.rest_almacenlaboratorio.Repository.Marca_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Marca_Service {

    @Autowired
    private Marca_Repository marcaRepository;

    public List<Marca_Entity> obtenerTodos() {
        return marcaRepository.findAll();
    }

    public Optional<Marca_Entity> obtenerPorId(Integer id) {
        return marcaRepository.findById(id);
    }

    public List<Marca_Entity> obtenerPorEstado(Boolean estado) {
        return marcaRepository.findByEstado(estado);
    }

    public Optional<Marca_Entity> obtenerPorNombre(String nombre) {
        return marcaRepository.findByNombre(nombre);
    }

    public Marca_Entity crear(Marca_Entity marca) {
        return marcaRepository.save(marca);
    }

    public Marca_Entity actualizar(Integer id, Marca_Entity marca) {
        return marcaRepository.findById(id)
                .map(marcaExistente -> {
                    marcaExistente.setNombre(marca.getNombre());
                    marcaExistente.setEstado(marca.getEstado());
                    return marcaRepository.save(marcaExistente);
                })
                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
    }

    public void eliminar(Integer id) {
        marcaRepository.deleteById(id);
    }

    public boolean existePorNombre(String nombre) {
        return marcaRepository.existsByNombre(nombre);
    }
}

