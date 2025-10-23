package org.example.rest_almacenlaboratorio.Service;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.StringUtils;
import org.example.rest_almacenlaboratorio.Mapper.Lote_Entity;
import org.example.rest_almacenlaboratorio.Repository.Lote_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class Lote_Service {

    @Autowired
    private Lote_Repository loteRepository;

    // ✅ Métodos originales sin modificar
    public List<Lote_Entity> obtenerTodos() {
        return loteRepository.findAll();
    }

    public Optional<Lote_Entity> obtenerPorId(Integer id) {
        return loteRepository.findById(id);
    }

    public List<Lote_Entity> obtenerPorEstado(Boolean estado) {
        return loteRepository.findByEstado(estado);
    }

    public List<Lote_Entity> obtenerPorReactivo(Integer idReactivo) {
        return loteRepository.findByIdReactivo_Id(idReactivo);
    }

    public List<Lote_Entity> obtenerPorCompra(Integer idCompra) {
        return loteRepository.findByIdCompra_Id(idCompra);
    }

    public List<Lote_Entity> obtenerLotesProximosAVencer(Date fecha) {
        return loteRepository.findByFechaExpiracionBefore(fecha);
    }

    public List<Lote_Entity> obtenerLotesPorRangoExpiracion(Date fechaInicio, Date fechaFin) {
        return loteRepository.findByFechaExpiracionBetween(fechaInicio, fechaFin);
    }

    // 🔧 Métodos mejorados con Apache Commons (validaciones agregadas)
    public Lote_Entity crear(Lote_Entity lote) {
        // ✅ Agregamos validaciones sin romper la funcionalidad original
        Validate.notNull(lote, "El lote no puede ser nulo");
        Validate.notNull(lote.getIdReactivo(), "El reactivo no puede ser nulo");
        Validate.notNull(lote.getIdCompra(), "La compra no puede ser nula");
        Validate.notNull(lote.getCantidadInicial(), "La cantidad inicial no puede ser nula");
        Validate.isTrue(lote.getCantidadInicial().compareTo(BigDecimal.ZERO) > 0,
                "La cantidad inicial debe ser mayor a 0");
        Validate.notNull(lote.getPrecioUnitario(), "El precio unitario no puede ser nulo");
        Validate.isTrue(lote.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0,
                "El precio unitario debe ser mayor a 0");

        // La lógica original se mantiene intacta
        if (lote.getEstado() == null) {
            lote.setEstado(true);
        }
        return loteRepository.save(lote);
    }

    public Lote_Entity actualizar(Integer id, Lote_Entity lote) {
        // ✅ Agregamos validación de ID sin cambiar la lógica
        Validate.notNull(id, "El ID no puede ser nulo");
        Validate.notNull(lote, "El lote no puede ser nulo");

        // La lógica original se mantiene intacta
        return loteRepository.findById(id)
                .map(loteExistente -> {
                    if (lote.getIdReactivo() != null) {
                        loteExistente.setIdReactivo(lote.getIdReactivo());
                    }
                    if (lote.getIdCompra() != null) {
                        loteExistente.setIdCompra(lote.getIdCompra());
                    }
                    if (lote.getCantidadInicial() != null) {
                        Validate.isTrue(lote.getCantidadInicial().compareTo(BigDecimal.ZERO) > 0,
                                "La cantidad debe ser mayor a 0");
                        loteExistente.setCantidadInicial(lote.getCantidadInicial());
                    }
                    if (lote.getPrecioUnitario() != null) {
                        Validate.isTrue(lote.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0,
                                "El precio debe ser mayor a 0");
                        loteExistente.setPrecioUnitario(lote.getPrecioUnitario());
                    }
                    if (lote.getFechaExpiracion() != null) {
                        loteExistente.setFechaExpiracion(lote.getFechaExpiracion());
                    }
                    if (lote.getEstado() != null) {
                        loteExistente.setEstado(lote.getEstado());
                    }
                    return loteRepository.save(loteExistente);
                })
                .orElseThrow(() -> new RuntimeException("Lote no encontrado con ID: " + id));
    }

    public void eliminar(Integer id) {
        // ✅ Validación agregada
        Validate.notNull(id, "El ID no puede ser nulo");

        // Lógica original intacta
        if (!loteRepository.existsById(id)) {
            throw new RuntimeException("Lote no encontrado con ID: " + id);
        }
        loteRepository.deleteById(id);
    }

    public Lote_Entity desactivarLote(Integer id) {
        // ✅ Validación agregada
        Validate.notNull(id, "El ID no puede ser nulo");

        // Lógica original intacta
        return loteRepository.findById(id)
                .map(lote -> {
                    lote.setEstado(false);
                    return loteRepository.save(lote);
                })
                .orElseThrow(() -> new RuntimeException("Lote no encontrado con ID: " + id));
    }

    public boolean existeLote(Integer id) {
        return loteRepository.existsById(id);
    }

    // 🆕 Métodos adicionales opcionales (no afectan a los existentes)

    /**
     * Valida si un lote está próximo a vencer (dentro de X días)
     */
    public boolean estaProximoAVencer(Lote_Entity lote, int diasUmbral) {
        Validate.notNull(lote, "El lote no puede ser nulo");
        Validate.notNull(lote.getFechaExpiracion(), "La fecha de expiración no puede ser nula");
        Validate.isTrue(diasUmbral > 0, "Los días umbral deben ser mayores a 0");

        long diasRestantes = (lote.getFechaExpiracion().getTime() - new Date().getTime())
                / (1000 * 60 * 60 * 24);
        return diasRestantes <= diasUmbral && diasRestantes >= 0;
    }

    /**
     * Obtiene mensaje descriptivo del estado del lote
     */
    public String obtenerEstadoDescriptivo(Lote_Entity lote) {
        Validate.notNull(lote, "El lote no puede ser nulo");

        if (!lote.getEstado()) {
            return "Inactivo";
        }

        if (lote.getFechaExpiracion() != null) {
            if (lote.getFechaExpiracion().before(new Date())) {
                return "Vencido";
            }
            if (estaProximoAVencer(lote, 30)) {
                return "Próximo a vencer";
            }
        }

        return "Activo";
    }
}
