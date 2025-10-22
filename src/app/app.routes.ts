import { Routes } from '@angular/router';
import { LoginComponent } from './Componentes/login-component/login-component';
import { PaletaColores } from './Componentes/paleta-colores/paleta-colores';
import { InicioComponent } from './Componentes/inicio-component/inicio-component';
import { DashboardComponent } from './Componentes/dashboard-component/dashboard-component';
import { Reportes } from './Componentes/reportes/reportes';
import { UsuarioComponent } from './Componentes/usuario-component/usuario-component';
import { InventarioComponent } from './Componentes/inventario-component/inventario-component';
import { GestionInformacion } from './Componentes/gestion-informacion/gestion-informacion';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'paleta-colores', component: PaletaColores },
  { 
    path: 'inicio', 
    component: InicioComponent,
    children: [
      { path: '', component: DashboardComponent },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'reportes', component: Reportes },
      { path: 'usuarios', component: UsuarioComponent },
      { path: 'inventario', component: InventarioComponent },
      { path: 'gestion-informacion', component: GestionInformacion },
      // Aquí puedes agregar más rutas hijas para los distintos módulos
      // { path: 'solicitudes', component: SolicitudesComponent },
    ]
  }
];
