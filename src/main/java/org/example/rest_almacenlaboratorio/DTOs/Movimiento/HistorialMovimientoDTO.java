package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO para representar el detalle de un movimiento en el historial
 * Corresponde al primer ResultSet del SP PA_ConsultarHistorialMovimientos
 */
public class HistorialMovimientoDTO {

    private Integer idMovimiento;
    private Instant fecha;
    private String tipoAccion;
    private String usuario;
    private String referencia;
    private String comentario;
    private String nombreReactivo;
    private String marca;
    private Integer numeroLote;
    private String almacenOrigen;
    private String almacenDestino;
    private BigDecimal cantidad;
    private BigDecimal precioVenta;
    private BigDecimal valorTotal;

    public HistorialMovimientoDTO() {
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public String getNombreReactivo() {
        return nombreReactivo;
    }

    public void setNombreReactivo(String nombreReactivo) {
        this.nombreReactivo = nombreReactivo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(Integer numeroLote) {
        this.numeroLote = numeroLote;
    }

    public String getAlmacenOrigen() {
        return almacenOrigen;
    }

    public void setAlmacenOrigen(String almacenOrigen) {
        this.almacenOrigen = almacenOrigen;
    }

    public String getAlmacenDestino() {
        return almacenDestino;
    }

    public void setAlmacenDestino(String almacenDestino) {
        this.almacenDestino = almacenDestino;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public String toString() {
        return "HistorialMovimientoDTO{" +
                "idMovimiento=" + idMovimiento +
                ", fecha=" + fecha +
                ", tipoAccion='" + tipoAccion + '\'' +
                ", usuario='" + usuario + '\'' +
                ", referencia='" + referencia + '\'' +
                ", nombreReactivo='" + nombreReactivo + '\'' +
                ", marca='" + marca + '\'' +
                ", numeroLote=" + numeroLote +
                ", almacenOrigen='" + almacenOrigen + '\'' +
                ", almacenDestino='" + almacenDestino + '\'' +
                ", cantidad=" + cantidad +
                ", valorTotal=" + valorTotal +
                '}';
    }
}

