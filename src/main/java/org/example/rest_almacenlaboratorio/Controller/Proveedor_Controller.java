package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.Mapper.Proveedor_Entity;
import org.example.rest_almacenlaboratorio.Service.Proveedor_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Rest_AlmacenLaboratorio/api/proveedores")
public class Proveedor_Controller {

    @Autowired
    private Proveedor_Service proveedorService;

    @GetMapping
    public ResponseEntity<List<Proveedor_Entity>> obtenerTodos() {
        return ResponseEntity.ok(proveedorService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor_Entity> obtenerPorId(@PathVariable Integer id) {
        return proveedorService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Proveedor_Entity> obtenerPorNombre(@PathVariable String nombre) {
        return proveedorService.obtenerPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<Proveedor_Entity> obtenerPorRuc(@PathVariable String ruc) {
        return proveedorService.obtenerPorRuc(ruc)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Proveedor_Entity> crear(@RequestBody Proveedor_Entity proveedor) {
        try {
            if (proveedorService.existePorRuc(proveedor.getRuc())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(proveedor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor_Entity> actualizar(@PathVariable Integer id, @RequestBody Proveedor_Entity proveedor) {
        try {
            return ResponseEntity.ok(proveedorService.actualizar(id, proveedor));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            proveedorService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

