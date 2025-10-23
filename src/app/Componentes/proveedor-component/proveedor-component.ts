import { Component, signal, computed, inject, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Rest } from '../../Servicios/rest';
import { Proveedor } from '../../Modelos/interfaces';

@Component({
  selector: 'app-proveedor-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './proveedor-component.html',
  styleUrl: './proveedor-component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProveedorComponent implements OnInit {
  private readonly restService = inject(Rest);
  private readonly fb = inject(FormBuilder);

  // Signals
  proveedores = signal<Proveedor[]>([]);
  proveedoresFiltrados = signal<Proveedor[]>([]);
  busqueda = signal<string>('');
  cargando = signal<boolean>(false);
  mostrarModal = signal<boolean>(false);
  modoEdicion = signal<boolean>(false);
  mensajeExito = signal<string>('');
  mensajeError = signal<string>('');

  // Formulario
  proveedorForm: FormGroup;

  // Computed
  totalProveedores = computed(() => this.proveedoresFiltrados().length);

  constructor() {
    this.proveedorForm = this.fb.group({
      id: [0],
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      ruc: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      telefono: ['', [Validators.required, Validators.pattern(/^\d{7,15}$/)]]
    });
  }

  ngOnInit(): void {
    this.cargarProveedores();
  }

  cargarProveedores(): void {
    this.cargando.set(true);
    this.restService.obtenerProveedores().subscribe({
      next: (data) => {
        console.log('Proveedores cargados:', data);
        this.proveedores.set([...data]); // Crear nueva referencia
        this.buscar(); // Aplicar filtro actual
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar proveedores:', error);
        this.mostrarMensajeError('Error al cargar los proveedores');
        this.cargando.set(false);
      }
    });
  }

  buscar(): void {
    const termino = this.busqueda().toLowerCase().trim();
    if (!termino) {
      this.proveedoresFiltrados.set([...this.proveedores()]); // Crear nueva referencia
      return;
    }

    const filtrados = this.proveedores().filter(proveedor =>
      proveedor.nombre.toLowerCase().includes(termino) ||
      proveedor.ruc.includes(termino) ||
      proveedor.telefono.includes(termino)
    );
    this.proveedoresFiltrados.set(filtrados);
  }

  limpiarBusqueda(): void {
    this.busqueda.set('');
    this.proveedoresFiltrados.set([...this.proveedores()]); // Crear nueva referencia
  }

  abrirModalNuevo(): void {
    this.modoEdicion.set(false);
    this.proveedorForm.reset({
      id: 0,
      nombre: '',
      ruc: '',
      telefono: ''
    });
    this.mostrarModal.set(true);
  }

  abrirModalEditar(proveedor: Proveedor): void {
    this.modoEdicion.set(true);
    this.proveedorForm.patchValue(proveedor);
    this.mostrarModal.set(true);
  }

  cerrarModal(): void {
    this.mostrarModal.set(false);
    this.proveedorForm.reset();
  }

  guardarProveedor(): void {
    if (this.proveedorForm.invalid) {
      this.mostrarMensajeError('Por favor complete todos los campos correctamente');
      return;
    }

    this.cargando.set(true);
    const formValue = this.proveedorForm.value;

    if (this.modoEdicion()) {
      // Actualizar - enviar con id
      const proveedor: Proveedor = {
        id: formValue.id,
        nombre: formValue.nombre,
        ruc: formValue.ruc,
        telefono: formValue.telefono
      };
      
      this.restService.actualizarProveedor(proveedor.id, proveedor).subscribe({
        next: () => {
          this.mostrarMensajeExito('Proveedor actualizado exitosamente');
          this.cerrarModal();
          this.cargarProveedores();
        },
        error: (error) => {
          console.error('Error al actualizar proveedor:', error);
          this.mostrarMensajeError('Error al actualizar el proveedor');
          this.cargando.set(false);
        }
      });
    } else {
      // Crear - enviar sin id
      const nuevoProveedor = {
        nombre: formValue.nombre,
        ruc: formValue.ruc,
        telefono: formValue.telefono
      };
      
      this.restService.crearProveedor(nuevoProveedor as any).subscribe({
        next: () => {
          this.mostrarMensajeExito('Proveedor creado exitosamente');
          this.cerrarModal();
          this.cargarProveedores();
        },
        error: (error) => {
          console.error('Error al crear proveedor:', error);
          this.mostrarMensajeError('Error al crear el proveedor');
          this.cargando.set(false);
        }
      });
    }
  }

  eliminarProveedor(proveedor: Proveedor): void {
    if (!confirm(`¿Está seguro de eliminar el proveedor "${proveedor.nombre}"?`)) {
      return;
    }

    this.cargando.set(true);
    this.restService.eliminarProveedor(proveedor.id).subscribe({
      next: () => {
        this.mostrarMensajeExito('Proveedor eliminado exitosamente');
        this.cargarProveedores();
      },
      error: (error) => {
        console.error('Error al eliminar proveedor:', error);
        this.mostrarMensajeError('Error al eliminar el proveedor');
        this.cargando.set(false);
      }
    });
  }

  mostrarMensajeExito(mensaje: string): void {
    this.mensajeExito.set(mensaje);
    setTimeout(() => this.mensajeExito.set(''), 3000);
  }

  mostrarMensajeError(mensaje: string): void {
    this.mensajeError.set(mensaje);
    setTimeout(() => this.mensajeError.set(''), 3000);
  }

  // Getters para validación del formulario
  get nombreInvalido(): boolean {
    const control = this.proveedorForm.get('nombre');
    return !!(control?.invalid && control?.touched);
  }

  get rucInvalido(): boolean {
    const control = this.proveedorForm.get('ruc');
    return !!(control?.invalid && control?.touched);
  }

  get telefonoInvalido(): boolean {
    const control = this.proveedorForm.get('telefono');
    return !!(control?.invalid && control?.touched);
  }
}
