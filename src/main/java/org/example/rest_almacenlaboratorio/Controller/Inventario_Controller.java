package org.example.rest_almacenlaboratorio.Controller;

import org.example.rest_almacenlaboratorio.DTOs.Inventario.InventarioAlmacenCompletoDTO;
import org.example.rest_almacenlaboratorio.Service.Inventario_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Rest_AlmacenLaboratorio/api/inventario")
@CrossOrigin(origins = "*")
public class Inventario_Controller {

    @Autowired
    private Inventario_Service inventarioService;

    /**
     * Consulta detallada del inventario de un almacén específico
     *
     * @param idAlmacen ID del almacén a consultar
     * @param nombreReactivo (Opcional) Filtro por nombre de reactivo
     * @return DTO con información del almacén, inventario detallado y resumen
     */
    @GetMapping("/almacen/{idAlmacen}/detallado")
    public ResponseEntity<?> consultarInventarioDetallado(
            @PathVariable Integer idAlmacen,
            @RequestParam(required = false) String nombreReactivo) {
        try {
            InventarioAlmacenCompletoDTO resultado =
                inventarioService.consultarInventarioDetallado(idAlmacen, nombreReactivo);

            if (resultado.getAlmacenInfo() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El almacén con ID " + idAlmacen + " no existe.");
            }

            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            // Si el mensaje contiene "no existe", retornar 404
            if (e.getMessage().contains("no existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al consultar el inventario: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error inesperado: " + e.getMessage());
        }
    }
}

