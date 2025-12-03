import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { signal } from '@angular/core';

import { HistorialComponent } from './historial-component';
import { Rest } from '../../Servicios/rest';
import { DetalleMovimiento, ResumenPorTipo, Reactivo, Almacen, Tipoaccion } from '../../Modelos/interfaces';

describe('HistorialComponent - Integration Tests', () => {
  let component: HistorialComponent;
  let fixture: ComponentFixture<HistorialComponent>;
  let httpMock: HttpTestingController;
  let restService: Rest;

  const mockReactivos: Reactivo[] = [
    { id: 1, nombre: 'Agua Destilada', idMarca: { id: 1, nombre: 'J.T.Baker', estado: true } },
    { id: 2, nombre: 'Ácido Sulfúrico 98%', idMarca: { id: 2, nombre: 'Merck', estado: true } }
  ];

  const mockAlmacenes: Almacen[] = [
    { id: 1, nombre: 'Almacén Principal', direccion: 'Calle 1', telefono: '123456' },
    { id: 2, nombre: 'Almacén Secundario', direccion: 'Calle 2', telefono: '789012' }
  ];

  const mockTiposAccion: Tipoaccion[] = [
    { id: 1, nombre: 'INGRESO' },
    { id: 2, nombre: 'TRASLADO' },
    { id: 3, nombre: 'CONSUMO' },
    { id: 4, nombre: 'AJUSTE' }
  ];

  const mockDetalleMovimientos: DetalleMovimiento[] = [
    {
      idMovimiento: 1,
      fecha: '2025-11-25T17:40:25.897Z',
      tipoAccion: 'INGRESO',
      usuario: 'Admin Usuario',
      referencia: 'ING001',
      comentario: 'Ingreso de prueba',
      nombreReactivo: 'Agua Destilada',
      marca: 'J.T.Baker',
      numeroLote: 10,
      almacenOrigen: null,
      almacenDestino: 'Almacén Principal',
      cantidad: 100,
      precioVenta: null,
      valorTotal: 1000
    },
    {
      idMovimiento: 2,
      fecha: '2025-11-25T17:38:20.867Z',
      tipoAccion: 'CONSUMO',
      usuario: 'Admin Usuario',
      referencia: 'CONS001',
      comentario: 'Consumo de prueba',
      nombreReactivo: 'Ácido Sulfúrico 98%',
      marca: 'Merck',
      numeroLote: 11,
      almacenOrigen: 'Almacén Principal',
      almacenDestino: null,
      cantidad: -10,
      precioVenta: null,
      valorTotal: -200
    }
  ];

  const mockResumenPorTipo: ResumenPorTipo[] = [
    { tipoAccion: 'INGRESO', totalMovimientos: 1, totalUnidades: 100, valorTotal: 1000 },
    { tipoAccion: 'CONSUMO', totalMovimientos: 1, totalUnidades: -10, valorTotal: -200 }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistorialComponent, HttpClientTestingModule],
      providers: [Rest, provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistorialComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    restService = TestBed.inject(Rest);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load initial data on init', () => {
    // Trigger ngOnInit
    fixture.detectChanges();

    // Expect 4 HTTP requests: reactivos, almacenes, tipos de acción, historial
    const reqReactivos = httpMock.expectOne(req => req.url.includes('/reactivos'));
    const reqAlmacenes = httpMock.expectOne(req => req.url.includes('/almacenes'));
    const reqTiposAccion = httpMock.expectOne(req => req.url.includes('/tipoaccion'));
    const reqHistorial = httpMock.expectOne(req => req.url.includes('/movimientos/historial'));

    // Respond with mock data
    reqReactivos.flush(mockReactivos);
    reqAlmacenes.flush(mockAlmacenes);
    reqTiposAccion.flush(mockTiposAccion);
    reqHistorial.flush({
      detalleMovimientos: mockDetalleMovimientos,
      resumenPorTipo: mockResumenPorTipo
    });

    // Verify data was loaded
    expect(component.reactivos()).toEqual(mockReactivos);
    expect(component.almacenes()).toEqual(mockAlmacenes);
    expect(component.tiposAccion()).toEqual(mockTiposAccion);
    expect(component.movimientos()).toEqual(mockDetalleMovimientos);
    expect(component.resumenPorTipo()).toEqual(mockResumenPorTipo);
    expect(component.cargando()).toBe(false);
  });

  it('should filter movements by reactivo', () => {
    fixture.detectChanges();

    // Complete initial requests
    httpMock.expectOne(req => req.url.includes('/reactivos')).flush(mockReactivos);
    httpMock.expectOne(req => req.url.includes('/almacenes')).flush(mockAlmacenes);
    httpMock.expectOne(req => req.url.includes('/tipoaccion')).flush(mockTiposAccion);
    httpMock.expectOne(req => req.url.includes('/movimientos/historial')).flush({
      detalleMovimientos: mockDetalleMovimientos,
      resumenPorTipo: mockResumenPorTipo
    });

    // Set filter
    component.filtroReactivo.set(1);
    component.buscarHistorial();

    // Expect request with query params
    const req = httpMock.expectOne(req => 
      req.url.includes('/movimientos/historial') && 
      req.params.has('idReactivo')
    );
    expect(req.request.params.get('idReactivo')).toBe('1');

    req.flush({
      detalleMovimientos: [mockDetalleMovimientos[0]],
      resumenPorTipo: [mockResumenPorTipo[0]]
    });

    expect(component.movimientos().length).toBe(1);
    expect(component.movimientos()[0].nombreReactivo).toBe('Agua Destilada');
  });

  it('should filter movements by almacen', () => {
    fixture.detectChanges();

    // Complete initial requests
    httpMock.expectOne(req => req.url.includes('/reactivos')).flush(mockReactivos);
    httpMock.expectOne(req => req.url.includes('/almacenes')).flush(mockAlmacenes);
    httpMock.expectOne(req => req.url.includes('/tipoaccion')).flush(mockTiposAccion);
    httpMock.expectOne(req => req.url.includes('/movimientos/historial')).flush({
      detalleMovimientos: mockDetalleMovimientos,
      resumenPorTipo: mockResumenPorTipo
    });

    // Set filter
    component.filtroAlmacen.set(1);
    component.buscarHistorial();

    const req = httpMock.expectOne(req => 
      req.url.includes('/movimientos/historial') && 
      req.params.has('idAlmacen')
    );
    expect(req.request.params.get('idAlmacen')).toBe('1');

    req.flush({
      detalleMovimientos: mockDetalleMovimientos,
      resumenPorTipo: mockResumenPorTipo
    });
  });

  it('should filter movements by date range', () => {
    fixture.detectChanges();

    // Complete initial requests
    httpMock.expectOne(req => req.url.includes('/reactivos')).flush(mockReactivos);
    httpMock.expectOne(req => req.url.includes('/almacenes')).flush(mockAlmacenes);
    httpMock.expectOne(req => req.url.includes('/tipoaccion')).flush(mockTiposAccion);
    httpMock.expectOne(req => req.url.includes('/movimientos/historial')).flush({
      detalleMovimientos: mockDetalleMovimientos,
      resumenPorTipo: mockResumenPorTipo
    });

    // Set date filters
    component.filtroFechaInicio.set('2025-11-01');
    component.filtroFechaFin.set('2025-11-30');
    component.buscarHistorial();

    const req = httpMock.expectOne(req => 
      req.url.includes('/movimientos/historial') && 
      req.params.has('fechaInicio') &&
      req.params.has('fechaFin')
    );
    expect(req.request.params.get('fechaInicio')).toBe('2025-11-01');
    expect(req.request.params.get('fechaFin')).toBe('2025-11-30');

    req.flush({
      detalleMovimientos: mockDetalleMovimientos,
      resumenPorTipo: mockResumenPorTipo
    });
  });

  it('should filter movements by tipo de accion', () => {
    fixture.detectChanges();

    // Complete initial requests
    httpMock.expectOne(req => req.url.includes('/reactivos')).flush(mockReactivos);
    httpMock.expectOne(req => req.url.includes('/almacenes')).flush(mockAlmacenes);
    httpMock.expectOne(req => req.url.includes('/tipoaccion')).flush(mockTiposAccion);
    httpMock.expectOne(req => req.url.includes('/movimientos/historial')).flush({
      detalleMovimientos: mockDetalleMovimientos,
      resumenPorTipo: mockResumenPorTipo
    });

    // Set filter
    component.filtroTipoAccion.set(1);
    component.buscarHistorial();

    const req = httpMock.expectOne(req => 
      req.url.includes('/movimientos/historial') && 
      req.params.has('idTipoAccion')
    );
    expect(req.request.params.get('idTipoAccion')).toBe('1');

    req.flush({
      detalleMovimientos: [mockDetalleMovimientos[0]],
      resumenPorTipo: [mockResumenPorTipo[0]]
    });

    expect(component.movimientos()[0].tipoAccion).toBe('INGRESO');
  });

  it('should clear all filters', () => {
    fixture.detectChanges();

    // Complete initial requests
    httpMock.expectOne(req => req.url.includes('/reactivos')).flush(mockReactivos);
    httpMock.expectOne(req => req.url.includes('/almacenes')).flush(mockAlmacenes);
    httpMock.expectOne(req => req.url.includes('/tipoaccion')).flush(mockTiposAccion);
    httpMock.expectOne(req => req.url.includes('/movimientos/historial')).flush({
      detalleMovimientos: mockDetalleMovimientos,
      resumenPorTipo: mockResumenPorTipo
    });

    // Set filters
    component.filtroReactivo.set(1);
    component.filtroAlmacen.set(1);
    component.filtroFechaInicio.set('2025-11-01');
    component.filtroFechaFin.set('2025-11-30');
    component.filtroTipoAccion.set(1);

    // Clear filters
    component.limpiarFiltros();

    // Verify filters are cleared
    expect(component.filtroReactivo()).toBeNull();
    expect(component.filtroAlmacen()).toBeNull();
    expect(component.filtroFechaInicio()).toBe('');
    expect(component.filtroFechaFin()).toBe('');
    expect(component.filtroTipoAccion()).toBeNull();

    // Expect new request without params
    const req = httpMock.expectOne(req => req.url.includes('/movimientos/historial'));
    expect(req.request.params.keys().length).toBe(0);

    req.flush({
      detalleMovimientos: mockDetalleMovimientos,
      resumenPorTipo: mockResumenPorTipo
    });
  });

  it('should handle error when loading historial', () => {
    fixture.detectChanges();

    // Complete initial requests except historial
    httpMock.expectOne(req => req.url.includes('/reactivos')).flush(mockReactivos);
    httpMock.expectOne(req => req.url.includes('/almacenes')).flush(mockAlmacenes);
    httpMock.expectOne(req => req.url.includes('/tipoaccion')).flush(mockTiposAccion);

    // Return error for historial
    const req = httpMock.expectOne(req => req.url.includes('/movimientos/historial'));
    req.flush('Error al cargar historial', { status: 500, statusText: 'Server Error' });

    expect(component.error()).toBe('Error al cargar el historial de movimientos');
    expect(component.cargando()).toBe(false);
  });

  it('should format fecha correctly', () => {
    const fecha = '2025-11-25T17:40:25.897Z';
    const formatted = component.formatearFechaHora(fecha);
    expect(formatted).toContain('25');
    expect(formatted).toContain('11');
    expect(formatted).toContain('2025');
  });

  it('should display movements in the table', () => {
    fixture.detectChanges();

    // Complete all initial requests
    httpMock.expectOne(req => req.url.includes('/reactivos')).flush(mockReactivos);
    httpMock.expectOne(req => req.url.includes('/almacenes')).flush(mockAlmacenes);
    httpMock.expectOne(req => req.url.includes('/tipoaccion')).flush(mockTiposAccion);
    httpMock.expectOne(req => req.url.includes('/movimientos/historial')).flush({
      detalleMovimientos: mockDetalleMovimientos,
      resumenPorTipo: mockResumenPorTipo
    });

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const rows = compiled.querySelectorAll('tbody tr');
    
    expect(rows.length).toBe(2);
    expect(compiled.textContent).toContain('Agua Destilada');
    expect(compiled.textContent).toContain('Ácido Sulfúrico 98%');
  });
});
