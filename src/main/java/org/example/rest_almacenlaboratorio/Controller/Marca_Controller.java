package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Marca_Entity;
import org.example.rest_almacenlaboratorio.Service.Marca_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Rest_AlmacenLaboratorio/api/marcas")
public class Marca_Controller {

    @Autowired
    private Marca_Service marcaService;

    @GetMapping
    public ResponseEntity<List<Marca_Entity>> obtenerTodos() {
        return ResponseEntity.ok(marcaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marca_Entity> obtenerPorId(@PathVariable Integer id) {
        return marcaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Marca_Entity>> obtenerPorEstado(@PathVariable Boolean estado) {
        return ResponseEntity.ok(marcaService.obtenerPorEstado(estado));
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Marca_Entity> obtenerPorNombre(@PathVariable String nombre) {
        return marcaService.obtenerPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Marca_Entity> crear(@RequestBody Marca_Entity marca) {
        try {
            if (marcaService.existePorNombre(marca.getNombre())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(marcaService.crear(marca));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marca_Entity> actualizar(@PathVariable Integer id, @RequestBody Marca_Entity marca) {
        try {
            return ResponseEntity.ok(marcaService.actualizar(id, marca));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            marcaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

