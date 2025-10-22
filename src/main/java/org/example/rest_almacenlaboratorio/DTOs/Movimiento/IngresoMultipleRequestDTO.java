package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.util.List;

public class IngresoMultipleRequestDTO {

    private Integer idUsuario;
    private Integer idProveedor;
    private Integer idAlmacenDestino;
    private String referencia;
    private String comentario;
    private List<LoteIngresoDTO> lotes;

    public IngresoMultipleRequestDTO() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
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

    public List<LoteIngresoDTO> getLotes() {
        return lotes;
    }

    public void setLotes(List<LoteIngresoDTO> lotes) {
        this.lotes = lotes;
    }
}

