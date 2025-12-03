package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.math.BigDecimal;

/**
 * DTO para representar el resumen de movimientos agrupado por tipo de acción
 * Corresponde al segundo ResultSet del SP PA_ConsultarHistorialMovimientos
 */
public class ResumenMovimientoDTO {

    private String tipoAccion;
    private Integer totalMovimientos;
    private BigDecimal totalUnidades;
    private BigDecimal valorTotal;

    public ResumenMovimientoDTO() {
    }

    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public Integer getTotalMovimientos() {
        return totalMovimientos;
    }

    public void setTotalMovimientos(Integer totalMovimientos) {
        this.totalMovimientos = totalMovimientos;
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

    @Override
    public String toString() {
        return "ResumenMovimientoDTO{" +
                "tipoAccion='" + tipoAccion + '\'' +
                ", totalMovimientos=" + totalMovimientos +
                ", totalUnidades=" + totalUnidades +
                ", valorTotal=" + valorTotal +
                '}';
    }
}

