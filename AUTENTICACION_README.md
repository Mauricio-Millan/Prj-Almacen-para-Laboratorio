# Sistema de Autenticación - Guía de Implementación

## ✅ Componentes Implementados

He implementado un **sistema completo de autenticación** para tu aplicación de control de reactivos de laboratorio, siguiendo las mejores prácticas de Angular.

### 📦 Archivos Creados/Modificados

#### 1. **Login Component** (`login-component`)
- ✅ Formulario reactivo con validación
- ✅ Consumo del servicio REST de login
- ✅ Manejo de estados (loading, error, success)
- ✅ Redirección automática al dashboard tras login exitoso
- ✅ Mostrar/ocultar contraseña
- ✅ Validaciones en tiempo real
- ✅ Mensajes de error específicos por tipo de error HTTP

#### 2. **Auth Service** (`auth.service.ts`)
Servicio centralizado para gestionar la autenticación:
```typescript
- guardarSesion(token, usuario) // Guarda token y usuario
- cerrarSesion() // Limpia sesión y redirige
- estaAutenticado() // Computed signal (true/false)
- usuario() // Computed signal con datos del usuario
- tieneRol(nombreRol) // Verifica roles
- obtenerNombreUsuario() // Retorna nombre del usuario
```

#### 3. **Auth Guards** (`auth.guard.ts`)
- **authGuard**: Protege rutas que requieren autenticación
- **loginGuard**: Evita que usuarios autenticados accedan al login

#### 4. **Navbar Component** (actualizado)
- ✅ Muestra nombre del usuario autenticado
- ✅ Muestra iniciales en avatar
- ✅ Menú dropdown con información del usuario
- ✅ Botón de cerrar sesión
- ✅ Información del rol del usuario

## 🚀 Cómo Funciona

### Flujo de Autenticación

1. **Usuario accede a la aplicación** → Redirigido a `/login`

2. **Completa el formulario** con:
   - Usuario (mínimo 3 caracteres)
   - Contraseña (mínimo 4 caracteres)

3. **Al hacer submit**:
   ```typescript
   // Se envía credenciales al backend
   POST /api/auth/login
   Body: { nombre: "usuario", clave: "contraseña" }
   ```

4. **Backend responde con**:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "usuario": {
       "id": 1,
       "nombre": "Juan Pérez",
       "dni": "12345678",
       "idRol": {
         "id": 1,
         "nombre": "Administrador"
       }
     }
   }
   ```

5. **Frontend guarda en localStorage**:
   - `auth_token` → Token JWT
   - `usuario` → Información del usuario

6. **Redirección automática** → `/inicio` (Dashboard)

7. **Navbar muestra**:
   - Iniciales del usuario
   - Nombre completo
   - Rol del usuario
   - Opciones de menú
   - Botón de cerrar sesión

### Protección de Rutas

```typescript
// Rutas protegidas (requieren autenticación)
{
  path: 'inicio',
  component: InicioComponent,
  canActivate: [authGuard] // ← Guard aplicado
}

// Ruta de login (usuarios NO autenticados)
{
  path: '',
  component: LoginComponent,
  canActivate: [loginGuard] // ← Evita acceso si ya está autenticado
}
```

## 💻 Uso del AuthService

### En cualquier componente:

```typescript
import { Component, inject, computed } from '@angular/core';
import { AuthService } from './Servicios/auth.service';

export class MiComponente {
  private readonly authService = inject(AuthService);

  // Verificar si está autenticado
  readonly estaAutenticado = computed(() => this.authService.estaAutenticado());

  // Obtener usuario actual
  readonly usuarioActual = computed(() => this.authService.usuario());

  // Obtener nombre
  readonly nombre = computed(() => this.authService.obtenerNombreUsuario());

  // Verificar rol
  esAdmin(): boolean {
    return this.authService.tieneRol('Administrador');
  }

  // Cerrar sesión
  logout(): void {
    this.authService.cerrarSesion();
  }
}
```

### En templates:

```html
@if (estaAutenticado()) {
  <p>Bienvenido, {{ nombre() }}</p>
  
  @if (esAdmin()) {
    <button>Panel de Administración</button>
  }
  
  <button (click)="logout()">Cerrar Sesión</button>
}
```

## 🔒 Seguridad Implementada

### ✅ Client-Side
1. **Validación de formularios** con Validators
2. **Guards de navegación** para proteger rutas
3. **Tokens en localStorage** (considerar httpOnly cookies en producción)
4. **Limpieza de sesión** al cerrar sesión

### ⚠️ Recomendaciones para Producción

```typescript
// TODO: Implementar en el backend
1. Tokens JWT con expiración
2. Refresh tokens
3. HTTPS obligatorio
4. HttpOnly cookies en lugar de localStorage
5. CORS configurado correctamente
6. Rate limiting en endpoints de auth
```

## 📝 Endpoints del Backend Utilizados

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "nombre": "usuario",
  "clave": "contraseña"
}

Response 200:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "nombre": "Juan Pérez",
    "dni": "12345678",
    "idRol": {
      "id": 1,
      "nombre": "Administrador"
    }
  },
  "message": "Login exitoso"
}

Response 401:
{
  "message": "Usuario o contraseña incorrectos"
}
```

### Register (opcional)
```http
POST /api/auth/register
Content-Type: application/json

{
  "nombre": "Nuevo Usuario",
  "clave": "contraseña123",
  "dni": "87654321",
  "fechaNacimiento": "1990-01-01",
  "idRol": {
    "id": 2,
    "nombre": "Usuario"
  }
}
```

## 🎨 Estados Visuales del Login

### Loading State
```
┌────────────────────────────────┐
│  [🔄 Spinner] Iniciando sesión...│
└────────────────────────────────┘
```

### Error State
```
┌────────────────────────────────┐
│ ❌ Usuario o contraseña         │
│    incorrectos                  │
│                                 │
│ [Reintentar]                    │
└────────────────────────────────┘
```

### Success State
```
Redirigiendo al dashboard... ✅
```

## 🧪 Pruebas

### Credenciales de Prueba
Asegúrate de tener usuarios creados en tu backend. Ejemplo:

```json
{
  "nombre": "admin",
  "clave": "admin123"
}
```

### Flujos a Probar

1. **Login exitoso**
   - ✅ Ingresar credenciales correctas
   - ✅ Verificar redirección a `/inicio`
   - ✅ Verificar que el navbar muestra el nombre del usuario

2. **Login fallido**
   - ✅ Ingresar credenciales incorrectas
   - ✅ Verificar mensaje de error
   - ✅ Verificar que NO redirige

3. **Validaciones**
   - ✅ Dejar campos vacíos
   - ✅ Usuario < 3 caracteres
   - ✅ Contraseña < 4 caracteres

4. **Navegación protegida**
   - ✅ Intentar acceder a `/inicio` sin login
   - ✅ Verificar redirección a login
   - ✅ Intentar acceder a login estando autenticado
   - ✅ Verificar redirección a inicio

5. **Cerrar sesión**
   - ✅ Click en "Cerrar Sesión"
   - ✅ Confirmar en el diálogo
   - ✅ Verificar limpieza de localStorage
   - ✅ Verificar redirección a login

## 📱 Responsive Design

El login es completamente responsive:
- **Móvil**: Formulario se adapta al ancho completo
- **Tablet**: Máximo 500px de ancho
- **Desktop**: Centrado con sombras

## 🎯 Próximas Mejoras Sugeridas

1. **Recordar sesión** (checkbox "Recordarme")
2. **Recuperar contraseña** (enlace funcional)
3. **Registro de usuarios** (formulario completo)
4. **Autenticación de 2 factores**
5. **SSO con Google/Microsoft**
6. **Interceptor HTTP** para agregar token a requests
7. **Refresh token automático**
8. **Historial de sesiones**

## 📚 Recursos Adicionales

- [Angular Forms](https://angular.dev/guide/forms)
- [Angular Router Guards](https://angular.dev/guide/router)
- [Angular Signals](https://angular.dev/guide/signals)
- [JWT.io](https://jwt.io/) - Para debuggear tokens

## ⚙️ Configuración en el Backend

Asegúrate de que tu backend tenga:

```java
@CrossOrigin(origins = "http://localhost:4200")
@PostMapping("/api/auth/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    // Validar credenciales
    // Generar token
    // Retornar response con token y usuario
}
```

## 🐛 Troubleshooting

### Error: "No se pudo conectar con el servidor"
- ✅ Verificar que el backend esté corriendo en `http://localhost:8080`
- ✅ Verificar CORS en el backend
- ✅ Verificar la URL en `rest.ts`

### Error: "Usuario o contraseña incorrectos"
- ✅ Verificar que el usuario exista en la BD
- ✅ Verificar que la contraseña sea correcta
- ✅ Verificar que el endpoint esté funcionando (Postman/Thunder Client)

### No redirige después del login
- ✅ Verificar que el backend retorne `token` y `usuario`
- ✅ Verificar console.log en el navegador
- ✅ Verificar que las rutas estén configuradas correctamente

---

## ✨ Resumen

Has implementado un sistema de autenticación completo con:
- ✅ Login con formulario reactivo
- ✅ Validaciones en tiempo real
- ✅ Consumo de API REST
- ✅ Manejo de errores robusto
- ✅ Redirección automática
- ✅ Guards de protección
- ✅ Servicio de autenticación centralizado
- ✅ Navbar con información del usuario
- ✅ Cierre de sesión seguro
- ✅ Estados visuales (loading, error, success)

¡Todo listo para empezar a usar el sistema! 🚀
