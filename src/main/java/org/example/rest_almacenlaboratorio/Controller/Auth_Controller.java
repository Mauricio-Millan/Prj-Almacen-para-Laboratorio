package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.DTOs.auth.LoginRequest;
import org.example.rest_almacenlaboratorio.Mapper.Usuario_Entity;
import org.example.rest_almacenlaboratorio.Service.Usuario_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class Auth_Controller {

    @Autowired
    private Usuario_Service usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return usuarioService.obtenerPorNombre(loginRequest.getNombre())
                .map(usuario -> {
                    if (usuario.getClave().equals(loginRequest.getClave())) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("mensaje", "Login exitoso");
                        response.put("usuario", usuario);
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.status(401).body("Credenciales inválidas");
                    }
                })
                .orElse(ResponseEntity.status(401).body("Credenciales inválidas"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Usuario_Entity usuario) {
        if (usuarioService.existePorNombre(usuario.getNombre())) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }
        if (usuario.getDni() != null && usuarioService.existePorDni(usuario.getDni())) {
            return ResponseEntity.badRequest().body("El DNI ya existe");
        }
        return ResponseEntity.ok(usuarioService.crear(usuario));
    }
}

