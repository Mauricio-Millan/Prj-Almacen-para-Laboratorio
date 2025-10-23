import { Component, signal, computed, inject, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Rest } from '../../Servicios/rest';
import { Marca } from '../../Modelos/interfaces';

@Component({
  selector: 'app-marca-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './marca-component.html',
  styleUrl: './marca-component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MarcaComponent implements OnInit {
  private readonly restService = inject(Rest);
  private readonly fb = inject(FormBuilder);

  // Signals
  marcas = signal<Marca[]>([]);
  marcasFiltradas = signal<Marca[]>([]);
  busqueda = signal<string>('');
  cargando = signal<boolean>(false);
  mostrarModal = signal<boolean>(false);
  modoEdicion = signal<boolean>(false);
  mensajeExito = signal<string>('');
  mensajeError = signal<string>('');

  // Formulario
  marcaForm: FormGroup;

  // Computed
  totalMarcas = computed(() => this.marcasFiltradas().length);
  marcasActivas = computed(() => this.marcasFiltradas().filter(m => m.estado).length);
  marcasInactivas = computed(() => this.marcasFiltradas().filter(m => !m.estado).length);

  constructor() {
    this.marcaForm = this.fb.group({
      id: [0],
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      estado: [true, Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarMarcas();
  }

  cargarMarcas(): void {
    this.cargando.set(true);
    this.restService.obtenerMarcas().subscribe({
      next: (data) => {
        console.log('Marcas cargadas:', data);
        this.marcas.set([...data]); // Crear nueva referencia
        this.buscar(); // Aplicar filtro actual
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar marcas:', error);
        this.mostrarMensajeError('Error al cargar las marcas');
        this.cargando.set(false);
      }
    });
  }

  buscar(): void {
    const termino = this.busqueda().toLowerCase().trim();
    if (!termino) {
      this.marcasFiltradas.set([...this.marcas()]); // Crear nueva referencia
      return;
    }

    const filtradas = this.marcas().filter(marca =>
      marca.nombre.toLowerCase().includes(termino) ||
      marca.id.toString().includes(termino)
    );
    this.marcasFiltradas.set(filtradas);
  }

  limpiarBusqueda(): void {
    this.busqueda.set('');
    this.marcasFiltradas.set([...this.marcas()]); // Crear nueva referencia
  }

  abrirModalNuevo(): void {
    this.modoEdicion.set(false);
    this.marcaForm.reset({
      id: 0,
      nombre: '',
      estado: true
    });
    this.mostrarModal.set(true);
  }

  abrirModalEditar(marca: Marca): void {
    this.modoEdicion.set(true);
    this.marcaForm.patchValue(marca);
    this.mostrarModal.set(true);
  }

  cerrarModal(): void {
    this.mostrarModal.set(false);
    this.marcaForm.reset();
  }

  guardarMarca(): void {
    if (this.marcaForm.invalid) {
      this.mostrarMensajeError('Por favor complete todos los campos requeridos');
      return;
    }

    this.cargando.set(true);
    const formValue = this.marcaForm.value;

    if (this.modoEdicion()) {
      // Actualizar - enviar con id
      const marca: Marca = {
        id: formValue.id,
        nombre: formValue.nombre,
        estado: formValue.estado
      };
      
      this.restService.actualizarMarca(marca.id, marca).subscribe({
        next: () => {
          this.mostrarMensajeExito('Marca actualizada exitosamente');
          this.cerrarModal();
          this.cargarMarcas();
        },
        error: (error) => {
          console.error('Error al actualizar marca:', error);
          this.mostrarMensajeError('Error al actualizar la marca');
          this.cargando.set(false);
        }
      });
    } else {
      // Crear - enviar sin id (solo nombre y estado)
      const nuevaMarca = {
        nombre: formValue.nombre,
        estado: formValue.estado
      };
      
      this.restService.crearMarca(nuevaMarca as any).subscribe({
        next: () => {
          this.mostrarMensajeExito('Marca creada exitosamente');
          this.cerrarModal();
          this.cargarMarcas();
        },
        error: (error) => {
          console.error('Error al crear marca:', error);
          this.mostrarMensajeError('Error al crear la marca');
          this.cargando.set(false);
        }
      });
    }
  }

  eliminarMarca(marca: Marca): void {
    if (!confirm(`¿Está seguro de eliminar la marca "${marca.nombre}"?`)) {
      return;
    }

    this.cargando.set(true);
    this.restService.eliminarMarca(marca.id).subscribe({
      next: () => {
        this.mostrarMensajeExito('Marca eliminada exitosamente');
        this.cargarMarcas();
      },
      error: (error) => {
        console.error('Error al eliminar marca:', error);
        this.mostrarMensajeError('Error al eliminar la marca');
        this.cargando.set(false);
      }
    });
  }

  cambiarEstado(marca: Marca): void {
    const nuevoEstado = !marca.estado;
    const marcaActualizada = { ...marca, estado: nuevoEstado };

    this.restService.actualizarMarca(marca.id, marcaActualizada).subscribe({
      next: () => {
        this.mostrarMensajeExito(`Marca ${nuevoEstado ? 'activada' : 'desactivada'} exitosamente`);
        this.cargarMarcas();
      },
      error: (error) => {
        console.error('Error al cambiar estado:', error);
        this.mostrarMensajeError('Error al cambiar el estado de la marca');
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
    const control = this.marcaForm.get('nombre');
    return !!(control?.invalid && control?.touched);
  }
}
