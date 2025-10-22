package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Lote_Entity;
import org.example.rest_almacenlaboratorio.Service.Lote_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/Rest_AlmacenLaboratorio/api/lotes")
public class Lote_Controller {

    @Autowired
    private Lote_Service loteService;

    // Obtener todos los lotes
    @GetMapping
    public ResponseEntity<List<Lote_Entity>> obtenerTodos() {
        return ResponseEntity.ok(loteService.obtenerTodos());
    }

    // Obtener lote por ID
    @GetMapping("/{id}")
    public ResponseEntity<Lote_Entity> obtenerPorId(@PathVariable Integer id) {
        return loteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener lotes por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Lote_Entity>> obtenerPorEstado(@PathVariable Boolean estado) {
        return ResponseEntity.ok(loteService.obtenerPorEstado(estado));
    }

    // Obtener lotes por reactivo
    @GetMapping("/reactivo/{idReactivo}")
    public ResponseEntity<List<Lote_Entity>> obtenerPorReactivo(@PathVariable Integer idReactivo) {
        return ResponseEntity.ok(loteService.obtenerPorReactivo(idReactivo));
    }

    // Obtener lotes por compra
    @GetMapping("/compra/{idCompra}")
    public ResponseEntity<List<Lote_Entity>> obtenerPorCompra(@PathVariable Integer idCompra) {
        return ResponseEntity.ok(loteService.obtenerPorCompra(idCompra));
    }

    // Obtener lotes próximos a vencer (antes de una fecha específica)
    @GetMapping("/proximos-vencer")
    public ResponseEntity<List<Lote_Entity>> obtenerLotesProximosAVencer(@RequestParam String fecha) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaVencimiento = formatter.parse(fecha);
            return ResponseEntity.ok(loteService.obtenerLotesProximosAVencer(fechaVencimiento));
        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener lotes por rango de expiración
    @GetMapping("/rango-expiracion")
    public ResponseEntity<List<Lote_Entity>> obtenerLotesPorRangoExpiracion(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date inicio = formatter.parse(fechaInicio);
            Date fin = formatter.parse(fechaFin);
            return ResponseEntity.ok(loteService.obtenerLotesPorRangoExpiracion(inicio, fin));
        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Crear nuevo lote
    @PostMapping
    public ResponseEntity<Lote_Entity> crear(@RequestBody Lote_Entity lote) {
        try {
            Lote_Entity nuevoLote = loteService.crear(lote);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLote);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar lote existente
    @PutMapping("/{id}")
    public ResponseEntity<Lote_Entity> actualizar(@PathVariable Integer id, @RequestBody Lote_Entity lote) {
        try {
            Lote_Entity loteActualizado = loteService.actualizar(id, lote);
            return ResponseEntity.ok(loteActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar lote (eliminación física)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            loteService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Desactivar lote (eliminación lógica)
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Lote_Entity> desactivar(@PathVariable Integer id) {
        try {
            Lote_Entity loteDesactivado = loteService.desactivarLote(id);
            return ResponseEntity.ok(loteDesactivado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Verificar si existe un lote
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeLote(@PathVariable Integer id) {
        return ResponseEntity.ok(loteService.existeLote(id));
    }
}

