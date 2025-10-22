package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Usuario_Entity;
import org.example.rest_almacenlaboratorio.Service.Usuario_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
