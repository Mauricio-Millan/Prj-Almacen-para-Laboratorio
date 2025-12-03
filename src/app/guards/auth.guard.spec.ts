import { TestBed } from '@angular/core/testing';
import { Router, RouterStateSnapshot, ActivatedRouteSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { authChildGuard, authGuard, loginGuard } from './auth.guard';
import { AuthService } from '../Servicios/auth.service';

class AuthServiceStub {
  private autenticado = false;

  tieneSesionActiva(): boolean {
    return this.autenticado;
  }

  setAutenticado(valor: boolean): void {
    this.autenticado = valor;
  }
}

describe('Auth Guards', () => {
  let authService: AuthServiceStub;
  let router: Router;

  const route = {} as ActivatedRouteSnapshot;
  const state = { url: '/inicio' } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: AuthService, useClass: AuthServiceStub }]
    });

    authService = TestBed.inject(AuthService) as unknown as AuthServiceStub;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
  });

  it('allows access when session exists', () => {
    authService.setAutenticado(true);

    const result = TestBed.runInInjectionContext(() => authGuard(route, state));

    expect(result).toBeTrue();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('blocks access and redirects when session is missing', () => {
    authService.setAutenticado(false);

    const result = TestBed.runInInjectionContext(() => authGuard(route, state));

    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/'], { queryParams: { returnUrl: '/inicio' } });
  });

  it('applies the same logic to child routes', () => {
    authService.setAutenticado(false);

    const result = TestBed.runInInjectionContext(() => authChildGuard(route, state));

    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalled();
  });

  it('login guard permits anonymous users', () => {
    authService.setAutenticado(false);

    const result = TestBed.runInInjectionContext(() => loginGuard(route, state));

    expect(result).toBeTrue();
  });

  it('login guard redirects authenticated users to /inicio', () => {
    authService.setAutenticado(true);

    const result = TestBed.runInInjectionContext(() => loginGuard(route, state));

    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/inicio']);
  });
});
