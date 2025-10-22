package org.example.rest_almacenlaboratorio.DTOs.Usuario;

import java.math.BigDecimal;

public class DistribucionTipoAccionDTO {
    private Integer idTipo;
    private String tipoAccion;
    private Integer cantidadMovimientos;
    private BigDecimal totalUnidades;
    private BigDecimal valorTotal;
    private BigDecimal porcentajeMovimientos;
    private BigDecimal porcentajeUnidades;

    public DistribucionTipoAccionDTO() {
    }

    // Getters y Setters
    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public Integer getCantidadMovimientos() {
        return cantidadMovimientos;
    }

    public void setCantidadMovimientos(Integer cantidadMovimientos) {
        this.cantidadMovimientos = cantidadMovimientos;
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

    public BigDecimal getPorcentajeMovimientos() {
        return porcentajeMovimientos;
    }

    public void setPorcentajeMovimientos(BigDecimal porcentajeMovimientos) {
        this.porcentajeMovimientos = porcentajeMovimientos;
    }

    public BigDecimal getPorcentajeUnidades() {
        return porcentajeUnidades;
    }

    public void setPorcentajeUnidades(BigDecimal porcentajeUnidades) {
        this.porcentajeUnidades = porcentajeUnidades;
    }
}

