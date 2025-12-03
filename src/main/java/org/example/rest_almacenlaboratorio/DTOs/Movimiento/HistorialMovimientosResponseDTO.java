package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.util.List;

/**
 * DTO para encapsular la respuesta completa del historial de movimientos
 * Incluye el detalle de movimientos y el resumen agrupado
 */
public class HistorialMovimientosResponseDTO {

    private List<HistorialMovimientoDTO> detalleMovimientos;
    private List<ResumenMovimientoDTO> resumenPorTipo;

    public HistorialMovimientosResponseDTO() {
    }

    public HistorialMovimientosResponseDTO(List<HistorialMovimientoDTO> detalleMovimientos,
                                           List<ResumenMovimientoDTO> resumenPorTipo) {
        this.detalleMovimientos = detalleMovimientos;
        this.resumenPorTipo = resumenPorTipo;
    }

    public List<HistorialMovimientoDTO> getDetalleMovimientos() {
        return detalleMovimientos;
    }

    public void setDetalleMovimientos(List<HistorialMovimientoDTO> detalleMovimientos) {
        this.detalleMovimientos = detalleMovimientos;
    }

    public List<ResumenMovimientoDTO> getResumenPorTipo() {
        return resumenPorTipo;
    }

    public void setResumenPorTipo(List<ResumenMovimientoDTO> resumenPorTipo) {
        this.resumenPorTipo = resumenPorTipo;
    }

    @Override
    public String toString() {
        return "HistorialMovimientosResponseDTO{" +
                "detalleMovimientos=" + (detalleMovimientos != null ? detalleMovimientos.size() : 0) + " registros" +
                ", resumenPorTipo=" + (resumenPorTipo != null ? resumenPorTipo.size() : 0) + " tipos" +
                '}';
    }
}
