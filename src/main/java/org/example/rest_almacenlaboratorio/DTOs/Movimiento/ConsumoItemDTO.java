package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class ConsumoItemDTO {

    @JsonProperty("id_lote")
    private Integer idLote;

    @JsonProperty("cantidad")
    private BigDecimal cantidad;

    public ConsumoItemDTO() {
    }

    public Integer getIdLote() {
        return idLote;
    }

    public void setIdLote(Integer idLote) {
        this.idLote = idLote;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
}

