# Documentación de Endpoints del Backend

## Base URL
```
http://localhost:8080/dbrestalmacenlaboratorio
```

## Endpoints Disponibles

### 🔐 Autenticación

#### Login
- **POST** `/api/usuarios/login`
- **Body**: `LoginRequest { nombre: string, clave: string }`
- **Response**: `{ usuario: UsuarioLogin, mensaje: string }`

---

### 👤 Usuarios

#### Listar todos los usuarios
- **GET** `/api/usuarios`
- **Response**: `Usuario[]`

#### Obtener usuario por ID
- **GET** `/api/usuarios/{id}`
- **Response**: `Usuario`

#### Crear usuario
- **POST** `/api/usuarios`
- **Body**: `Usuario`
- **Response**: `Usuario`

#### Actualizar usuario
- **PUT** `/api/usuarios/{id}`
- **Body**: `Usuario`
- **Response**: `Usuario`

#### Eliminar usuario
- **DELETE** `/api/usuarios/{id}`
- **Response**: `200 OK`

---

### 🏷️ Roles

#### Listar todos los roles
- **GET** `/api/roles`
- **Response**: `Role[]`

#### Obtener rol por ID
- **GET** `/api/roles/{id}`
- **Response**: `Role`

#### Crear rol
- **POST** `/api/roles`
- **Body**: `Role`
- **Response**: `Role`

#### Actualizar rol
- **PUT** `/api/roles/{id}`
- **Body**: `Role`
- **Response**: `Role`

#### Eliminar rol
- **DELETE** `/api/roles/{id}`
- **Response**: `200 OK`

---

### 🏭 Marcas

#### Listar todas las marcas
- **GET** `/api/marcas`
- **Response**: `Marca[]`

#### Obtener marca por ID
- **GET** `/api/marcas/{id}`
- **Response**: `Marca`

#### Crear marca
- **POST** `/api/marcas`
- **Body**: `Marca`
- **Response**: `Marca`

#### Actualizar marca
- **PUT** `/api/marcas/{id}`
- **Body**: `Marca`
- **Response**: `Marca`

#### Eliminar marca
- **DELETE** `/api/marcas/{id}`
- **Response**: `200 OK`

---

### 🧪 Reactivos

#### Listar todos los reactivos
- **GET** `/api/reactivos`
- **Response**: `Reactivo[]`

#### Obtener reactivo por ID
- **GET** `/api/reactivos/{id}`
- **Response**: `Reactivo`

#### Crear reactivo
- **POST** `/api/reactivos`
- **Body**: `Reactivo`
- **Response**: `Reactivo`

#### Actualizar reactivo
- **PUT** `/api/reactivos/{id}`
- **Body**: `Reactivo`
- **Response**: `Reactivo`

#### Eliminar reactivo
- **DELETE** `/api/reactivos/{id}`
- **Response**: `200 OK`

---

### 🏢 Departamentos

#### Listar todos los departamentos
- **GET** `/api/departamentos`
- **Response**: `Departamento[]`

#### Obtener departamento por ID
- **GET** `/api/departamentos/{id}`
- **Response**: `Departamento`

#### Crear departamento
- **POST** `/api/departamentos`
- **Body**: `Departamento`
- **Response**: `Departamento`

#### Actualizar departamento
- **PUT** `/api/departamentos/{id}`
- **Body**: `Departamento`
- **Response**: `Departamento`

#### Eliminar departamento
- **DELETE** `/api/departamentos/{id}`
- **Response**: `200 OK`

---

### 🏪 Proveedores

#### Listar todos los proveedores
- **GET** `/api/proveedores`
- **Response**: `Proveedor[]`

#### Obtener proveedor por ID
- **GET** `/api/proveedores/{id}`
- **Response**: `Proveedor`

#### Crear proveedor
- **POST** `/api/proveedores`
- **Body**: `Proveedor`
- **Response**: `Proveedor`

#### Actualizar proveedor
- **PUT** `/api/proveedores/{id}`
- **Body**: `Proveedor`
- **Response**: `Proveedor`

#### Eliminar proveedor
- **DELETE** `/api/proveedores/{id}`
- **Response**: `200 OK`

---

### 🏬 Almacenes

#### Listar todos los almacenes
- **GET** `/api/almacenes`
- **Response**: `Almacen[]`

#### Obtener almacén por ID
- **GET** `/api/almacenes/{id}`
- **Response**: `Almacen`

#### Crear almacén
- **POST** `/api/almacenes`
- **Body**: `Almacen`
- **Response**: `Almacen`

#### Actualizar almacén
- **PUT** `/api/almacenes/{id}`
- **Body**: `Almacen`
- **Response**: `Almacen`

#### Eliminar almacén
- **DELETE** `/api/almacenes/{id}`
- **Response**: `200 OK`

---

### 📦 Lotes

#### Listar todos los lotes
- **GET** `/api/lotes`
- **Response**: `Lote[]`

#### Obtener lote por ID
- **GET** `/api/lotes/{id}`
- **Response**: `Lote`

#### Obtener lotes por compra
- **GET** `/api/lotes/compra/{idCompra}`
- **Response**: `Lote[]`

#### Crear lote
- **POST** `/api/lotes`
- **Body**: `Lote`
- **Response**: `Lote`

#### Actualizar lote
- **PUT** `/api/lotes/{id}`
- **Body**: `Lote`
- **Response**: `Lote`

#### Eliminar lote
- **DELETE** `/api/lotes/{id}`
- **Response**: `200 OK`

---

### 🔄 Tipos de Acción

#### Listar todos los tipos de acción
- **GET** `/api/tipoacciones`
- **Response**: `Tipoaccion[]`

#### Obtener tipo de acción por ID
- **GET** `/api/tipoacciones/{id}`
- **Response**: `Tipoaccion`

#### Crear tipo de acción
- **POST** `/api/tipoacciones`
- **Body**: `Tipoaccion`
- **Response**: `Tipoaccion`

#### Actualizar tipo de acción
- **PUT** `/api/tipoacciones/{id}`
- **Body**: `Tipoaccion`
- **Response**: `Tipoaccion`

#### Eliminar tipo de acción
- **DELETE** `/api/tipoacciones/{id}`
- **Response**: `200 OK`

---

### 📋 Movimientos

#### Listar todos los movimientos
- **GET** `/api/movimientos`
- **Response**: `Movimiento[]`

#### Obtener movimiento por ID
- **GET** `/api/movimientos/{id}`
- **Response**: `Movimiento`

#### Crear movimiento
- **POST** `/api/movimientos`
- **Body**: `Movimiento`
- **Response**: `Movimiento`

#### Actualizar movimiento
- **PUT** `/api/movimientos/{id}`
- **Body**: `Movimiento`
- **Response**: `Movimiento`

#### Eliminar movimiento
- **DELETE** `/api/movimientos/{id}`
- **Response**: `200 OK`

---

### 📝 Líneas de Movimiento

#### Listar todas las líneas de movimiento
- **GET** `/api/movimientolineas`
- **Response**: `Movimientolinea[]`

#### Obtener línea de movimiento por ID
- **GET** `/api/movimientolineas/{id}`
- **Response**: `Movimientolinea`

#### Crear línea de movimiento
- **POST** `/api/movimientolineas`
- **Body**: `Movimientolinea`
- **Response**: `Movimientolinea`

#### Actualizar línea de movimiento
- **PUT** `/api/movimientolineas/{id}`
- **Body**: `Movimientolinea`
- **Response**: `Movimientolinea`

#### Eliminar línea de movimiento
- **DELETE** `/api/movimientolineas/{id}`
- **Response**: `200 OK`

---

### 🛒 Compras

#### Listar todas las compras
- **GET** `/api/compras`
- **Response**: `Compra[]`

#### Obtener compra por ID
- **GET** `/api/compras/{id}`
- **Response**: `Compra`

#### Crear compra
- **POST** `/api/compras`
- **Body**: `Compra`
- **Response**: `Compra`

#### Actualizar compra
- **PUT** `/api/compras/{id}`
- **Body**: `Compra`
- **Response**: `Compra`

#### Eliminar compra
- **DELETE** `/api/compras/{id}`
- **Response**: `200 OK`

---

### 📤 Consumos

#### Listar todos los consumos
- **GET** `/api/consumos`
- **Response**: `Consumo[]`

#### Obtener consumo por ID
- **GET** `/api/consumos/{id}`
- **Response**: `Consumo`

#### Crear consumo
- **POST** `/api/consumos`
- **Body**: `Consumo`
- **Response**: `Consumo`

#### Actualizar consumo
- **PUT** `/api/consumos/{id}`
- **Body**: `Consumo`
- **Response**: `Consumo`

#### Eliminar consumo
- **DELETE** `/api/consumos/{id}`
- **Response**: `200 OK`

---

### 📊 Inventario

#### Consultar inventario detallado por almacén
- **GET** `/api/inventario/almacen/{idAlmacen}/detallado`
- **Query Params**: `nombreReactivo` (opcional)
- **Response**: `Object` (Inventario detallado)

---

### 🔄 Operaciones Múltiples

#### Registrar traslado múltiple
- **POST** `/api/trasladomultiple`
- **Body**: `TrasladoMultipleRequestDTO`
  ```typescript
  {
    idUsuario: number;
    idAlmacenOrigen: number;
    idAlmacenDestino: number;
    referencia: string;
    comentario: string;
    traslados: Array<{
      id_lote: number;
      cantidad: number;
    }>;
  }
  ```
- **Response**: `200 OK`

#### Registrar ingreso múltiple (Compra)
- **POST** `/api/ingresomultiple`
- **Body**: `IngresoMultipleRequestDTO`
  ```typescript
  {
    idUsuario: number;
    idProveedor: number;
    idAlmacenDestino: number;
    referencia: string;
    comentario: string;
    lotes: Array<{
      id_reactivo: number;
      cantidad: number;
      precio_unitario: number;
      fecha_expiracion: string; // date
    }>;
  }
  ```
- **Response**: `200 OK`

#### Registrar consumo múltiple
- **POST** `/api/consumomultiple`
- **Body**: `ConsumoMultipleRequestDTO`
  ```typescript
  {
    idUsuario: number;
    idDepartamento: number;
    idAlmacenOrigen: number;
    referencia: string;
    comentario: string;
    consumos: Array<{
      id_lote: number;
      cantidad: number;
    }>;
  }
  ```
- **Response**: `200 OK`

---

## Notas de Implementación

### Estructura de Datos

El backend utiliza una estructura relacional con las siguientes relaciones principales:

1. **Usuario** → **Role** (Many-to-One)
2. **Reactivo** → **Marca** (Many-to-One)
3. **Lote** → **Reactivo** (Many-to-One)
4. **Lote** → **Compra** (Many-to-One)
5. **Movimiento** → **Usuario** (Many-to-One)
6. **Movimiento** → **Tipoaccion** (Many-to-One)
7. **Movimientolinea** → **Movimiento** (Many-to-One)
8. **Movimientolinea** → **Lote** (Many-to-One)
9. **Movimientolinea** → **Almacen** (Origen y Destino)
10. **Compra** → **Usuario** (Many-to-One)
11. **Compra** → **Proveedor** (Many-to-One)
12. **Compra** → **Movimiento** (Many-to-One)
13. **Consumo** → **Usuario** (Many-to-One)
14. **Consumo** → **Departamento** (Many-to-One)
15. **Consumo** → **Movimiento** (Many-to-One)

### Flujos de Negocio

#### Flujo de Compra (Ingreso Múltiple)
1. Se crea un **Movimiento** con tipo "INGRESO"
2. Se crea una **Compra** asociada al movimiento
3. Se crean múltiples **Lotes** asociados a la compra
4. Se crean **Movimientolinea** por cada lote ingresado

#### Flujo de Consumo Múltiple
1. Se crea un **Movimiento** con tipo "CONSUMO"
2. Se crea un **Consumo** asociado al movimiento
3. Se crean **Movimientolinea** por cada lote consumido
4. Se actualiza la cantidad disponible en cada lote

#### Flujo de Traslado Múltiple
1. Se crea un **Movimiento** con tipo "TRASLADO"
2. Se crean **Movimientolinea** por cada lote trasladado
3. Se especifica almacén origen y destino
4. Se actualiza la cantidad disponible en cada almacén

### Consideraciones

- **Fechas**: Usar formato ISO 8601 (`YYYY-MM-DD` o `YYYY-MM-DDTHH:mm:ss`)
- **Estado de Lote**: `true` = activo, `false` = inactivo/vencido
- **Cantidades**: Tipo `number` (decimal permitido)
- **IDs**: Todos son enteros (`int32`)

### Validaciones en Frontend

Deberás implementar validaciones para:

1. ✅ Cantidad no puede ser negativa
2. ✅ Cantidad a trasladar/consumir ≤ cantidad disponible
3. ✅ Fecha de expiración > fecha actual
4. ✅ Precio unitario > 0
5. ✅ Almacén origen ≠ almacén destino (en traslados)
6. ✅ Lote debe estar activo para operaciones
7. ✅ Usuario debe existir y estar autenticado

---

## Próximos Pasos

1. ✅ Actualizar interfaces TypeScript
2. ⏭️ Actualizar servicio REST con todos los endpoints
3. ⏭️ Implementar formularios para operaciones múltiples
4. ⏭️ Crear componente de inventario detallado
5. ⏭️ Implementar dashboard con estadísticas reales
