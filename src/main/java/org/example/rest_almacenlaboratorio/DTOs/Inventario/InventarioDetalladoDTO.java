package org.example.rest_almacenlaboratorio.DTOs.Inventario;

public class InventarioDetalladoDTO {
    private Integer idInventario;
    private Integer idReactivo;
    private String nombreReactivo;
    private String marca;
    private Integer numeroLote;
    private Double precioUnitario;
    private String fechaExpiracion;
    private Integer cantidadInicialLote;
    private Integer stockActual;
    private String estadoStock;
    private String estadoExpiracion;
    private Integer diasParaExpiracion;

    public InventarioDetalladoDTO() {
    }

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public Integer getIdReactivo() {
        return idReactivo;
    }

    public void setIdReactivo(Integer idReactivo) {
        this.idReactivo = idReactivo;
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

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Integer getCantidadInicialLote() {
        return cantidadInicialLote;
    }

    public void setCantidadInicialLote(Integer cantidadInicialLote) {
        this.cantidadInicialLote = cantidadInicialLote;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public String getEstadoStock() {
        return estadoStock;
    }

    public void setEstadoStock(String estadoStock) {
        this.estadoStock = estadoStock;
    }

    public String getEstadoExpiracion() {
        return estadoExpiracion;
    }

    public void setEstadoExpiracion(String estadoExpiracion) {
        this.estadoExpiracion = estadoExpiracion;
    }

    public Integer getDiasParaExpiracion() {
        return diasParaExpiracion;
    }

    public void setDiasParaExpiracion(Integer diasParaExpiracion) {
        this.diasParaExpiracion = diasParaExpiracion;
    }
}

