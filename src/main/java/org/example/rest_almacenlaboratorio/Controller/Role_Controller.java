package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Role_Entity;
import org.example.rest_almacenlaboratorio.Service.Role_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class Role_Controller {

    @Autowired
    private Role_Service roleService;

    @GetMapping
    public ResponseEntity<List<Role_Entity>> obtenerTodos() {
        try {
            List<Role_Entity> roles = roleService.obtenerTodos();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role_Entity> obtenerPorId(@PathVariable Integer id) {
        try {
            return roleService.obtenerPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Role_Entity> obtenerPorNombre(@PathVariable String nombre) {
        try {
            return roleService.obtenerPorNombre(nombre)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/existe/{id}")
    public ResponseEntity<Boolean> existeRole(@PathVariable Integer id) {
        try {
            boolean existe = roleService.existeRole(id);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

