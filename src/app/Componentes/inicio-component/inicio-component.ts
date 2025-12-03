import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SideBarComponent } from '../side-bar-component/side-bar-component';
import { NavbarComponent } from '../navbar-component/navbar-component';
import { RouterOutlet, Router } from '@angular/router';
import { Rest } from '../../Servicios/rest';
import { AuthService } from '../../Servicios/auth.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { 
  StatsCard, 
  MovimientoResumen, 
  Alerta,
  InventarioItem
} from '../../Modelos/interfaces';

type InventarioConAlmacen = InventarioItem & {
  idAlmacenOrigen?: number;
  nombreAlmacen?: string;
};

interface LoteCritico {
  idInventario: number;
  nombreReactivo: string;
  numeroLote: number;
  almacen: string;
  stockActual: number;
  cantidadInicialLote: number;
  porcentajeStock: number;
  diasParaExpiracion: number | null;
  fechaExpiracion: string;
  estadoExpiracion: string;
  motivo: 'expiracion' | 'stock' | 'ambos';
}

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [
    CommonModule,
    SideBarComponent,
    NavbarComponent,
    RouterOutlet
  ],
  templateUrl: './inicio-component.html',
  styleUrls: ['./inicio-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InicioComponent implements OnInit {
  private readonly restService = inject(Rest);
  private readonly authService = inject(AuthService);
  public readonly router = inject(Router);
  private readonly almacenesDashboard = [1, 2, 3];
  private readonly diasExpiracionUmbral = 30;
  private readonly porcentajeStockUmbral = 0.25;

  // Signals para el estado del componente
  readonly sidebarCollapsed = signal(false);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  // Signals para datos del dashboard
  readonly statsCards = signal<StatsCard[]>([]);
  readonly movimientosRecientes = signal<MovimientoResumen[]>([]);
  readonly alertas = signal<Alerta[]>([]);
  readonly lotesCriticos = signal<LoteCritico[]>([]);

  // Usuario actual
  readonly usuarioActual = this.authService.usuario;

  ngOnInit(): void {
    this.cargarDatosDashboard();
  }

  toggleSidebar(): void {
    this.sidebarCollapsed.update(value => !value);
  }

  isRootPath(): boolean {
    return this.router.url === '/inicio' || this.router.url === '/inicio/';
  }

  private cargarDatosDashboard(): void {
    this.cargando.set(true);
    this.error.set(null);

    const solicitudes = this.almacenesDashboard.map(id =>
      this.restService.obtenerInventarioDetallado(id).pipe(
        catchError((err) => {
          console.error(`Error al cargar inventario del almacén ${id}:`, err);
          return of(null);
        })
      )
    );

    forkJoin(solicitudes).subscribe({
      next: (respuestas) => {
        const inventarioUnificado = this.unificarInventarios(respuestas);

        if (!inventarioUnificado.length) {
          this.error.set('No se pudo cargar el inventario de los almacenes configurados.');
          this.statsCards.set([]);
          this.movimientosRecientes.set([]);
          this.alertas.set([]);
          this.lotesCriticos.set([]);
          this.cargando.set(false);
          return;
        }

        this.procesarDatosInventario(inventarioUnificado);
        const lotesCriticos = this.obtenerLotesCriticos(inventarioUnificado);
        this.lotesCriticos.set(lotesCriticos);
        this.generarAlertasDesdeLotes(lotesCriticos);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error inesperado al cargar datos del dashboard:', err);
        this.error.set('Error al cargar los datos del dashboard');
        this.cargando.set(false);
      }
    });
  }

  private procesarDatosInventario(inventario: InventarioConAlmacen[]): void {
    // Calcular estadísticas desde el inventario
    this.calcularEstadisticasInventario(inventario);
    
    // Generar movimientos recientes simulados (ya que no vienen en esta API)
    this.generarMovimientosRecientes(inventario);
    
  }

  private unificarInventarios(respuestas: Array<any | null>): InventarioConAlmacen[] {
    const items: InventarioConAlmacen[] = [];

    respuestas.forEach((respuesta, index) => {
      if (!respuesta) {
        return;
      }

      const inventarioRespuesta: InventarioItem[] = respuesta?.inventarioDetallado || [];
      const almacenInfo = respuesta?.almacenInfo;
      const idAlmacen = almacenInfo?.idAlmacen ?? this.almacenesDashboard[index];
      const nombreAlmacen = almacenInfo?.nombreAlmacen ?? `Almacén ${idAlmacen}`;

      inventarioRespuesta.forEach(item => {
        items.push({
          ...item,
          idAlmacenOrigen: idAlmacen,
          nombreAlmacen
        });
      });
    });

    return items;
  }

  private calcularEstadisticasInventario(inventario: InventarioItem[]): void {
    // Total de reactivos únicos
    const totalReactivos = inventario.length;

    // Items con stock bajo (estadoStock = 'Bajo' o 'Crítico')
    const stockBajo = inventario.filter(item => 
      item.estadoStock === 'Bajo' || item.estadoStock === 'Crítico'
    ).length;

    // Items por vencer (estadoExpiracion != 'Óptimo')
    const porVencer = inventario.filter(item => 
      item.estadoExpiracion === 'Por Vencer' || 
      item.estadoExpiracion === 'Próximo a Vencer' ||
      item.estadoExpiracion === 'Vencido'
    ).length;

    // Movimientos del día (simulado, ya que no viene en esta API)
    const movimientosHoy = 0; // Placeholder

    const cards: StatsCard[] = [
      {
        titulo: 'Reactivos Totales',
        valor: totalReactivos,
        color: 'blue',
        icono: '🧪'
      },
      {
        titulo: 'Stock Bajo',
        valor: stockBajo,
        color: 'yellow',
        icono: '⚠️'
      },
      {
        titulo: 'Por Vencer',
        valor: porVencer,
        color: 'red',
        icono: '⏰'
      },
      {
        titulo: 'Movimientos Hoy',
        valor: movimientosHoy,
        color: 'green',
        icono: '📊'
      }
    ];

    this.statsCards.set(cards);
  }

  private generarMovimientosRecientes(inventario: InventarioConAlmacen[]): void {
    // Generar movimientos basados en los últimos items del inventario
    const movimientos: MovimientoResumen[] = inventario
      .slice(0, 5)
      .map((item, index) => ({
        id: item.idInventario ?? index + 1,
        fecha: new Date().toLocaleDateString('es-PE'),
        tipo: 'Inventario',
        reactivo: `${item.nombreReactivo || 'Sin nombre'} · ${item.nombreAlmacen || 'Almacén'}`,
        cantidad: `${item.stockActual} unidades`,
        usuario: 'Sistema',
        colorTipo: 'blue' as const
      }));

    this.movimientosRecientes.set(movimientos);
  }

  private generarAlertasDesdeLotes(lotes: LoteCritico[]): void {
    const lotesPorVencer = lotes.filter(lote => lote.motivo === 'expiracion' || lote.motivo === 'ambos').length;
    const lotesStockBajo = lotes.filter(lote => lote.motivo === 'stock' || lote.motivo === 'ambos').length;

    const alertas: Alerta[] = [
      {
        tipo: lotesPorVencer > 0 ? 'danger' : 'info',
        titulo: 'Lotes por vencer o vencidos',
        mensaje: lotesPorVencer > 0
          ? 'Revisa los lotes resaltados en el tablero y coordina reposiciones.'
          : 'Sin lotes próximos a vencer en los almacenes monitoreados.',
        conteo: lotesPorVencer
      },
      {
        tipo: lotesStockBajo > 0 ? 'warning' : 'info',
        titulo: 'Lotes con stock inferior al 25%',
        mensaje: lotesStockBajo > 0
          ? 'Prioriza la compra o traslado para estos lotes críticos.'
          : 'Todos los lotes supervisados mantienen niveles estables.',
        conteo: lotesStockBajo
      }
    ];

    this.alertas.set(alertas);
  }

  private obtenerLotesCriticos(inventario: InventarioConAlmacen[]): LoteCritico[] {
    return inventario
      .filter((item) => this.esLoteCritico(item))
      .map((item) => {
        const porcentajeStock = this.calcularPorcentajeStock(item);
        const riesgoStock = this.esRiesgoStock(porcentajeStock);
        const diasParaExpiracion = typeof item.diasParaExpiracion === 'number' ? item.diasParaExpiracion : null;
        const riesgoExpiracion = this.esRiesgoExpiracion(diasParaExpiracion);
        const motivo: LoteCritico['motivo'] = riesgoStock && riesgoExpiracion
          ? 'ambos'
          : riesgoExpiracion
            ? 'expiracion'
            : 'stock';

        return {
          idInventario: item.idInventario,
          nombreReactivo: item.nombreReactivo,
          numeroLote: item.numeroLote,
          almacen: item.nombreAlmacen || `Almacén ${item.idAlmacenOrigen ?? '—'}`,
          stockActual: item.stockActual,
          cantidadInicialLote: item.cantidadInicialLote,
          porcentajeStock,
          diasParaExpiracion,
          fechaExpiracion: item.fechaExpiracion,
          estadoExpiracion: item.estadoExpiracion,
          motivo
        };
      })
      .sort((a, b) => {
        const diasA = a.diasParaExpiracion ?? Number.POSITIVE_INFINITY;
        const diasB = b.diasParaExpiracion ?? Number.POSITIVE_INFINITY;

        if (diasA === diasB) {
          return a.porcentajeStock - b.porcentajeStock;
        }

        return diasA - diasB;
      })
      .slice(0, 10);
  }

  private esLoteCritico(item: InventarioItem): boolean {
    const porcentajeStock = this.calcularPorcentajeStock(item);
    return this.esRiesgoStock(porcentajeStock) || this.esRiesgoExpiracion(item.diasParaExpiracion ?? null);
  }

  private calcularPorcentajeStock(item: InventarioItem): number {
    if (!item.cantidadInicialLote || item.cantidadInicialLote <= 0) {
      return 0;
    }
    return item.stockActual / item.cantidadInicialLote;
  }

  private esRiesgoStock(porcentaje: number): boolean {
    return porcentaje <= this.porcentajeStockUmbral;
  }

  private esRiesgoExpiracion(dias: number | null): boolean {
    if (dias === null || Number.isNaN(dias)) {
      return false;
    }
    return dias <= this.diasExpiracionUmbral;
  }

  formatearFecha(fecha?: string): string {
    if (!fecha) {
      return 'Sin fecha';
    }
    const date = new Date(fecha);
    const dia = String(date.getDate()).padStart(2, '0');
    const mes = String(date.getMonth() + 1).padStart(2, '0');
    const anio = date.getFullYear();
    return `${dia}/${mes}/${anio}`;
  }

  formatearPorcentajeStock(valor: number): string {
    const porcentaje = Math.round(Math.min(Math.max(valor, 0), 1) * 100);
    return `${porcentaje}%`;
  }

  formatearDiasRestantes(dias: number | null): string {
    if (dias === null) {
      return 'Sin registro';
    }
    if (dias < 0) {
      return 'Vencido';
    }
    if (dias === 0) {
      return 'Vence hoy';
    }
    return `${dias} días`;
  }

  getExpiracionBadgeClass(dias: number | null): string {
    if (dias === null) {
      return 'bg-gray-100 text-gray-800';
    }
    if (dias <= 0) {
      return 'bg-red-100 text-red-800';
    }
    if (dias <= this.diasExpiracionUmbral) {
      return 'bg-yellow-100 text-yellow-800';
    }
    return 'bg-green-100 text-green-800';
  }

  getMotivoLabel(motivo: LoteCritico['motivo']): string {
    switch (motivo) {
      case 'ambos':
        return 'Stock y vencimiento';
      case 'expiracion':
        return 'Por vencer';
      default:
        return 'Stock < 25%';
    }
  }

  getMotivoClass(motivo: LoteCritico['motivo']): string {
    const mapa: Record<LoteCritico['motivo'], string> = {
      expiracion: 'bg-orange-100 text-orange-800',
      stock: 'bg-amber-100 text-amber-800',
      ambos: 'bg-red-100 text-red-800'
    };
    return mapa[motivo];
  }

  getColorClass(color: 'blue' | 'green' | 'yellow' | 'red'): string {
    const colorMap = {
      blue: 'bg-blue-500',
      green: 'bg-green-600',
      yellow: 'bg-yellow-500',
      red: 'bg-red-500'
    };
    return colorMap[color];
  }

  getTipoClass(colorTipo: 'green' | 'red' | 'blue'): string {
    const colorMap = {
      green: 'bg-green-100 text-green-800',
      red: 'bg-red-100 text-red-800',
      blue: 'bg-blue-100 text-blue-800'
    };
    return colorMap[colorTipo];
  }

  getAlertaClass(tipo: 'warning' | 'danger' | 'info'): string {
    const alertaMap = {
      warning: 'bg-yellow-50 border-yellow-200',
      danger: 'bg-red-50 border-red-200',
      info: 'bg-blue-50 border-blue-200'
    };
    return alertaMap[tipo];
  }
}
