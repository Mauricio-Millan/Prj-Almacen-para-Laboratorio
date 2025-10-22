package org.example.rest_almacenlaboratorio.Mapper;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "marca")
public class Marca_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "estado")
    private Boolean estado;

}