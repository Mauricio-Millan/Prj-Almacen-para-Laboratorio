package org.example.rest_almacenlaboratorio.DTOs.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.rest_almacenlaboratorio.Mapper.Usuario_Entity;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {
    private Integer id;
    private String nombre;
    private String dni;
    private LocalDate fechaNacimiento;
    private Integer rolId;
    private String rolNombre;

    public UsuarioResponseDTO(Usuario_Entity usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.dni = usuario.getDni();
        this.fechaNacimiento = usuario.getFechaNacimiento();
        if (usuario.getIdRol() != null) {
            this.rolId = usuario.getIdRol().getId();
            this.rolNombre = usuario.getIdRol().getNombre();
        }
    }
}
