import { Injectable, inject, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { UsuarioLogin } from '../Modelos/interfaces';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly router = inject(Router);

  // Signals para el estado de autenticación (modo demo sin token)
  private readonly _usuario = signal<UsuarioLogin | null>(this.obtenerUsuarioGuardado());

  // Computed signals públicos
  readonly usuario = computed(() => this._usuario());
  readonly estaAutenticado = computed(() => !!this._usuario());

  constructor() {
    // Sincronizar el estado con localStorage al iniciar
    this.sincronizarEstado();
  }

  /**
   * Guarda los datos del usuario después de un login exitoso (modo demo)
   */
  guardarSesion(usuario: UsuarioLogin): void {
    localStorage.setItem('usuario', JSON.stringify(usuario));
    this._usuario.set(usuario);
  }

  /**
   * Cierra la sesión y limpia los datos almacenados
   */
  cerrarSesion(): void {
    localStorage.removeItem('usuario');
    this._usuario.set(null);
    this.router.navigate(['/login']);
  }

  /**
   * Obtiene el usuario almacenado en localStorage
   */
  private obtenerUsuarioGuardado(): UsuarioLogin | null {
    if (typeof window !== 'undefined') {
      const usuarioStr = localStorage.getItem('usuario');
      if (usuarioStr) {
        try {
          return JSON.parse(usuarioStr) as UsuarioLogin;
        } catch {
          return null;
        }
      }
    }
    return null;
  }

  /**
   * Sincroniza el estado interno con localStorage
   */
  private sincronizarEstado(): void {
    const usuario = this.obtenerUsuarioGuardado();
    this._usuario.set(usuario);
  }

  /**
   * Verifica si el usuario tiene un rol específico
   */
  tieneRol(nombreRol: string): boolean {
    const usuario = this._usuario();
    return usuario?.idRol?.nombre === nombreRol;
  }

  /**
   * Obtiene el nombre del usuario actual
   */
  obtenerNombreUsuario(): string {
    return this._usuario()?.nombre || 'Usuario';
  }

  /**
   * Indica si existe una sesión activa en memoria o en localStorage
   */
  tieneSesionActiva(): boolean {
    return !!this._usuario();
  }
}
