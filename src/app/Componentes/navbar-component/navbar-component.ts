import { Component, Output, EventEmitter, signal, inject, computed, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
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
  private readonly router = inject(Router);

  @Output() toggleSidebarEvent = new EventEmitter<void>();
  
  // Signals
  showNotifications = signal<boolean>(false);
  showUserMenu = signal<boolean>(false);
  
  // Computed signal para obtener el usuario del AuthService
  usuario = computed(() => this.authService.usuario());

  toggleSidebar(): void {
    this.toggleSidebarEvent.emit();
  }

  toggleNotifications(): void {
    this.showNotifications.update(value => !value);
    this.showUserMenu.set(false); // Cerrar el menú de usuario
    
    // Cerrar el modal cuando se hace clic en otra parte
    if (this.showNotifications()) {
      setTimeout(() => {
        document.addEventListener('click', this.closeNotificationsOnClickOutside);
      });
    }
  }

  toggleUserMenu(): void {
    this.showUserMenu.update(value => !value);
    this.showNotifications.set(false); // Cerrar notificaciones
    
    // Cerrar el modal cuando se hace clic en otra parte
    if (this.showUserMenu()) {
      setTimeout(() => {
        document.addEventListener('click', this.closeUserMenuOnClickOutside);
      });
    }
  }

  closeNotificationsOnClickOutside = (event: MouseEvent): void => {
    const target = event.target as HTMLElement;
    if (!target.closest('.notifications-container')) {
      this.showNotifications.set(false);
      document.removeEventListener('click', this.closeNotificationsOnClickOutside);
    }
  }

  closeUserMenuOnClickOutside = (event: MouseEvent): void => {
    const target = event.target as HTMLElement;
    if (!target.closest('.relative')) {
      this.showUserMenu.set(false);
      document.removeEventListener('click', this.closeUserMenuOnClickOutside);
    }
  }

  iniciales(): string {
    const user = this.usuario();
    if (!user || !user.nombre) return 'U';
    
    const nombres = user.nombre.trim().split(' ');
    if (nombres.length >= 2) {
      return (nombres[0][0] + nombres[1][0]).toUpperCase();
    }
    return nombres[0][0].toUpperCase();
  }

  nombreUsuario(): string {
    const user = this.usuario();
    return user?.nombre || 'Usuario';
  }

  cerrarSesion(): void {
    this.authService.cerrarSesion();
    this.router.navigate(['/']);
  }
}
