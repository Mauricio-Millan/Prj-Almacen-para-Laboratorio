package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Proveedor_Entity;
import org.example.rest_almacenlaboratorio.Repository.Proveedor_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Proveedor_Service {

    @Autowired
    private Proveedor_Repository proveedorRepository;

    public List<Proveedor_Entity> obtenerTodos() {
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor_Entity> obtenerPorId(Integer id) {
        return proveedorRepository.findById(id);
    }

    public Optional<Proveedor_Entity> obtenerPorNombre(String nombre) {
        return proveedorRepository.findByNombre(nombre);
    }

    public Optional<Proveedor_Entity> obtenerPorRuc(String ruc) {
        return proveedorRepository.findByRuc(ruc);
    }

    public Proveedor_Entity crear(Proveedor_Entity proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public Proveedor_Entity actualizar(Integer id, Proveedor_Entity proveedor) {
        return proveedorRepository.findById(id)
                .map(proveedorExistente -> {
                    proveedorExistente.setNombre(proveedor.getNombre());
                    proveedorExistente.setRuc(proveedor.getRuc());
                    proveedorExistente.setTelefono(proveedor.getTelefono());
                    return proveedorRepository.save(proveedorExistente);
                })
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }

    public void eliminar(Integer id) {
        proveedorRepository.deleteById(id);
    }

    public boolean existePorRuc(String ruc) {
        return proveedorRepository.existsByRuc(ruc);
    }
}

