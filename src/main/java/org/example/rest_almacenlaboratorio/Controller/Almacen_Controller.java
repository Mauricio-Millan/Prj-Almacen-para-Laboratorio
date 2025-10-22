package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Almacen_Entity;
import org.example.rest_almacenlaboratorio.Service.Almacen_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/almacenes")
@CrossOrigin(origins = "*")
public class Almacen_Controller {

    @Autowired
    private Almacen_Service almacenService;

    @GetMapping
    public ResponseEntity<List<Almacen_Entity>> obtenerTodos() {
        return ResponseEntity.ok(almacenService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Almacen_Entity> obtenerPorId(@PathVariable Integer id) {
        return almacenService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Almacen_Entity> obtenerPorNombre(@PathVariable String nombre) {
        return almacenService.obtenerPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Almacen_Entity almacen) {
        if (almacenService.existePorNombre(almacen.getNombre())) {
            return ResponseEntity.badRequest().body("El almacén con ese nombre ya existe");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(almacenService.crear(almacen));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Almacen_Entity almacen) {
        try {
            return ResponseEntity.ok(almacenService.actualizar(id, almacen));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        almacenService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

