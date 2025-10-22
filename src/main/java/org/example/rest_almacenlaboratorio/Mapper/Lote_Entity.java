package org.example.rest_almacenlaboratorio.Mapper;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lote")
public class Lote_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_reactivo", nullable = false)
    private Reactivo_Entity idReactivo;

    @ManyToOne
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra_Entity idCompra;

    @Column(name = "cantidad_inicial", precision = 10, scale = 2)
    private BigDecimal cantidadInicial;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "fecha_expiracion")
    private Date fechaExpiracion;

    @Column(name = "estado")
    private Boolean estado;

}