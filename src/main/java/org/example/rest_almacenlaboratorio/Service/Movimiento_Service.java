package org.example.rest_almacenlaboratorio.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.rest_almacenlaboratorio.DTOs.Movimiento.*;
import org.example.rest_almacenlaboratorio.Mapper.Movimiento_Entity;
import org.example.rest_almacenlaboratorio.Repository.Movimiento_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class Movimiento_Service {

    @Autowired
    private Movimiento_Repository movimientoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper;

    public Movimiento_Service() {
        this.objectMapper = new ObjectMapper();
        // Registrar el módulo de Java 8 date/time para soportar LocalDate, LocalDateTime, etc.
        this.objectMapper.registerModule(new JavaTimeModule());
        // Configurar para que serialice las fechas como strings en formato ISO-8601
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // ========================================================================
    // MÉTODOS CRUD BÁSICOS (usando Repository)
    // ========================================================================

    public List<Movimiento_Entity> obtenerTodos() {
        return movimientoRepository.findAll();
    }

    public Optional<Movimiento_Entity> obtenerPorId(Integer id) {
        return movimientoRepository.findById(id);
    }

    public List<Movimiento_Entity> obtenerPorUsuario(Integer idUsuario) {
        return movimientoRepository.findByIdUsuario_Id(idUsuario);
    }

    public List<Movimiento_Entity> obtenerPorTipoAccion(Integer idTipoAccion) {
        return movimientoRepository.findByIdTipoAccion_Id(idTipoAccion);
    }

    public List<Movimiento_Entity> obtenerPorRangoFechas(Instant fechaInicio, Instant fechaFin) {
        return movimientoRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    public List<Movimiento_Entity> obtenerPorReferencia(String referencia) {
        return movimientoRepository.findByReferencia(referencia);
    }

    public Movimiento_Entity crear(Movimiento_Entity movimiento) {
        if (movimiento.getCreatedAt() == null) {
            movimiento.setCreatedAt(Instant.now());
        }
        if (movimiento.getFecha() == null) {
            movimiento.setFecha(Instant.now());
        }
        return movimientoRepository.save(movimiento);
    }

    public Movimiento_Entity actualizar(Integer id, Movimiento_Entity movimiento) {
        return movimientoRepository.findById(id)
                .map(movimientoExistente -> {
                    movimientoExistente.setFecha(movimiento.getFecha());
                    movimientoExistente.setIdUsuario(movimiento.getIdUsuario());
                    movimientoExistente.setIdTipoAccion(movimiento.getIdTipoAccion());
                    movimientoExistente.setReferencia(movimiento.getReferencia());
                    movimientoExistente.setComentario(movimiento.getComentario());
                    return movimientoRepository.save(movimientoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
    }

    public void eliminar(Integer id) {
        movimientoRepository.deleteById(id);
    }

    // ========================================================================
    // MÉTODOS PARA PROCEDIMIENTOS ALMACENADOS (usando JDBC)
    // ========================================================================
    // Nota: Los procedimientos almacenados se ejecutan directamente con JDBC
    // porque realizan transacciones complejas que involucran múltiples tablas
    // y lógica de negocio que está en la base de datos.
    // ========================================================================

    /**
     * Registra un ingreso múltiple de lotes usando el procedimiento PA_RegistrarIngresoMultiple
     * Este método ejecuta directamente el SP que maneja la transacción completa de:
     * - Crear registro de Movimiento
     * - Crear registro de Compra
     * - Crear múltiples Lotes
     * - Crear múltiples MovimientoLinea
     * - Actualizar InventarioAlmacen
     */
    public IngresoMultipleResponseDTO registrarIngresoMultiple(IngresoMultipleRequestDTO request) throws Exception {
        return jdbcTemplate.execute((Connection conn) -> {
            String sql = "{CALL PA_RegistrarIngresoMultiple(?, ?, ?, ?, ?, ?)}";

            try (CallableStatement cs = conn.prepareCall(sql)) {
                // Configurar parámetros de entrada
                cs.setInt(1, request.getIdUsuario());
                cs.setInt(2, request.getIdProveedor());
                cs.setInt(3, request.getIdAlmacenDestino());
                cs.setString(4, request.getReferencia());

                if (request.getComentario() != null && !request.getComentario().trim().isEmpty()) {
                    cs.setString(5, request.getComentario());
                } else {
                    cs.setNull(5, java.sql.Types.NVARCHAR);
                }

                // Convertir lista de lotes a JSON para el SP
                String lotesJson;
                try {
                    lotesJson = objectMapper.writeValueAsString(request.getLotes());
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    throw new RuntimeException("Error al convertir lotes a JSON: " + e.getMessage(), e);
                }
                cs.setString(6, lotesJson);

                // Ejecutar y obtener resultado
                boolean hasResults = cs.execute();

                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (rs.next()) {
                            IngresoMultipleResponseDTO response = new IngresoMultipleResponseDTO();
                            response.setIdMovimiento(rs.getInt("ID_Movimiento"));
                            response.setIdCompra(rs.getInt("ID_Compra"));
                            response.setTotalLotesRegistrados(rs.getInt("Total_Lotes_Registrados"));
                            response.setTotalUnidades(rs.getBigDecimal("Total_Unidades"));
                            response.setValorTotal(rs.getBigDecimal("Valor_Total"));
                            return response;
                        }
                    }
                }

                throw new RuntimeException("No se obtuvo respuesta del procedimiento almacenado");
            }
        });
    }

    /**
     * Registra un consumo múltiple de lotes usando el procedimiento PA_RegistrarConsumoMultiple
     * Este método ejecuta directamente el SP que maneja la transacción completa de:
     * - Validar stock disponible
     * - Crear registro de Movimiento
     * - Crear registro de Consumo
     * - Crear múltiples MovimientoLinea
     * - Actualizar InventarioAlmacen (decrementar stock)
     */
    public ConsumoMultipleResponseDTO registrarConsumoMultiple(ConsumoMultipleRequestDTO request) throws Exception {
        return jdbcTemplate.execute((Connection conn) -> {
            String sql = "{CALL PA_RegistrarConsumoMultiple(?, ?, ?, ?, ?, ?)}";

            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, request.getIdUsuario());
                cs.setInt(2, request.getIdDepartamento());
                cs.setInt(3, request.getIdAlmacenOrigen());
                cs.setString(4, request.getReferencia());

                if (request.getComentario() != null && !request.getComentario().trim().isEmpty()) {
                    cs.setString(5, request.getComentario());
                } else {
                    cs.setNull(5, java.sql.Types.NVARCHAR);
                }

                String consumosJson;
                try {
                    consumosJson = objectMapper.writeValueAsString(request.getConsumos());
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    throw new RuntimeException("Error al convertir consumos a JSON: " + e.getMessage(), e);
                }
                cs.setString(6, consumosJson);

                boolean hasResults = cs.execute();

                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (rs.next()) {
                            ConsumoMultipleResponseDTO response = new ConsumoMultipleResponseDTO();
                            response.setIdMovimiento(rs.getInt("ID_Movimiento"));
                            response.setTotalLotesConsumidos(rs.getInt("Total_Lotes_Consumidos"));
                            response.setTotalUnidadesConsumidas(rs.getBigDecimal("Total_Unidades_Consumidas"));
                            return response;
                        }
                    }
                }

                throw new RuntimeException("No se obtuvo respuesta del procedimiento almacenado");
            }
        });
    }

    /**
     * Registra un traslado múltiple de lotes usando el procedimiento PA_RegistrarTrasladoMultiple
     * Este método ejecuta directamente el SP que maneja la transacción completa de:
     * - Validar almacenes diferentes
     * - Validar stock disponible en almacén origen
     * - Crear registro de Movimiento
     * - Crear múltiples MovimientoLinea
     * - Actualizar InventarioAlmacen origen (decrementar)
     * - Actualizar InventarioAlmacen destino (incrementar)
     */
    public TrasladoMultipleResponseDTO registrarTrasladoMultiple(TrasladoMultipleRequestDTO request) throws Exception {
        return jdbcTemplate.execute((Connection conn) -> {
            String sql = "{CALL PA_RegistrarTrasladoMultiple(?, ?, ?, ?, ?, ?)}";

            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, request.getIdUsuario());
                cs.setInt(2, request.getIdAlmacenOrigen());
                cs.setInt(3, request.getIdAlmacenDestino());
                cs.setString(4, request.getReferencia());

                if (request.getComentario() != null && !request.getComentario().trim().isEmpty()) {
                    cs.setString(5, request.getComentario());
                } else {
                    cs.setNull(5, java.sql.Types.NVARCHAR);
                }

                String trasladosJson;
                try {
                    trasladosJson = objectMapper.writeValueAsString(request.getTraslados());
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    throw new RuntimeException("Error al convertir traslados a JSON: " + e.getMessage(), e);
                }
                cs.setString(6, trasladosJson);

                boolean hasResults = cs.execute();

                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (rs.next()) {
                            TrasladoMultipleResponseDTO response = new TrasladoMultipleResponseDTO();
                            response.setIdMovimiento(rs.getInt("ID_Movimiento"));
                            response.setTotalLotesTrasladados(rs.getInt("Total_Lotes_Trasladados"));
                            response.setTotalUnidades(rs.getBigDecimal("Total_Unidades"));
                            return response;
                        }
                    }
                }

                throw new RuntimeException("No se obtuvo respuesta del procedimiento almacenado");
            }
        });
    }

    /**
     * Registra un ajuste múltiple de lotes usando el procedimiento PA_RegistrarAjusteMultiple
     * Este método ejecuta directamente el SP que maneja la transacción completa de:
     * - Validar usuario y almacén
     * - Crear registro de Movimiento con tipo_accion = 4 (Ajuste)
     * - Validar stock suficiente para ajustes negativos (decrementos)
     * - Validar que ajustes positivos no superen la cantidad inicial del lote
     * - Crear múltiples MovimientoLinea
     * - Actualizar InventarioAlmacen (según el delta: positivo incrementa, negativo decrementa)
     */
    public AjusteMultipleResponseDTO registrarAjusteMultiple(AjusteMultipleRequestDTO request) throws Exception {
        return jdbcTemplate.execute((Connection conn) -> {
            String sql = "{CALL PA_RegistrarAjusteMultiple(?, ?, ?, ?, ?)}";

            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, request.getIdUsuario());
                cs.setInt(2, request.getIdAlmacenOrigen());
                cs.setString(3, request.getReferencia());

                if (request.getComentario() != null && !request.getComentario().trim().isEmpty()) {
                    cs.setString(4, request.getComentario());
                } else {
                    cs.setNull(4, java.sql.Types.NVARCHAR);
                }

                // Convertir lista de ajustes a JSON para el SP
                String ajustesJson;
                try {
                    ajustesJson = objectMapper.writeValueAsString(request.getAjustes());
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    throw new RuntimeException("Error al convertir ajustes a JSON: " + e.getMessage(), e);
                }
                cs.setString(5, ajustesJson);

                // Ejecutar y obtener resultado
                boolean hasResults = cs.execute();

                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (rs.next()) {
                            AjusteMultipleResponseDTO response = new AjusteMultipleResponseDTO();
                            response.setIdMovimiento(rs.getInt("ID_Movimiento"));
                            response.setTotalLotesAjustados(rs.getInt("Total_Lotes_Ajustados"));
                            response.setAjustesPositivos(rs.getInt("Ajustes_Positivos"));
                            response.setAjustesNegativos(rs.getInt("Ajustes_Negativos"));
                            response.setTotalIncrementos(rs.getBigDecimal("Total_Incrementos"));
                            response.setTotalDecrementos(rs.getBigDecimal("Total_Decrementos"));
                            response.setDeltaNeto(rs.getBigDecimal("Delta_Neto"));
                            return response;
                        }
                    }
                }

                throw new RuntimeException("No se obtuvo respuesta del procedimiento almacenado");
            }
        });
    }

    /**
     * Consulta el historial de movimientos con filtros opcionales
     * Utiliza el procedimiento PA_ConsultarHistorialMovimientos que retorna:
     * - Primer ResultSet: Detalle de movimientos
     * - Segundo ResultSet: Resumen agrupado por tipo de acción
     *
     * @param idReactivo ID del reactivo a filtrar (opcional)
     * @param idAlmacen ID del almacén a filtrar (opcional)
     * @param fechaInicio Fecha inicio del rango (opcional, por defecto: hace 3 meses)
     * @param fechaFin Fecha fin del rango (opcional, por defecto: hoy)
     * @param idTipoAccion ID del tipo de acción a filtrar (opcional)
     * @return HistorialMovimientosResponseDTO con detalle y resumen
     */
    public HistorialMovimientosResponseDTO consultarHistorialMovimientos(
            Integer idReactivo,
            Integer idAlmacen,
            Instant fechaInicio,
            Instant fechaFin,
            Integer idTipoAccion) throws Exception {

        return jdbcTemplate.execute((Connection conn) -> {
            String sql = "{CALL PA_ConsultarHistorialMovimientos(?, ?, ?, ?, ?)}";

            try (CallableStatement cs = conn.prepareCall(sql)) {
                // Configurar parámetros (todos opcionales)
                if (idReactivo != null) {
                    cs.setInt(1, idReactivo);
                } else {
                    cs.setNull(1, java.sql.Types.INTEGER);
                }

                if (idAlmacen != null) {
                    cs.setInt(2, idAlmacen);
                } else {
                    cs.setNull(2, java.sql.Types.INTEGER);
                }

                if (fechaInicio != null) {
                    cs.setTimestamp(3, java.sql.Timestamp.from(fechaInicio));
                } else {
                    cs.setNull(3, java.sql.Types.TIMESTAMP);
                }

                if (fechaFin != null) {
                    cs.setTimestamp(4, java.sql.Timestamp.from(fechaFin));
                } else {
                    cs.setNull(4, java.sql.Types.TIMESTAMP);
                }

                if (idTipoAccion != null) {
                    cs.setInt(5, idTipoAccion);
                } else {
                    cs.setNull(5, java.sql.Types.INTEGER);
                }

                // Ejecutar procedimiento
                boolean hasResults = cs.execute();

                java.util.List<HistorialMovimientoDTO> detalleMovimientos = new java.util.ArrayList<>();
                java.util.List<ResumenMovimientoDTO> resumenPorTipo = new java.util.ArrayList<>();

                // Procesar primer ResultSet (Detalle de movimientos)
                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            HistorialMovimientoDTO detalle = new HistorialMovimientoDTO();
                            detalle.setIdMovimiento(rs.getInt("ID_Movimiento"));

                            java.sql.Timestamp timestamp = rs.getTimestamp("Fecha");
                            if (timestamp != null) {
                                detalle.setFecha(timestamp.toInstant());
                            }

                            detalle.setTipoAccion(rs.getString("Tipo_Accion"));
                            detalle.setUsuario(rs.getString("Usuario"));
                            detalle.setReferencia(rs.getString("Referencia"));
                            detalle.setComentario(rs.getString("Comentario"));
                            detalle.setNombreReactivo(rs.getString("Nombre_Reactivo"));
                            detalle.setMarca(rs.getString("Marca"));
                            detalle.setNumeroLote(rs.getInt("Numero_Lote"));
                            detalle.setAlmacenOrigen(rs.getString("Almacen_Origen"));
                            detalle.setAlmacenDestino(rs.getString("Almacen_Destino"));
                            detalle.setCantidad(rs.getBigDecimal("Cantidad"));
                            detalle.setPrecioVenta(rs.getBigDecimal("Precio_Venta"));
                            detalle.setValorTotal(rs.getBigDecimal("Valor_Total"));

                            detalleMovimientos.add(detalle);
                        }
                    }
                }

                // Procesar segundo ResultSet (Resumen por tipo)
                if (cs.getMoreResults()) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            ResumenMovimientoDTO resumen = new ResumenMovimientoDTO();
                            resumen.setTipoAccion(rs.getString("Tipo_Accion"));
                            resumen.setTotalMovimientos(rs.getInt("Total_Movimientos"));
                            resumen.setTotalUnidades(rs.getBigDecimal("Total_Unidades"));
                            resumen.setValorTotal(rs.getBigDecimal("Valor_Total"));

                            resumenPorTipo.add(resumen);
                        }
                    }
                }

                return new HistorialMovimientosResponseDTO(detalleMovimientos, resumenPorTipo);
            }
        });
    }
}

