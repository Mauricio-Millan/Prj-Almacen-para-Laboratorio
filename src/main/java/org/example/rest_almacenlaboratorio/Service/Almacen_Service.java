package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Almacen_Entity;
import org.example.rest_almacenlaboratorio.Repository.Almacen_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Almacen_Service {

    @Autowired
    private Almacen_Repository almacenRepository;

    public List<Almacen_Entity> obtenerTodos() {
        return almacenRepository.findAll();
    }

    public Optional<Almacen_Entity> obtenerPorId(Integer id) {
        return almacenRepository.findById(id);
    }

    public Optional<Almacen_Entity> obtenerPorNombre(String nombre) {
        return almacenRepository.findByNombre(nombre);
    }

    public Almacen_Entity crear(Almacen_Entity almacen) {
        return almacenRepository.save(almacen);
    }

    public Almacen_Entity actualizar(Integer id, Almacen_Entity almacen) {
        return almacenRepository.findById(id)
                .map(almacenExistente -> {
                    almacenExistente.setNombre(almacen.getNombre());
                    almacenExistente.setDireccion(almacen.getDireccion());
                    almacenExistente.setTelefono(almacen.getTelefono());
                    return almacenRepository.save(almacenExistente);
                })
                .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));
    }

    public void eliminar(Integer id) {
        almacenRepository.deleteById(id);
    }

    public boolean existePorNombre(String nombre) {
        return almacenRepository.existsByNombre(nombre);
    }
}

