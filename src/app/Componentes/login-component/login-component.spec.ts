import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login-component';
import { Rest } from '../../Servicios/rest';
import { AuthService } from '../../Servicios/auth.service';

const mockUsuario = {
  id: 1,
  nombre: 'admin',
  dni: '12345678',
  fechaNacimiento: '1990-01-01',
  idRol: { id: 1, nombre: 'ADMIN' }
};

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let restServiceSpy: jasmine.SpyObj<Rest>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const activatedRouteStub = {
    snapshot: {
      queryParamMap: convertToParamMap({})
    }
  };

  beforeEach(async () => {
    restServiceSpy = jasmine.createSpyObj('Rest', ['login']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['guardarSesion']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate', 'navigateByUrl']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: Rest, useValue: restServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteStub }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should show validation error when form is invalid', () => {
    component.loginForm.patchValue({ nombre: '', clave: '' });

    component.onSubmit();

    expect(component.error()).toBe('Por favor, completa todos los campos correctamente');
    expect(restServiceSpy.login).not.toHaveBeenCalled();
  });

  it('should login successfully and redirect to returnUrl', () => {
    activatedRouteStub.snapshot.queryParamMap = convertToParamMap({ returnUrl: '/inicio/dashboard' });
    restServiceSpy.login.and.returnValue(of({ usuario: mockUsuario, mensaje: 'OK' }));

    component.loginForm.setValue({ nombre: 'admin', clave: '1234' });
    component.onSubmit();

    expect(restServiceSpy.login).toHaveBeenCalledWith({ nombre: 'admin', clave: '1234' });
    expect(authServiceSpy.guardarSesion).toHaveBeenCalledWith(mockUsuario);
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/inicio/dashboard');
  });

  it('should default to /inicio when returnUrl is missing', () => {
    activatedRouteStub.snapshot.queryParamMap = convertToParamMap({});
    restServiceSpy.login.and.returnValue(of({ usuario: mockUsuario, mensaje: 'OK' }));

    component.loginForm.setValue({ nombre: 'admin', clave: '1234' });
    component.onSubmit();

    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/inicio');
  });

  it('should show error message when credentials are invalid', () => {
    restServiceSpy.login.and.returnValue(throwError(() => ({ status: 401 })));

    component.loginForm.setValue({ nombre: 'admin', clave: 'wrong' });
    component.onSubmit();

    expect(component.error()).toBe('Usuario o contraseña incorrectos');
    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });
});
