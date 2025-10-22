import { Component, Output, EventEmitter, inject, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../Servicios/auth.service';

@Component({
  selector: 'app-navbar-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar-component.html',
  styleUrls: ['./navbar-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {
  private readonly authService = inject(AuthService);

  @Output() toggleSidebarEvent = new EventEmitter<void>();
  
  // Signals para el estado del componente
  readonly showNotifications = signal(false);
  readonly showUserMenu = signal(false);

  // Computed signals para información del usuario
  readonly usuario = computed(() => this.authService.usuario());
  readonly nombreUsuario = computed(() => this.authService.obtenerNombreUsuario());
  readonly iniciales = computed(() => {
    const nombre = this.nombreUsuario();
    const palabras = nombre.split(' ');
    if (palabras.length >= 2) {
      return (palabras[0][0] + palabras[1][0]).toUpperCase();
    }
    return nombre.substring(0, 2).toUpperCase();
  });

  toggleSidebar(): void {
    this.toggleSidebarEvent.emit();
  }

  toggleNotifications(): void {
    this.showNotifications.update(value => !value);
    if (this.showUserMenu()) {
      this.showUserMenu.set(false);
    }
  }

  toggleUserMenu(): void {
    this.showUserMenu.update(value => !value);
    if (this.showNotifications()) {
      this.showNotifications.set(false);
    }
  }

  cerrarSesion(): void {
    if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
      this.authService.cerrarSesion();
    }
  }
}
