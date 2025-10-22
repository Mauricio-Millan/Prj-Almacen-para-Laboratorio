package org.example.rest_almacenlaboratorio.Mapper;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movimientolinea")
public class Movimientolinea_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_movimiento", nullable = false)
    private Movimiento_Entity idMovimiento;

    @ManyToOne
    @JoinColumn(name = "id_almacen_origen")
    private Almacen_Entity idAlmacenOrigen;

    @ManyToOne
    @JoinColumn(name = "id_almacen_destino")
    private Almacen_Entity idAlmacenDestino;

    @ManyToOne
    @JoinColumn(name = "id_lote", nullable = false)
    private Lote_Entity idLote;

    @Column(name = "cantidad_delta", nullable = false, precision = 10)
    private BigDecimal cantidadDelta;

    @Column(name = "precio_venta", precision = 10)
    private BigDecimal precioVenta;

}