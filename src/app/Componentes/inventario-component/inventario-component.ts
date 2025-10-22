import { Component, signal, computed, inject, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Rest } from '../../Servicios/rest';
import { Almacen, InventarioDetallado, InventarioItem, AlmacenInfo } from '../../Modelos/interfaces';

@Component({
  selector: 'app-inventario-component',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario-component.html',
  styleUrl: './inventario-component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InventarioComponent implements OnInit {
  private readonly restService = inject(Rest);

  // Signals
  almacenes = signal<Almacen[]>([]);
  almacenSeleccionado = signal<number | null>(null);
  almacenInfo = signal<AlmacenInfo | null>(null);
  busquedaReactivo = signal<string>('');
  inventario = signal<InventarioItem[]>([]);
  cargando = signal<boolean>(false);
  error = signal<string>('');

  // Computed
  inventarioFiltrado = computed(() => {
    const inventarioData = this.inventario() || [];
    const busqueda = this.busquedaReactivo().toLowerCase().trim();

    if (!busqueda) {
      return inventarioData;
    }

    return inventarioData.filter(item =>
      item.nombreReactivo.toLowerCase().includes(busqueda) ||
      item.marca.toLowerCase().includes(busqueda)
    );
  });

  totalReactivos = computed(() => {
    const filtrado = this.inventarioFiltrado();
    return Array.isArray(filtrado) ? filtrado.length : 0;
  });
  
  totalUnidades = computed(() => {
    const filtrado = this.inventarioFiltrado();
    return Array.isArray(filtrado) ? filtrado.reduce((total, item) => total + item.stockActual, 0) : 0;
  });

  valorTotalInventario = computed(() => {
    const filtrado = this.inventarioFiltrado();
    return Array.isArray(filtrado) ? filtrado.reduce((total, item) => total + (item.stockActual * item.precioUnitario), 0) : 0;
  });

  ngOnInit(): void {
    console.log('=== InventarioComponent ngOnInit ===');
    // Cargar almacenes primero
    this.cargarAlmacenes();
    // Seleccionar automáticamente el almacén con ID 1 (Inventario Central)
    console.log('Estableciendo almacén seleccionado = 1');
    this.almacenSeleccionado.set(1);
    console.log('Almacén seleccionado:', this.almacenSeleccionado());
    // Cargar el inventario del almacén ID 1
    this.cargarInventario();
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

  cargarInventario(): void {
    const idAlmacen = this.almacenSeleccionado();
    
    console.log('=== cargarInventario ===');
    console.log('ID Almacén seleccionado:', idAlmacen);
    
    if (!idAlmacen) {
      console.log('No hay almacén seleccionado');
      this.inventario.set([]);
      return;
    }

    this.cargando.set(true);
    this.error.set('');

    const nombreReactivo = this.busquedaReactivo().trim() || undefined;
    console.log('Nombre reactivo (búsqueda):', nombreReactivo);
    console.log('Llamando a obtenerInventarioDetallado...');

    this.restService.obtenerInventarioDetallado(idAlmacen, nombreReactivo).subscribe({
      next: (data) => {
        console.log('Datos recibidos del backend:', data);
        
        // El backend retorna un objeto con la estructura: { almacenInfo, inventarioDetallado, resumen }
        // Extraemos la información del almacén y el array de inventario
        const almacenInfoData: AlmacenInfo | null = data?.almacenInfo || null;
        const inventarioArray: InventarioItem[] = data?.inventarioDetallado || [];
        console.log('Información del almacén:', almacenInfoData);
        console.log('Array de inventario extraído:', inventarioArray);
        console.log('Cantidad de items:', inventarioArray.length);
        
        this.almacenInfo.set(almacenInfoData);
        this.inventario.set(inventarioArray);
        this.cargando.set(false);
        
        console.log('Inventario actualizado:', this.inventario());
      },
      error: (err) => {
        console.error('Error al cargar inventario:', err);
        console.error('Detalles del error:', err.error);
        this.error.set('Error al cargar el inventario');
        this.inventario.set([]);
        this.cargando.set(false);
      }
    });
  }

  onAlmacenChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const idAlmacen = parseInt(target.value);
    this.almacenSeleccionado.set(idAlmacen);
    this.cargarInventario();
  }

  onBusquedaChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.busquedaReactivo.set(target.value);
  }

  buscar(): void {
    this.cargarInventario();
  }

  limpiarBusqueda(): void {
    this.busquedaReactivo.set('');
    this.cargarInventario();
  }

  formatearPrecio(precio: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN'
    }).format(precio);
  }
}
