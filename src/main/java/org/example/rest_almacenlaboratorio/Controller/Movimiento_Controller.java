package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.DTOs.Movimiento.*;
import org.example.rest_almacenlaboratorio.Mapper.Movimiento_Entity;
import org.example.rest_almacenlaboratorio.Service.Movimiento_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Rest_AlmacenLaboratorio/api/movimientos")
@CrossOrigin(origins = "*")
public class Movimiento_Controller {

    @Autowired
    private Movimiento_Service movimientoService;

    // ========================================================================
    // ENDPOINTS CRUD BÁSICOS
    // ========================================================================

    @GetMapping
    public ResponseEntity<List<Movimiento_Entity>> obtenerTodos() {
        return ResponseEntity.ok(movimientoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento_Entity> obtenerPorId(@PathVariable Integer id) {
        return movimientoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Movimiento_Entity>> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(movimientoService.obtenerPorUsuario(idUsuario));
    }

    @GetMapping("/tipo-accion/{idTipoAccion}")
    public ResponseEntity<List<Movimiento_Entity>> obtenerPorTipoAccion(@PathVariable Integer idTipoAccion) {
        return ResponseEntity.ok(movimientoService.obtenerPorTipoAccion(idTipoAccion));
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<Movimiento_Entity>> obtenerPorRangoFechas(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        try {
            Instant inicio = Instant.parse(fechaInicio);
            Instant fin = Instant.parse(fechaFin);
            return ResponseEntity.ok(movimientoService.obtenerPorRangoFechas(inicio, fin));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/referencia/{referencia}")
    public ResponseEntity<List<Movimiento_Entity>> obtenerPorReferencia(@PathVariable String referencia) {
        return ResponseEntity.ok(movimientoService.obtenerPorReferencia(referencia));
    }

    @PostMapping
    public ResponseEntity<Movimiento_Entity> crear(@RequestBody Movimiento_Entity movimiento) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.crear(movimiento));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movimiento_Entity> actualizar(@PathVariable Integer id, @RequestBody Movimiento_Entity movimiento) {
        try {
            return ResponseEntity.ok(movimientoService.actualizar(id, movimiento));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            movimientoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========================================================================
    // ENDPOINTS PARA PROCEDIMIENTOS ALMACENADOS TRANSACCIONALES
    // ========================================================================

    /**
     * Registra un ingreso múltiple de lotes en una sola transacción
     *
     * Endpoint: POST /Rest_AlmacenLaboratorio/api/movimientos/ingreso-multiple
     *
     * Body ejemplo:
     * {
     *   "idUsuario": 1,
     *   "idProveedor": 2,
     *   "idAlmacenDestino": 1,
     *   "referencia": "COMPRA-2025-001",
     *   "comentario": "Compra mensual de reactivos",
     *   "lotes": [
     *     {
     *       "id_reactivo": 1,
     *       "cantidad": 50,
     *       "precio_unitario": 45.5,
     *       "fecha_expiracion": "2026-12-31"
     *     },
     *     {
     *       "id_reactivo": 2,
     *       "cantidad": 100,
     *       "precio_unitario": 25.0,
     *       "fecha_expiracion": "2027-06-30"
     *     }
     *   ]
     * }
     *
     * Response ejemplo:
     * {
     *   "idMovimiento": 15,
     *   "idCompra": 8,
     *   "totalLotesRegistrados": 2,
     *   "totalUnidades": 150,
     *   "valorTotal": 4775.00
     * }
     */
    @PostMapping("/ingreso-multiple")
    public ResponseEntity<?> registrarIngresoMultiple(@RequestBody IngresoMultipleRequestDTO request) {
        try {
            IngresoMultipleResponseDTO response = movimientoService.registrarIngresoMultiple(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al registrar ingreso múltiple");
            error.put("mensaje", e.getMessage());
            error.put("detalle", "Verifique que el usuario, proveedor, almacén y reactivos existan en la base de datos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Registra un consumo múltiple de lotes en una sola transacción
     *
     * Endpoint: POST /Rest_AlmacenLaboratorio/api/movimientos/consumo-multiple
     *
     * Body ejemplo:
     * {
     *   "idUsuario": 1,
     *   "idDepartamento": 3,
     *   "idAlmacenOrigen": 1,
     *   "referencia": "CONSUMO-2025-001",
     *   "comentario": "Consumo para experimento X",
     *   "consumos": [
     *     {
     *       "id_lote": 5,
     *       "cantidad": 10
     *     },
     *     {
     *       "id_lote": 8,
     *       "cantidad": 25
     *     }
     *   ]
     * }
     *
     * Response ejemplo:
     * {
     *   "idMovimiento": 16,
     *   "totalLotesConsumidos": 2,
     *   "totalUnidadesConsumidas": 35
     * }
     */
    @PostMapping("/consumo-multiple")
    public ResponseEntity<?> registrarConsumoMultiple(@RequestBody ConsumoMultipleRequestDTO request) {
        try {
            ConsumoMultipleResponseDTO response = movimientoService.registrarConsumoMultiple(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al registrar consumo múltiple");
            error.put("mensaje", e.getMessage());
            error.put("detalle", "Verifique que haya stock suficiente y que los lotes existan en el almacén");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Registra un traslado múltiple de lotes entre almacenes
     *
     * Endpoint: POST /Rest_AlmacenLaboratorio/api/movimientos/traslado-multiple
     *
     * Body ejemplo:
     * {
     *   "idUsuario": 1,
     *   "idAlmacenOrigen": 1,
     *   "idAlmacenDestino": 2,
     *   "referencia": "TRASLADO-2025-001",
     *   "comentario": "Reorganización de inventario",
     *   "traslados": [
     *     {
     *       "id_lote": 3,
     *       "cantidad": 15
     *     },
     *     {
     *       "id_lote": 7,
     *       "cantidad": 30
     *     }
     *   ]
     * }
     *
     * Response ejemplo:
     * {
     *   "idMovimiento": 17,
     *   "totalLotesTrasladados": 2,
     *   "totalUnidades": 45
     * }
     */
    @PostMapping("/traslado-multiple")
    public ResponseEntity<?> registrarTrasladoMultiple(@RequestBody TrasladoMultipleRequestDTO request) {
        try {
            TrasladoMultipleResponseDTO response = movimientoService.registrarTrasladoMultiple(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al registrar traslado múltiple");
            error.put("mensaje", e.getMessage());
            error.put("detalle", "Verifique que los almacenes sean diferentes y haya stock suficiente en el origen");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

