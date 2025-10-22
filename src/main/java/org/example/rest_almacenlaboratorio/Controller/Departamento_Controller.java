package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Departamento_Entity;
import org.example.rest_almacenlaboratorio.Service.Departamento_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@CrossOrigin(origins = "*")
public class Departamento_Controller {

    @Autowired
    private Departamento_Service departamentoService;

    @GetMapping
    public ResponseEntity<List<Departamento_Entity>> obtenerTodos() {
        return ResponseEntity.ok(departamentoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departamento_Entity> obtenerPorId(@PathVariable Integer id) {
        return departamentoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Departamento_Entity> obtenerPorNombre(@PathVariable String nombre) {
        return departamentoService.obtenerPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Departamento_Entity departamento) {
        if (departamentoService.existePorNombre(departamento.getNombre())) {
            return ResponseEntity.badRequest().body("El departamento con ese nombre ya existe");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departamentoService.crear(departamento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Departamento_Entity departamento) {
        try {
            return ResponseEntity.ok(departamentoService.actualizar(id, departamento));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        departamentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

