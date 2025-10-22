package org.example.rest_almacenlaboratorio.DTOs.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TopReactivoDTO {
    private Integer idReactivo;
    private String nombreReactivo;
    private String marca;
    private Integer vecesMovido;
    private BigDecimal totalUnidades;
    private BigDecimal promedioUnidadesPorMovimiento;
    private BigDecimal valorTotal;
    private LocalDateTime primeraVez;
    private LocalDateTime ultimaVez;
    private Integer diasEntrePrimeraYUltima;

    public TopReactivoDTO() {
    }

    // Getters y Setters
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

    public Integer getVecesMovido() {
        return vecesMovido;
    }

    public void setVecesMovido(Integer vecesMovido) {
        this.vecesMovido = vecesMovido;
    }

    public BigDecimal getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(BigDecimal totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    public BigDecimal getPromedioUnidadesPorMovimiento() {
        return promedioUnidadesPorMovimiento;
    }

    public void setPromedioUnidadesPorMovimiento(BigDecimal promedioUnidadesPorMovimiento) {
        this.promedioUnidadesPorMovimiento = promedioUnidadesPorMovimiento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getPrimeraVez() {
        return primeraVez;
    }

    public void setPrimeraVez(LocalDateTime primeraVez) {
        this.primeraVez = primeraVez;
    }

    public LocalDateTime getUltimaVez() {
        return ultimaVez;
    }

    public void setUltimaVez(LocalDateTime ultimaVez) {
        this.ultimaVez = ultimaVez;
    }

    public Integer getDiasEntrePrimeraYUltima() {
        return diasEntrePrimeraYUltima;
    }

    public void setDiasEntrePrimeraYUltima(Integer diasEntrePrimeraYUltima) {
        this.diasEntrePrimeraYUltima = diasEntrePrimeraYUltima;
    }
}

