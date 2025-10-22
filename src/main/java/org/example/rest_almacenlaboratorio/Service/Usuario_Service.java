package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Usuario_Entity;
import org.example.rest_almacenlaboratorio.Repository.Usuario_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class Usuario_Service {

    @Autowired
    private Usuario_Repository usuarioRepository;

    public List<Usuario_Entity> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario_Entity> obtenerPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario_Entity> obtenerPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    public Optional<Usuario_Entity> obtenerPorDni(String dni) {
        return usuarioRepository.findByDni(dni);
    }

    public Usuario_Entity crear(Usuario_Entity usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario_Entity actualizar(Integer id, Usuario_Entity usuario) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNombre(usuario.getNombre());
                    usuarioExistente.setDni(usuario.getDni());
                    if (usuario.getClave() != null && !usuario.getClave().isEmpty()) {
                        usuarioExistente.setClave(usuario.getClave());
                    }
                    usuarioExistente.setFechaNacimiento(usuario.getFechaNacimiento());
                    usuarioExistente.setIdRol(usuario.getIdRol());
                    return usuarioRepository.save(usuarioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void eliminar(Integer id) {
        usuarioRepository.deleteById(id);
    }

    public boolean existePorNombre(String nombre) {
        return usuarioRepository.existsByNombre(nombre);
    }

    public boolean existePorDni(String dni) {
        return usuarioRepository.existsByDni(dni);
    }
}
