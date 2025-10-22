package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.math.BigDecimal;

public class IngresoMultipleResponseDTO {

    private Integer idMovimiento;
    private Integer idCompra;
    private Integer totalLotesRegistrados;
    private BigDecimal totalUnidades;
    private BigDecimal valorTotal;

    public IngresoMultipleResponseDTO() {
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Integer getTotalLotesRegistrados() {
        return totalLotesRegistrados;
    }

    public void setTotalLotesRegistrados(Integer totalLotesRegistrados) {
        this.totalLotesRegistrados = totalLotesRegistrados;
    }

    public BigDecimal getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(BigDecimal totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}

