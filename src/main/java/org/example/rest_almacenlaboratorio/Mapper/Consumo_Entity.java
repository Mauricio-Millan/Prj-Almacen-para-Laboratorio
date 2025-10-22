package org.example.rest_almacenlaboratorio.Mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "consumo")
public class Consumo_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario_Entity idUsuario;

    @ManyToOne
    @JoinColumn(name = "id_movimiento")
    private Movimiento_Entity idMovimiento;

    @ManyToOne
    @JoinColumn(name = "id_departamento")
    private Departamento_Entity idDepartamento;

    @Column(name = "fecha")
    private LocalDate fecha;

}