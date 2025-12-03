package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.util.List;

/**
 * DTO para la petición de ajuste múltiple de lotes
 * Corresponde a los parámetros del SP PA_RegistrarAjusteMultiple
 */
public class AjusteMultipleRequestDTO {

    private Integer idUsuario;
    private Integer idAlmacenOrigen;
    private String referencia;
    private String comentario;
    private List<AjusteItemDTO> ajustes;

    public AjusteMultipleRequestDTO() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdAlmacenOrigen() {
        return idAlmacenOrigen;
    }

    public void setIdAlmacenOrigen(Integer idAlmacenOrigen) {
        this.idAlmacenOrigen = idAlmacenOrigen;
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

    public List<AjusteItemDTO> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjusteItemDTO> ajustes) {
        this.ajustes = ajustes;
    }

    @Override
    public String toString() {
        return "AjusteMultipleRequestDTO{" +
                "idUsuario=" + idUsuario +
                ", idAlmacenOrigen=" + idAlmacenOrigen +
                ", referencia='" + referencia + '\'' +
                ", comentario='" + comentario + '\'' +
                ", ajustes=" + ajustes +
                '}';
    }
}

