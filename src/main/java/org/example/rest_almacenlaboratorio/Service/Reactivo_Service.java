package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Reactivo_Entity;
import org.example.rest_almacenlaboratorio.Repository.Reactivo_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Reactivo_Service {

    @Autowired
    private Reactivo_Repository reactivoRepository;

    public List<Reactivo_Entity> obtenerTodos() {
        return reactivoRepository.findAll();
    }

    public Optional<Reactivo_Entity> obtenerPorId(Integer id) {
        return reactivoRepository.findById(id);
    }

    public Optional<Reactivo_Entity> obtenerPorNombre(String nombre) {
        return reactivoRepository.findByNombre(nombre);
    }

    public List<Reactivo_Entity> obtenerPorMarca(Integer idMarca) {
        return reactivoRepository.findByIdMarca_Id(idMarca);
    }

    public Reactivo_Entity crear(Reactivo_Entity reactivo) {
        if (reactivoRepository.existsByNombre(reactivo.getNombre())) {
            throw new RuntimeException("Ya existe un reactivo con el nombre: " + reactivo.getNombre());
        }
        return reactivoRepository.save(reactivo);
    }

    public Reactivo_Entity actualizar(Integer id, Reactivo_Entity reactivo) {
        return reactivoRepository.findById(id)
                .map(reactivoExistente -> {
                    // Verificar si el nuevo nombre ya existe en otro reactivo
                    if (!reactivoExistente.getNombre().equals(reactivo.getNombre()) &&
                            reactivoRepository.existsByNombre(reactivo.getNombre())) {
                        throw new RuntimeException("Ya existe un reactivo con el nombre: " + reactivo.getNombre());
                    }
                    reactivoExistente.setNombre(reactivo.getNombre());
                    reactivoExistente.setIdMarca(reactivo.getIdMarca());
                    return reactivoRepository.save(reactivoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Reactivo no encontrado con ID: " + id));
    }

    public void eliminar(Integer id) {
        if (!reactivoRepository.existsById(id)) {
            throw new RuntimeException("Reactivo no encontrado con ID: " + id);
        }
        reactivoRepository.deleteById(id);
    }

    public boolean existeReactivo(Integer id) {
        return reactivoRepository.existsById(id);
    }

    public boolean existeReactivoPorNombre(String nombre) {
        return reactivoRepository.existsByNombre(nombre);
    }
}

