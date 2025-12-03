import { Component, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Rest } from '../../Servicios/rest';
import { AuthService } from '../../Servicios/auth.service';

@Component({
  selector: 'app-login-component',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-component.html',
  styleUrl: './login-component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent {
  private readonly restService = inject(Rest);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  // Signals para el estado del componente
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly mostrarPassword = signal(false);

  // Formulario reactivo
  readonly loginForm: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    clave: ['', [Validators.required, Validators.minLength(4)]]
  });

  onSubmit(): void {
    // Validar formulario
    if (this.loginForm.invalid) {
      this.markFormGroupTouched(this.loginForm);
      this.error.set('Por favor, completa todos los campos correctamente');
      return;
    }

    // Limpiar error previo
    this.error.set(null);
    this.cargando.set(true);

    const credentials = this.loginForm.value;

    this.restService.login(credentials).subscribe({
      next: (response) => {
        console.log('Login exitoso:', response);

        // Verificar que se recibió el usuario y el mensaje
        if (response.usuario && response.mensaje) {
          // Guardar sesión usando el AuthService (modo demo sin token)
          this.authService.guardarSesion(response.usuario);
          
          this.cargando.set(false);

          const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || '/inicio';
          this.router.navigateByUrl(returnUrl);
        } else {
          // Si no se recibió usuario, mostrar error
          this.cargando.set(false);
          this.error.set('Respuesta del servidor incompleta. Intenta nuevamente.');
        }
      },
      error: (err) => {
        console.error('Error en login:', err);
        this.cargando.set(false);

        // Manejar diferentes tipos de errores
        if (err.status === 0) {
          this.error.set('No se pudo conectar con el servidor. Verifica tu conexión.');
        } else if (err.status === 401) {
          this.error.set('Usuario o contraseña incorrectos');
        } else if (err.status === 404) {
          this.error.set('Servicio de autenticación no disponible');
        } else if (err.status === 500) {
          this.error.set('Error interno del servidor. Intenta más tarde.');
        } else {
          this.error.set(err.error?.message || 'Error al iniciar sesión. Intenta nuevamente.');
        }
      }
    });
  }

  toggleMostrarPassword(): void {
    this.mostrarPassword.update(value => !value);
  }

  // Método helper para marcar todos los campos como touched
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  // Getters para validación en template
  get nombreControl() {
    return this.loginForm.get('nombre');
  }

  get claveControl() {
    return this.loginForm.get('clave');
  }
}
