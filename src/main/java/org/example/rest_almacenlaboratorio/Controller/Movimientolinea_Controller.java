package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Movimientolinea_Entity;
import org.example.rest_almacenlaboratorio.Service.Movimientolinea_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Rest_AlmacenLaboratorio/api/movimientolineas")
public class Movimientolinea_Controller {

    @Autowired
    private Movimientolinea_Service movimientolineaService;

    @GetMapping
    public ResponseEntity<List<Movimientolinea_Entity>> obtenerTodos() {
        return ResponseEntity.ok(movimientolineaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimientolinea_Entity> obtenerPorId(@PathVariable Integer id) {
        return movimientolineaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/movimiento/{idMovimiento}")
    public ResponseEntity<List<Movimientolinea_Entity>> obtenerPorMovimiento(@PathVariable Integer idMovimiento) {
        return ResponseEntity.ok(movimientolineaService.obtenerPorMovimiento(idMovimiento));
    }

    @GetMapping("/lote/{idLote}")
    public ResponseEntity<List<Movimientolinea_Entity>> obtenerPorLote(@PathVariable Integer idLote) {
        return ResponseEntity.ok(movimientolineaService.obtenerPorLote(idLote));
    }

    @GetMapping("/almacen-origen/{idAlmacenOrigen}")
    public ResponseEntity<List<Movimientolinea_Entity>> obtenerPorAlmacenOrigen(@PathVariable Integer idAlmacenOrigen) {
        return ResponseEntity.ok(movimientolineaService.obtenerPorAlmacenOrigen(idAlmacenOrigen));
    }

    @GetMapping("/almacen-destino/{idAlmacenDestino}")
    public ResponseEntity<List<Movimientolinea_Entity>> obtenerPorAlmacenDestino(@PathVariable Integer idAlmacenDestino) {
        return ResponseEntity.ok(movimientolineaService.obtenerPorAlmacenDestino(idAlmacenDestino));
    }

    @PostMapping
    public ResponseEntity<Movimientolinea_Entity> crear(@RequestBody Movimientolinea_Entity movimientolinea) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(movimientolineaService.crear(movimientolinea));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movimientolinea_Entity> actualizar(@PathVariable Integer id, @RequestBody Movimientolinea_Entity movimientolinea) {
        try {
            return ResponseEntity.ok(movimientolineaService.actualizar(id, movimientolinea));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            movimientolineaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

