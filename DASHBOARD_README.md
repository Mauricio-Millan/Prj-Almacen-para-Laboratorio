# Dashboard de Control de Reactivos de Laboratorio

## 📋 Descripción

Sistema de gestión y control de reactivos de laboratorio desarrollado con Angular 20, siguiendo las mejores prácticas y utilizando la arquitectura moderna de componentes standalone con Signals.

## 🚀 Características Principales

### Dashboard Inteligente
- **Estadísticas en Tiempo Real**: Visualización de métricas clave del inventario
  - Total de reactivos registrados
  - Lotes próximos a vencer
  - Movimientos del día
  - Alertas de stock bajo

- **Gestión de Lotes**: 
  - Monitoreo de fechas de vencimiento
  - Alertas automáticas por vencimiento próximo (30 días)
  - Estados visuales (Crítico, Advertencia, Normal)

- **Seguimiento de Movimientos**:
  - Registro de todas las operaciones del día
  - Historial de acciones por usuario
  - Referencias y comentarios

## 🛠️ Tecnologías Utilizadas

- **Angular 20**: Framework principal
- **TypeScript 5.8**: Lenguaje de programación
- **Tailwind CSS 4.1**: Framework de estilos
- **RxJS 7.8**: Programación reactiva
- **Signals**: Gestión de estado moderna de Angular

## 📦 Arquitectura del Proyecto

```
src/app/
├── Componentes/
│   ├── dashboard-component/      # Dashboard principal
│   ├── login-component/          # Autenticación
│   ├── inventario-component/     # Gestión de inventario
│   ├── navbar-component/         # Barra de navegación
│   ├── side-bar-component/       # Menú lateral
│   └── reportes/                 # Generación de reportes
├── Servicios/
│   └── rest.ts                   # Servicio REST API
└── Modelos/
    └── interfaces.ts             # Definiciones TypeScript
```

## 🔌 API Backend

El frontend consume una API REST desarrollada en Spring Boot con los siguientes endpoints:

### Endpoints Principales

#### Reactivos
- `GET /api/reactivos` - Listar todos los reactivos
- `GET /api/reactivos/{id}` - Obtener reactivo por ID
- `POST /api/reactivos` - Crear nuevo reactivo
- `PUT /api/reactivos/{id}` - Actualizar reactivo
- `DELETE /api/reactivos/{id}` - Eliminar reactivo

#### Lotes
- `GET /Rest_AlmacenLaboratorio/api/lotes` - Listar todos los lotes
- `GET /Rest_AlmacenLaboratorio/api/lotes/proximos-vencer` - Lotes próximos a vencer
- `GET /Rest_AlmacenLaboratorio/api/lotes/reactivo/{idReactivo}` - Lotes por reactivo
- `PATCH /Rest_AlmacenLaboratorio/api/lotes/{id}/desactivar` - Desactivar lote

#### Movimientos
- `GET /Rest_AlmacenLaboratorio/api/movimientos` - Listar movimientos
- `GET /Rest_AlmacenLaboratorio/api/movimientos/rango-fechas` - Movimientos por rango
- `POST /Rest_AlmacenLaboratorio/api/movimientos` - Crear movimiento

#### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar usuario

## 🎯 Mejores Prácticas Implementadas

### Angular Best Practices
✅ Componentes standalone (sin NgModules)
✅ Signals para gestión de estado
✅ Computed signals para estado derivado
✅ OnPush change detection
✅ Control flow nativo (`@if`, `@for`, `@switch`)
✅ `inject()` function en lugar de constructor injection
✅ `input()` y `output()` functions

### TypeScript Best Practices
✅ Strict type checking
✅ Type inference cuando es obvio
✅ Evitar el tipo `any`
✅ Interfaces bien definidas

### Servicios
✅ `providedIn: 'root'` para singleton services
✅ Responsabilidad única
✅ Separación de concerns

## 🔧 Configuración

### Requisitos Previos
- Node.js 18+ 
- npm o yarn
- Backend corriendo en `http://localhost:8080`

### Instalación

```bash
# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm start

# El proyecto estará disponible en http://localhost:4200
```

### Compilación para Producción

```bash
npm run build
```

## 📊 Componentes del Dashboard

### StatsCards
Tarjetas con métricas principales que se actualizan automáticamente:
- Reactivos Totales
- Lotes por Vencer (próximos 30 días)
- Movimientos del Día
- Stock Bajo

### Reactivos Recientes
Lista de los últimos 5 reactivos registrados con:
- Nombre del reactivo
- Marca asociada
- Estado (Activo/Inactivo)

### Lotes Próximos a Vencer
Tabla con información detallada:
- Nombre del reactivo y marca
- Cantidad disponible
- Fecha de vencimiento
- Días restantes
- Estado visual (Crítico < 7 días, Advertencia < 30 días)

### Movimientos del Día
Feed de actividad con:
- Tipo de acción
- Usuario responsable
- Fecha y hora
- Referencias y comentarios

## 🎨 Sistema de Diseño

El proyecto utiliza Tailwind CSS con un sistema de colores personalizado:

- **Primario**: Azul (`blue-600`, `blue-700`)
- **Advertencia**: Amarillo (`yellow-500`, `yellow-700`)
- **Éxito**: Verde (`green-500`, `green-700`)
- **Error**: Rojo (`red-500`, `red-700`)
- **Neutral**: Grises (`gray-100` a `gray-900`)

## 🔐 Seguridad

- Validación de datos en el frontend
- Manejo de errores robusto
- Tokens de autenticación (preparado para JWT)

## 📈 Próximas Funcionalidades

- [ ] Gráficos y reportes avanzados
- [ ] Exportación a PDF/Excel
- [ ] Notificaciones push
- [ ] Gestión de permisos por rol
- [ ] Código QR para reactivos
- [ ] Dashboard personalizable
- [ ] Modo oscuro

## 👥 Equipo de Desarrollo

Proyecto desarrollado para el curso Integrador 1 - Ciclo 4

## 📄 Licencia

Proyecto académico - Universidad
