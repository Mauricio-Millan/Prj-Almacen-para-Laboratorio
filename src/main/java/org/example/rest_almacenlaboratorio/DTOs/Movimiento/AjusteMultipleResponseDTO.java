package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.math.BigDecimal;

/**
 * DTO para la respuesta del ajuste múltiple de lotes
 * Corresponde al resultado del SP PA_RegistrarAjusteMultiple
 */
public class AjusteMultipleResponseDTO {

    private Integer idMovimiento;
    private Integer totalLotesAjustados;
    private Integer ajustesPositivos;
    private Integer ajustesNegativos;
    private BigDecimal totalIncrementos;
    private BigDecimal totalDecrementos;
    private BigDecimal deltaNeto;

    public AjusteMultipleResponseDTO() {
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Integer getTotalLotesAjustados() {
        return totalLotesAjustados;
    }

    public void setTotalLotesAjustados(Integer totalLotesAjustados) {
        this.totalLotesAjustados = totalLotesAjustados;
    }

    public Integer getAjustesPositivos() {
        return ajustesPositivos;
    }

    public void setAjustesPositivos(Integer ajustesPositivos) {
        this.ajustesPositivos = ajustesPositivos;
    }

    public Integer getAjustesNegativos() {
        return ajustesNegativos;
    }

    public void setAjustesNegativos(Integer ajustesNegativos) {
        this.ajustesNegativos = ajustesNegativos;
    }

    public BigDecimal getTotalIncrementos() {
        return totalIncrementos;
    }

    public void setTotalIncrementos(BigDecimal totalIncrementos) {
        this.totalIncrementos = totalIncrementos;
    }

    public BigDecimal getTotalDecrementos() {
        return totalDecrementos;
    }

    public void setTotalDecrementos(BigDecimal totalDecrementos) {
        this.totalDecrementos = totalDecrementos;
    }

    public BigDecimal getDeltaNeto() {
        return deltaNeto;
    }

    public void setDeltaNeto(BigDecimal deltaNeto) {
        this.deltaNeto = deltaNeto;
    }

    @Override
    public String toString() {
        return "AjusteMultipleResponseDTO{" +
                "idMovimiento=" + idMovimiento +
                ", totalLotesAjustados=" + totalLotesAjustados +
                ", ajustesPositivos=" + ajustesPositivos +
                ", ajustesNegativos=" + ajustesNegativos +
                ", totalIncrementos=" + totalIncrementos +
                ", totalDecrementos=" + totalDecrementos +
                ", deltaNeto=" + deltaNeto +
                '}';
    }
}
