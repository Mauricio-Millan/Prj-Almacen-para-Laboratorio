package org.example.rest_almacenlaboratorio.Controller;

import org.apache.commons.lang3.StringUtils;
import org.example.rest_almacenlaboratorio.Service.Lote_ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller independiente para exportaciones de lotes a Excel
 * No modifica Lote_Controller existente
 */
@RestController
@RequestMapping("Rest_AlmacenLaboratorio/api/lotesexport")
@CrossOrigin(origins = "*")
public class Lote_ExportController {

    @Autowired
    private Lote_ExportService exportService;

    private static final SimpleDateFormat FILE_DATE_FORMAT =
        new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     * GET /api/lotes/export/excel
     * Exporta todos los lotes a Excel
     *
     * Ejemplo: GET http://localhost:8080/api/lotes/export/excel
     */
    @GetMapping("/excel")
    public ResponseEntity<?> exportarTodos() {
        try {
            byte[] excelBytes = exportService.exportarLotesAExcel();
            return crearRespuestaExcel(excelBytes, "lotes_todos");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearMensajeError("Error al generar archivo Excel: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearMensajeError("Error inesperado: " + e.getMessage()));
        }
    }

    /**
     * GET /api/lotes/export/excel/reactivo/{id}
     * Exporta lotes de un reactivo específico
     *
     * Ejemplo: GET http://localhost:8080/api/lotes/export/excel/reactivo/5
     */
    @GetMapping("/excel/reactivo/{id}")
    public ResponseEntity<?> exportarPorReactivo(@PathVariable Integer id) {
        try {
            byte[] excelBytes = exportService.exportarLotesPorReactivo(id);
            return crearRespuestaExcel(excelBytes, "lotes_reactivo_" + id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearMensajeError(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearMensajeError("Error al generar archivo Excel: " + e.getMessage()));
        }
    }

    /**
     * GET /api/lotes/export/excel/proximos-vencer
     * Exporta lotes próximos a vencer antes de cierta fecha
     *
     * Parámetro: fecha (formato: yyyy-MM-dd)
     * Ejemplo: GET http://localhost:8080/api/lotes/export/excel/proximos-vencer?fecha=2025-12-31
     */
    @GetMapping("/excel/proximos-vencer")
    public ResponseEntity<?> exportarProximosAVencer(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha) {
        try {
            byte[] excelBytes = exportService.exportarLotesProximosAVencer(fecha);
            return crearRespuestaExcel(excelBytes, "lotes_proximos_vencer");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearMensajeError(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearMensajeError("Error al generar archivo Excel: " + e.getMessage()));
        }
    }

    /**
     * GET /api/lotes/export/excel/estado/{estado}
     * Exporta lotes por estado (activo/inactivo)
     *
     * Parámetro: estado (true = activos, false = inactivos)
     * Ejemplo: GET http://localhost:8080/api/lotes/export/excel/estado/true
     */
    @GetMapping("/excel/estado/{estado}")
    public ResponseEntity<?> exportarPorEstado(@PathVariable Boolean estado) {
        try {
            byte[] excelBytes = exportService.exportarLotesPorEstado(estado);
            String sufijo = estado ? "activos" : "inactivos";
            return crearRespuestaExcel(excelBytes, "lotes_" + sufijo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearMensajeError(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearMensajeError("Error al generar archivo Excel: " + e.getMessage()));
        }
    }

    /**
     * GET /api/lotes/export/excel/rango-expiracion
     * Exporta lotes por rango de fecha de expiración
     *
     * Parámetros: fechaInicio y fechaFin (formato: yyyy-MM-dd)
     * Ejemplo: GET http://localhost:8080/api/lotes/export/excel/rango-expiracion?fechaInicio=2025-01-01&fechaFin=2025-12-31
     */
    @GetMapping("/excel/rango-expiracion")
    public ResponseEntity<?> exportarPorRangoExpiracion(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
        try {
            byte[] excelBytes = exportService.exportarLotesPorRangoExpiracion(fechaInicio, fechaFin);
            return crearRespuestaExcel(excelBytes, "lotes_rango_expiracion");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearMensajeError(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearMensajeError("Error al generar archivo Excel: " + e.getMessage()));
        }
    }

    /**
     * GET /api/lotes/export/info
     * Retorna información sobre los endpoints de exportación disponibles
     */
    @GetMapping("/info")
    public ResponseEntity<?> obtenerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("servicio", "Exportación de Lotes a Excel");
        info.put("version", "1.0");
        info.put("endpoints", new String[] {
            "GET /api/lotes/export/excel - Exporta todos los lotes",
            "GET /api/lotes/export/excel/reactivo/{id} - Exporta lotes por reactivo",
            "GET /api/lotes/export/excel/proximos-vencer?fecha=yyyy-MM-dd - Exporta lotes próximos a vencer",
            "GET /api/lotes/export/excel/estado/{true|false} - Exporta lotes por estado",
            "GET /api/lotes/export/excel/rango-expiracion?fechaInicio=yyyy-MM-dd&fechaFin=yyyy-MM-dd - Exporta por rango de expiración"
        });
        info.put("formato", "Excel (.xlsx)");
        return ResponseEntity.ok(info);
    }

    // ========================================================================
    // MÉTODOS DE UTILIDAD
    // ========================================================================

    /**
     * Crea respuesta HTTP con el archivo Excel
     */
    private ResponseEntity<byte[]> crearRespuestaExcel(byte[] contenido, String nombreBase) {
        String timestamp = FILE_DATE_FORMAT.format(new Date());
        String fileName = String.format("%s_%s.xlsx", nombreBase, timestamp);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(contenido.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(contenido);
    }

    /**
     * Crea un mapa con mensaje de error
     */
    private Map<String, String> crearMensajeError(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", StringUtils.defaultIfBlank(mensaje, "Error desconocido"));
        error.put("timestamp", new Date().toString());
        return error;
    }
}

