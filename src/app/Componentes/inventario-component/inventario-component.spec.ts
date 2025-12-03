import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { InventarioComponent } from './inventario-component';
import { Rest } from '../../Servicios/rest';
import { Almacen, AlmacenInfo, InventarioItem } from '../../Modelos/interfaces';

interface InventarioResponseMock {
  almacenInfo: AlmacenInfo;
  inventarioDetallado: InventarioItem[];
  resumen?: { totalReactivos: number; totalUnidades: number; valorTotal: number };
}

describe('InventarioComponent - Integration', () => {
  let component: InventarioComponent;
  let fixture: ComponentFixture<InventarioComponent>;
  let httpMock: HttpTestingController;

  const mockAlmacenes: Almacen[] = [
    { id: 1, nombre: 'Almacén Central', direccion: 'Av. Siempre Viva 123', telefono: '999-111-222' },
    { id: 2, nombre: 'Almacén Secundario', direccion: 'Calle Falsa 456', telefono: '999-333-444' }
  ];

  const inventarioItems: InventarioItem[] = [
    {
      idInventario: 101,
      idReactivo: 10,
      nombreReactivo: 'Agua Destilada',
      marca: 'JT Baker',
      numeroLote: 55,
      cantidadInicialLote: 200,
      stockActual: 150,
      precioUnitario: 12.5,
      fechaExpiracion: '2026-05-01',
      diasParaExpiracion: 180,
      estadoExpiracion: 'Vigente',
      estadoStock: 'Stock Normal'
    },
    {
      idInventario: 102,
      idReactivo: 11,
      nombreReactivo: 'Ácido Acético',
      marca: 'Merck',
      numeroLote: 77,
      cantidadInicialLote: 100,
      stockActual: 20,
      precioUnitario: 45,
      fechaExpiracion: '2025-12-10',
      diasParaExpiracion: 30,
      estadoExpiracion: 'Por Vencer',
      estadoStock: 'Stock Bajo'
    }
  ];

  const mockInventarioResponse: InventarioResponseMock = {
    almacenInfo: {
      idAlmacen: 1,
      nombreAlmacen: 'Almacén Central',
      direccion: 'Av. Siempre Viva 123',
      telefono: '999-111-222'
    },
    inventarioDetallado: inventarioItems,
    resumen: {
      totalReactivos: 2,
      totalUnidades: 170,
      valorTotal: 12.5 * 150 + 45 * 20
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventarioComponent, HttpClientTestingModule],
      providers: [Rest]
    }).compileComponents();

    fixture = TestBed.createComponent(InventarioComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  function flushInitialRequests(response: InventarioResponseMock = mockInventarioResponse): void {
    fixture.detectChanges();

    const reqAlmacenes = httpMock.expectOne(req => req.url.includes('/api/almacenes'));
    reqAlmacenes.flush(mockAlmacenes);

    const reqInventario = httpMock.expectOne(req => req.url.includes('/inventario/almacen/1/detallado'));
    expect(reqInventario.request.params.keys().length).toBe(0);
    reqInventario.flush(response);

    fixture.detectChanges();
  }

  it('should load almacenes and inventory on init', () => {
    flushInitialRequests();

    expect(component.almacenes()).toEqual(mockAlmacenes);
    expect(component.almacenSeleccionado()).toBe(1);
    expect(component.almacenInfo()).toEqual(mockInventarioResponse.almacenInfo);
    expect(component.inventario()).toEqual(inventarioItems);
    expect(component.cargando()).toBeFalse();
    expect(component.error()).toBe('');
  });

  it('should send nombreReactivo param when searching', () => {
    flushInitialRequests();

    component.busquedaReactivo.set('Agua');
    component.buscar();

    const req = httpMock.expectOne(req => req.url.includes('/inventario/almacen/1/detallado'));
    expect(req.request.params.get('nombreReactivo')).toBe('Agua');

    req.flush(mockInventarioResponse);
  });

  it('should clear search and reload inventory without params', () => {
    flushInitialRequests();

    component.busquedaReactivo.set('Acético');
    component.buscar();

    const reqBusqueda = httpMock.expectOne(req => req.url.includes('/inventario/almacen/1/detallado'));
    expect(reqBusqueda.request.params.get('nombreReactivo')).toBe('Acético');
    reqBusqueda.flush(mockInventarioResponse);

    component.limpiarBusqueda();

    const reqLimpieza = httpMock.expectOne(req => req.url.includes('/inventario/almacen/1/detallado'));
    expect(reqLimpieza.request.params.keys().length).toBe(0);
    reqLimpieza.flush(mockInventarioResponse);
  });

  it('should handle inventory load error', () => {
    fixture.detectChanges();

    httpMock.expectOne(req => req.url.includes('/api/almacenes')).flush(mockAlmacenes);

    const reqInventario = httpMock.expectOne(req => req.url.includes('/inventario/almacen/1/detallado'));
    reqInventario.flush('Error backend', { status: 500, statusText: 'Server Error' });

    expect(component.error()).toBe('Error al cargar el inventario');
    expect(component.inventario()).toEqual([]);
    expect(component.cargando()).toBeFalse();
  });

  it('should compute totals based on filtered results', () => {
    flushInitialRequests();

    expect(component.totalReactivos()).toBe(2);
    expect(component.totalUnidades()).toBe(170);
    expect(component.valorTotalInventario()).toBeCloseTo(12.5 * 150 + 45 * 20);

    component.busquedaReactivo.set('ácido');
    fixture.detectChanges();

    const filtrado = component.inventarioFiltrado();
    expect(filtrado.length).toBe(1);
    expect(filtrado[0].nombreReactivo).toContain('Ácido');
    expect(component.totalReactivos()).toBe(1);
    expect(component.totalUnidades()).toBe(20);
  });

  it('should render table rows with inventory data', () => {
    flushInitialRequests();

    const compiled = fixture.nativeElement as HTMLElement;
    const rows = compiled.querySelectorAll('tbody tr');
    expect(rows.length).toBe(2);
    expect(compiled.textContent).toContain('Agua Destilada');
    expect(compiled.textContent).toContain('Ácido Acético');
  });
});
