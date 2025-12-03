// Interfaces para las entidades del backend

export interface Role {
  id: number;
  nombre: string;
}

export interface Usuario {
  id: number;
  nombre: string;
  clave?: string;
  dni: string;
  fechaNacimiento: string;
  idRol: Role;
}

export interface Marca {
  id: number;
  nombre: string;
  estado: boolean;
}

export interface Reactivo {
  id: number;
  nombre: string;
  idMarca: Marca;
}

export interface Departamento {
  id: number;
  nombre: string;
  responsable: string;
  ubicacion: string;
}

export interface Proveedor {
  id: number;
  nombre: string;
  ruc: string;
  telefono: string;
}

export interface Almacen {
  id: number;
  nombre: string;
  direccion: string;
  telefono: string;
}

// Interfaz para la información del almacén en la respuesta de inventario
export interface AlmacenInfo {
  idAlmacen: number;
  nombreAlmacen: string;
  direccion: string;
  telefono: string;
}

export interface Tipoaccion {
  id: number;
  nombre: string;
}

export interface Movimiento {
  id: number;
  fecha: string;
  idUsuario: Usuario;
  idTipoAccion: Tipoaccion;
  referencia: string;
  comentario: string;
  createdAt: string;
}

export interface Compra {
  id: number;
  idUsuario: Usuario;
  idMovimiento: Movimiento;
  idProveedor: Proveedor;
  fecha: string;
}

export interface Lote {
  id: number;
  idReactivo: Reactivo;
  idCompra: Compra;
  cantidadInicial: number;
  precioUnitario: number;
  fechaExpiracion: string;
  estado: boolean;
}

export interface Consumo {
  id: number;
  idUsuario: Usuario;
  idMovimiento: Movimiento;
  idDepartamento: Departamento;
  fecha: string;
}

export interface Movimientolinea {
  id: number;
  idMovimiento: Movimiento;
  idAlmacenOrigen: Almacen;
  idAlmacenDestino: Almacen;
  idLote: Lote;
  cantidadDelta: number;
  precioVenta: number;
}

export interface LoginRequest {
  nombre: string;
  clave: string;
}

export interface LoginResponse {
  usuario: UsuarioLogin;
  mensaje: string;
}

export interface UsuarioLogin {
  id: number;
  nombre: string;
  clave?: string; // Opcional por seguridad
  dni: string;
  fechaNacimiento: string;
  idRol: RolSimple;
}

export interface RolSimple {
  id: number;
  nombre: string;
}
// Interfaces para estadísticas del dashboard
export interface DashboardStats {
  reactivosTotales: number;
  lotesProximosVencer: number;
  movimientosHoy: number;
  stockBajo: number;
  variacionMensual: number;
}

export interface ReactivoStock {
  reactivo: Reactivo;
  cantidadTotal: number;
  cantidadDisponible: number;
  ubicacion: string;
}

export interface MovimientoReciente {
  movimiento: Movimiento;
  tipo: string;
  cantidad: number;
  reactivo: string;
}

// ==========================================
// DTOs PARA OPERACIONES MÚLTIPLES
// ==========================================

export interface TrasladoItemDTO {
  id_lote: number;
  cantidad: number;
}

export interface TrasladoMultipleRequestDTO {
  idUsuario: number;
  idAlmacenOrigen: number;
  idAlmacenDestino: number;
  referencia: string;
  comentario: string;
  traslados: TrasladoItemDTO[];
}

export interface LoteIngresoDTO {
  id_reactivo: number;
  cantidad: number;
  precio_unitario: number;
  fecha_expiracion: string;
}

export interface IngresoMultipleRequestDTO {
  idUsuario: number;
  idProveedor: number;
  idAlmacenDestino: number;
  referencia: string;
  comentario: string;
  lotes: LoteIngresoDTO[];
}

export interface ConsumoItemDTO {
  id_lote: number;
  cantidad: number;
}

export interface ConsumoMultipleRequestDTO {
  idUsuario: number;
  idDepartamento: number;
  idAlmacenOrigen: number;
  referencia: string;
  comentario: string;
  consumos: ConsumoItemDTO[];
}

export interface AjusteItemDTO {
  id_lote: number;
  cantidad_delta: number;
}

export interface AjusteMultipleRequestDTO {
  idUsuario: number;
  idAlmacenOrigen: number;
  referencia: string;
  comentario: string;
  ajustes: AjusteItemDTO[];
}

// ==========================================
// INTERFACES PARA DASHBOARD E INVENTARIO
// ==========================================

export interface StatsCard {
  titulo: string;
  valor: number;
  color: 'blue' | 'green' | 'yellow' | 'red';
  icono: string;
}

export interface MovimientoResumen {
  id: number;
  fecha: string;
  tipo: string;
  reactivo: string;
  cantidad: string;
  usuario: string;
  colorTipo: 'green' | 'red' | 'blue';
}

export interface Alerta {
  tipo: 'warning' | 'danger' | 'info';
  titulo: string;
  mensaje: string;
  conteo?: number;
}

// Interfaz para el item de inventario que retorna el backend
export interface InventarioItem {
  idInventario: number;
  idReactivo: number;
  nombreReactivo: string;
  marca: string;
  numeroLote: number;
  cantidadInicialLote: number;
  stockActual: number;
  precioUnitario: number;
  fechaExpiracion: string;
  diasParaExpiracion: number;
  estadoExpiracion: string;
  estadoStock: string;
}

// Interfaz para el agrupamiento de reactivos con sus lotes
export interface InventarioDetallado {
  idReactivo: number;
  nombreReactivo: string;
  nombreMarca: string;
  cantidadTotal: number;
  lotes: LoteInventario[];
}

export interface LoteInventario {
  idLote: number;
  cantidadDisponible: number;
  fechaExpiracion: string;
  precioUnitario: number;
  estado: boolean;
}

// ==========================================
// INTERFACES PARA HISTORIAL DE USUARIO
// ==========================================

export interface ActividadUsuario {
  idMovimiento: number;
  fechaHora: string;
  fecha: string;
  hora: string;
  tipoAccion: string;
  referencia: string;
  comentario: string;
  detalleOperacion: string;
  totalItems: number;
  totalUnidades: number;
  almacenesOrigen: string;
  almacenesDestino: string;
  reactivosInvolucrados: string;
  cantidadReactivosDiferentes: number;
  valorTotal: number;
  diasTranscurridos: number;
}

export interface ResumenEstadistico {
  totalMovimientos: number;
  totalIngresos: number;
  totalConsumos: number;
  totalTraslados: number;
  totalAjustes: number;
  totalLotesMovidos: number;
  totalReactivosDiferentes: number;
  totalAlmacenesUsados: number;
  totalUnidadesMovidas: number;
  promedioUnidadesPorMovimiento: number;
  valorTotalMovimientos: number;
  primeraActividad: string;
  ultimaActividad: string;
  diasActivo: number;
  promedioMovimientosPorDia: number;
}

export interface DistribucionTipo {
  idTipo: number;
  tipoAccion: string;
  cantidadMovimientos: number;
  totalUnidades: number;
  valorTotal: number;
  porcentajeMovimientos: number;
  porcentajeUnidades: number;
}

export interface TopReactivo {
  idReactivo: number;
  nombreReactivo: string;
  marca: string;
  vecesMovido: number;
  totalUnidades: number;
  promedioUnidadesPorMovimiento: number;
  valorTotal: number;
  primeraVez: string;
  ultimaVez: string;
  diasEntrePrimeraYUltima: number;
}

export interface LineaTiempoUsuario {
  usuarioInfo: {
    idUsuario: number;
    nombreUsuario: string;
    dni: string;
    rol: string;
    fechaNacimiento: string;
  };
  actividades: ActividadUsuario[];
  resumenEstadistico: ResumenEstadistico;
  distribucionPorTipo: DistribucionTipo[];
  topReactivos: TopReactivo[];
}

// ==========================================
// ALIAS PARA COMPATIBILIDAD
// ==========================================

export type LoginCredentials = LoginRequest;

// ==========================================
// HISTORIAL DE MOVIMIENTOS
// ==========================================

export interface DetalleMovimiento {
  idMovimiento: number;
  fecha: string;
  tipoAccion: string;
  usuario: string;
  referencia: string;
  comentario: string;
  nombreReactivo: string;
  marca: string;
  numeroLote: number;
  almacenOrigen: string | null;
  almacenDestino: string | null;
  cantidad: number;
  precioVenta: number | null;
  valorTotal: number;
}

export interface ResumenPorTipo {
  tipoAccion: string;
  totalMovimientos: number;
  totalUnidades: number;
  valorTotal: number;
}

export interface HistorialMovimientosResponse {
  detalleMovimientos: DetalleMovimiento[];
  resumenPorTipo: ResumenPorTipo[];
}
