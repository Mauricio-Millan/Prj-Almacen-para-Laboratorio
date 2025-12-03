import { Component, inject, signal, computed, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { Rest } from '../../Servicios/rest';
import { AuthService } from '../../Servicios/auth.service';
import { 
  Almacen, 
  Reactivo, 
  Proveedor, 
  Departamento, 
  Lote,
  IngresoMultipleRequestDTO,
  TrasladoMultipleRequestDTO,
  ConsumoMultipleRequestDTO,
  AjusteMultipleRequestDTO
} from '../../Modelos/interfaces';

type TipoMovimiento = 'INGRESO' | 'TRASLADO' | 'CONSUMO' | 'AJUSTE';

@Component({
  selector: 'app-movimiento-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './movimiento-component.html',
  styleUrl: './movimiento-component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MovimientoComponent implements OnInit {
  private readonly restService = inject(Rest);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  // Signals para el estado del componente
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly exito = signal<string | null>(null);
  readonly tipoMovimiento = signal<TipoMovimiento>('INGRESO');
  
  // Datos para los formularios
  readonly almacenes = signal<Almacen[]>([]);
  readonly almacenesDestinoFiltrados = signal<Almacen[]>([]);
  readonly almacenesOrigenFiltrados = signal<Almacen[]>([]);
  readonly reactivos = signal<Reactivo[]>([]);
  readonly reactivosActivos = signal<Reactivo[]>([]);
  readonly proveedores = signal<Proveedor[]>([]);
  readonly departamentos = signal<Departamento[]>([]);
  readonly lotes = signal<Lote[]>([]);
  readonly lotesFiltrados = signal<Lote[]>([]);

  // Usuario actual
  readonly usuarioActual = this.authService.usuario;

  // Formularios
  ingresoForm: FormGroup;
  trasladoForm: FormGroup;
  consumoForm: FormGroup;
  ajusteForm: FormGroup;

  constructor() {
    // Formulario de Ingreso
    this.ingresoForm = this.fb.group({
      idAlmacenDestino: [null, Validators.required],
      idProveedor: [null, Validators.required],
      referencia: ['', Validators.required],
      comentario: [''],
      lotes: this.fb.array([])
    });

    // Formulario de Traslado
    this.trasladoForm = this.fb.group({
      idAlmacenOrigen: [null, Validators.required],
      idAlmacenDestino: [null, Validators.required],
      referencia: ['', Validators.required],
      comentario: [''],
      traslados: this.fb.array([])
    });

    // Formulario de Consumo
    this.consumoForm = this.fb.group({
      idAlmacenOrigen: [null, Validators.required],
      idDepartamento: [null, Validators.required],
      referencia: ['', Validators.required],
      comentario: [''],
      consumos: this.fb.array([])
    });

    // Formulario de Ajuste
    this.ajusteForm = this.fb.group({
      idAlmacenOrigen: [null, Validators.required],
      referencia: ['', Validators.required],
      comentario: [''],
      ajustes: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.cargarDatos();
  }

  private cargarDatos(): void {
    this.cargando.set(true);
    
    // Cargar almacenes
    this.restService.obtenerAlmacenes().subscribe({
      next: (almacenes) => {
        this.almacenes.set(almacenes);
        this.almacenesOrigenFiltrados.set(almacenes);
        this.almacenesDestinoFiltrados.set(almacenes);
      },
      error: (err) => console.error('Error al cargar almacenes:', err)
    });

    // Cargar reactivos
    this.restService.obtenerReactivos().subscribe({
      next: (reactivos) => {
        this.reactivos.set(reactivos);
        // Usar todos los reactivos (el backend filtrará activos si es necesario)
        this.reactivosActivos.set(reactivos);
      },
      error: (err) => console.error('Error al cargar reactivos:', err)
    });

    // Cargar proveedores
    this.restService.obtenerProveedores().subscribe({
      next: (proveedores) => this.proveedores.set(proveedores),
      error: (err) => console.error('Error al cargar proveedores:', err)
    });

    // Cargar departamentos
    this.restService.obtenerDepartamentos().subscribe({
      next: (departamentos) => this.departamentos.set(departamentos),
      error: (err) => console.error('Error al cargar departamentos:', err)
    });

    // Cargar lotes
    this.restService.obtenerLotes().subscribe({
      next: (lotes) => {
        this.lotes.set(lotes);
        this.lotesFiltrados.set(lotes);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar lotes:', err);
        this.cargando.set(false);
      }
    });
  }

  // Getters para FormArrays
  get lotesFormArray(): FormArray {
    return this.ingresoForm.get('lotes') as FormArray;
  }

  get trasladosFormArray(): FormArray {
    return this.trasladoForm.get('traslados') as FormArray;
  }

  get consumosFormArray(): FormArray {
    return this.consumoForm.get('consumos') as FormArray;
  }

  get ajustesFormArray(): FormArray {
    return this.ajusteForm.get('ajustes') as FormArray;
  }

  // Cambiar tipo de movimiento
  cambiarTipoMovimiento(tipo: TipoMovimiento): void {
    this.tipoMovimiento.set(tipo);
    this.error.set(null);
    this.exito.set(null);
  }

  // ==================== INGRESO ====================
  puedeAgregarLoteIngreso(): boolean {
    const almacen = this.ingresoForm.get('idAlmacenDestino')?.value;
    const proveedor = this.ingresoForm.get('idProveedor')?.value;
    const referencia = this.ingresoForm.get('referencia')?.value;
    
    return !!(almacen && proveedor && referencia);
  }

  agregarLoteIngreso(): void {
    if (!this.puedeAgregarLoteIngreso()) {
      this.error.set('Debes completar Almacén Destino, Proveedor y Referencia antes de agregar lotes');
      setTimeout(() => this.error.set(null), 3000);
      return;
    }

    const loteGroup = this.fb.group({
      id_reactivo: [null, Validators.required],
      cantidad: [null, [Validators.required, Validators.min(1)]],
      precio_unitario: [null, [Validators.required, Validators.min(0)]],
      fecha_expiracion: [null, Validators.required]
    });
    this.lotesFormArray.push(loteGroup);
  }

  eliminarLoteIngreso(index: number): void {
    this.lotesFormArray.removeAt(index);
  }

  registrarIngreso(): void {
    if (this.ingresoForm.invalid) {
      this.marcarCamposComoTocados(this.ingresoForm);
      this.error.set('Por favor, completa todos los campos correctamente');
      return;
    }

    if (this.lotesFormArray.length === 0) {
      this.error.set('Debes agregar al menos un lote');
      return;
    }

    const usuario = this.usuarioActual();
    if (!usuario) {
      this.error.set('Usuario no autenticado');
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    const formValue = this.ingresoForm.value;
    const data: IngresoMultipleRequestDTO = {
      idUsuario: usuario.id,
      idProveedor: parseInt(formValue.idProveedor),
      idAlmacenDestino: parseInt(formValue.idAlmacenDestino),
      referencia: formValue.referencia,
      comentario: formValue.comentario || '',
      lotes: formValue.lotes.map((lote: any) => ({
        id_reactivo: parseInt(lote.id_reactivo),
        cantidad: parseFloat(lote.cantidad),
        precio_unitario: parseFloat(lote.precio_unitario),
        fecha_expiracion: lote.fecha_expiracion || null
      }))
    };

    this.restService.registrarIngresoMultiple(data).subscribe({
      next: (response) => {
        this.exito.set('Ingreso registrado exitosamente');
        this.ingresoForm.reset();
        this.lotesFormArray.clear();
        this.cargando.set(false);
        setTimeout(() => this.exito.set(null), 3000);
      },
      error: (err) => {
        console.error('Error al registrar ingreso:', err);
        this.error.set(err.error?.message || 'Error al registrar el ingreso');
        this.cargando.set(false);
      }
    });
  }

  // ==================== TRASLADO ====================
  onAlmacenOrigenChange(): void {
    const origenId = this.trasladoForm.get('idAlmacenOrigen')?.value;
    
    // Filtrar almacenes destino (excluir el origen)
    if (origenId) {
      const filtrados = this.almacenes().filter(a => a.id !== parseInt(origenId));
      this.almacenesDestinoFiltrados.set(filtrados);
      
      // Si el destino actual es igual al origen, limpiarlo
      const destinoActual = this.trasladoForm.get('idAlmacenDestino')?.value;
      if (destinoActual && parseInt(destinoActual) === parseInt(origenId)) {
        this.trasladoForm.get('idAlmacenDestino')?.setValue(null);
      }
    } else {
      this.almacenesDestinoFiltrados.set(this.almacenes());
    }
    
    // Filtrar lotes
    this.filtrarLotesPorAlmacen();
  }

  onAlmacenDestinoChange(): void {
    const destinoId = this.trasladoForm.get('idAlmacenDestino')?.value;
    
    // Filtrar almacenes origen (excluir el destino)
    if (destinoId) {
      const filtrados = this.almacenes().filter(a => a.id !== parseInt(destinoId));
      this.almacenesOrigenFiltrados.set(filtrados);
      
      // Si el origen actual es igual al destino, limpiarlo
      const origenActual = this.trasladoForm.get('idAlmacenOrigen')?.value;
      if (origenActual && parseInt(origenActual) === parseInt(destinoId)) {
        this.trasladoForm.get('idAlmacenOrigen')?.setValue(null);
      }
    } else {
      this.almacenesOrigenFiltrados.set(this.almacenes());
    }
  }

  filtrarLotesPorAlmacen(): void {
    const almacenId = this.trasladoForm.get('idAlmacenOrigen')?.value;
    
    if (almacenId) {
      this.restService.obtenerLotesPorAlmacen(parseInt(almacenId)).subscribe({
        next: (lotes) => {
          this.lotesFiltrados.set(lotes);
        },
        error: (err) => {
          console.error('Error al cargar lotes del almacén:', err);
          this.lotesFiltrados.set([]);
        }
      });
    } else {
      this.lotesFiltrados.set([]);
    }
    
    // Limpiar traslados si cambia el almacén
    this.trasladosFormArray.clear();
  }

  puedeAgregarTraslado(): boolean {
    const origen = this.trasladoForm.get('idAlmacenOrigen')?.value;
    const destino = this.trasladoForm.get('idAlmacenDestino')?.value;
    const referencia = this.trasladoForm.get('referencia')?.value;
    
    return !!(origen && destino && referencia);
  }

  agregarTraslado(): void {
    if (!this.puedeAgregarTraslado()) {
      this.error.set('Debes completar Almacén Origen, Almacén Destino y Referencia antes de agregar items');
      setTimeout(() => this.error.set(null), 3000);
      return;
    }

    const trasladoGroup = this.fb.group({
      id_lote: [null, Validators.required],
      cantidad: [null, [Validators.required, Validators.min(1)]]
    });
    this.trasladosFormArray.push(trasladoGroup);
  }

  eliminarTraslado(index: number): void {
    this.trasladosFormArray.removeAt(index);
  }

  registrarTraslado(): void {
    if (this.trasladoForm.invalid) {
      this.marcarCamposComoTocados(this.trasladoForm);
      this.error.set('Por favor, completa todos los campos correctamente');
      return;
    }

    if (this.trasladosFormArray.length === 0) {
      this.error.set('Debes agregar al menos un traslado');
      return;
    }

    const usuario = this.usuarioActual();
    if (!usuario) {
      this.error.set('Usuario no autenticado');
      return;
    }

    const formValue = this.trasladoForm.value;
    
    if (formValue.idAlmacenOrigen === formValue.idAlmacenDestino) {
      this.error.set('El almacén de origen y destino no pueden ser el mismo');
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    const data: TrasladoMultipleRequestDTO = {
      idUsuario: usuario.id,
      idAlmacenOrigen: parseInt(formValue.idAlmacenOrigen),
      idAlmacenDestino: parseInt(formValue.idAlmacenDestino),
      referencia: formValue.referencia,
      comentario: formValue.comentario || '',
      traslados: formValue.traslados.map((t: any) => ({
        id_lote: parseInt(t.id_lote),
        cantidad: parseFloat(t.cantidad)
      }))
    };

    this.restService.registrarTrasladoMultiple(data).subscribe({
      next: (response) => {
        this.exito.set('Traslado registrado exitosamente');
        this.trasladoForm.reset();
        this.trasladosFormArray.clear();
        this.lotesFiltrados.set(this.lotes());
        this.cargando.set(false);
        setTimeout(() => this.exito.set(null), 3000);
      },
      error: (err) => {
        console.error('Error al registrar traslado:', err);
        this.error.set(err.error?.message || 'Error al registrar el traslado');
        this.cargando.set(false);
      }
    });
  }

  // ==================== CONSUMO ====================
  filtrarLotesConsumo(): void {
    const almacenId = this.consumoForm.get('idAlmacenOrigen')?.value;
    
    if (almacenId) {
      this.restService.obtenerLotesPorAlmacen(parseInt(almacenId)).subscribe({
        next: (lotes) => {
          this.lotesFiltrados.set(lotes);
        },
        error: (err) => {
          console.error('Error al cargar lotes del almacén:', err);
          this.lotesFiltrados.set([]);
        }
      });
    } else {
      this.lotesFiltrados.set([]);
    }
    
    // Limpiar consumos si cambia el almacén
    this.consumosFormArray.clear();
  }

  puedeAgregarConsumo(): boolean {
    const almacen = this.consumoForm.get('idAlmacenOrigen')?.value;
    const departamento = this.consumoForm.get('idDepartamento')?.value;
    const referencia = this.consumoForm.get('referencia')?.value;
    
    return !!(almacen && departamento && referencia);
  }

  agregarConsumo(): void {
    if (!this.puedeAgregarConsumo()) {
      this.error.set('Debes completar Almacén Origen, Departamento y Referencia antes de agregar items');
      setTimeout(() => this.error.set(null), 3000);
      return;
    }

    const consumoGroup = this.fb.group({
      id_lote: [null, Validators.required],
      cantidad: [null, [Validators.required, Validators.min(1)]]
    });
    this.consumosFormArray.push(consumoGroup);
  }

  eliminarConsumo(index: number): void {
    this.consumosFormArray.removeAt(index);
  }

  registrarConsumo(): void {
    if (this.consumoForm.invalid) {
      this.marcarCamposComoTocados(this.consumoForm);
      this.error.set('Por favor, completa todos los campos correctamente');
      return;
    }

    if (this.consumosFormArray.length === 0) {
      this.error.set('Debes agregar al menos un consumo');
      return;
    }

    const usuario = this.usuarioActual();
    if (!usuario) {
      this.error.set('Usuario no autenticado');
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    const formValue = this.consumoForm.value;
    const data: ConsumoMultipleRequestDTO = {
      idUsuario: usuario.id,
      idDepartamento: parseInt(formValue.idDepartamento),
      idAlmacenOrigen: parseInt(formValue.idAlmacenOrigen),
      referencia: formValue.referencia,
      comentario: formValue.comentario || '',
      consumos: formValue.consumos.map((c: any) => ({
        id_lote: parseInt(c.id_lote),
        cantidad: parseFloat(c.cantidad)
      }))
    };

    this.restService.registrarConsumoMultiple(data).subscribe({
      next: (response) => {
        this.exito.set('Consumo registrado exitosamente');
        this.consumoForm.reset();
        this.consumosFormArray.clear();
        this.lotesFiltrados.set(this.lotes());
        this.cargando.set(false);
        setTimeout(() => this.exito.set(null), 3000);
      },
      error: (err) => {
        console.error('Error al registrar consumo:', err);
        this.error.set(err.error?.message || 'Error al registrar el consumo');
        this.cargando.set(false);
      }
    });
  }

  // ==================== AJUSTE ====================
  filtrarLotesAjuste(): void {
    const almacenId = this.ajusteForm.get('idAlmacenOrigen')?.value;
    
    if (almacenId) {
      this.restService.obtenerLotesPorAlmacen(parseInt(almacenId)).subscribe({
        next: (lotes) => {
          this.lotesFiltrados.set(lotes);
        },
        error: (err) => {
          console.error('Error al cargar lotes del almacén:', err);
          this.lotesFiltrados.set([]);
        }
      });
    } else {
      this.lotesFiltrados.set([]);
    }
    
    // Limpiar ajustes si cambia el almacén
    this.ajustesFormArray.clear();
  }

  puedeAgregarAjuste(): boolean {
    const almacen = this.ajusteForm.get('idAlmacenOrigen')?.value;
    const referencia = this.ajusteForm.get('referencia')?.value;
    
    return !!(almacen && referencia);
  }

  agregarAjuste(): void {
    if (!this.puedeAgregarAjuste()) {
      this.error.set('Debes completar Almacén y Referencia antes de agregar items');
      setTimeout(() => this.error.set(null), 3000);
      return;
    }

    const ajusteGroup = this.fb.group({
      id_lote: [null, Validators.required],
      cantidad_delta: [null, Validators.required]
    });
    this.ajustesFormArray.push(ajusteGroup);
  }

  eliminarAjuste(index: number): void {
    this.ajustesFormArray.removeAt(index);
  }

  registrarAjuste(): void {
    if (this.ajusteForm.invalid) {
      this.marcarCamposComoTocados(this.ajusteForm);
      this.error.set('Por favor, completa todos los campos correctamente');
      return;
    }

    if (this.ajustesFormArray.length === 0) {
      this.error.set('Debes agregar al menos un ajuste');
      return;
    }

    const usuario = this.usuarioActual();
    if (!usuario) {
      this.error.set('Usuario no autenticado');
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    const formValue = this.ajusteForm.value;
    const data: AjusteMultipleRequestDTO = {
      idUsuario: usuario.id,
      idAlmacenOrigen: parseInt(formValue.idAlmacenOrigen),
      referencia: formValue.referencia,
      comentario: formValue.comentario || '',
      ajustes: formValue.ajustes.map((a: any) => ({
        id_lote: parseInt(a.id_lote),
        cantidad_delta: parseFloat(a.cantidad_delta)
      }))
    };

    this.restService.registrarAjusteMultiple(data).subscribe({
      next: (response) => {
        this.exito.set('Ajuste registrado exitosamente');
        this.ajusteForm.reset();
        this.ajustesFormArray.clear();
        this.lotesFiltrados.set(this.lotes());
        this.cargando.set(false);
        setTimeout(() => this.exito.set(null), 3000);
      },
      error: (err) => {
        console.error('Error al registrar ajuste:', err);
        this.error.set(err.error?.message || 'Error al registrar el ajuste');
        this.cargando.set(false);
      }
    });
  }

  // ==================== UTILIDADES ====================
  private marcarCamposComoTocados(form: FormGroup): void {
    Object.keys(form.controls).forEach(key => {
      const control = form.get(key);
      control?.markAsTouched();
      
      if (control instanceof FormArray) {
        control.controls.forEach(formGroup => {
          this.marcarCamposComoTocados(formGroup as FormGroup);
        });
      }
    });
  }

  obtenerNombreReactivo(idReactivo: number): string {
    const reactivo = this.reactivos().find(r => r.id === idReactivo);
    return reactivo ? reactivo.nombre : 'Desconocido';
  }

  obtenerInfoLote(idLote: number): string {
    const lote = this.lotesFiltrados().find(l => l.id === idLote);
    if (!lote) return 'Lote no encontrado';
    
    const fechaExp = lote.fechaExpiracion ? new Date(lote.fechaExpiracion).toLocaleDateString('es-ES') : 'Sin fecha';
    const stock = lote.cantidadInicial || 0;
    
    return `Lote #${lote.id} - ${lote.idReactivo.nombre} | Stock: ${stock} | Vence: ${fechaExp}`;
  }
}

