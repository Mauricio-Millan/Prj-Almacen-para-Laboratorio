package org.example.rest_almacenlaboratorio.DTOs.Inventario;

public class ResumenAlmacenDTO {
    private Integer totalReactivosDistintos;
    private Integer totalLotes;
    private Integer totalUnidades;
    private Double valorTotalInventario;
    private Double promedioStockPorLote;
    private Integer lotesSinStock;
    private Integer lotesConStock;
    private Integer lotesStockBajo;
    private Integer lotesExpirados;
    private Integer lotesProximosExpirar;

    public ResumenAlmacenDTO() {
    }

    public Integer getTotalReactivosDistintos() {
        return totalReactivosDistintos;
    }

    public void setTotalReactivosDistintos(Integer totalReactivosDistintos) {
        this.totalReactivosDistintos = totalReactivosDistintos;
    }

    public Integer getTotalLotes() {
        return totalLotes;
    }

    public void setTotalLotes(Integer totalLotes) {
        this.totalLotes = totalLotes;
    }

    public Integer getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(Integer totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    public Double getValorTotalInventario() {
        return valorTotalInventario;
    }

    public void setValorTotalInventario(Double valorTotalInventario) {
        this.valorTotalInventario = valorTotalInventario;
    }

    public Double getPromedioStockPorLote() {
        return promedioStockPorLote;
    }

    public void setPromedioStockPorLote(Double promedioStockPorLote) {
        this.promedioStockPorLote = promedioStockPorLote;
    }

    public Integer getLotesSinStock() {
        return lotesSinStock;
    }

    public void setLotesSinStock(Integer lotesSinStock) {
        this.lotesSinStock = lotesSinStock;
    }

    public Integer getLotesConStock() {
        return lotesConStock;
    }

    public void setLotesConStock(Integer lotesConStock) {
        this.lotesConStock = lotesConStock;
    }

    public Integer getLotesStockBajo() {
        return lotesStockBajo;
    }

    public void setLotesStockBajo(Integer lotesStockBajo) {
        this.lotesStockBajo = lotesStockBajo;
    }

    public Integer getLotesExpirados() {
        return lotesExpirados;
    }

    public void setLotesExpirados(Integer lotesExpirados) {
        this.lotesExpirados = lotesExpirados;
    }

    public Integer getLotesProximosExpirar() {
        return lotesProximosExpirar;
    }

    public void setLotesProximosExpirar(Integer lotesProximosExpirar) {
        this.lotesProximosExpirar = lotesProximosExpirar;
    }
}

