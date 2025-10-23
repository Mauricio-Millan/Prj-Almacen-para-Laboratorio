import { Component, signal, computed, inject, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Rest } from '../../Servicios/rest';
import { Reactivo, Marca } from '../../Modelos/interfaces';

@Component({
  selector: 'app-reactivo-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reactivo-component.html',
  styleUrl: './reactivo-component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReactivoComponent implements OnInit {
  private readonly restService = inject(Rest);
  private readonly fb = inject(FormBuilder);

  // Signals
  reactivos = signal<Reactivo[]>([]);
  reactivosFiltrados = signal<Reactivo[]>([]);
  marcas = signal<Marca[]>([]);
  marcasActivas = signal<Marca[]>([]);
  busqueda = signal<string>('');
  cargando = signal<boolean>(false);
  mostrarModal = signal<boolean>(false);
  modoEdicion = signal<boolean>(false);
  mensajeExito = signal<string>('');
  mensajeError = signal<string>('');

  // Formulario
  reactivoForm: FormGroup;

  // Computed
  totalReactivos = computed(() => this.reactivosFiltrados().length);

  constructor() {
    this.reactivoForm = this.fb.group({
      id: [0],
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      idMarca: [null, [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.cargarMarcas();
    this.cargarReactivos();
  }

  cargarMarcas(): void {
    this.restService.obtenerMarcas().subscribe({
      next: (data) => {
        this.marcas.set([...data]); // Crear nueva referencia
        // Filtrar solo marcas activas para el formulario
        this.marcasActivas.set(data.filter(m => m.estado));
      },
      error: (error) => {
        console.error('Error al cargar marcas:', error);
      }
    });
  }

  cargarReactivos(): void {
    this.cargando.set(true);
    this.restService.obtenerReactivos().subscribe({
      next: (data) => {
        console.log('Reactivos cargados:', data);
        this.reactivos.set([...data]); // Crear nueva referencia
        this.buscar(); // Aplicar filtro actual
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar reactivos:', error);
        this.mostrarMensajeError('Error al cargar los reactivos');
        this.cargando.set(false);
      }
    });
  }

  buscar(): void {
    const termino = this.busqueda().toLowerCase().trim();
    if (!termino) {
      this.reactivosFiltrados.set([...this.reactivos()]); // Crear nueva referencia
      return;
    }

    const filtrados = this.reactivos().filter(reactivo =>
      reactivo.nombre.toLowerCase().includes(termino) ||
      reactivo.idMarca?.nombre?.toLowerCase().includes(termino)
    );
    this.reactivosFiltrados.set(filtrados);
  }

  limpiarBusqueda(): void {
    this.busqueda.set('');
    this.reactivosFiltrados.set([...this.reactivos()]); // Crear nueva referencia
  }

  abrirModalNuevo(): void {
    this.modoEdicion.set(false);
    this.reactivoForm.reset({
      id: 0,
      nombre: '',
      idMarca: null
    });
    this.mostrarModal.set(true);
  }

  abrirModalEditar(reactivo: Reactivo): void {
    this.modoEdicion.set(true);
    this.reactivoForm.patchValue({
      id: reactivo.id,
      nombre: reactivo.nombre,
      idMarca: reactivo.idMarca?.id || null
    });
    this.mostrarModal.set(true);
  }

  cerrarModal(): void {
    this.mostrarModal.set(false);
    this.reactivoForm.reset();
  }

  guardarReactivo(): void {
    if (this.reactivoForm.invalid) {
      this.mostrarMensajeError('Por favor complete todos los campos correctamente');
      return;
    }

    this.cargando.set(true);
    const formValue = this.reactivoForm.value;
    
    // Encontrar la marca seleccionada
    const idMarcaSeleccionada = Number(formValue.idMarca);
    const marcaSeleccionada = this.marcas().find(m => m.id === idMarcaSeleccionada);
    
    if (!marcaSeleccionada) {
      this.mostrarMensajeError('No se encontró la marca seleccionada');
      this.cargando.set(false);
      return;
    }

    console.log('Marca seleccionada:', marcaSeleccionada);

    if (this.modoEdicion()) {
      // Actualizar - enviar con id completo
      const reactivo: Reactivo = {
        id: formValue.id,
        nombre: formValue.nombre,
        idMarca: marcaSeleccionada
      };
      
      this.restService.actualizarReactivo(reactivo.id, reactivo).subscribe({
        next: () => {
          this.mostrarMensajeExito('Reactivo actualizado exitosamente');
          this.cerrarModal();
          this.cargarReactivos();
        },
        error: (error) => {
          console.error('Error al actualizar reactivo:', error);
          this.mostrarMensajeError('Error al actualizar el reactivo');
          this.cargando.set(false);
        }
      });
    } else {
      // Crear - enviar sin id del reactivo, pero con objeto marca completo
      const nuevoReactivo = {
        nombre: formValue.nombre,
        idMarca: marcaSeleccionada
      };
      
      console.log('Creando reactivo:', nuevoReactivo);
      
      this.restService.crearReactivo(nuevoReactivo as any).subscribe({
        next: (response) => {
          console.log('Reactivo creado:', response);
          this.mostrarMensajeExito('Reactivo creado exitosamente');
          this.cerrarModal();
          this.cargarReactivos();
        },
        error: (error) => {
          console.error('Error al crear reactivo:', error);
          this.mostrarMensajeError('Error al crear el reactivo');
          this.cargando.set(false);
        }
      });
    }
  }

  eliminarReactivo(reactivo: Reactivo): void {
    if (!confirm(`¿Está seguro de eliminar el reactivo "${reactivo.nombre}"?`)) {
      return;
    }

    this.cargando.set(true);
    this.restService.eliminarReactivo(reactivo.id).subscribe({
      next: () => {
        this.mostrarMensajeExito('Reactivo eliminado exitosamente');
        this.cargarReactivos();
      },
      error: (error) => {
        console.error('Error al eliminar reactivo:', error);
        this.mostrarMensajeError('Error al eliminar el reactivo');
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
    const control = this.reactivoForm.get('nombre');
    return !!(control?.invalid && control?.touched);
  }

  get marcaInvalida(): boolean {
    const control = this.reactivoForm.get('idMarca');
    return !!(control?.invalid && control?.touched);
  }
}
