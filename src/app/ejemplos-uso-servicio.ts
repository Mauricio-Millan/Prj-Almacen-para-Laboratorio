// Ejemplos de uso del servicio REST

import { Component, inject, signal } from '@angular/core';
import { Rest } from './Servicios/rest';

/**
 * EJEMPLO 1: Obtener todos los reactivos
 */
export class EjemploReactivos {
  private readonly restService = inject(Rest);
  readonly reactivos = signal<any[]>([]);

  cargarReactivos(): void {
    this.restService.obtenerReactivos().subscribe({
      next: (data) => {
        console.log('Reactivos:', data);
        this.reactivos.set(data);
      },
      error: (err) => console.error('Error:', err)
    });
  }
}

/**
 * EJEMPLO 2: Crear un nuevo reactivo
 */
export class EjemploCrearReactivo {
  private readonly restService = inject(Rest);

  crearReactivo(): void {
    const nuevoReactivo = {
      id: 0,
      nombre: 'Ácido Clorhídrico',
      idMarca: {
        id: 1,
        nombre: 'Merck',
        estado: true
      }
    };

    this.restService.crearReactivo(nuevoReactivo).subscribe({
      next: (response) => console.log('Reactivo creado:', response),
      error: (err) => console.error('Error al crear:', err)
    });
  }
}

/**
 * EJEMPLO 3: Obtener lotes próximos a vencer
 */
export class EjemploLotesVencimiento {
  private readonly restService = inject(Rest);
  readonly lotesProximosVencer = signal<any[]>([]);

  cargarLotesProximosVencer(): void {
    const fechaLimite = new Date();
    fechaLimite.setDate(fechaLimite.getDate() + 30); // Próximos 30 días
    
    this.restService.obtenerLotesProximosAVencer(
      fechaLimite.toISOString()
    ).subscribe({
      next: (data) => {
        console.log('Lotes próximos a vencer:', data);
        this.lotesProximosVencer.set(data);
      },
      error: (err) => console.error('Error:', err)
    });
  }
}

/**
 * EJEMPLO 4: Obtener movimientos por rango de fechas
 */
export class EjemploMovimientos {
  private readonly restService = inject(Rest);
  readonly movimientos = signal<any[]>([]);

  cargarMovimientosSemana(): void {
    const hoy = new Date();
    const haceUnaSemana = new Date();
    haceUnaSemana.setDate(hoy.getDate() - 7);

    this.restService.obtenerMovimientosPorRangoFechas(
      haceUnaSemana.toISOString().split('T')[0],
      hoy.toISOString().split('T')[0]
    ).subscribe({
      next: (data) => {
        console.log('Movimientos de la semana:', data);
        this.movimientos.set(data);
      },
      error: (err) => console.error('Error:', err)
    });
  }
}

/**
 * EJEMPLO 5: Login de usuario
 */
export class EjemploLogin {
  private readonly restService = inject(Rest);

  iniciarSesion(usuario: string, clave: string): void {
    this.restService.login({ nombre: usuario, clave }).subscribe({
      next: (response) => {
        console.log('Login exitoso:', response);
        // Aquí guardarías el token en localStorage o sessionStorage
        if (response.mensaje) {
          localStorage.setItem('auth_token', response.mensaje);
        }
      },
      error: (err) => {
        console.error('Error en login:', err);
        alert('Usuario o contraseña incorrectos');
      }
    });
  }
}

/**
 * EJEMPLO 6: Crear un nuevo lote
 * NOTA: Este es un ejemplo simplificado. En la práctica, deberías obtener
 * los objetos completos del backend antes de crear relaciones.
 */
export class EjemploCrearLote {
  private readonly restService = inject(Rest);

  async crearLote(): Promise<void> {
    // En un caso real, primero obtendrías estos datos del backend
    try {
      const reactivo = await this.restService.obtenerReactivoPorId(1).toPromise();
      const compra = await this.restService.obtenerCompraPorId(1).toPromise();
      
      if (!reactivo || !compra) {
        console.error('No se pudieron obtener los datos necesarios');
        return;
      }

      const nuevoLote = {
        id: 0,
        idReactivo: reactivo,
        idCompra: compra,
        cantidadInicial: 10,
        precioUnitario: 50.00,
        fechaExpiracion: '2025-12-31T00:00:00',
        estado: true
      };

      this.restService.crearLote(nuevoLote).subscribe({
        next: (response) => console.log('Lote creado:', response),
        error: (err) => console.error('Error al crear lote:', err)
      });
    } catch (error) {
      console.error('Error en la creación del lote:', error);
    }
  }
}

/**
 * EJEMPLO 7: Obtener consumos por departamento
 */
export class EjemploConsumos {
  private readonly restService = inject(Rest);
  readonly consumos = signal<any[]>([]);

  cargarConsumosDepartamento(idDepartamento: number): void {
    this.restService.obtenerConsumosPorDepartamento(idDepartamento).subscribe({
      next: (data) => {
        console.log('Consumos del departamento:', data);
        this.consumos.set(data);
      },
      error: (err) => console.error('Error:', err)
    });
  }
}

/**
 * EJEMPLO 8: Desactivar un lote
 */
export class EjemploDesactivarLote {
  private readonly restService = inject(Rest);

  desactivarLote(idLote: number): void {
    this.restService.desactivarLote(idLote).subscribe({
      next: (response) => {
        console.log('Lote desactivado:', response);
        alert('Lote desactivado correctamente');
      },
      error: (err) => {
        console.error('Error al desactivar:', err);
        alert('Error al desactivar el lote');
      }
    });
  }
}

/**
 * EJEMPLO 9: Obtener compras por proveedor
 */
export class EjemploCompras {
  private readonly restService = inject(Rest);
  readonly compras = signal<any[]>([]);

  cargarComprasProveedor(idProveedor: number): void {
    this.restService.obtenerComprasPorProveedor(idProveedor).subscribe({
      next: (data) => {
        console.log('Compras del proveedor:', data);
        this.compras.set(data);
      },
      error: (err) => console.error('Error:', err)
    });
  }
}

/**
 * EJEMPLO 10: Búsqueda de reactivo por nombre
 */
export class EjemploBusqueda {
  private readonly restService = inject(Rest);
  readonly resultado = signal<any | null>(null);

  buscarReactivoPorNombre(nombre: string): void {
    this.restService.obtenerReactivoPorNombre(nombre).subscribe({
      next: (data) => {
        console.log('Reactivo encontrado:', data);
        this.resultado.set(data);
      },
      error: (err) => {
        console.error('Reactivo no encontrado:', err);
        this.resultado.set(null);
      }
    });
  }
}

/**
 * EJEMPLO 11: Validación de CVEs en dependencias
 */
export class EjemploValidaciones {
  private readonly restService = inject(Rest);

  validarExistenciaReactivo(id: number): void {
    this.restService.existeReactivo(id).subscribe({
      next: (existe) => {
        if (existe) {
          console.log('El reactivo existe');
        } else {
          console.log('El reactivo no existe');
        }
      },
      error: (err) => console.error('Error en validación:', err)
    });
  }

  validarExistenciaLote(id: number): void {
    this.restService.existeLote(id).subscribe({
      next: (existe) => {
        if (existe) {
          console.log('El lote existe');
        } else {
          console.log('El lote no existe');
        }
      },
      error: (err) => console.error('Error en validación:', err)
    });
  }
}

/**
 * EJEMPLO 12: Manejo de errores robusto
 */
export class EjemploManejoErrores {
  private readonly restService = inject(Rest);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly datos = signal<any[]>([]);

  cargarDatosConManejoErrores(): void {
    this.cargando.set(true);
    this.error.set(null);

    this.restService.obtenerReactivos().subscribe({
      next: (data) => {
        this.datos.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error detallado:', err);
        
        // Manejo específico de errores
        if (err.status === 0) {
          this.error.set('No se pudo conectar con el servidor');
        } else if (err.status === 404) {
          this.error.set('Recurso no encontrado');
        } else if (err.status === 500) {
          this.error.set('Error interno del servidor');
        } else {
          this.error.set('Error al cargar los datos');
        }
        
        this.cargando.set(false);
      },
      complete: () => {
        console.log('Petición completada');
      }
    });
  }
}

/**
 * EJEMPLO 13: Actualización de reactivo
 */
export class EjemploActualizar {
  private readonly restService = inject(Rest);

  actualizarReactivo(id: number): void {
    const reactivoActualizado = {
      id: id,
      nombre: 'Ácido Sulfúrico Actualizado',
      idMarca: {
        id: 2,
        nombre: 'Sigma-Aldrich',
        estado: true
      }
    };

    this.restService.actualizarReactivo(id, reactivoActualizado).subscribe({
      next: (response) => {
        console.log('Reactivo actualizado:', response);
        alert('Reactivo actualizado correctamente');
      },
      error: (err) => {
        console.error('Error al actualizar:', err);
        alert('Error al actualizar el reactivo');
      }
    });
  }
}

/**
 * EJEMPLO 14: Obtener marcas activas
 */
export class EjemploMarcas {
  private readonly restService = inject(Rest);
  readonly marcasActivas = signal<any[]>([]);

  cargarMarcasActivas(): void {
    this.restService.obtenerMarcasPorEstado(true).subscribe({
      next: (data) => {
        console.log('Marcas activas:', data);
        this.marcasActivas.set(data);
      },
      error: (err) => console.error('Error:', err)
    });
  }
}

/**
 * EJEMPLO 15: Obtener movimientos por usuario
 */
export class EjemploMovimientosUsuario {
  private readonly restService = inject(Rest);
  readonly movimientosUsuario = signal<any[]>([]);

  cargarMovimientosUsuario(idUsuario: number): void {
    this.restService.obtenerMovimientosPorUsuario(idUsuario).subscribe({
      next: (data) => {
        console.log('Movimientos del usuario:', data);
        this.movimientosUsuario.set(data);
      },
      error: (err) => console.error('Error:', err)
    });
  }
}
