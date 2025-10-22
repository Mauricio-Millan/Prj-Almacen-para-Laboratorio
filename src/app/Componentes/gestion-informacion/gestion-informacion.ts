import { Component, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MarcaComponent } from '../marca-component/marca-component';
import { ProveedorComponent } from '../proveedor-component/proveedor-component';
import { ReactivoComponent } from '../reactivo-component/reactivo-component';

type TabType = 'marcas' | 'proveedores' | 'reactivos';

@Component({
  selector: 'app-gestion-informacion',
  standalone: true,
  imports: [CommonModule, MarcaComponent, ProveedorComponent, ReactivoComponent],
  templateUrl: './gestion-informacion.html',
  styleUrl: './gestion-informacion.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GestionInformacion {
  tabActiva = signal<TabType>('marcas');

  cambiarTab(tab: TabType): void {
    this.tabActiva.set(tab);
  }
}
