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
}

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
// ALIAS PARA COMPATIBILIDAD
// ==========================================

export type LoginCredentials = LoginRequest;
