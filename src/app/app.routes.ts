import { Routes } from '@angular/router';
import { LoginComponent } from './Componentes/login-component/login-component';
import { PaletaColores } from './Componentes/paleta-colores/paleta-colores';
import { InicioComponent } from './Componentes/inicio-component/inicio-component';
import { DashboardComponent } from './Componentes/dashboard-component/dashboard-component';
import { Reportes } from './Componentes/reportes/reportes';

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
      // Aquí puedes agregar más rutas hijas para los distintos módulos
      // { path: 'inventario', component: InventarioComponent },
      // { path: 'solicitudes', component: SolicitudesComponent },
    ]
  }
];
