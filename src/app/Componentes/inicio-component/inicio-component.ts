import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SideBarComponent } from '../side-bar-component/side-bar-component';
import { NavbarComponent } from '../navbar-component/navbar-component';
import { RouterOutlet, Router } from '@angular/router';
import { Rest } from '../../Servicios/rest';
import { AuthService } from '../../Servicios/auth.service';
import { 
  StatsCard, 
  MovimientoResumen, 
  Alerta
} from '../../Modelos/interfaces';

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

  // Signals para el estado del componente
  readonly sidebarCollapsed = signal(false);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  // Signals para datos del dashboard
  readonly statsCards = signal<StatsCard[]>([]);
  readonly movimientosRecientes = signal<MovimientoResumen[]>([]);
  readonly alertas = signal<Alerta[]>([]);

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

    // Cargar datos del inventario del almacén 1
    this.restService.obtenerInventarioDetallado(1).subscribe({
      next: (data: any) => {
        console.log('Datos de inventario recibidos:', data);
        this.procesarDatosInventario(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar datos del dashboard:', err);
        this.error.set('Error al cargar los datos del dashboard');
        this.cargando.set(false);
      }
    });
  }

  private procesarDatosInventario(data: any): void {
    const inventario = data?.inventarioDetallado || [];
    
    // Calcular estadísticas desde el inventario
    this.calcularEstadisticasInventario(inventario);
    
    // Generar movimientos recientes simulados (ya que no vienen en esta API)
    this.generarMovimientosRecientes(inventario);
    
    // Generar alertas desde el inventario
    this.generarAlertasInventario(inventario);
  }

  private calcularEstadisticasInventario(inventario: any[]): void {
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

  private generarMovimientosRecientes(inventario: any[]): void {
    // Generar movimientos basados en los últimos items del inventario
    const movimientos: MovimientoResumen[] = inventario
      .slice(0, 5)
      .map((item, index) => ({
        id: index + 1,
        fecha: new Date().toLocaleDateString('es-PE'),
        tipo: 'Inventario',
        reactivo: item.nombreReactivo || 'Sin nombre',
        cantidad: `${item.stockActual} unidades`,
        usuario: 'Sistema',
        colorTipo: 'blue' as const
      }));

    this.movimientosRecientes.set(movimientos);
  }

  private generarAlertasInventario(inventario: any[]): void {
    const alertas: Alerta[] = [];

    // Alertas de stock bajo
    const itemsBajoStock = inventario.filter(item => 
      item.estadoStock === 'Bajo' || item.estadoStock === 'Crítico'
    );
    
    if (itemsBajoStock.length > 0) {
      alertas.push({
        tipo: 'warning',
        titulo: 'Stock Bajo',
        mensaje: `${itemsBajoStock.length} reactivos con stock bajo o crítico`
      });
    }

    // Alertas de vencimiento
    const itemsPorVencer = inventario.filter(item => 
      item.estadoExpiracion === 'Por Vencer' || item.estadoExpiracion === 'Vencido'
    );
    
    if (itemsPorVencer.length > 0) {
      alertas.push({
        tipo: 'danger',
        titulo: 'Vencimiento',
        mensaje: `${itemsPorVencer.length} reactivos por vencer o vencidos`
      });
    }

    this.alertas.set(alertas);
  }

  private formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    const dia = String(date.getDate()).padStart(2, '0');
    const mes = String(date.getMonth() + 1).padStart(2, '0');
    const anio = date.getFullYear();
    return `${dia}/${mes}/${anio}`;
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
