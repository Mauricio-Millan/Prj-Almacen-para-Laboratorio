package org.example.rest_almacenlaboratorio.Service;

import org.example.rest_almacenlaboratorio.DTOs.Usuario.*;
import org.example.rest_almacenlaboratorio.Mapper.Usuario_Entity;
import org.example.rest_almacenlaboratorio.Repository.Usuario_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Usuario_Service {

    @Autowired
    private Usuario_Repository usuarioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ========================================================================
    // MÉTODOS CRUD BÁSICOS
    // ========================================================================

    public List<Usuario_Entity> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario_Entity> obtenerPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario_Entity> obtenerPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    public Optional<Usuario_Entity> obtenerPorDni(String dni) {
        return usuarioRepository.findByDni(dni);
    }

    public Usuario_Entity crear(Usuario_Entity usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario_Entity actualizar(Integer id, Usuario_Entity usuario) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNombre(usuario.getNombre());
                    usuarioExistente.setDni(usuario.getDni());
                    if (usuario.getClave() != null && !usuario.getClave().isEmpty()) {
                        usuarioExistente.setClave(usuario.getClave());
                    }
                    usuarioExistente.setFechaNacimiento(usuario.getFechaNacimiento());
                    usuarioExistente.setIdRol(usuario.getIdRol());
                    return usuarioRepository.save(usuarioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void eliminar(Integer id) {
        usuarioRepository.deleteById(id);
    }

    public boolean existePorNombre(String nombre) {
        return usuarioRepository.existsByNombre(nombre);
    }

    public boolean existePorDni(String dni) {
        return usuarioRepository.existsByDni(dni);
    }

    // ========================================================================
    // MÉTODO PARA PROCEDIMIENTO ALMACENADO: Línea de Tiempo del Usuario
    // ========================================================================

    /**
     * Obtiene la línea de tiempo completa de actividades de un usuario
     * Ejecuta el procedimiento PA_ObtenerLineaTiempoUsuario que retorna 5 ResultSets:
     * 1. Información del usuario
     * 2. Línea de tiempo de actividades
     * 3. Resumen estadístico del período
     * 4. Distribución por tipo de acción
     * 5. Top 10 reactivos más movidos
     */
    public LineaTiempoUsuarioDTO obtenerLineaTiempoUsuario(Integer idUsuario,
                                                            LocalDateTime fechaInicio,
                                                            LocalDateTime fechaFin,
                                                            Integer limite) throws Exception {
        return jdbcTemplate.execute((Connection conn) -> {
            String sql = "{CALL PA_ObtenerLineaTiempoUsuario(?, ?, ?, ?)}";

            try (CallableStatement cs = conn.prepareCall(sql)) {
                // Configurar parámetros
                cs.setInt(1, idUsuario);

                if (fechaInicio != null) {
                    cs.setTimestamp(2, Timestamp.valueOf(fechaInicio));
                } else {
                    cs.setNull(2, Types.TIMESTAMP);
                }

                if (fechaFin != null) {
                    cs.setTimestamp(3, Timestamp.valueOf(fechaFin));
                } else {
                    cs.setNull(3, Types.TIMESTAMP);
                }

                if (limite != null) {
                    cs.setInt(4, limite);
                } else {
                    cs.setInt(4, 50); // Valor por defecto
                }

                LineaTiempoUsuarioDTO resultado = new LineaTiempoUsuarioDTO();
                boolean hasResults = cs.execute();

                // RESULTSET 1: Información del Usuario
                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (rs.next()) {
                            UsuarioInfoDTO usuarioInfo = new UsuarioInfoDTO();
                            usuarioInfo.setIdUsuario(rs.getInt("ID_Usuario"));
                            usuarioInfo.setNombreUsuario(rs.getString("Nombre_Usuario"));
                            usuarioInfo.setDni(rs.getString("DNI"));
                            usuarioInfo.setRol(rs.getString("Rol"));
                            Date fechaNac = rs.getDate("Fecha_Nacimiento");
                            if (fechaNac != null) {
                                usuarioInfo.setFechaNacimiento(fechaNac.toLocalDate());
                            }
                            resultado.setUsuarioInfo(usuarioInfo);
                        }
                    }
                }

                // RESULTSET 2: Línea de Tiempo de Actividades
                if (cs.getMoreResults()) {
                    List<ActividadUsuarioDTO> actividades = new ArrayList<>();
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            ActividadUsuarioDTO actividad = new ActividadUsuarioDTO();
                            actividad.setIdMovimiento(rs.getInt("ID_Movimiento"));
                            Timestamp fechaHora = rs.getTimestamp("Fecha_Hora");
                            if (fechaHora != null) {
                                actividad.setFechaHora(fechaHora.toLocalDateTime());
                            }
                            actividad.setFecha(rs.getString("Fecha"));
                            actividad.setHora(rs.getString("Hora"));
                            actividad.setTipoAccion(rs.getString("Tipo_Accion"));
                            actividad.setReferencia(rs.getString("Referencia"));
                            actividad.setComentario(rs.getString("Comentario"));
                            actividad.setDetalleOperacion(rs.getString("Detalle_Operacion"));
                            actividad.setTotalItems(rs.getInt("Total_Items"));
                            actividad.setTotalUnidades(rs.getBigDecimal("Total_Unidades"));
                            actividad.setAlmacenesOrigen(rs.getString("Almacenes_Origen"));
                            actividad.setAlmacenesDestino(rs.getString("Almacenes_Destino"));
                            actividad.setReactivosInvolucrados(rs.getString("Reactivos_Involucrados"));
                            actividad.setCantidadReactivosDiferentes(rs.getInt("Cantidad_Reactivos_Diferentes"));
                            actividad.setValorTotal(rs.getBigDecimal("Valor_Total"));
                            actividad.setDiasTranscurridos(rs.getInt("Dias_Transcurridos"));
                            actividades.add(actividad);
                        }
                    }
                    resultado.setActividades(actividades);
                }

                // RESULTSET 3: Resumen Estadístico
                if (cs.getMoreResults()) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (rs.next()) {
                            ResumenEstadisticoDTO resumen = new ResumenEstadisticoDTO();
                            resumen.setTotalMovimientos(rs.getInt("Total_Movimientos"));
                            resumen.setTotalIngresos(rs.getInt("Total_Ingresos"));
                            resumen.setTotalConsumos(rs.getInt("Total_Consumos"));
                            resumen.setTotalTraslados(rs.getInt("Total_Traslados"));
                            resumen.setTotalAjustes(rs.getInt("Total_Ajustes"));
                            resumen.setTotalLotesMovidos(rs.getInt("Total_Lotes_Movidos"));
                            resumen.setTotalReactivosDiferentes(rs.getInt("Total_Reactivos_Diferentes"));
                            resumen.setTotalAlmacenesUsados(rs.getInt("Total_Almacenes_Usados"));
                            resumen.setTotalUnidadesMovidas(rs.getBigDecimal("Total_Unidades_Movidas"));
                            resumen.setPromedioUnidadesPorMovimiento(rs.getBigDecimal("Promedio_Unidades_Por_Movimiento"));
                            resumen.setValorTotalMovimientos(rs.getBigDecimal("Valor_Total_Movimientos"));
                            Timestamp primera = rs.getTimestamp("Primera_Actividad");
                            if (primera != null) {
                                resumen.setPrimeraActividad(primera.toLocalDateTime());
                            }
                            Timestamp ultima = rs.getTimestamp("Ultima_Actividad");
                            if (ultima != null) {
                                resumen.setUltimaActividad(ultima.toLocalDateTime());
                            }
                            resumen.setDiasActivo(rs.getInt("Dias_Activo"));
                            resumen.setPromedioMovimientosPorDia(rs.getBigDecimal("Promedio_Movimientos_Por_Dia"));
                            resultado.setResumenEstadistico(resumen);
                        }
                    }
                }

                // RESULTSET 4: Distribución por Tipo de Acción
                if (cs.getMoreResults()) {
                    List<DistribucionTipoAccionDTO> distribucion = new ArrayList<>();
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            DistribucionTipoAccionDTO dist = new DistribucionTipoAccionDTO();
                            dist.setIdTipo(rs.getInt("ID_Tipo"));
                            dist.setTipoAccion(rs.getString("Tipo_Accion"));
                            dist.setCantidadMovimientos(rs.getInt("Cantidad_Movimientos"));
                            dist.setTotalUnidades(rs.getBigDecimal("Total_Unidades"));
                            dist.setValorTotal(rs.getBigDecimal("Valor_Total"));
                            dist.setPorcentajeMovimientos(rs.getBigDecimal("Porcentaje_Movimientos"));
                            dist.setPorcentajeUnidades(rs.getBigDecimal("Porcentaje_Unidades"));
                            distribucion.add(dist);
                        }
                    }
                    resultado.setDistribucionPorTipo(distribucion);
                }

                // RESULTSET 5: Top 10 Reactivos
                if (cs.getMoreResults()) {
                    List<TopReactivoDTO> topReactivos = new ArrayList<>();
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            TopReactivoDTO top = new TopReactivoDTO();
                            top.setIdReactivo(rs.getInt("ID_Reactivo"));
                            top.setNombreReactivo(rs.getString("Nombre_Reactivo"));
                            top.setMarca(rs.getString("Marca"));
                            top.setVecesMovido(rs.getInt("Veces_Movido"));
                            top.setTotalUnidades(rs.getBigDecimal("Total_Unidades"));
                            top.setPromedioUnidadesPorMovimiento(rs.getBigDecimal("Promedio_Unidades_Por_Movimiento"));
                            top.setValorTotal(rs.getBigDecimal("Valor_Total"));
                            Timestamp primeraVez = rs.getTimestamp("Primera_Vez");
                            if (primeraVez != null) {
                                top.setPrimeraVez(primeraVez.toLocalDateTime());
                            }
                            Timestamp ultimaVez = rs.getTimestamp("Ultima_Vez");
                            if (ultimaVez != null) {
                                top.setUltimaVez(ultimaVez.toLocalDateTime());
                            }
                            top.setDiasEntrePrimeraYUltima(rs.getInt("Dias_Entre_Primera_Y_Ultima"));
                            topReactivos.add(top);
                        }
                    }
                    resultado.setTopReactivos(topReactivos);
                }

                return resultado;
            }
        });
    }
}

