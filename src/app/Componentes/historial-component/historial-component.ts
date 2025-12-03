import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Rest } from '../../Servicios/rest';
import { DetalleMovimiento, ResumenPorTipo, Reactivo, Almacen, Tipoaccion } from '../../Modelos/interfaces';

@Component({
  selector: 'app-historial-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './historial-component.html',
  styleUrl: './historial-component.css'
})
export class HistorialComponent implements OnInit {
  private restService = inject(Rest);

  // Signals
  movimientos = signal<DetalleMovimiento[]>([]);
  resumenPorTipo = signal<ResumenPorTipo[]>([]);
  reactivos = signal<Reactivo[]>([]);
  almacenes = signal<Almacen[]>([]);
  tiposAccion = signal<Tipoaccion[]>([]);
  cargando = signal<boolean>(false);
  error = signal<string | null>(null);

  // Filtros
  filtroReactivo = signal<number | null>(null);
  filtroAlmacen = signal<number | null>(null);
  filtroFechaInicio = signal<string>('');
  filtroFechaFin = signal<string>('');
  filtroTipoAccion = signal<number | null>(null);

  ngOnInit(): void {
    this.cargarDatos();
  }

  private cargarDatos(): void {
    this.cargando.set(true);

    // Cargar reactivos
    this.restService.obtenerReactivos().subscribe({
      next: (reactivos) => this.reactivos.set(reactivos),
      error: (err) => console.error('Error al cargar reactivos:', err)
    });

    // Cargar almacenes
    this.restService.obtenerAlmacenes().subscribe({
      next: (almacenes) => this.almacenes.set(almacenes),
      error: (err) => console.error('Error al cargar almacenes:', err)
    });

    // Cargar tipos de acción
    this.restService.obtenerTipoacciones().subscribe({
      next: (tipos) => this.tiposAccion.set(tipos),
      error: (err) => console.error('Error al cargar tipos de acción:', err)
    });

    // Cargar historial completo sin filtros
    this.restService.obtenerHistorialMovimientos().subscribe({
      next: (response) => {
        this.movimientos.set(response.detalleMovimientos);
        this.resumenPorTipo.set(response.resumenPorTipo);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar historial:', err);
        this.error.set('Error al cargar el historial de movimientos');
        this.cargando.set(false);
      }
    });
  }

  buscarHistorial(): void {
    this.cargando.set(true);
    this.error.set(null);

    this.restService.obtenerHistorialMovimientos(
      this.filtroReactivo() ?? undefined,
      this.filtroAlmacen() ?? undefined,
      this.filtroFechaInicio() || undefined,
      this.filtroFechaFin() || undefined,
      this.filtroTipoAccion() ?? undefined
    ).subscribe({
      next: (response) => {
        this.movimientos.set(response.detalleMovimientos);
        this.resumenPorTipo.set(response.resumenPorTipo);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar historial:', err);
        this.error.set('Error al cargar el historial de movimientos');
        this.cargando.set(false);
      }
    });
  }

  limpiarFiltros(): void {
    this.filtroReactivo.set(null);
    this.filtroAlmacen.set(null);
    this.filtroFechaInicio.set('');
    this.filtroFechaFin.set('');
    this.filtroTipoAccion.set(null);
    this.buscarHistorial();
  }

  formatearFecha(fecha: string): string {
    if (!fecha) return '-';
    return new Date(fecha).toLocaleDateString('es-ES');
  }

  formatearFechaHora(fecha: string): string {
    if (!fecha) return '-';
    return new Date(fecha).toLocaleString('es-ES');
  }
}
