package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.math.BigDecimal;

public class ConsumoMultipleResponseDTO {

    private Integer idMovimiento;
    private Integer totalLotesConsumidos;
    private BigDecimal totalUnidadesConsumidas;

    public ConsumoMultipleResponseDTO() {
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Integer getTotalLotesConsumidos() {
        return totalLotesConsumidos;
    }

    public void setTotalLotesConsumidos(Integer totalLotesConsumidos) {
        this.totalLotesConsumidos = totalLotesConsumidos;
    }

    public BigDecimal getTotalUnidadesConsumidas() {
        return totalUnidadesConsumidas;
    }

    public void setTotalUnidadesConsumidas(BigDecimal totalUnidadesConsumidas) {
        this.totalUnidadesConsumidas = totalUnidadesConsumidas;
    }
}

