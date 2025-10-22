package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Tipoaccion_Entity;
import org.example.rest_almacenlaboratorio.Service.Tipoaccion_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipoacciones")
@CrossOrigin(origins = "*")
public class Tipoaccion_Controller {

    @Autowired
    private Tipoaccion_Service tipoaccionService;

    @GetMapping
    public ResponseEntity<List<Tipoaccion_Entity>> obtenerTodos() {
        try {
            List<Tipoaccion_Entity> tipoacciones = tipoaccionService.obtenerTodos();
            return ResponseEntity.ok(tipoacciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tipoaccion_Entity> obtenerPorId(@PathVariable Integer id) {
        try {
            return tipoaccionService.obtenerPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Tipoaccion_Entity> obtenerPorNombre(@PathVariable String nombre) {
        try {
            return tipoaccionService.obtenerPorNombre(nombre)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/existe/{id}")
    public ResponseEntity<Boolean> existeTipoaccion(@PathVariable Integer id) {
        try {
            boolean existe = tipoaccionService.existeTipoaccion(id);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
