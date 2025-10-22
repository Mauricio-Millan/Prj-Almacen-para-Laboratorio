package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.util.List;

public class TrasladoMultipleRequestDTO {

    private Integer idUsuario;
    private Integer idAlmacenOrigen;
    private Integer idAlmacenDestino;
    private String referencia;
    private String comentario;
    private List<TrasladoItemDTO> traslados;

    public TrasladoMultipleRequestDTO() {
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

    public Integer getIdAlmacenDestino() {
        return idAlmacenDestino;
    }

    public void setIdAlmacenDestino(Integer idAlmacenDestino) {
        this.idAlmacenDestino = idAlmacenDestino;
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

    public List<TrasladoItemDTO> getTraslados() {
        return traslados;
    }

    public void setTraslados(List<TrasladoItemDTO> traslados) {
        this.traslados = traslados;
    }
}

