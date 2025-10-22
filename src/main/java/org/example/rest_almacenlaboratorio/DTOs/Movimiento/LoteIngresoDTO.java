package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public class LoteIngresoDTO {

    @JsonProperty("id_reactivo")
    private Integer idReactivo;

    @JsonProperty("cantidad")
    private BigDecimal cantidad;

    @JsonProperty("precio_unitario")
    private BigDecimal precioUnitario;

    @JsonProperty("fecha_expiracion")
    private LocalDate fechaExpiracion;

    public LoteIngresoDTO() {
    }

    public Integer getIdReactivo() {
        return idReactivo;
    }

    public void setIdReactivo(Integer idReactivo) {
        this.idReactivo = idReactivo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDate fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
}

