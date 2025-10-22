import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import {
  Usuario,
  Reactivo,
  Lote,
  Movimiento,
  Consumo,
  Compra,
  Almacen,
  Proveedor,
  Departamento,
  Marca,
  Tipoaccion,
  Role,
  Movimientolinea,
  LoginRequest,
  LoginResponse,
  DashboardStats
} from '../Modelos/interfaces';

@Injectable({
  providedIn: 'root'
})
export class Rest {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/dbrestalmacenlaboratorio';
  private readonly apiUrl = `${this.baseUrl}/api`;
  private readonly restUrl = `${this.baseUrl}/Rest_AlmacenLaboratorio/api`;

  // ==================== AUTH ====================
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials);
  }

  register(usuario: Usuario): Observable<any> {
    return this.http.post(`${this.apiUrl}/usuarios`, usuario);
  }

  // ==================== USUARIOS ====================
  obtenerUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.apiUrl}/usuarios`);
  }

  obtenerUsuarioPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/usuarios/${id}`);
  }

  obtenerUsuarioPorNombre(nombre: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/usuarios/nombre/${nombre}`);
  }

  obtenerUsuarioPorDni(dni: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/usuarios/dni/${dni}`);
  }

  crearUsuario(usuario: Usuario): Observable<any> {
    return this.http.post(`${this.apiUrl}/usuarios`, usuario);
  }

  actualizarUsuario(id: number, usuario: Usuario): Observable<any> {
    return this.http.put(`${this.apiUrl}/usuarios/${id}`, usuario);
  }

  eliminarUsuario(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/usuarios/${id}`);
  }

  // ==================== REACTIVOS ====================
  obtenerReactivos(): Observable<Reactivo[]> {
    return this.http.get<Reactivo[]>(`${this.apiUrl}/reactivos`);
  }

  obtenerReactivoPorId(id: number): Observable<Reactivo> {
    return this.http.get<Reactivo>(`${this.apiUrl}/reactivos/${id}`);
  }

  obtenerReactivoPorNombre(nombre: string): Observable<Reactivo> {
    return this.http.get<Reactivo>(`${this.apiUrl}/reactivos/nombre/${nombre}`);
  }

  obtenerReactivosPorMarca(idMarca: number): Observable<Reactivo[]> {
    return this.http.get<Reactivo[]>(`${this.apiUrl}/reactivos/marca/${idMarca}`);
  }

  existeReactivo(id: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/reactivos/existe/${id}`);
  }

  crearReactivo(reactivo: Reactivo): Observable<any> {
    return this.http.post(`${this.apiUrl}/reactivos`, reactivo);
  }

  actualizarReactivo(id: number, reactivo: Reactivo): Observable<any> {
    return this.http.put(`${this.apiUrl}/reactivos/${id}`, reactivo);
  }

  eliminarReactivo(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/reactivos/${id}`);
  }

  // ==================== LOTES ====================
  obtenerLotes(): Observable<Lote[]> {
    return this.http.get<Lote[]>(`${this.restUrl}/lotes`);
  }

  obtenerLotePorId(id: number): Observable<Lote> {
    return this.http.get<Lote>(`${this.restUrl}/lotes/${id}`);
  }

  obtenerLotesPorReactivo(idReactivo: number): Observable<Lote[]> {
    return this.http.get<Lote[]>(`${this.restUrl}/lotes/reactivo/${idReactivo}`);
  }

  obtenerLotesPorCompra(idCompra: number): Observable<Lote[]> {
    return this.http.get<Lote[]>(`${this.restUrl}/lotes/compra/${idCompra}`);
  }

  obtenerLotesPorEstado(estado: boolean): Observable<Lote[]> {
    return this.http.get<Lote[]>(`${this.restUrl}/lotes/estado/${estado}`);
  }

  obtenerLotesProximosAVencer(fecha: string): Observable<Lote[]> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<Lote[]>(`${this.restUrl}/lotes/proximos-vencer`, { params });
  }

  obtenerLotesPorRangoExpiracion(fechaInicio: string, fechaFin: string): Observable<Lote[]> {
    const params = new HttpParams()
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin);
    return this.http.get<Lote[]>(`${this.restUrl}/lotes/rango-expiracion`, { params });
  }

  existeLote(id: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.restUrl}/lotes/${id}/existe`);
  }

  crearLote(lote: Lote): Observable<Lote> {
    return this.http.post<Lote>(`${this.restUrl}/lotes`, lote);
  }

  actualizarLote(id: number, lote: Lote): Observable<Lote> {
    return this.http.put<Lote>(`${this.restUrl}/lotes/${id}`, lote);
  }

  desactivarLote(id: number): Observable<Lote> {
    return this.http.patch<Lote>(`${this.restUrl}/lotes/${id}/desactivar`, {});
  }

  eliminarLote(id: number): Observable<any> {
    return this.http.delete(`${this.restUrl}/lotes/${id}`);
  }

  // ==================== MOVIMIENTOS ====================
  obtenerMovimientos(): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(`${this.restUrl}/movimientos`);
  }

  obtenerMovimientoPorId(id: number): Observable<Movimiento> {
    return this.http.get<Movimiento>(`${this.restUrl}/movimientos/${id}`);
  }

  obtenerMovimientosPorUsuario(idUsuario: number): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(`${this.restUrl}/movimientos/usuario/${idUsuario}`);
  }

  obtenerMovimientosPorTipoAccion(idTipoAccion: number): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(`${this.restUrl}/movimientos/tipo-accion/${idTipoAccion}`);
  }

  obtenerMovimientosPorReferencia(referencia: string): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(`${this.restUrl}/movimientos/referencia/${referencia}`);
  }

  obtenerMovimientosPorRangoFechas(fechaInicio: string, fechaFin: string): Observable<Movimiento[]> {
    const params = new HttpParams()
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin);
    return this.http.get<Movimiento[]>(`${this.restUrl}/movimientos/rango-fechas`, { params });
  }

  crearMovimiento(movimiento: Movimiento): Observable<Movimiento> {
    return this.http.post<Movimiento>(`${this.restUrl}/movimientos`, movimiento);
  }

  actualizarMovimiento(id: number, movimiento: Movimiento): Observable<Movimiento> {
    return this.http.put<Movimiento>(`${this.restUrl}/movimientos/${id}`, movimiento);
  }

  eliminarMovimiento(id: number): Observable<any> {
    return this.http.delete(`${this.restUrl}/movimientos/${id}`);
  }

  // ==================== MOVIMIENTO LINEAS ====================
  obtenerMovimientolineas(): Observable<Movimientolinea[]> {
    return this.http.get<Movimientolinea[]>(`${this.restUrl}/movimientolineas`);
  }

  obtenerMovimientolineaPorId(id: number): Observable<Movimientolinea> {
    return this.http.get<Movimientolinea>(`${this.restUrl}/movimientolineas/${id}`);
  }

  obtenerMovimientolineasPorMovimiento(idMovimiento: number): Observable<Movimientolinea[]> {
    return this.http.get<Movimientolinea[]>(`${this.restUrl}/movimientolineas/movimiento/${idMovimiento}`);
  }

  obtenerMovimientolineasPorLote(idLote: number): Observable<Movimientolinea[]> {
    return this.http.get<Movimientolinea[]>(`${this.restUrl}/movimientolineas/lote/${idLote}`);
  }

  obtenerMovimientolineasPorAlmacenOrigen(idAlmacenOrigen: number): Observable<Movimientolinea[]> {
    return this.http.get<Movimientolinea[]>(`${this.restUrl}/movimientolineas/almacen-origen/${idAlmacenOrigen}`);
  }

  obtenerMovimientolineasPorAlmacenDestino(idAlmacenDestino: number): Observable<Movimientolinea[]> {
    return this.http.get<Movimientolinea[]>(`${this.restUrl}/movimientolineas/almacen-destino/${idAlmacenDestino}`);
  }

  crearMovimientolinea(movimientolinea: Movimientolinea): Observable<Movimientolinea> {
    return this.http.post<Movimientolinea>(`${this.restUrl}/movimientolineas`, movimientolinea);
  }

  actualizarMovimientolinea(id: number, movimientolinea: Movimientolinea): Observable<Movimientolinea> {
    return this.http.put<Movimientolinea>(`${this.restUrl}/movimientolineas/${id}`, movimientolinea);
  }

  eliminarMovimientolinea(id: number): Observable<any> {
    return this.http.delete(`${this.restUrl}/movimientolineas/${id}`);
  }

  // ==================== CONSUMOS ====================
  obtenerConsumos(): Observable<Consumo[]> {
    return this.http.get<Consumo[]>(`${this.apiUrl}/consumos`);
  }

  obtenerConsumoPorId(id: number): Observable<Consumo> {
    return this.http.get<Consumo>(`${this.apiUrl}/consumos/${id}`);
  }

  obtenerConsumosPorUsuario(idUsuario: number): Observable<Consumo[]> {
    return this.http.get<Consumo[]>(`${this.apiUrl}/consumos/usuario/${idUsuario}`);
  }

  obtenerConsumosPorDepartamento(idDepartamento: number): Observable<Consumo[]> {
    return this.http.get<Consumo[]>(`${this.apiUrl}/consumos/departamento/${idDepartamento}`);
  }

  obtenerConsumosPorFecha(fecha: string): Observable<Consumo[]> {
    return this.http.get<Consumo[]>(`${this.apiUrl}/consumos/fecha/${fecha}`);
  }

  crearConsumo(consumo: Consumo): Observable<Consumo> {
    return this.http.post<Consumo>(`${this.apiUrl}/consumos`, consumo);
  }

  actualizarConsumo(id: number, consumo: Consumo): Observable<any> {
    return this.http.put(`${this.apiUrl}/consumos/${id}`, consumo);
  }

  eliminarConsumo(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/consumos/${id}`);
  }

  // ==================== COMPRAS ====================
  obtenerCompras(): Observable<Compra[]> {
    return this.http.get<Compra[]>(`${this.apiUrl}/compras`);
  }

  obtenerCompraPorId(id: number): Observable<Compra> {
    return this.http.get<Compra>(`${this.apiUrl}/compras/${id}`);
  }

  obtenerComprasPorUsuario(idUsuario: number): Observable<Compra[]> {
    return this.http.get<Compra[]>(`${this.apiUrl}/compras/usuario/${idUsuario}`);
  }

  obtenerComprasPorProveedor(idProveedor: number): Observable<Compra[]> {
    return this.http.get<Compra[]>(`${this.apiUrl}/compras/proveedor/${idProveedor}`);
  }

  obtenerComprasPorFecha(fecha: string): Observable<Compra[]> {
    return this.http.get<Compra[]>(`${this.apiUrl}/compras/fecha/${fecha}`);
  }

  crearCompra(compra: Compra): Observable<Compra> {
    return this.http.post<Compra>(`${this.apiUrl}/compras`, compra);
  }

  actualizarCompra(id: number, compra: Compra): Observable<any> {
    return this.http.put(`${this.apiUrl}/compras/${id}`, compra);
  }

  eliminarCompra(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/compras/${id}`);
  }

  // ==================== ALMACENES ====================
  obtenerAlmacenes(): Observable<Almacen[]> {
    return this.http.get<Almacen[]>(`${this.apiUrl}/almacenes`);
  }

  obtenerAlmacenPorId(id: number): Observable<Almacen> {
    return this.http.get<Almacen>(`${this.apiUrl}/almacenes/${id}`);
  }

  obtenerAlmacenPorNombre(nombre: string): Observable<Almacen> {
    return this.http.get<Almacen>(`${this.apiUrl}/almacenes/nombre/${nombre}`);
  }

  crearAlmacen(almacen: Almacen): Observable<any> {
    return this.http.post(`${this.apiUrl}/almacenes`, almacen);
  }

  actualizarAlmacen(id: number, almacen: Almacen): Observable<any> {
    return this.http.put(`${this.apiUrl}/almacenes/${id}`, almacen);
  }

  eliminarAlmacen(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/almacenes/${id}`);
  }

  // ==================== PROVEEDORES ====================
  obtenerProveedores(): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(`${this.restUrl}/proveedores`);
  }

  obtenerProveedorPorId(id: number): Observable<Proveedor> {
    return this.http.get<Proveedor>(`${this.restUrl}/proveedores/${id}`);
  }

  obtenerProveedorPorRuc(ruc: string): Observable<Proveedor> {
    return this.http.get<Proveedor>(`${this.restUrl}/proveedores/ruc/${ruc}`);
  }

  obtenerProveedorPorNombre(nombre: string): Observable<Proveedor> {
    return this.http.get<Proveedor>(`${this.restUrl}/proveedores/nombre/${nombre}`);
  }

  crearProveedor(proveedor: Proveedor): Observable<Proveedor> {
    return this.http.post<Proveedor>(`${this.restUrl}/proveedores`, proveedor);
  }

  actualizarProveedor(id: number, proveedor: Proveedor): Observable<Proveedor> {
    return this.http.put<Proveedor>(`${this.restUrl}/proveedores/${id}`, proveedor);
  }

  eliminarProveedor(id: number): Observable<any> {
    return this.http.delete(`${this.restUrl}/proveedores/${id}`);
  }

  // ==================== DEPARTAMENTOS ====================
  obtenerDepartamentos(): Observable<Departamento[]> {
    return this.http.get<Departamento[]>(`${this.apiUrl}/departamentos`);
  }

  obtenerDepartamentoPorId(id: number): Observable<Departamento> {
    return this.http.get<Departamento>(`${this.apiUrl}/departamentos/${id}`);
  }

  obtenerDepartamentoPorNombre(nombre: string): Observable<Departamento> {
    return this.http.get<Departamento>(`${this.apiUrl}/departamentos/nombre/${nombre}`);
  }

  crearDepartamento(departamento: Departamento): Observable<any> {
    return this.http.post(`${this.apiUrl}/departamentos`, departamento);
  }

  actualizarDepartamento(id: number, departamento: Departamento): Observable<any> {
    return this.http.put(`${this.apiUrl}/departamentos/${id}`, departamento);
  }

  eliminarDepartamento(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/departamentos/${id}`);
  }

  // ==================== MARCAS ====================
  obtenerMarcas(): Observable<Marca[]> {
    return this.http.get<Marca[]>(`${this.restUrl}/marcas`);
  }

  obtenerMarcaPorId(id: number): Observable<Marca> {
    return this.http.get<Marca>(`${this.restUrl}/marcas/${id}`);
  }

  obtenerMarcaPorNombre(nombre: string): Observable<Marca> {
    return this.http.get<Marca>(`${this.restUrl}/marcas/nombre/${nombre}`);
  }

  obtenerMarcasPorEstado(estado: boolean): Observable<Marca[]> {
    return this.http.get<Marca[]>(`${this.restUrl}/marcas/estado/${estado}`);
  }

  crearMarca(marca: Marca): Observable<Marca> {
    return this.http.post<Marca>(`${this.restUrl}/marcas`, marca);
  }

  actualizarMarca(id: number, marca: Marca): Observable<Marca> {
    return this.http.put<Marca>(`${this.restUrl}/marcas/${id}`, marca);
  }

  eliminarMarca(id: number): Observable<any> {
    return this.http.delete(`${this.restUrl}/marcas/${id}`);
  }

  // ==================== TIPO ACCIONES ====================
  obtenerTipoacciones(): Observable<Tipoaccion[]> {
    return this.http.get<Tipoaccion[]>(`${this.apiUrl}/tipoacciones`);
  }

  obtenerTipoaccionPorId(id: number): Observable<Tipoaccion> {
    return this.http.get<Tipoaccion>(`${this.apiUrl}/tipoacciones/${id}`);
  }

  obtenerTipoaccionPorNombre(nombre: string): Observable<Tipoaccion> {
    return this.http.get<Tipoaccion>(`${this.apiUrl}/tipoacciones/nombre/${nombre}`);
  }

  existeTipoaccion(id: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/tipoacciones/existe/${id}`);
  }

  // ==================== ROLES ====================
  obtenerRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${this.apiUrl}/roles`);
  }

  obtenerRolePorId(id: number): Observable<Role> {
    return this.http.get<Role>(`${this.apiUrl}/roles/${id}`);
  }

  obtenerRolePorNombre(nombre: string): Observable<Role> {
    return this.http.get<Role>(`${this.apiUrl}/roles/nombre/${nombre}`);
  }

  existeRole(id: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/roles/existe/${id}`);
  }

  // ==================== INVENTARIO ====================
  obtenerInventarioDetallado(idAlmacen: number, nombreReactivo?: string): Observable<any> {
    let params = new HttpParams();
    if (nombreReactivo) {
      params = params.set('nombreReactivo', nombreReactivo);
    }
    return this.http.get(`${this.restUrl}/inventario/almacen/${idAlmacen}/detallado`, { params });
  }

  // ==================== OPERACIONES MÚLTIPLES ====================
  registrarTrasladoMultiple(request: any): Observable<any> {
    return this.http.post(`${this.restUrl}/trasladomultiple`, request);
  }

  registrarIngresoMultiple(request: any): Observable<any> {
    return this.http.post(`${this.restUrl}/ingresomultiple`, request);
  }

  registrarConsumoMultiple(request: any): Observable<any> {
    return this.http.post(`${this.restUrl}/consumomultiple`, request);
  }

  // ==================== DASHBOARD STATS ====================
  obtenerEstadisticasDashboard(): Observable<DashboardStats> {
    const hoy = new Date().toISOString().split('T')[0];
    const treintaDias = new Date();
    treintaDias.setDate(treintaDias.getDate() + 30);
    const fechaLimite = treintaDias.toISOString().split('T')[0];

    return this.http.get<Reactivo[]>(`${this.apiUrl}/reactivos`).pipe(
      map(reactivos => {
        // Aquí podrías hacer llamadas adicionales si necesitas más datos
        return {
          reactivosTotales: reactivos.length,
          lotesProximosVencer: 0, // Se calculará con otra llamada
          movimientosHoy: 0, // Se calculará con otra llamada
          stockBajo: 0, // Se calculará según lógica de negocio
          variacionMensual: 3.5
        };
      })
    );
  }
}
