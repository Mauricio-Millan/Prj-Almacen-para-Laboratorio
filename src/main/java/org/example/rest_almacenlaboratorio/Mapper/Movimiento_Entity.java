package org.example.rest_almacenlaboratorio.Mapper;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movimiento")
public class Movimiento_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario_Entity idUsuario;

    @ManyToOne
    @JoinColumn(name = "id_tipo_accion", nullable = false)
    private Tipoaccion_Entity idTipoAccion;

    @Column(name = "referencia")
    private String referencia;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}