package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.DTOs.Inventario.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class Inventario_Service {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public InventarioAlmacenCompletoDTO consultarInventarioDetallado(Integer idAlmacen, String nombreReactivo) {
        InventarioAlmacenCompletoDTO resultado = new InventarioAlmacenCompletoDTO();

        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            // Preparar la llamada al procedimiento almacenado
            String sql = "{CALL PA_ConsultarInventarioAlmacenDetallado(?, ?)}";

            try (CallableStatement stmt = conn.prepareCall(sql)) {
                // Establecer parámetros
                stmt.setInt(1, idAlmacen);
                if (nombreReactivo != null && !nombreReactivo.trim().isEmpty()) {
                    stmt.setString(2, nombreReactivo);
                } else {
                    stmt.setNull(2, Types.NVARCHAR);
                }

                // Ejecutar y obtener los múltiples ResultSets
                boolean hasResults = stmt.execute();

                // 1. Primer ResultSet: Información del almacén
                if (hasResults) {
                    try (ResultSet rs = stmt.getResultSet()) {
                        if (rs.next()) {
                            AlmacenInfoDTO almacenInfo = new AlmacenInfoDTO();
                            almacenInfo.setIdAlmacen(rs.getInt("ID_Almacen"));
                            almacenInfo.setNombreAlmacen(rs.getString("Nombre_Almacen"));
                            almacenInfo.setDireccion(rs.getString("Direccion"));
                            almacenInfo.setTelefono(rs.getString("Telefono"));
                            resultado.setAlmacenInfo(almacenInfo);
                        }
                    }
                }

                // 2. Segundo ResultSet: Inventario detallado
                if (stmt.getMoreResults()) {
                    List<InventarioDetalladoDTO> inventarioDetallado = new ArrayList<>();
                    try (ResultSet rs = stmt.getResultSet()) {
                        while (rs.next()) {
                            InventarioDetalladoDTO detalle = new InventarioDetalladoDTO();
                            detalle.setIdInventario(rs.getInt("ID_Inventario"));
                            detalle.setIdReactivo(rs.getInt("ID_Reactivo"));
                            detalle.setNombreReactivo(rs.getString("Nombre_Reactivo"));
                            detalle.setMarca(rs.getString("Marca"));
                            detalle.setNumeroLote(rs.getInt("Numero_Lote"));
                            detalle.setPrecioUnitario(rs.getDouble("Precio_Unitario"));
                            detalle.setFechaExpiracion(rs.getString("Fecha_Expiracion"));
                            detalle.setCantidadInicialLote(rs.getInt("Cantidad_Inicial_Lote"));
                            detalle.setStockActual(rs.getInt("Stock_Actual"));
                            detalle.setEstadoStock(rs.getString("Estado_Stock"));
                            detalle.setEstadoExpiracion(rs.getString("Estado_Expiracion"));
                            detalle.setDiasParaExpiracion(rs.getInt("Dias_Para_Expiracion"));
                            inventarioDetallado.add(detalle);
                        }
                    }
                    resultado.setInventarioDetallado(inventarioDetallado);
                }

                // 3. Tercer ResultSet: Resumen del almacén
                if (stmt.getMoreResults()) {
                    try (ResultSet rs = stmt.getResultSet()) {
                        if (rs.next()) {
                            ResumenAlmacenDTO resumen = new ResumenAlmacenDTO();
                            resumen.setTotalReactivosDistintos(rs.getInt("Total_Reactivos_Distintos"));
                            resumen.setTotalLotes(rs.getInt("Total_Lotes"));
                            resumen.setTotalUnidades(rs.getInt("Total_Unidades"));
                            resumen.setValorTotalInventario(rs.getDouble("Valor_Total_Inventario"));
                            resumen.setPromedioStockPorLote(rs.getDouble("Promedio_Stock_Por_Lote"));
                            resumen.setLotesSinStock(rs.getInt("Lotes_Sin_Stock"));
                            resumen.setLotesConStock(rs.getInt("Lotes_Con_Stock"));
                            resumen.setLotesStockBajo(rs.getInt("Lotes_Stock_Bajo"));
                            resumen.setLotesExpirados(rs.getInt("Lotes_Expirados"));
                            resumen.setLotesProximosExpirar(rs.getInt("Lotes_Proximos_Expirar"));
                            resultado.setResumen(resumen);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar inventario detallado: " + e.getMessage(), e);
        }

        return resultado;
    }
}

