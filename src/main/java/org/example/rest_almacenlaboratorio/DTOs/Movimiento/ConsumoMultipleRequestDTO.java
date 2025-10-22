package org.example.rest_almacenlaboratorio.DTOs.Movimiento;

import java.util.List;

public class ConsumoMultipleRequestDTO {

    private Integer idUsuario;
    private Integer idDepartamento;
    private Integer idAlmacenOrigen;
    private String referencia;
    private String comentario;
    private List<ConsumoItemDTO> consumos;

    public ConsumoMultipleRequestDTO() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
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

    public List<ConsumoItemDTO> getConsumos() {
        return consumos;
    }

    public void setConsumos(List<ConsumoItemDTO> consumos) {
        this.consumos = consumos;
    }
}

