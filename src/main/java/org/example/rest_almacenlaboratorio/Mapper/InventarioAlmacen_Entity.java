package org.example.rest_almacenlaboratorio.Mapper;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inventario_almacen")
public class InventarioAlmacen_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_almacen", nullable = false)
    private Almacen_Entity idAlmacen;

    @ManyToOne
    @JoinColumn(name = "id_lote", nullable = false)
    private Lote_Entity idLote;

    @ColumnDefault("0")
    @Column(name = "stock", nullable = false, precision = 10)
    private BigDecimal stock;

}