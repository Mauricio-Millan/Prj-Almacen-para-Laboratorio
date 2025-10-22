import { Component, inject, signal, computed, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Rest } from '../../Servicios/rest';
import { Reactivo, Lote, Movimiento, DashboardStats } from '../../Modelos/interfaces';

interface StatsCard {
  title: string;
  value: number;
  icon: string;
  bgColor: string;
  textColor: string;
  trend: number;
  trendText: string;
  trendPositive: boolean;
}

@Component({
  selector: 'app-dashboard-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-component.html',
  styleUrls: ['./dashboard-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit {
  private readonly restService = inject(Rest);

  // Signals para el estado del componente
  readonly reactivos = signal<Reactivo[]>([]);
  readonly lotes = signal<Lote[]>([]);
  readonly movimientos = signal<Movimiento[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  // Computed signals para estadísticas derivadas
  readonly reactivosTotales = computed(() => this.reactivos().length);
  
  readonly lotesProximosVencer = computed(() => {
    const hoy = new Date();
    const treintaDias = new Date();
    treintaDias.setDate(treintaDias.getDate() + 30);
    
    return this.lotes().filter(lote => {
      if (!lote.fechaExpiracion || !lote.estado) return false;
      const fechaExp = new Date(lote.fechaExpiracion);
      return fechaExp >= hoy && fechaExp <= treintaDias;
    }).length;
  });

  readonly movimientosHoy = computed(() => {
    const hoy = new Date().toISOString().split('T')[0];
    return this.movimientos().filter(mov => {
      const fechaMov = new Date(mov.fecha).toISOString().split('T')[0];
      return fechaMov === hoy;
    }).length;
  });

  readonly stockBajo = computed(() => {
    // Lotes con cantidad inicial menor a 10 unidades (lógica de ejemplo)
    return this.lotes().filter(lote => 
      lote.estado && lote.cantidadInicial < 10
    ).length;
  });

  readonly statsCards = computed<StatsCard[]>(() => [
    {
      title: 'Reactivos Totales',
      value: this.reactivosTotales(),
      icon: 'search',
      bgColor: 'bg-blue-100',
      textColor: 'text-blue-700',
      trend: 3.5,
      trendText: 'Desde el último mes',
      trendPositive: true
    },
    {
      title: 'Por Vencer',
      value: this.lotesProximosVencer(),
      icon: 'clock',
      bgColor: 'bg-yellow-100',
      textColor: 'text-yellow-700',
      trend: -2.1,
      trendText: 'Desde el último mes',
      trendPositive: false
    },
    {
      title: 'Movimientos Hoy',
      value: this.movimientosHoy(),
      icon: 'activity',
      bgColor: 'bg-green-100',
      textColor: 'text-green-700',
      trend: 12.5,
      trendText: 'Desde ayer',
      trendPositive: true
    },
    {
      title: 'Stock Bajo',
      value: this.stockBajo(),
      icon: 'alert',
      bgColor: 'bg-red-100',
      textColor: 'text-red-700',
      trend: -5.3,
      trendText: 'Desde la semana pasada',
      trendPositive: true
    }
  ]);

  readonly reactivosRecientes = computed(() => {
    return this.reactivos().slice(0, 5);
  });

  readonly lotesVencimientoProximo = computed(() => {
    return this.lotes()
      .filter(lote => lote.estado && lote.fechaExpiracion)
      .sort((a, b) => {
        const fechaA = new Date(a.fechaExpiracion).getTime();
        const fechaB = new Date(b.fechaExpiracion).getTime();
        return fechaA - fechaB;
      })
      .slice(0, 5);
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando.set(true);
    this.error.set(null);

    // Cargar reactivos
    this.restService.obtenerReactivos().subscribe({
      next: (data) => this.reactivos.set(data),
      error: (err) => {
        console.error('Error al cargar reactivos:', err);
        this.error.set('Error al cargar los datos de reactivos');
      }
    });

    // Cargar lotes
    this.restService.obtenerLotes().subscribe({
      next: (data) => this.lotes.set(data),
      error: (err) => {
        console.error('Error al cargar lotes:', err);
        this.error.set('Error al cargar los datos de lotes');
      }
    });

    // Cargar movimientos
    this.restService.obtenerMovimientos().subscribe({
      next: (data) => {
        this.movimientos.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar movimientos:', err);
        this.error.set('Error al cargar los datos de movimientos');
        this.cargando.set(false);
      }
    });
  }

  formatearFecha(fecha: string): string {
    if (!fecha) return 'N/A';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  }

  diasParaVencer(fechaExpiracion: string): number {
    if (!fechaExpiracion) return 0;
    const hoy = new Date();
    const fechaExp = new Date(fechaExpiracion);
    const diferencia = fechaExp.getTime() - hoy.getTime();
    return Math.ceil(diferencia / (1000 * 60 * 60 * 24));
  }

  getColorEstadoLote(dias: number): string {
    if (dias < 0) return 'text-red-600';
    if (dias <= 7) return 'text-red-500';
    if (dias <= 30) return 'text-yellow-500';
    return 'text-green-500';
  }
}
