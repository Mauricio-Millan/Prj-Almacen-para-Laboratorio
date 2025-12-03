import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import jsPDF from 'jspdf';
import {
  HistorialMovimientosResponse,
  ResumenPorTipo,
  DetalleMovimiento,
  InventarioDetallado,
  InventarioItem,
  Almacen,
  AlmacenInfo
} from '../../Modelos/interfaces';
import { Rest } from '../../Servicios/rest';

type ReportType = 'acciones' | 'inventario';

interface TablaColumna {
  titulo: string;
  ancho: number;
}

interface TablaFila {
  valores: string[];
  fillColor?: [number, number, number];
}

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css'
})
export class Reportes {
  private readonly rest = inject(Rest);

  readonly reporteEnProceso = signal<ReportType | null>(null);
  readonly alerta = signal<{ tipo: 'success' | 'error'; mensaje: string } | null>(null);
  readonly almacenes = signal<Almacen[]>([]);
  readonly cargandoAlmacenes = signal<boolean>(false);
  readonly almacenSeleccionado = signal<number | null>(null);

  constructor() {
    this.cargarAlmacenes();
  }

  generarReporteAcciones(): void {
    if (this.reporteEnProceso() === 'acciones') {
      return;
    }

    const almacenId = this.obtenerAlmacenSeleccionado();
    if (!almacenId) {
      this.alerta.set({ tipo: 'error', mensaje: 'Selecciona un almacén antes de generar el reporte.' });
      return;
    }

    this.alerta.set(null);
    this.reporteEnProceso.set('acciones');

    this.rest.obtenerHistorialMovimientos(undefined, almacenId).subscribe({
      next: (response) => {
        this.crearPdfAcciones(response);
        this.alerta.set({ tipo: 'success', mensaje: 'Reporte de acciones descargado correctamente.' });
        this.reporteEnProceso.set(null);
      },
      error: (error) => {
        console.error('Error al obtener historial de movimientos', error);
        this.alerta.set({ tipo: 'error', mensaje: 'No se pudo generar el reporte de acciones.' });
        this.reporteEnProceso.set(null);
      }
    });
  }

  generarReporteInventario(): void {
    if (this.reporteEnProceso() === 'inventario') {
      return;
    }

    const almacenId = this.obtenerAlmacenSeleccionado();
    if (!almacenId) {
      this.alerta.set({ tipo: 'error', mensaje: 'Selecciona un almacén antes de generar el reporte.' });
      return;
    }

    this.alerta.set(null);
    this.reporteEnProceso.set('inventario');

    this.rest.obtenerInventarioDetallado(almacenId).subscribe({
      next: (response) => {
        this.crearPdfInventario(response, almacenId);
        this.alerta.set({ tipo: 'success', mensaje: 'Reporte de inventario descargado correctamente.' });
        this.reporteEnProceso.set(null);
      },
      error: (error) => {
        console.error('Error al obtener inventario detallado', error);
        this.alerta.set({ tipo: 'error', mensaje: 'No se pudo generar el reporte de inventario.' });
        this.reporteEnProceso.set(null);
      }
    });
  }

  onAlmacenChange(event: Event): void {
    const value = Number((event.target as HTMLSelectElement).value);
    this.almacenSeleccionado.set(Number.isFinite(value) ? value : null);
  }

  private cargarAlmacenes(): void {
    this.cargandoAlmacenes.set(true);
    this.rest.obtenerAlmacenes().subscribe({
      next: (lista) => {
        const almacenes = lista ?? [];
        this.almacenes.set(almacenes);
        if (!this.almacenSeleccionado() && almacenes.length) {
          this.almacenSeleccionado.set(almacenes[0].id);
        }
        this.cargandoAlmacenes.set(false);
      },
      error: (error) => {
        console.error('Error al obtener almacenes', error);
        this.alerta.set({ tipo: 'error', mensaje: 'No se pudo cargar la lista de almacenes.' });
        this.cargandoAlmacenes.set(false);
      }
    });
  }

  private obtenerAlmacenSeleccionado(): number | null {
    return this.almacenSeleccionado();
  }

  private crearPdfAcciones(data: HistorialMovimientosResponse): void {
    const doc = new jsPDF();
    this.agregarEncabezado(doc, 'Reporte de acciones del almacén');

    let y = 40;
    const resumen: ResumenPorTipo[] = data?.resumenPorTipo ?? [];
    if (resumen.length) {
      doc.setFontSize(12);
      y = this.escribirLinea(doc, 'Resumen por tipo de acción', y);
      doc.setFontSize(10);
      resumen.forEach((item) => {
        const texto = `${item.tipoAccion}: ${item.totalMovimientos} movimientos | ${item.totalUnidades} unidades | Valor S/ ${item.valorTotal.toFixed(2)}`;
        y = this.escribirLinea(doc, texto, y);
      });
      y += 4;
    }

    doc.setFontSize(12);
    y = this.escribirLinea(doc, 'Detalle de movimientos', y);
    doc.setFontSize(10);

    const detalle: DetalleMovimiento[] = data?.detalleMovimientos ?? [];
    if (!detalle.length) {
      this.escribirLinea(doc, 'No se encontraron movimientos para los filtros actuales.', y);
      this.guardarPdf(doc, 'reporte-acciones');
      return;
    }

    const columnas: TablaColumna[] = [
      { titulo: 'Fecha', ancho: 26 },
      { titulo: 'Tipo', ancho: 22 },
      { titulo: 'Reactivo / Marca', ancho: 46 },
      { titulo: 'Cantidad', ancho: 22 },
      { titulo: 'Almacenes', ancho: 38 },
      { titulo: 'Referencia / Valor', ancho: 44 }
    ];

    const filas: TablaFila[] = detalle.map((movimiento) => {
      const trayecto = [
        movimiento.almacenOrigen ? `Origen: ${movimiento.almacenOrigen}` : null,
        movimiento.almacenDestino ? `Destino: ${movimiento.almacenDestino}` : null
      ]
        .filter(Boolean)
        .join(' → ');

      return {
        valores: [
          this.formatearFecha(movimiento.fecha),
          movimiento.tipoAccion ?? '—',
          `${movimiento.nombreReactivo ?? 'Reactivo'} (${movimiento.marca ?? 's/m'})`,
          `${movimiento.cantidad ?? 0} uds`,
          trayecto || '—',
          `Ref: ${movimiento.referencia ?? '—'} | S/ ${(movimiento.valorTotal ?? 0).toFixed(2)}`
        ],
        fillColor: this.obtenerColorPorTipo(movimiento.tipoAccion)
      };
    });

    y = this.dibujarTabla(doc, columnas, filas, y + 2);

    this.guardarPdf(doc, 'reporte-acciones');
  }

  private crearPdfInventario(payload: any, almacenId: number): void {
    const doc = new jsPDF();
    this.agregarEncabezado(doc, 'Reporte de inventario detallado');

    const { items, info } = this.normalizarInventario(payload);
    let y = 40;

    if (info) {
      doc.setFontSize(11);
      y = this.escribirLinea(doc, `Almacén: ${info.nombreAlmacen ?? 'Sin identificar'} (ID ${info.idAlmacen ?? almacenId})`, y);
      if (info.direccion || info.telefono) {
        y = this.escribirLinea(
          doc,
          [info.direccion ? `Dirección: ${info.direccion}` : null, info.telefono ? `Teléfono: ${info.telefono}` : null]
            .filter(Boolean)
            .join(' | '),
          y
        );
      }
      y += 4;
    }

    if (!items.length) {
      this.escribirLinea(doc, 'No se encontraron registros de inventario para el almacén.', y);
      this.guardarPdf(doc, 'reporte-inventario');
      return;
    }

    const columnas: TablaColumna[] = [
      { titulo: 'Reactivo', ancho: 44 },
      { titulo: 'Marca', ancho: 30 },
      { titulo: 'Lote', ancho: 24 },
      { titulo: 'Stock', ancho: 22 },
      { titulo: 'Expiración', ancho: 28 },
      { titulo: 'Días restantes', ancho: 28 }
    ];

    const filas: TablaFila[] = [];

    items.forEach((item) => {
      const nombreReactivo = item.nombreReactivo ?? 'Reactivo sin nombre';
      const marca = (item as InventarioDetallado).nombreMarca ?? (item as InventarioItem).marca ?? '—';

      if (this.esInventarioDetallado(item)) {
        item.lotes.forEach((lote) => {
          const diasRestantes = this.obtenerDiasRestantes(lote.fechaExpiracion, lote as any);
          filas.push({
            valores: [
              nombreReactivo,
              marca,
              `#${lote.idLote}`,
              `${lote.cantidadDisponible ?? 0} uds`,
              this.formatearFecha(lote.fechaExpiracion),
              diasRestantes !== null ? `${diasRestantes} días` : '—'
            ],
            fillColor: this.obtenerColorPorVencimiento(diasRestantes)
          });
        });
      } else {
        const inventario = item as InventarioItem;
        const diasRestantes = this.obtenerDiasRestantes(inventario.fechaExpiracion, inventario);
        filas.push({
          valores: [
            nombreReactivo,
            marca,
            `#${inventario.numeroLote ?? inventario.idInventario}`,
            `${inventario.stockActual ?? inventario.cantidadInicialLote ?? 0} uds`,
            this.formatearFecha(inventario.fechaExpiracion),
            diasRestantes !== null ? `${diasRestantes} días` : '—'
          ],
          fillColor: this.obtenerColorPorVencimiento(diasRestantes)
        });
      }
    });

    y = this.dibujarTabla(doc, columnas, filas, y + 2);

    this.guardarPdf(doc, 'reporte-inventario');
  }

  private normalizarInventario(payload: any): { items: Array<InventarioDetallado | InventarioItem>; info: AlmacenInfo | null } {
    if (!payload) {
      return { items: [], info: null };
    }

    if (Array.isArray(payload)) {
      return { items: payload, info: null };
    }

    const info: AlmacenInfo | null = payload.almacenInfo ?? payload.infoAlmacen ?? null;

    const posiblesClaves = ['inventarioDetallado', 'inventario', 'detalle', 'reactivos', 'data', 'items'];
    for (const clave of posiblesClaves) {
      if (Array.isArray(payload[clave])) {
        return { items: payload[clave], info };
      }
    }

    return { items: [], info };
  }

  private esInventarioDetallado(item: InventarioDetallado | InventarioItem): item is InventarioDetallado {
    return Array.isArray((item as InventarioDetallado).lotes);
  }

  private agregarEncabezado(doc: jsPDF, titulo: string): void {
    doc.setFontSize(16);
    doc.text(titulo, 14, 20);
    doc.setFontSize(10);
    doc.text(`Generado: ${new Date().toLocaleString()}`, 14, 28);
  }

  private escribirLinea(doc: jsPDF, texto: string, y: number): number {
    const lineHeight = 6;
    const lineas = doc.splitTextToSize(texto, 180);
    lineas.forEach((linea: string) => {
      if (y >= 280) {
        doc.addPage();
        y = 20;
      }
      doc.text(linea, 14, y);
      y += lineHeight;
    });
    return y;
  }

  private formatearFecha(valor?: string | null): string {
    if (!valor) {
      return 'Sin fecha';
    }
    const fecha = new Date(valor);
    return isNaN(fecha.getTime()) ? valor : fecha.toLocaleDateString();
  }

  private guardarPdf(doc: jsPDF, baseName: string): void {
    const fecha = new Date();
    const nombre = `${baseName}-${fecha.getFullYear()}${(fecha.getMonth() + 1)
      .toString()
      .padStart(2, '0')}${fecha
      .getDate()
      .toString()
      .padStart(2, '0')}-${fecha
      .getHours()
      .toString()
      .padStart(2, '0')}${fecha
      .getMinutes()
      .toString()
      .padStart(2, '0')}.pdf`;
    doc.save(nombre);
  }

  private obtenerDiasRestantes(fechaExpiracion?: string | null, item?: { diasParaExpiracion?: number | null }): number | null {
    if (item?.diasParaExpiracion !== undefined && item?.diasParaExpiracion !== null) {
      return Number.isFinite(item.diasParaExpiracion) ? Math.trunc(item.diasParaExpiracion) : null;
    }
    if (!fechaExpiracion) {
      return null;
    }
    const fecha = new Date(fechaExpiracion);
    if (isNaN(fecha.getTime())) {
      return null;
    }
    const hoy = new Date();
    const diff = fecha.getTime() - hoy.getTime();
    return Math.floor(diff / (1000 * 60 * 60 * 24));
  }

  private dibujarTabla(doc: jsPDF, columnas: TablaColumna[], filas: TablaFila[], inicioY: number): number {
    const inicioX = 14;
    const headerHeight = 8;
    let headerTop = inicioY;
    const drawHeader = () => {
      let x = inicioX;
      doc.setFontSize(10);
      doc.setFillColor(226, 232, 240);
      doc.setTextColor(30, 41, 59);
      columnas.forEach((col) => {
        doc.rect(x, headerTop, col.ancho, headerHeight, 'F');
        doc.text(col.titulo, x + 2, headerTop + headerHeight - 3);
        x += col.ancho;
      });
    };

    drawHeader();
    let y = headerTop + headerHeight;
    const defaultTextColor: [number, number, number] = [31, 41, 55];
    doc.setDrawColor(209, 213, 219);

    filas.forEach((fila) => {
      const lineArrays = fila.valores.map((valor, idx) => {
        const anchoDisponible = Math.max(columnas[idx].ancho - 4, 4);
        const contenido = valor ?? '—';
        const fragmentos = doc.splitTextToSize(contenido, anchoDisponible);
        return Array.isArray(fragmentos) ? fragmentos : [fragmentos];
      });
      const maxLineas = Math.max(1, ...lineArrays.map((l) => l.length));
      const rowHeight = Math.max(8, maxLineas * 5 + 2);

      if (y + rowHeight > 280) {
        doc.addPage();
        headerTop = 20;
        drawHeader();
        y = headerTop + headerHeight;
      }

      let x = inicioX;
      columnas.forEach((col, idx) => {
        if (fila.fillColor) {
          doc.setFillColor(...fila.fillColor);
          doc.rect(x, y, col.ancho, rowHeight, 'FD');
        } else {
          doc.setFillColor(255, 255, 255);
          doc.rect(x, y, col.ancho, rowHeight);
        }

        doc.setTextColor(...defaultTextColor);
        let textY = y + 5;
        lineArrays[idx].forEach((linea) => {
          doc.text(linea, x + 2, textY);
          textY += 5;
        });
        x += col.ancho;
      });

      y += rowHeight;
    });

    doc.setTextColor(...defaultTextColor);
    return y;
  }

  private obtenerColorPorTipo(tipo?: string | null): [number, number, number] | undefined {
    const nombre = (tipo ?? '').toLowerCase();
    if (nombre.includes('consumo')) {
      return [254, 226, 226];
    }
    if (nombre.includes('ingreso')) {
      return [209, 250, 229];
    }
    if (nombre.includes('ajuste')) {
      return [254, 249, 195];
    }
    if (nombre.includes('traslado')) {
      return [219, 234, 254];
    }
    return undefined;
  }

  private obtenerColorPorVencimiento(diasRestantes: number | null): [number, number, number] | undefined {
    if (diasRestantes === null || diasRestantes === undefined) {
      return undefined;
    }
    if (diasRestantes < 60) {
      return [254, 226, 226];
    }
    if (diasRestantes < 180) {
      return [254, 249, 195];
    }
    return undefined;
  }
}
