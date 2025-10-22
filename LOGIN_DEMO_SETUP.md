# Configuración del Login para Backend Demo

## Resumen de Cambios

Se ha ajustado el sistema de autenticación para aceptar el formato de respuesta del backend sin implementación de seguridad JWT (modo demo).

### Backend Response Format

El backend devuelve:

```json
{
  "usuario": {
    "id": 1,
    "nombre": "Admin",
    "clave": "password123",
    "dni": "12345678",
    "fechaNacimiento": "1990-01-01",
    "idRol": {
      "id": 1,
      "nombre": "ADMINISTRADOR"
    }
  },
  "mensaje": "Login exitoso"
}
```

## Archivos Modificados

### 1. `/src/app/Modelos/interfaces.ts`

**Interfaces actualizadas:**

```typescript
export interface LoginResponse {
  usuario: UsuarioLogin;
  mensaje: string;
}

export interface UsuarioLogin {
  id: number;
  nombre: string;
  clave?: string;
  dni: string;
  fechaNacimiento: string;
  idRol: RolSimple;
}

export interface RolSimple {
  id: number;
  nombre: string;
}
```

### 2. `/src/app/Servicios/auth.service.ts`

**Cambios principales:**
- ✅ Eliminado soporte para JWT token
- ✅ Cambiado `Usuario` a `UsuarioLogin`
- ✅ Método `guardarSesion()` ahora solo recibe usuario (sin token)
- ✅ Eliminado `_token` signal
- ✅ `estaAutenticado` verifica existencia de usuario (no token)
- ✅ Sincronización con localStorage simplificada

```typescript
// ANTES
guardarSesion(token: string, usuario: Usuario): void {
  localStorage.setItem('auth_token', token);
  localStorage.setItem('usuario', JSON.stringify(usuario));
  this._token.set(token);
  this._usuario.set(usuario);
}

// AHORA
guardarSesion(usuario: UsuarioLogin): void {
  localStorage.setItem('usuario', JSON.stringify(usuario));
  this._usuario.set(usuario);
}
```

### 3. `/src/app/Componentes/login-component/login-component.ts`

**Cambios en validación de respuesta:**

```typescript
// ANTES
if (response.token && response.usuario) {
  this.authService.guardarSesion(response.token, response.usuario);
  // ...
}

// AHORA
if (response.usuario && response.mensaje) {
  this.authService.guardarSesion(response.usuario);
  // ...
}
```

## Funcionamiento del Login

### Flujo de Autenticación

1. **Usuario completa formulario** con `nombre` y `clave`
2. **Se envía request** a `POST /api/usuarios/login`
3. **Backend responde** con `{usuario, mensaje}`
4. **Login component valida** que existan `usuario` y `mensaje`
5. **AuthService guarda** usuario en localStorage
6. **Usuario es redirigido** a `/inicio`

### Credenciales de Prueba

Según el backend documentado en Swagger:

```
Usuario: Admin
Contraseña: password123
```

### Persistencia de Sesión

- ✅ Usuario guardado en `localStorage` (key: 'usuario')
- ✅ Sesión persiste entre recargas de página
- ✅ `AuthService` carga automáticamente usuario al iniciar
- ✅ Guards verifican autenticación basándose en usuario (no token)

## Guards Actualizados

### authGuard

Protege rutas que requieren autenticación:

```typescript
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.estaAutenticado()) {
    return true;
  }

  return router.navigate(['/login']);
};
```

**Rutas protegidas:**
- `/inicio`
- `/dashboard`
- `/inventario`
- `/reportes`

### loginGuard

Previene acceso a login si ya está autenticado:

```typescript
export const loginGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.estaAutenticado()) {
    return true;
  }

  return router.navigate(['/inicio']);
};
```

## Navbar Integration

El navbar accede al usuario actual mediante signals:

```typescript
readonly usuarioActual = this.authService.usuario;
readonly nombreUsuario = computed(() => 
  this.usuarioActual()?.nombre || 'Usuario'
);
readonly rolUsuario = computed(() => 
  this.usuarioActual()?.idRol?.nombre || 'Sin rol'
);
```

## Modo Demo vs Producción

### Estado Actual (DEMO)

- ❌ Sin JWT tokens
- ❌ Sin refresh tokens
- ❌ Sin expiración de sesión
- ❌ Clave enviada en respuesta
- ✅ Validación básica de credenciales
- ✅ Persistencia en localStorage

### Para Producción (Futuro)

Cuando se implemente seguridad adecuada, se deberá:

1. Backend devuelva JWT token:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "usuario": { ... }
   }
   ```

2. Actualizar interfaces:
   ```typescript
   export interface LoginResponse {
     token: string;
     usuario: UsuarioLogin;
   }
   ```

3. Restaurar AuthService con token:
   ```typescript
   guardarSesion(token: string, usuario: UsuarioLogin): void {
     localStorage.setItem('token', token);
     localStorage.setItem('usuario', JSON.stringify(usuario));
     // ...
   }
   ```

4. Implementar HTTP Interceptor para agregar token a requests
5. Implementar refresh token logic
6. Agregar expiración de sesión

## Testing

### Verificar Login

1. Iniciar backend: `http://localhost:8080`
2. Iniciar frontend: `ng serve`
3. Navegar a: `http://localhost:4200/login`
4. Ingresar credenciales:
   - Usuario: `Admin`
   - Contraseña: `password123`
5. Verificar redirección a `/inicio`
6. Verificar navbar muestra nombre de usuario

### Verificar Persistencia

1. Hacer login exitoso
2. Recargar página (F5)
3. Verificar que sigue autenticado
4. Verificar que no redirige a login

### Verificar Logout

1. Click en menú de usuario (navbar)
2. Click en "Cerrar Sesión"
3. Verificar redirección a `/login`
4. Verificar que localStorage está limpio
5. Intentar acceder a ruta protegida
6. Verificar redirección a `/login`

## Notas de Seguridad

⚠️ **ADVERTENCIA**: Esta configuración es solo para demo y NO debe usarse en producción.

**Problemas de seguridad actuales:**

1. **Clave en respuesta**: Backend devuelve la clave en la respuesta
2. **Sin tokens**: No hay mecanismo de expiración de sesión
3. **Sin HTTPS**: Credenciales enviadas sin cifrado
4. **Sin CORS apropiado**: Configuración permisiva
5. **Sin rate limiting**: Vulnerable a ataques de fuerza bruta
6. **Sin validación de dominio**: Token puede usarse desde cualquier origen

**Recomendaciones para producción:**

- ✅ Implementar JWT con expiración
- ✅ Usar HTTPS en toda comunicación
- ✅ Nunca devolver contraseñas en respuestas
- ✅ Implementar refresh tokens
- ✅ Configurar CORS apropiadamente
- ✅ Agregar rate limiting
- ✅ Validar origen de requests
- ✅ Implementar 2FA para usuarios sensibles

## Próximos Pasos

1. ✅ Login funcional con backend demo
2. ⏭️ Probar integración completa
3. ⏭️ Implementar mensajes de éxito/error en UI
4. ⏭️ Agregar loading spinner mejorado
5. ⏭️ Implementar "Recordar usuario"
6. ⏭️ Preparar migración a JWT cuando backend esté listo
