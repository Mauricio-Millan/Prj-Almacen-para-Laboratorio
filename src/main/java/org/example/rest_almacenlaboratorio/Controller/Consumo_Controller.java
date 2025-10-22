package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Consumo_Entity;
import org.example.rest_almacenlaboratorio.Service.Consumo_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consumos")
@CrossOrigin(origins = "*")
public class Consumo_Controller {

    @Autowired
    private Consumo_Service consumoService;

    @GetMapping
    public ResponseEntity<List<Consumo_Entity>> obtenerTodos() {
        return ResponseEntity.ok(consumoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consumo_Entity> obtenerPorId(@PathVariable Integer id) {
        return consumoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Consumo_Entity>> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(consumoService.obtenerPorFecha(fecha));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Consumo_Entity>> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(consumoService.obtenerPorUsuario(idUsuario));
    }

    @GetMapping("/departamento/{idDepartamento}")
    public ResponseEntity<List<Consumo_Entity>> obtenerPorDepartamento(@PathVariable Integer idDepartamento) {
        return ResponseEntity.ok(consumoService.obtenerPorDepartamento(idDepartamento));
    }

    @PostMapping
    public ResponseEntity<Consumo_Entity> crear(@RequestBody Consumo_Entity consumo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(consumoService.crear(consumo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Consumo_Entity consumo) {
        try {
            return ResponseEntity.ok(consumoService.actualizar(id, consumo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        consumoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

