package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.math.BigDecimal;

public class TrasladoMultipleResponseDTO {

    private Integer idMovimiento;
    private Integer totalLotesTrasladados;
    private BigDecimal totalUnidades;

    public TrasladoMultipleResponseDTO() {
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Integer getTotalLotesTrasladados() {
        return totalLotesTrasladados;
    }

    public void setTotalLotesTrasladados(Integer totalLotesTrasladados) {
        this.totalLotesTrasladados = totalLotesTrasladados;
    }

    public BigDecimal getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(BigDecimal totalUnidades) {
        this.totalUnidades = totalUnidades;
    }
}
