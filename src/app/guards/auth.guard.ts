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

/**
 * Guard para validar accesos según el rol del usuario
 */
export const roleGuard: CanActivateFn = (route, state) => {
  if (!validarAcceso(state.url)) {
    return false;
  }

  const router = inject(Router);
  const authService = inject(AuthService);
  const rolActual = (authService.obtenerRolActual() ?? '').toUpperCase();

  const normalizar = (valor: string) => valor.toUpperCase();
  const rolesPermitidos = (route.data?.['rolesPermitidos'] as string[] | undefined)?.map(normalizar);
  const rolesDenegados = (route.data?.['rolesDenegados'] as string[] | undefined)?.map(normalizar);

  const permitidoPorLista = !rolesPermitidos || rolesPermitidos.includes(rolActual);
  const noDenegado = !rolesDenegados || !rolesDenegados.includes(rolActual);

  if (permitidoPorLista && noDenegado) {
    return true;
  }

  console.warn('Rol sin permisos para la ruta solicitada');
  router.navigate(['/inicio']);
  return false;
};
