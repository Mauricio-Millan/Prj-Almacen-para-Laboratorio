package org.example.rest_almacenlaboratorio.DTOs.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ActividadUsuarioDTO {
    private Integer idMovimiento;
    private LocalDateTime fechaHora;
    private String fecha;
    private String hora;
    private String tipoAccion;
    private String referencia;
    private String comentario;
    private String detalleOperacion;
    private Integer totalItems;
    private BigDecimal totalUnidades;
    private String almacenesOrigen;
    private String almacenesDestino;
    private String reactivosInvolucrados;
    private Integer cantidadReactivosDiferentes;
    private BigDecimal valorTotal;
    private Integer diasTranscurridos;

    public ActividadUsuarioDTO() {
    }

    // Getters y Setters
    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getDetalleOperacion() {
        return detalleOperacion;
    }

    public void setDetalleOperacion(String detalleOperacion) {
        this.detalleOperacion = detalleOperacion;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(BigDecimal totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    public String getAlmacenesOrigen() {
        return almacenesOrigen;
    }

    public void setAlmacenesOrigen(String almacenesOrigen) {
        this.almacenesOrigen = almacenesOrigen;
    }

    public String getAlmacenesDestino() {
        return almacenesDestino;
    }

    public void setAlmacenesDestino(String almacenesDestino) {
        this.almacenesDestino = almacenesDestino;
    }

    public String getReactivosInvolucrados() {
        return reactivosInvolucrados;
    }

    public void setReactivosInvolucrados(String reactivosInvolucrados) {
        this.reactivosInvolucrados = reactivosInvolucrados;
    }

    public Integer getCantidadReactivosDiferentes() {
        return cantidadReactivosDiferentes;
    }

    public void setCantidadReactivosDiferentes(Integer cantidadReactivosDiferentes) {
        this.cantidadReactivosDiferentes = cantidadReactivosDiferentes;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Integer getDiasTranscurridos() {
        return diasTranscurridos;
    }

    public void setDiasTranscurridos(Integer diasTranscurridos) {
        this.diasTranscurridos = diasTranscurridos;
    }
}

