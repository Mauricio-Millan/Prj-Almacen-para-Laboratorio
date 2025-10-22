package org.example.rest_almacenlaboratorio.DTOs.Inventario;

public class AlmacenInfoDTO {
    private Integer idAlmacen;
    private String nombreAlmacen;
    private String direccion;
    private String telefono;

    public AlmacenInfoDTO() {
    }

    public AlmacenInfoDTO(Integer idAlmacen, String nombreAlmacen, String direccion, String telefono) {
        this.idAlmacen = idAlmacen;
        this.nombreAlmacen = nombreAlmacen;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public Integer getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(Integer idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    public String getNombreAlmacen() {
        return nombreAlmacen;
    }

    public void setNombreAlmacen(String nombreAlmacen) {
        this.nombreAlmacen = nombreAlmacen;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

