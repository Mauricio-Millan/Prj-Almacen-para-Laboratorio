import { Component, Input, Output, EventEmitter, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../Servicios/auth.service';

@Component({
  selector: 'app-side-bar-component',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './side-bar-component.html',
  styleUrls: ['./side-bar-component.css']
})
export class SideBarComponent {
  @Input() collapsed = false;
  @Output() toggleSidebarEvent = new EventEmitter<void>();

  private readonly authService = inject(AuthService);
  readonly usuarioActual = this.authService.usuario;
  readonly esUsuarioBasico = computed(() => (this.usuarioActual()?.idRol?.nombre ?? '').toUpperCase() === 'USUARIO');

  toggleSidebar() {
    this.toggleSidebarEvent.emit();
  }
}
