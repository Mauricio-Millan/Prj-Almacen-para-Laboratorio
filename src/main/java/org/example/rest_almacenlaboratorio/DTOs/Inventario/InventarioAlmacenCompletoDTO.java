package org.example.rest_almacenlaboratorio.DTOs.Inventario;

import java.util.List;

public class InventarioAlmacenCompletoDTO {
    private AlmacenInfoDTO almacenInfo;
    private List<InventarioDetalladoDTO> inventarioDetallado;
    private ResumenAlmacenDTO resumen;

    public InventarioAlmacenCompletoDTO() {
    }

    public InventarioAlmacenCompletoDTO(AlmacenInfoDTO almacenInfo,
                                         List<InventarioDetalladoDTO> inventarioDetallado,
                                         ResumenAlmacenDTO resumen) {
        this.almacenInfo = almacenInfo;
        this.inventarioDetallado = inventarioDetallado;
        this.resumen = resumen;
    }

    public AlmacenInfoDTO getAlmacenInfo() {
        return almacenInfo;
    }

    public void setAlmacenInfo(AlmacenInfoDTO almacenInfo) {
        this.almacenInfo = almacenInfo;
    }

    public List<InventarioDetalladoDTO> getInventarioDetallado() {
        return inventarioDetallado;
    }

    public void setInventarioDetallado(List<InventarioDetalladoDTO> inventarioDetallado) {
        this.inventarioDetallado = inventarioDetallado;
    }

    public ResumenAlmacenDTO getResumen() {
        return resumen;
    }

    public void setResumen(ResumenAlmacenDTO resumen) {
        this.resumen = resumen;
    }
}
