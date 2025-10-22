package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.DTOs.Usuario.LineaTiempoUsuarioDTO;
import org.example.rest_almacenlaboratorio.Mapper.Usuario_Entity;
import org.example.rest_almacenlaboratorio.Service.Usuario_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class Usuario_Controller {

    @Autowired
    private Usuario_Service usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario_Entity>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario_Entity> obtenerPorId(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Usuario_Entity> obtenerPorNombre(@PathVariable String nombre) {
        return usuarioService.obtenerPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Usuario_Entity> obtenerPorDni(@PathVariable String dni) {
        return usuarioService.obtenerPorDni(dni)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario_Entity usuario) {
        if (usuarioService.existePorNombre(usuario.getNombre())) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }
        if (usuario.getDni() != null && usuarioService.existePorDni(usuario.getDni())) {
            return ResponseEntity.badRequest().body("El DNI ya existe");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.crear(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Usuario_Entity usuario) {
        try {
            return ResponseEntity.ok(usuarioService.actualizar(id, usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // ========================================================================
    // ENDPOINT PARA PROCEDIMIENTO ALMACENADO: Línea de Tiempo del Usuario
    // ========================================================================

    /**
     * Obtiene la línea de tiempo completa de actividades de un usuario
     *
     * Endpoint: GET /api/usuarios/{id}/linea-tiempo
     *
     * Parámetros opcionales:
     * - fechaInicio: Fecha y hora de inicio (formato: yyyy-MM-dd'T'HH:mm:ss)
     * - fechaFin: Fecha y hora de fin (formato: yyyy-MM-dd'T'HH:mm:ss)
     * - limite: Número máximo de actividades a retornar (default: 50)
     *
     * Ejemplo de uso:
     * GET /api/usuarios/1/linea-tiempo
     * GET /api/usuarios/1/linea-tiempo?fechaInicio=2025-01-01T00:00:00&fechaFin=2025-12-31T23:59:59&limite=100
     *
     * Retorna:
     * {
     *   "usuarioInfo": { información básica del usuario },
     *   "actividades": [ lista de actividades ordenadas por fecha ],
     *   "resumenEstadistico": { métricas agregadas del período },
     *   "distribucionPorTipo": [ distribución por tipo de acción ],
     *   "topReactivos": [ top 10 reactivos más movidos ]
     * }
     */
    @GetMapping("/{id}/linea-tiempo")
    public ResponseEntity<?> obtenerLineaTiempoUsuario(
            @PathVariable Integer id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Integer limite) {
        try {
            LineaTiempoUsuarioDTO lineaTiempo = usuarioService.obtenerLineaTiempoUsuario(id, fechaInicio, fechaFin, limite);

            // Verificar si el usuario existe
            if (lineaTiempo.getUsuarioInfo() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Usuario no encontrado");
                error.put("mensaje", "El usuario con ID " + id + " no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            return ResponseEntity.ok(lineaTiempo);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener línea de tiempo del usuario");
            error.put("mensaje", e.getMessage());
            error.put("detalle", "Verifique que el usuario exista y los parámetros sean correctos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
