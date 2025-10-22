import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { CanActivateFn } from '@angular/router';

/**
 * Guard de autenticación para proteger rutas
 * Verifica si el usuario tiene un token de autenticación válido
 */
export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('auth_token');

  if (token) {
    // Usuario autenticado, permitir acceso
    return true;
  }

  // Usuario no autenticado, redirigir al login
  console.warn('Acceso denegado. Redirigiendo al login...');
  router.navigate(['/'], { 
    queryParams: { returnUrl: state.url } 
  });
  return false;
};

/**
 * Guard para evitar que usuarios autenticados accedan al login
 */
export const loginGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('auth_token');

  if (!token) {
    // Usuario no autenticado, permitir acceso al login
    return true;
  }

  // Usuario ya autenticado, redirigir al inicio
  console.log('Usuario ya autenticado. Redirigiendo al inicio...');
  router.navigate(['/inicio']);
  return false;
};
