import { Component, inject, signal, computed, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SideBarComponent } from '../side-bar-component/side-bar-component';
import { NavbarComponent } from '../navbar-component/navbar-component';
import { RouterOutlet, Router } from '@angular/router';
import { Rest } from '../../Servicios/rest';
import { AuthService } from '../../Servicios/auth.service';
import { 
  StatsCard, 
  MovimientoResumen, 
  Alerta,
  Reactivo,
  Lote,
  Movimiento,
  Movimientolinea
} from '../../Modelos/interfaces';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [
    CommonModule,
    SideBarComponent,
    NavbarComponent
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

    // Cargar todos los datos en paralelo
    forkJoin({
      reactivos: this.restService.obtenerReactivos(),
      lotes: this.restService.obtenerLotes(),
      movimientos: this.restService.obtenerMovimientos(),
      movimientolineas: this.restService.obtenerMovimientolineas()
    }).subscribe({
      next: (datos) => {
        this.procesarDatos(datos);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar datos del dashboard:', err);
        this.error.set('Error al cargar los datos del dashboard');
        this.cargando.set(false);
      }
    });
  }

  private procesarDatos(datos: {
    reactivos: Reactivo[];
    lotes: Lote[];
    movimientos: Movimiento[];
    movimientolineas: Movimientolinea[];
  }): void {
    // Procesar estadísticas
    this.calcularEstadisticas(datos);

    // Procesar movimientos recientes
    this.procesarMovimientosRecientes(datos.movimientos, datos.movimientolineas);

    // Generar alertas
    this.generarAlertas(datos.lotes, datos.reactivos);
  }

  private calcularEstadisticas(datos: {
    reactivos: Reactivo[];
    lotes: Lote[];
    movimientos: Movimiento[];
    movimientolineas: Movimientolinea[];
  }): void {
    const ahora = new Date();
    const treintaDiasAdelante = new Date();
    treintaDiasAdelante.setDate(ahora.getDate() + 30);

    // Total de reactivos
    const totalReactivos = datos.reactivos.length;

    // Stock disponible (suma de cantidades iniciales de lotes activos)
    const stockDisponible = datos.lotes
      .filter(lote => lote.estado)
      .reduce((sum, lote) => sum + lote.cantidadInicial, 0);

    // Lotes por vencer (en los próximos 30 días)
    const lotesPorVencer = datos.lotes.filter(lote => {
      if (!lote.estado) return false;
      const fechaExpiracion = new Date(lote.fechaExpiracion);
      return fechaExpiracion >= ahora && fechaExpiracion <= treintaDiasAdelante;
    }).length;

    // Stock bajo (reactivos con menos de 10 unidades totales)
    const reactivosConStockBajo = new Set<number>();
    const stockPorReactivo = new Map<number, number>();

    datos.lotes.forEach(lote => {
      if (lote.estado) {
        const reactivoId = lote.idReactivo.id;
        const stockActual = stockPorReactivo.get(reactivoId) || 0;
        stockPorReactivo.set(reactivoId, stockActual + lote.cantidadInicial);
      }
    });

    stockPorReactivo.forEach((cantidad, reactivoId) => {
      if (cantidad < 10) {
        reactivosConStockBajo.add(reactivoId);
      }
    });

    const stockBajo = reactivosConStockBajo.size;

    this.statsCards.set([
      {
        titulo: 'Total Reactivos',
        valor: totalReactivos,
        color: 'blue',
        icono: '🧪'
      },
      {
        titulo: 'Stock Disponible',
        valor: Math.round(stockDisponible),
        color: 'green',
        icono: '📦'
      },
      {
        titulo: 'Por Vencer',
        valor: lotesPorVencer,
        color: 'yellow',
        icono: '⚠️'
      },
      {
        titulo: 'Stock Bajo',
        valor: stockBajo,
        color: 'red',
        icono: '⬇️'
      }
    ]);
  }

  private procesarMovimientosRecientes(
    movimientos: Movimiento[],
    movimientolineas: Movimientolinea[]
  ): void {
    // Ordenar movimientos por fecha (más recientes primero)
    const movimientosOrdenados = [...movimientos]
      .sort((a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime())
      .slice(0, 10); // Tomar los 10 más recientes

    const movimientosResumen: MovimientoResumen[] = movimientosOrdenados.map(mov => {
      // Buscar líneas de movimiento asociadas
      const lineas = movimientolineas.filter(l => l.idMovimiento.id === mov.id);
      
      // Calcular cantidad total del movimiento
      const cantidadTotal = lineas.reduce((sum, linea) => sum + Math.abs(linea.cantidadDelta), 0);
      
      // Obtener nombre del reactivo de la primera línea
      const primerLinea = lineas[0];
      const nombreReactivo = primerLinea?.idLote?.idReactivo?.nombre || 'N/A';

      // Determinar color según tipo de acción
      let colorTipo: 'green' | 'red' | 'blue' = 'blue';
      const tipoAccion = mov.idTipoAccion.nombre.toLowerCase();
      
      if (tipoAccion.includes('compra') || tipoAccion.includes('ingreso')) {
        colorTipo = 'green';
      } else if (tipoAccion.includes('consumo') || tipoAccion.includes('salida')) {
        colorTipo = 'red';
      } else if (tipoAccion.includes('traslado') || tipoAccion.includes('transferencia')) {
        colorTipo = 'blue';
      }

      // Formatear cantidad con signo
      const signo = colorTipo === 'green' ? '+' : colorTipo === 'red' ? '-' : '';
      const cantidadFormateada = `${signo}${cantidadTotal.toFixed(2)}L`;

      return {
        id: mov.id,
        fecha: this.formatearFecha(mov.fecha),
        tipo: mov.idTipoAccion.nombre,
        reactivo: nombreReactivo,
        cantidad: cantidadFormateada,
        usuario: mov.idUsuario.nombre,
        colorTipo
      };
    });

    this.movimientosRecientes.set(movimientosResumen);
  }

  private generarAlertas(lotes: Lote[], reactivos: Reactivo[]): void {
    const ahora = new Date();
    const treintaDiasAdelante = new Date();
    treintaDiasAdelante.setDate(ahora.getDate() + 30);

    const alertasGeneradas: Alerta[] = [];

    // Alerta de vencimiento
    const lotesProximosVencer = lotes.filter(lote => {
      if (!lote.estado) return false;
      const fechaExpiracion = new Date(lote.fechaExpiracion);
      return fechaExpiracion >= ahora && fechaExpiracion <= treintaDiasAdelante;
    }).length;

    if (lotesProximosVencer > 0) {
      alertasGeneradas.push({
        tipo: 'warning',
        titulo: 'Vencimiento:',
        mensaje: `${lotesProximosVencer} lotes vencen en 30 días`
      });
    }

    // Alerta de stock crítico
    const stockPorReactivo = new Map<number, number>();
    lotes.forEach(lote => {
      if (lote.estado) {
        const reactivoId = lote.idReactivo.id;
        const stockActual = stockPorReactivo.get(reactivoId) || 0;
        stockPorReactivo.set(reactivoId, stockActual + lote.cantidadInicial);
      }
    });

    let reactivosStockBajo = 0;
    stockPorReactivo.forEach((cantidad) => {
      if (cantidad < 10) {
        reactivosStockBajo++;
      }
    });

    if (reactivosStockBajo > 0) {
      alertasGeneradas.push({
        tipo: 'danger',
        titulo: 'Stock crítico:',
        mensaje: `${reactivosStockBajo} reactivos bajo stock mínimo`
      });
    }

    // Alerta recordatoria (ejemplo)
    alertasGeneradas.push({
      tipo: 'info',
      titulo: 'Recordatorio:',
      mensaje: 'Revisión mensual pendiente'
    });

    this.alertas.set(alertasGeneradas);
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
