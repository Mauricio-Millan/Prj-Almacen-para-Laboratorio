package org.example.rest_almacenlaboratorio.DTOs.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDTO {
    private String nombre;
    private String clave;
    private String dni;
    private LocalDate fechaNacimiento;
    private Integer idRol;
}

