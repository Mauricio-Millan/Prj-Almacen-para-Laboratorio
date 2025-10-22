package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Role_Entity;
import org.example.rest_almacenlaboratorio.Repository.Role_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Role_Service {

    @Autowired
    private Role_Repository roleRepository;

    public List<Role_Entity> obtenerTodos() {
        return roleRepository.findAll();
    }

    public Optional<Role_Entity> obtenerPorId(Integer id) {
        return roleRepository.findById(id);
    }

    public Optional<Role_Entity> obtenerPorNombre(String nombre) {
        return roleRepository.findByNombre(nombre);
    }

    public boolean existeRole(Integer id) {
        return roleRepository.existsById(id);
    }

    public boolean existeRolePorNombre(String nombre) {
        return roleRepository.existsByNombre(nombre);
    }
}

