import { Component, inject, signal, computed, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Rest } from '../../Servicios/rest';
import { AuthService } from '../../Servicios/auth.service';
import { Usuario, Role, LineaTiempoUsuario } from '../../Modelos/interfaces';

@Component({
  selector: 'app-usuario-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './usuario-component.html',
  styleUrl: './usuario-component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsuarioComponent implements OnInit {
  private readonly restService = inject(Rest);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  // Signals para el estado del componente
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly exito = signal<string | null>(null);
  readonly usuarios = signal<Usuario[]>([]);
  readonly roles = signal<Role[]>([]);
  readonly usuariosFiltrados = signal<Usuario[]>([]);
  readonly mostrarModal = signal(false);
  readonly modoEdicion = signal(false);
  readonly usuarioSeleccionado = signal<Usuario | null>(null);
  readonly busqueda = signal('');

  // Signals para el historial
  readonly mostrarModalHistorial = signal(false);
  readonly usuarioHistorial = signal<Usuario | null>(null);
  readonly lineaTiempo = signal<LineaTiempoUsuario | null>(null);
  readonly cargandoHistorial = signal(false);
  readonly errorHistorial = signal<string | null>(null);
  readonly fechaDesde = signal<string>('');
  readonly fechaHasta = signal<string>('');
  readonly tipoAccionFiltro = signal<string>('Todos');

  // Computed para actividades filtradas
  readonly actividadesFiltradas = computed(() => {
    const timeline = this.lineaTiempo();
    const filtroTipo = this.tipoAccionFiltro();
    
    if (!timeline || !timeline.actividades) return [];
    
    if (filtroTipo === 'Todos') {
      return timeline.actividades;
    }
    
    return timeline.actividades.filter(a => a.tipoAccion === filtroTipo);
  });

  // Computed para obtener tipos únicos de acciones
  readonly tiposAccionDisponibles = computed(() => {
    const timeline = this.lineaTiempo();
    if (!timeline || !timeline.actividades) return ['Todos'];
    
    const tipos = new Set(timeline.actividades.map(a => a.tipoAccion));
    return ['Todos', ...Array.from(tipos).sort()];
  });

  // Usuario actual
  readonly usuarioActual = this.authService.usuario;

  // Formulario de usuario
  usuarioForm: FormGroup = this.fb.group({
    id: [null],
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    clave: ['', [Validators.required, Validators.minLength(4)]],
    dni: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
    fechaNacimiento: ['', [Validators.required]],
    idRol: [null, [Validators.required]]
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  private cargarDatos(): void {
    this.cargando.set(true);
    this.error.set(null);

    // Cargar usuarios y roles en paralelo
    this.restService.obtenerUsuarios().subscribe({
      next: (usuarios) => {
        this.usuarios.set(usuarios);
        this.usuariosFiltrados.set(usuarios);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar usuarios:', err);
        this.error.set('Error al cargar la lista de usuarios');
        this.cargando.set(false);
      }
    });

    this.restService.obtenerRoles().subscribe({
      next: (roles) => {
        this.roles.set(roles);
      },
      error: (err) => {
        console.error('Error al cargar roles:', err);
      }
    });
  }

  buscarUsuarios(): void {
    const termino = this.busqueda().toLowerCase().trim();
    
    if (!termino) {
      this.usuariosFiltrados.set(this.usuarios());
      return;
    }

    const filtrados = this.usuarios().filter(usuario => 
      usuario.nombre.toLowerCase().includes(termino) ||
      usuario.dni.includes(termino)
    );

    this.usuariosFiltrados.set(filtrados);
  }

  abrirModalNuevo(): void {
    this.modoEdicion.set(false);
    this.usuarioSeleccionado.set(null);
    this.usuarioForm.reset();
    this.usuarioForm.get('clave')?.setValidators([Validators.required, Validators.minLength(4)]);
    this.usuarioForm.get('clave')?.updateValueAndValidity();
    this.mostrarModal.set(true);
    this.error.set(null);
    this.exito.set(null);
  }

  abrirModalEditar(usuario: Usuario): void {
    this.modoEdicion.set(true);
    this.usuarioSeleccionado.set(usuario);
    
    this.usuarioForm.patchValue({
      id: usuario.id,
      nombre: usuario.nombre,
      clave: '', // No mostrar la clave actual
      dni: usuario.dni,
      fechaNacimiento: usuario.fechaNacimiento,
      idRol: usuario.idRol.id
    });

    // En modo edición, la clave es opcional
    this.usuarioForm.get('clave')?.clearValidators();
    this.usuarioForm.get('clave')?.updateValueAndValidity();
    
    this.mostrarModal.set(true);
    this.error.set(null);
    this.exito.set(null);
  }

  cerrarModal(): void {
    this.mostrarModal.set(false);
    this.usuarioForm.reset();
    this.error.set(null);
    this.exito.set(null);
  }

  guardarUsuario(): void {
    console.log('=== Iniciando guardarUsuario ===');
    console.log('Formulario válido:', this.usuarioForm.valid);
    console.log('Valores del formulario:', this.usuarioForm.value);
    
    if (this.usuarioForm.invalid) {
      this.marcarCamposComoTocados();
      this.error.set('Por favor, completa todos los campos correctamente');
      console.log('Errores del formulario:', this.usuarioForm.errors);
      Object.keys(this.usuarioForm.controls).forEach(key => {
        const control = this.usuarioForm.get(key);
        if (control?.invalid) {
          console.log(`Campo ${key} inválido:`, control.errors);
        }
      });
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    const formValue = this.usuarioForm.value;
    const rolId = parseInt(formValue.idRol);
    const rolSeleccionado = this.roles().find(r => r.id === rolId);

    console.log('Rol seleccionado ID:', rolId);
    console.log('Roles disponibles:', this.roles());
    console.log('Rol encontrado:', rolSeleccionado);

    if (!rolSeleccionado) {
      this.error.set('Rol no válido');
      this.cargando.set(false);
      return;
    }

    let usuarioData: any;

    if (this.modoEdicion()) {
      // Para actualizar, incluir el ID
      usuarioData = {
        id: formValue.id,
        nombre: formValue.nombre,
        clave: formValue.clave || undefined,
        dni: formValue.dni,
        fechaNacimiento: formValue.fechaNacimiento,
        idRol: rolSeleccionado
      };
      
      console.log('Datos del usuario a actualizar:', usuarioData);
      console.log('Actualizando usuario...');
      this.restService.actualizarUsuario(usuarioData.id, usuarioData).subscribe({
        next: (response) => {
          console.log('Usuario actualizado exitosamente:', response);
          this.exito.set('Usuario actualizado correctamente');
          this.cargando.set(false);
          this.cargarDatos();
          setTimeout(() => this.cerrarModal(), 1500);
        },
        error: (err) => {
          console.error('Error al actualizar usuario:', err);
          this.error.set(err.error?.message || 'Error al actualizar el usuario');
          this.cargando.set(false);
        }
      });
    } else {
      // Para crear nuevo, NO incluir el ID
      usuarioData = {
        nombre: formValue.nombre,
        clave: formValue.clave,
        dni: formValue.dni,
        fechaNacimiento: formValue.fechaNacimiento,
        idRol: rolSeleccionado
      };
      
      console.log('Datos del usuario a crear (sin ID):', usuarioData);
      console.log('Creando nuevo usuario...');
      this.restService.crearUsuario(usuarioData).subscribe({
        next: (response) => {
          console.log('Usuario creado exitosamente:', response);
          this.exito.set('Usuario creado correctamente');
          this.cargando.set(false);
          this.cargarDatos();
          setTimeout(() => this.cerrarModal(), 1500);
        },
        error: (err) => {
          console.error('Error al crear usuario:', err);
          console.error('Detalles del error:', err.error);
          this.error.set(err.error?.message || 'Error al crear el usuario');
          this.cargando.set(false);
        }
      });
    }
  }

  eliminarUsuario(usuario: Usuario): void {
    // Verificar que no se elimine a sí mismo
    if (this.usuarioActual()?.id === usuario.id) {
      this.error.set('No puedes eliminar tu propio usuario');
      setTimeout(() => this.error.set(null), 3000);
      return;
    }

    if (!confirm(`¿Estás seguro de eliminar al usuario "${usuario.nombre}"?`)) {
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    this.restService.eliminarUsuario(usuario.id).subscribe({
      next: () => {
        this.exito.set('Usuario eliminado correctamente');
        this.cargarDatos();
        setTimeout(() => this.exito.set(null), 3000);
      },
      error: (err) => {
        console.error('Error al eliminar usuario:', err);
        this.error.set('Error al eliminar el usuario');
        this.cargando.set(false);
        setTimeout(() => this.error.set(null), 3000);
      }
    });
  }

  private marcarCamposComoTocados(): void {
    Object.keys(this.usuarioForm.controls).forEach(key => {
      const control = this.usuarioForm.get(key);
      control?.markAsTouched();
    });
  }

  // Getters para controles del formulario
  get nombreControl() { return this.usuarioForm.get('nombre'); }
  get claveControl() { return this.usuarioForm.get('clave'); }
  get dniControl() { return this.usuarioForm.get('dni'); }
  get fechaNacimientoControl() { return this.usuarioForm.get('fechaNacimiento'); }
  get rolControl() { return this.usuarioForm.get('idRol'); }

  // Métodos auxiliares
  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    const dia = String(date.getDate()).padStart(2, '0');
    const mes = String(date.getMonth() + 1).padStart(2, '0');
    const anio = date.getFullYear();
    return `${dia}/${mes}/${anio}`;
  }

  getRolBadgeClass(rol: string): string {
    const rolLower = rol.toLowerCase();
    if (rolLower.includes('admin')) {
      return 'bg-purple-100 text-purple-800';
    } else if (rolLower.includes('supervisor')) {
      return 'bg-blue-100 text-blue-800';
    } else {
      return 'bg-gray-100 text-gray-800';
    }
  }

  // Métodos para el historial
  verHistorial(usuario: Usuario): void {
    this.usuarioHistorial.set(usuario);
    this.mostrarModalHistorial.set(true);
    
    // Calcular fechas por defecto (últimos 30 días)
    const hoy = new Date();
    const hace30Dias = new Date();
    hace30Dias.setDate(hoy.getDate() - 30);
    
    this.fechaHasta.set(this.formatearFechaISO(hoy));
    this.fechaDesde.set(this.formatearFechaISO(hace30Dias));
    
    this.cargarHistorial();
  }

  cargarHistorial(): void {
    const usuario = this.usuarioHistorial();
    if (!usuario) return;

    const desde = this.fechaDesde();
    const hasta = this.fechaHasta();

    // Validar que las fechas estén completas
    if (!desde || !hasta) {
      this.errorHistorial.set('Por favor, selecciona ambas fechas');
      return;
    }

    this.cargandoHistorial.set(true);
    this.errorHistorial.set(null);
    this.tipoAccionFiltro.set('Todos'); // Reset filtro de tipo

    console.log('Cargando historial con fechas:', { desde, hasta });

    this.restService.obtenerLineaTiempoUsuario(
      usuario.id,
      desde,
      hasta
    ).subscribe({
      next: (data) => {
        console.log('Datos recibidos:', data);
        this.lineaTiempo.set(data);
        this.cargandoHistorial.set(false);
      },
      error: (err) => {
        console.error('Error al cargar historial:', err);
        this.errorHistorial.set('Error al cargar el historial del usuario');
        this.cargandoHistorial.set(false);
      }
    });
  }

  cerrarModalHistorial(): void {
    this.mostrarModalHistorial.set(false);
    this.usuarioHistorial.set(null);
    this.lineaTiempo.set(null);
    this.errorHistorial.set(null);
    this.tipoAccionFiltro.set('Todos');
  }

  formatearFechaISO(fecha: Date): string {
    return fecha.toISOString().split('T')[0];
  }

  formatearFechaHora(fecha: string): string {
    const date = new Date(fecha);
    const dia = String(date.getDate()).padStart(2, '0');
    const mes = String(date.getMonth() + 1).padStart(2, '0');
    const anio = date.getFullYear();
    const hora = String(date.getHours()).padStart(2, '0');
    const minuto = String(date.getMinutes()).padStart(2, '0');
    return `${dia}/${mes}/${anio} ${hora}:${minuto}`;
  }

  getTipoActividadClass(tipo: string): string {
    const tipoLower = tipo.toLowerCase();
    if (tipoLower.includes('creación') || tipoLower.includes('entrada')) {
      return 'bg-green-100 text-green-800';
    } else if (tipoLower.includes('actualización') || tipoLower.includes('modificación')) {
      return 'bg-blue-100 text-blue-800';
    } else if (tipoLower.includes('eliminación') || tipoLower.includes('salida')) {
      return 'bg-red-100 text-red-800';
    } else if (tipoLower.includes('consulta')) {
      return 'bg-gray-100 text-gray-800';
    } else {
      return 'bg-yellow-100 text-yellow-800';
    }
  }
}
