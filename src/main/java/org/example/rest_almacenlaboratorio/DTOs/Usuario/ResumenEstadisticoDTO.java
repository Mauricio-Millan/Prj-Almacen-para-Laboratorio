package org.example.rest_almacenlaboratorio.DTOs.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ResumenEstadisticoDTO {
    private Integer totalMovimientos;
    private Integer totalIngresos;
    private Integer totalConsumos;
    private Integer totalTraslados;
    private Integer totalAjustes;
    private Integer totalLotesMovidos;
    private Integer totalReactivosDiferentes;
    private Integer totalAlmacenesUsados;
    private BigDecimal totalUnidadesMovidas;
    private BigDecimal promedioUnidadesPorMovimiento;
    private BigDecimal valorTotalMovimientos;
    private LocalDateTime primeraActividad;
    private LocalDateTime ultimaActividad;
    private Integer diasActivo;
    private BigDecimal promedioMovimientosPorDia;

    public ResumenEstadisticoDTO() {
    }

    // Getters y Setters
    public Integer getTotalMovimientos() {
        return totalMovimientos;
    }

    public void setTotalMovimientos(Integer totalMovimientos) {
        this.totalMovimientos = totalMovimientos;
    }

    public Integer getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(Integer totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public Integer getTotalConsumos() {
        return totalConsumos;
    }

    public void setTotalConsumos(Integer totalConsumos) {
        this.totalConsumos = totalConsumos;
    }

    public Integer getTotalTraslados() {
        return totalTraslados;
    }

    public void setTotalTraslados(Integer totalTraslados) {
        this.totalTraslados = totalTraslados;
    }

    public Integer getTotalAjustes() {
        return totalAjustes;
    }

    public void setTotalAjustes(Integer totalAjustes) {
        this.totalAjustes = totalAjustes;
    }

    public Integer getTotalLotesMovidos() {
        return totalLotesMovidos;
    }

    public void setTotalLotesMovidos(Integer totalLotesMovidos) {
        this.totalLotesMovidos = totalLotesMovidos;
    }

    public Integer getTotalReactivosDiferentes() {
        return totalReactivosDiferentes;
    }

    public void setTotalReactivosDiferentes(Integer totalReactivosDiferentes) {
        this.totalReactivosDiferentes = totalReactivosDiferentes;
    }

    public Integer getTotalAlmacenesUsados() {
        return totalAlmacenesUsados;
    }

    public void setTotalAlmacenesUsados(Integer totalAlmacenesUsados) {
        this.totalAlmacenesUsados = totalAlmacenesUsados;
    }

    public BigDecimal getTotalUnidadesMovidas() {
        return totalUnidadesMovidas;
    }

    public void setTotalUnidadesMovidas(BigDecimal totalUnidadesMovidas) {
        this.totalUnidadesMovidas = totalUnidadesMovidas;
    }

    public BigDecimal getPromedioUnidadesPorMovimiento() {
        return promedioUnidadesPorMovimiento;
    }

    public void setPromedioUnidadesPorMovimiento(BigDecimal promedioUnidadesPorMovimiento) {
        this.promedioUnidadesPorMovimiento = promedioUnidadesPorMovimiento;
    }

    public BigDecimal getValorTotalMovimientos() {
        return valorTotalMovimientos;
    }

    public void setValorTotalMovimientos(BigDecimal valorTotalMovimientos) {
        this.valorTotalMovimientos = valorTotalMovimientos;
    }

    public LocalDateTime getPrimeraActividad() {
        return primeraActividad;
    }

    public void setPrimeraActividad(LocalDateTime primeraActividad) {
        this.primeraActividad = primeraActividad;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(LocalDateTime ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public Integer getDiasActivo() {
        return diasActivo;
    }

    public void setDiasActivo(Integer diasActivo) {
        this.diasActivo = diasActivo;
    }

    public BigDecimal getPromedioMovimientosPorDia() {
        return promedioMovimientosPorDia;
    }

    public void setPromedioMovimientosPorDia(BigDecimal promedioMovimientosPorDia) {
        this.promedioMovimientosPorDia = promedioMovimientosPorDia;
    }
}

