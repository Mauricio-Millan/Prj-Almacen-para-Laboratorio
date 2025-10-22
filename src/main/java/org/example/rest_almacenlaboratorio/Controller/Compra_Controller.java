package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Compra_Entity;
import org.example.rest_almacenlaboratorio.Service.Compra_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class Compra_Controller {

    @Autowired
    private Compra_Service compraService;

    @GetMapping
    public ResponseEntity<List<Compra_Entity>> obtenerTodos() {
        return ResponseEntity.ok(compraService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra_Entity> obtenerPorId(@PathVariable Integer id) {
        return compraService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Compra_Entity>> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(compraService.obtenerPorFecha(fecha));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Compra_Entity>> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(compraService.obtenerPorUsuario(idUsuario));
    }

    @GetMapping("/proveedor/{idProveedor}")
    public ResponseEntity<List<Compra_Entity>> obtenerPorProveedor(@PathVariable Integer idProveedor) {
        return ResponseEntity.ok(compraService.obtenerPorProveedor(idProveedor));
    }

    @PostMapping
    public ResponseEntity<Compra_Entity> crear(@RequestBody Compra_Entity compra) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compraService.crear(compra));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Compra_Entity compra) {
        try {
            return ResponseEntity.ok(compraService.actualizar(id, compra));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        compraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

