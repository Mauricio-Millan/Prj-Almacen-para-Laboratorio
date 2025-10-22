package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Reactivo_Entity;
import org.example.rest_almacenlaboratorio.Service.Reactivo_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reactivos")
@CrossOrigin(origins = "*")
public class Reactivo_Controller {

    @Autowired
    private Reactivo_Service reactivoService;

    @GetMapping
    public ResponseEntity<List<Reactivo_Entity>> obtenerTodos() {
        try {
            List<Reactivo_Entity> reactivos = reactivoService.obtenerTodos();
            return ResponseEntity.ok(reactivos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reactivo_Entity> obtenerPorId(@PathVariable Integer id) {
        try {
            return reactivoService.obtenerPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Reactivo_Entity> obtenerPorNombre(@PathVariable String nombre) {
        try {
            return reactivoService.obtenerPorNombre(nombre)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/marca/{idMarca}")
    public ResponseEntity<List<Reactivo_Entity>> obtenerPorMarca(@PathVariable Integer idMarca) {
        try {
            List<Reactivo_Entity> reactivos = reactivoService.obtenerPorMarca(idMarca);
            return ResponseEntity.ok(reactivos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Reactivo_Entity reactivo) {
        try {
            Reactivo_Entity nuevoReactivo = reactivoService.crear(reactivo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoReactivo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Reactivo_Entity reactivo) {
        try {
            Reactivo_Entity reactivoActualizado = reactivoService.actualizar(id, reactivo);
            return ResponseEntity.ok(reactivoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            reactivoService.eliminar(id);
            return ResponseEntity.ok("Reactivo eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/existe/{id}")
    public ResponseEntity<Boolean> existeReactivo(@PathVariable Integer id) {
        try {
            boolean existe = reactivoService.existeReactivo(id);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

