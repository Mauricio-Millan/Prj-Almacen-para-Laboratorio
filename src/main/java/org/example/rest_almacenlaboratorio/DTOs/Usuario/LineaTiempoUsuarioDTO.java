package org.example.rest_almacenlaboratorio.DTOs.Usuario;

import java.util.List;

public class LineaTiempoUsuarioDTO {
    private UsuarioInfoDTO usuarioInfo;
    private List<ActividadUsuarioDTO> actividades;
    private ResumenEstadisticoDTO resumenEstadistico;
    private List<DistribucionTipoAccionDTO> distribucionPorTipo;
    private List<TopReactivoDTO> topReactivos;

    public LineaTiempoUsuarioDTO() {
    }

    public LineaTiempoUsuarioDTO(UsuarioInfoDTO usuarioInfo,
                                  List<ActividadUsuarioDTO> actividades,
                                  ResumenEstadisticoDTO resumenEstadistico,
                                  List<DistribucionTipoAccionDTO> distribucionPorTipo,
                                  List<TopReactivoDTO> topReactivos) {
        this.usuarioInfo = usuarioInfo;
        this.actividades = actividades;
        this.resumenEstadistico = resumenEstadistico;
        this.distribucionPorTipo = distribucionPorTipo;
        this.topReactivos = topReactivos;
    }

    public UsuarioInfoDTO getUsuarioInfo() {
        return usuarioInfo;
    }

    public void setUsuarioInfo(UsuarioInfoDTO usuarioInfo) {
        this.usuarioInfo = usuarioInfo;
    }

    public List<ActividadUsuarioDTO> getActividades() {
        return actividades;
    }

    public void setActividades(List<ActividadUsuarioDTO> actividades) {
        this.actividades = actividades;
    }

    public ResumenEstadisticoDTO getResumenEstadistico() {
        return resumenEstadistico;
    }

    public void setResumenEstadistico(ResumenEstadisticoDTO resumenEstadistico) {
        this.resumenEstadistico = resumenEstadistico;
    }

    public List<DistribucionTipoAccionDTO> getDistribucionPorTipo() {
        return distribucionPorTipo;
    }

    public void setDistribucionPorTipo(List<DistribucionTipoAccionDTO> distribucionPorTipo) {
        this.distribucionPorTipo = distribucionPorTipo;
    }

    public List<TopReactivoDTO> getTopReactivos() {
        return topReactivos;
    }

    public void setTopReactivos(List<TopReactivoDTO> topReactivos) {
        this.topReactivos = topReactivos;
    }
}
