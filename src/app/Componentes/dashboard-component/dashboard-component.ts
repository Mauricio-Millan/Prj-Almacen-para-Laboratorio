import { Component, ChangeDetectionStrategy, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Rest } from '../../Servicios/rest';
import { Almacen, InventarioItem, AlmacenInfo } from '../../Modelos/interfaces';

@Component({
  selector: 'app-dashboard-component',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard-component.html',
  styleUrls: ['./dashboard-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit {
  private readonly restService = inject(Rest);

  // Signals
  almacenes = signal<Almacen[]>([]);
  almacenSeleccionado = signal<number>(1); // Default to almacén ID 1
  almacenInfo = signal<AlmacenInfo | null>(null);
  inventario = signal<InventarioItem[]>([]);
  resumen = signal<any>(null);
  cargando = signal<boolean>(false);
  error = signal<string>('');

  // Computed properties for dashboard statistics
  totalReactivos = computed(() => {
    const inv = this.inventario();
    return Array.isArray(inv) ? inv.length : 0;
  });

  reactivosPorVencer = computed(() => {
    const inv = this.inventario();
    if (!Array.isArray(inv)) return 0;
    
    // Filtrar por items con diasParaExpiracion <= 30
    return inv.filter(item => 
      item.diasParaExpiracion !== null && 
      item.diasParaExpiracion >= 0 && 
      item.diasParaExpiracion <= 30
    ).length;
  });

  reactivosBajoStock = computed(() => {
    const inv = this.inventario();
    if (!Array.isArray(inv)) return 0;
    
    // Filtrar por items con estadoStock 'Bajo' o 'Crítico'
    return inv.filter(item => 
      item.estadoStock === 'Bajo' || item.estadoStock === 'Crítico'
    ).length;
  });

  valorTotalInventario = computed(() => {
    const inv = this.inventario();
    if (!Array.isArray(inv)) return 0;
    
    return inv.reduce((total, item) => 
      total + (item.stockActual * item.precioUnitario), 0
    );
  });

  // Reactivos recientes (últimos 5 items)
  reactivosRecientes = computed(() => {
    const inv = this.inventario();
    if (!Array.isArray(inv)) return [];
    
    // Ordenar por ID descendente (asumiendo que IDs más altos son más recientes)
    return [...inv]
      .sort((a, b) => (b.idInventario || 0) - (a.idInventario || 0))
      .slice(0, 5);
  });

  ngOnInit(): void {
    this.cargarAlmacenes();
    this.cargarDatosAlmacen();
  }

  cargarAlmacenes(): void {
    this.restService.obtenerAlmacenes().subscribe({
      next: (almacenes) => {
        this.almacenes.set(almacenes);
      },
      error: (err) => {
        console.error('Error al cargar almacenes:', err);
        this.error.set('Error al cargar los almacenes');
      }
    });
  }

  cargarDatosAlmacen(): void {
    const idAlmacen = this.almacenSeleccionado();
    
    if (!idAlmacen) {
      return;
    }

    this.cargando.set(true);
    this.error.set('');

    this.restService.obtenerInventarioDetallado(idAlmacen).subscribe({
      next: (data: any) => {
        console.log('Datos del almacén recibidos:', data);
        
        // El backend retorna: { almacenInfo, inventarioDetallado, resumen }
        const almacenInfoData: AlmacenInfo | null = data?.almacenInfo || null;
        const inventarioArray: InventarioItem[] = data?.inventarioDetallado || [];
        const resumenData = data?.resumen || null;
        
        this.almacenInfo.set(almacenInfoData);
        this.inventario.set(inventarioArray);
        this.resumen.set(resumenData);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar datos del almacén:', err);
        this.error.set('Error al cargar los datos del almacén');
        this.cargando.set(false);
      }
    });
  }

  onAlmacenChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const idAlmacen = parseInt(select.value, 10);
    this.almacenSeleccionado.set(idAlmacen);
    this.cargarDatosAlmacen();
  }

  getEstadoClass(estado: string): string {
    const clases: { [key: string]: string } = {
      'Óptimo': 'bg-green-100 text-green-800',
      'Bueno': 'bg-blue-100 text-blue-800',
      'Bajo': 'bg-yellow-100 text-yellow-800',
      'Crítico': 'bg-red-100 text-red-800'
    };
    return clases[estado] || 'bg-gray-100 text-gray-800';
  }

  formatearPrecio(precio: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN'
    }).format(precio);
  }
}


