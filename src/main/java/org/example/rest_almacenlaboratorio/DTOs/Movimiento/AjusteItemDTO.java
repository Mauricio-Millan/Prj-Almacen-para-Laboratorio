package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * DTO para representar un item de ajuste individual
 * Corresponde a cada elemento del array JSON que recibe el SP PA_RegistrarAjusteMultiple
 */
public class AjusteItemDTO {

    @JsonProperty("id_lote")
    private Integer idLote;

    @JsonProperty("cantidad_delta")
    private BigDecimal cantidadDelta;

    public AjusteItemDTO() {
    }

    public AjusteItemDTO(Integer idLote, BigDecimal cantidadDelta) {
        this.idLote = idLote;
        this.cantidadDelta = cantidadDelta;
    }

    public Integer getIdLote() {
        return idLote;
    }

    public void setIdLote(Integer idLote) {
        this.idLote = idLote;
    }

    public BigDecimal getCantidadDelta() {
        return cantidadDelta;
    }

    public void setCantidadDelta(BigDecimal cantidadDelta) {
        this.cantidadDelta = cantidadDelta;
    }

    @Override
    public String toString() {
        return "AjusteItemDTO{" +
                "idLote=" + idLote +
                ", cantidadDelta=" + cantidadDelta +
                '}';
    }
}

