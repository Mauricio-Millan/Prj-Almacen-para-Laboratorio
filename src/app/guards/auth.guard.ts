import { inject } from '@angular/core';
import { Router, CanActivateFn, CanActivateChildFn } from '@angular/router';
import { AuthService } from '../Servicios/auth.service';

/**
 * Guard de autenticación para proteger rutas
 * Verifica si el usuario tiene un token de autenticación válido
 */
const validarAcceso = (stateUrl: string): boolean => {
  const router = inject(Router);
  const authService = inject(AuthService);

  if (authService.tieneSesionActiva()) {
    return true;
  }

  console.warn('Acceso denegado. Redirigiendo al login...');
  router.navigate(['/'], {
    queryParams: { returnUrl: stateUrl }
  });
  return false;
};

export const authGuard: CanActivateFn = (route, state) => validarAcceso(state.url);

export const authChildGuard: CanActivateChildFn = (route, state) => validarAcceso(state.url);

/**
 * Guard para evitar que usuarios autenticados accedan al login
 */
export const loginGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  if (!authService.tieneSesionActiva()) {
    return true;
  }

  console.log('Usuario ya autenticado. Redirigiendo al inicio...');
  router.navigate(['/inicio']);
  return false;
};
