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
}

