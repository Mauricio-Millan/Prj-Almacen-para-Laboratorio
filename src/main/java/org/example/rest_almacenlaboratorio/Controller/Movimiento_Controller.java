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

    /**
     * Registra un ajuste múltiple de inventario de lotes
     *
     * Endpoint: POST /Rest_AlmacenLaboratorio/api/movimientos/ajuste-multiple
     *
     * Body ejemplo:
     * {
     *   "idUsuario": 1,
     *   "idAlmacenOrigen": 1,
     *   "referencia": "AJUSTE-2025-001",
     *   "comentario": "Ajuste por inventario físico",
     *   "ajustes": [
     *     {
     *       "id_lote": 5,
     *       "cantidad_delta": 10.5
     *     },
     *     {
     *       "id_lote": 8,
     *       "cantidad_delta": -5.0
     *     }
     *   ]
     * }
     *
     * Nota: cantidad_delta puede ser:
     * - Positivo: Incrementa el stock (ejemplo: 10.5 agrega 10.5 unidades)
     * - Negativo: Decrementa el stock (ejemplo: -5.0 resta 5.0 unidades)
     *
     * Validaciones del procedimiento:
     * - Para ajustes negativos: valida que haya stock suficiente
     * - Para ajustes positivos: valida que no supere la cantidad inicial del lote
     *
     * Response ejemplo:
     * {
     *   "idMovimiento": 18,
     *   "totalLotesAjustados": 2,
     *   "ajustesPositivos": 1,
     *   "ajustesNegativos": 1,
     *   "totalIncrementos": 10.5,
     *   "totalDecrementos": 5.0,
     *   "deltaNeto": 5.5
     * }
     */
    @PostMapping("/ajuste-multiple")
    public ResponseEntity<?> registrarAjusteMultiple(@RequestBody AjusteMultipleRequestDTO request) {
        try {
            AjusteMultipleResponseDTO response = movimientoService.registrarAjusteMultiple(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al registrar ajuste múltiple");
            error.put("mensaje", e.getMessage());
            error.put("detalle", "Verifique que: 1) Haya stock suficiente para ajustes negativos, " +
                    "2) Los ajustes positivos no superen la cantidad inicial del lote, " +
                    "3) El usuario y almacén existan en la base de datos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Consulta el historial de movimientos con múltiples filtros opcionales
     *
     * Endpoint: GET /Rest_AlmacenLaboratorio/api/movimientos/historial
     *
     * Parámetros opcionales:
     * - idReactivo: Filtrar por reactivo específico
     * - idAlmacen: Filtrar por almacén (origen o destino)
     * - fechaInicio: Fecha inicio del rango (formato ISO 8601, por defecto: hace 3 meses)
     * - fechaFin: Fecha fin del rango (formato ISO 8601, por defecto: hoy)
     * - idTipoAccion: Filtrar por tipo de acción (1=Ingreso, 2=Consumo, 3=Traslado, 4=Ajuste)
     *
     * Ejemplo de uso:
     * GET /api/movimientos/historial
     * GET /api/movimientos/historial?idReactivo=5
     * GET /api/movimientos/historial?idAlmacen=1&idTipoAccion=1
     * GET /api/movimientos/historial?fechaInicio=2025-01-01T00:00:00Z&fechaFin=2025-12-31T23:59:59Z
     *
     * Response:
     * {
     *   "detalleMovimientos": [
     *     {
     *       "idMovimiento": 1,
     *       "fecha": "2025-11-18T10:30:00Z",
     *       "tipoAccion": "Ingreso",
     *       "usuario": "Juan Pérez",
     *       "referencia": "COMPRA-2025-001",
     *       "comentario": "Compra mensual",
     *       "nombreReactivo": "Ácido Sulfúrico",
     *       "marca": "Merck",
     *       "numeroLote": 5,
     *       "almacenOrigen": null,
     *       "almacenDestino": "Almacén Principal",
     *       "cantidad": 100.0,
     *       "precioVenta": null,
     *       "valorTotal": 4500.00
     *     }
     *   ],
     *   "resumenPorTipo": [
     *     {
     *       "tipoAccion": "Ingreso",
     *       "totalMovimientos": 5,
     *       "totalUnidades": 500.0,
     *       "valorTotal": 22500.00
     *     },
     *     {
     *       "tipoAccion": "Consumo",
     *       "totalMovimientos": 3,
     *       "totalUnidades": 150.0,
     *       "valorTotal": 6750.00
     *     }
     *   ]
     * }
     */
    @GetMapping("/historial")
    public ResponseEntity<?> consultarHistorialMovimientos(
            @RequestParam(required = false) Integer idReactivo,
            @RequestParam(required = false) Integer idAlmacen,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) Integer idTipoAccion) {
        try {
            Instant inicio = null;
            Instant fin = null;

            // Parsear fechas si se proporcionan
            if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
                try {
                    inicio = Instant.parse(fechaInicio);
                } catch (Exception e) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Formato de fecha inválido");
                    error.put("mensaje", "fechaInicio debe estar en formato ISO 8601 (ejemplo: 2025-11-18T00:00:00Z)");
                    return ResponseEntity.badRequest().body(error);
                }
            }

            if (fechaFin != null && !fechaFin.trim().isEmpty()) {
                try {
                    fin = Instant.parse(fechaFin);
                } catch (Exception e) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Formato de fecha inválido");
                    error.put("mensaje", "fechaFin debe estar en formato ISO 8601 (ejemplo: 2025-11-18T23:59:59Z)");
                    return ResponseEntity.badRequest().body(error);
                }
            }

            HistorialMovimientosResponseDTO response = movimientoService.consultarHistorialMovimientos(
                    idReactivo, idAlmacen, inicio, fin, idTipoAccion);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al consultar historial de movimientos");
            error.put("mensaje", e.getMessage());
            error.put("detalle", "Verifique los parámetros proporcionados");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

