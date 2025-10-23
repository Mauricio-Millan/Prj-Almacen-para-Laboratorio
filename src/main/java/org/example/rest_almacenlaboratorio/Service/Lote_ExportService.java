package org.example.rest_almacenlaboratorio.Service;

import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.rest_almacenlaboratorio.Mapper.Lote_Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Servicio independiente para exportación de lotes a Excel
 * No modifica ni afecta Lote_Service existente
 */
@Service
public class Lote_ExportService {

    @Autowired
    private Lote_Service loteService; // Usa el servicio existente sin modificarlo

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Exporta todos los lotes a Excel
     */
    public byte[] exportarLotesAExcel() throws IOException {
        List<Lote_Entity> lotes = loteService.obtenerTodos();
        return generarExcel(lotes, "Todos los Lotes");
    }

    /**
     * Exporta lotes por reactivo
     */
    public byte[] exportarLotesPorReactivo(Integer idReactivo) throws IOException {
        Validate.notNull(idReactivo, "El ID del reactivo no puede ser nulo");
        List<Lote_Entity> lotes = loteService.obtenerPorReactivo(idReactivo);
        return generarExcel(lotes, "Lotes del Reactivo " + idReactivo);
    }

    /**
     * Exporta lotes próximos a vencer
     */
    public byte[] exportarLotesProximosAVencer(Date fecha) throws IOException {
        Validate.notNull(fecha, "La fecha no puede ser nula");
        List<Lote_Entity> lotes = loteService.obtenerLotesProximosAVencer(fecha);
        return generarExcel(lotes, "Lotes Próximos a Vencer");
    }

    /**
     * Exporta lotes por estado
     */
    public byte[] exportarLotesPorEstado(Boolean estado) throws IOException {
        Validate.notNull(estado, "El estado no puede ser nulo");
        List<Lote_Entity> lotes = loteService.obtenerPorEstado(estado);
        String titulo = estado ? "Lotes Activos" : "Lotes Inactivos";
        return generarExcel(lotes, titulo);
    }

    /**
     * Exporta lotes por rango de fecha de expiración
     */
    public byte[] exportarLotesPorRangoExpiracion(Date fechaInicio, Date fechaFin) throws IOException {
        Validate.notNull(fechaInicio, "La fecha de inicio no puede ser nula");
        Validate.notNull(fechaFin, "La fecha de fin no puede ser nula");
        List<Lote_Entity> lotes = loteService.obtenerLotesPorRangoExpiracion(fechaInicio, fechaFin);
        return generarExcel(lotes, "Lotes por Rango de Expiración");
    }

    /**
     * Método privado que genera el archivo Excel
     */
    private byte[] generarExcel(List<Lote_Entity> lotes, String titulo) throws IOException {
        Validate.notNull(lotes, "La lista de lotes no puede ser nula");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Lotes");

            // Crear estilos
            CellStyle headerStyle = crearEstiloEncabezado(workbook);
            CellStyle dateStyle = crearEstiloFecha(workbook);
            CellStyle moneyStyle = crearEstiloMoneda(workbook);
            CellStyle numberStyle = crearEstiloNumero(workbook);

            // Crear título (Fila 0)
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(titulo);
            titleCell.setCellStyle(crearEstiloTitulo(workbook));

            // Fecha de generación (Fila 1)
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generado el: " + DATE_FORMAT.format(new Date()));

            // Crear encabezados (Fila 2)
            Row headerRow = sheet.createRow(2);
            String[] columnas = {"ID", "Reactivo", "Marca", "Compra ID", "Cantidad Inicial",
                                "Precio Unitario", "Precio Total", "Fecha Expiración", "Estado", "Estado Descriptivo"};

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos (desde Fila 3)
            int rowNum = 3;
            double totalGeneral = 0;

            for (Lote_Entity lote : lotes) {
                Row row = sheet.createRow(rowNum++);

                // ID
                row.createCell(0).setCellValue(lote.getId() != null ? lote.getId() : 0);

                // Reactivo
                String reactivo = "N/A";
                String marca = "N/A";
                if (lote.getIdReactivo() != null) {
                    reactivo = lote.getIdReactivo().getNombre() != null ?
                              lote.getIdReactivo().getNombre() : "N/A";
                    if (lote.getIdReactivo().getIdMarca() != null) {
                        marca = lote.getIdReactivo().getIdMarca().getNombre() != null ?
                               lote.getIdReactivo().getIdMarca().getNombre() : "N/A";
                    }
                }
                row.createCell(1).setCellValue(reactivo);
                row.createCell(2).setCellValue(marca);

                // Compra ID
                Integer compraId = lote.getIdCompra() != null ? lote.getIdCompra().getId() : null;
                row.createCell(3).setCellValue(compraId != null ? compraId : 0);

                // Cantidad Inicial
                Cell cantidadCell = row.createCell(4);
                if (lote.getCantidadInicial() != null) {
                    cantidadCell.setCellValue(lote.getCantidadInicial().doubleValue());
                    cantidadCell.setCellStyle(numberStyle);
                }

                // Precio Unitario
                Cell precioCell = row.createCell(5);
                if (lote.getPrecioUnitario() != null) {
                    precioCell.setCellValue(lote.getPrecioUnitario().doubleValue());
                    precioCell.setCellStyle(moneyStyle);
                }

                // Precio Total
                Cell precioTotalCell = row.createCell(6);
                if (lote.getCantidadInicial() != null && lote.getPrecioUnitario() != null) {
                    double precioTotal = lote.getCantidadInicial().doubleValue() *
                                        lote.getPrecioUnitario().doubleValue();
                    precioTotalCell.setCellValue(precioTotal);
                    precioTotalCell.setCellStyle(moneyStyle);
                    totalGeneral += precioTotal;
                }

                // Fecha Expiración
                Cell fechaCell = row.createCell(7);
                if (lote.getFechaExpiracion() != null) {
                    fechaCell.setCellValue(DATE_FORMAT.format(lote.getFechaExpiracion()));
                    fechaCell.setCellStyle(dateStyle);
                } else {
                    fechaCell.setCellValue("Sin fecha");
                }

                // Estado
                row.createCell(8).setCellValue(
                    lote.getEstado() != null && lote.getEstado() ? "Activo" : "Inactivo"
                );

                // Estado Descriptivo
                String estadoDescriptivo = loteService.obtenerEstadoDescriptivo(lote);
                Cell estadoDescCell = row.createCell(9);
                estadoDescCell.setCellValue(estadoDescriptivo);

                // Aplicar estilo según el estado
                CellStyle estadoStyle = workbook.createCellStyle();
                Font estadoFont = workbook.createFont();
                if ("Vencido".equals(estadoDescriptivo)) {
                    estadoFont.setColor(IndexedColors.RED.getIndex());
                    estadoFont.setBold(true);
                } else if ("Próximo a vencer".equals(estadoDescriptivo)) {
                    estadoFont.setColor(IndexedColors.ORANGE.getIndex());
                    estadoFont.setBold(true);
                }
                estadoStyle.setFont(estadoFont);
                estadoDescCell.setCellStyle(estadoStyle);
            }

            // Fila de totales
            Row totalRow = sheet.createRow(rowNum + 1);
            Cell totalLabelCell = totalRow.createCell(5);
            totalLabelCell.setCellValue("TOTAL:");
            CellStyle totalLabelStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalLabelStyle.setFont(totalFont);
            totalLabelCell.setCellStyle(totalLabelStyle);

            Cell totalValueCell = totalRow.createCell(6);
            totalValueCell.setCellValue(totalGeneral);
            CellStyle totalValueStyle = workbook.createCellStyle();
            totalValueStyle.cloneStyleFrom(moneyStyle);
            totalValueStyle.setFont(totalFont);
            totalValueCell.setCellStyle(totalValueStyle);

            // Resumen
            Row resumenRow = sheet.createRow(rowNum + 3);
            resumenRow.createCell(0).setCellValue("Total de lotes: " + lotes.size());

            // Autoajustar columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ========== Métodos de Estilos ==========

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle crearEstiloFecha(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloMoneda(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle crearEstiloNumero(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
}

