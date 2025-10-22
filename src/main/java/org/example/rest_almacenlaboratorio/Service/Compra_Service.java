package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.Mapper.Compra_Entity;
import org.example.rest_almacenlaboratorio.Repository.Compra_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class Compra_Service {

    @Autowired
    private Compra_Repository compraRepository;

    public List<Compra_Entity> obtenerTodos() {
        return compraRepository.findAll();
    }

    public Optional<Compra_Entity> obtenerPorId(Integer id) {
        return compraRepository.findById(id);
    }

    public List<Compra_Entity> obtenerPorFecha(LocalDate fecha) {
        return compraRepository.findByFecha(fecha);
    }

    public List<Compra_Entity> obtenerPorUsuario(Integer idUsuario) {
        return compraRepository.findByIdUsuarioId(idUsuario);
    }

    public List<Compra_Entity> obtenerPorProveedor(Integer idProveedor) {
        return compraRepository.findByIdProveedorId(idProveedor);
    }

    public Compra_Entity crear(Compra_Entity compra) {
        return compraRepository.save(compra);
    }

    public Compra_Entity actualizar(Integer id, Compra_Entity compra) {
        return compraRepository.findById(id)
                .map(compraExistente -> {
                    compraExistente.setIdUsuario(compra.getIdUsuario());
                    compraExistente.setIdMovimiento(compra.getIdMovimiento());
                    compraExistente.setIdProveedor(compra.getIdProveedor());
                    compraExistente.setFecha(compra.getFecha());
                    return compraRepository.save(compraExistente);
                })
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
    }

    public void eliminar(Integer id) {
        compraRepository.deleteById(id);
    }
}

