-- MySQL Script para el proyecto almacen de laboratorio
USE master
GO

-- Eliminar la base de datos si existe (sintaxis SQL Server)
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'DBRESTALMACENLABORATORIO')
    DROP DATABASE DBRESTALMACENLABORATORIO;
GO

-- Crear la base de datos
CREATE DATABASE DBRESTALMACENLABORATORIO;
GO

-- Usar la base de datos
USE DBRESTALMACENLABORATORIO;
GO

--------------------------------------------------------------------------------

-- Creación de Tablas (con IDENTITY para autoincremento)

CREATE TABLE Roles (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  nombre VARCHAR(255)
);

CREATE TABLE Usuario (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  nombre NVARCHAR(255) NOT NULL,
  clave VARCHAR(200),
  dni VARCHAR(255),
  fecha_nacimiento DATE,
  id_rol INT
);

CREATE TABLE TipoAccion (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  nombre NVARCHAR(255) NOT NULL
);

CREATE TABLE Almacen (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  nombre NVARCHAR(255) NOT NULL,
  direccion VARCHAR(255),
  telefono VARCHAR(255)
);

CREATE TABLE Marca (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  nombre NVARCHAR(255),
  estado BIT
);

CREATE TABLE Proveedor (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  nombre NVARCHAR(255),
  ruc VARCHAR(255),
  telefono VARCHAR(255)
);

CREATE TABLE Departamento (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  nombre NVARCHAR(255),
  responsable VARCHAR(255),
  ubicacion VARCHAR(255)
);

CREATE TABLE Reactivo (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  nombre NVARCHAR(255) NOT NULL,
  id_marca INT
);

CREATE TABLE Lote (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  id_reactivo INT NOT NULL,
  id_compra INT NOT NULL,
  cantidad_inicial DECIMAL(10,2),
  precio_unitario DECIMAL(10,2),
  fecha_expiracion DATE,
  estado BIT
);

CREATE TABLE Inventario_Almacen (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  id_almacen INT NOT NULL,
  id_lote INT NOT NULL,
  stock DECIMAL NOT NULL DEFAULT 0
);

CREATE TABLE Movimiento (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  fecha DATETIME NOT NULL,
  id_usuario INT NOT NULL,
  id_tipo_accion INT NOT NULL,
  referencia NVARCHAR(255),
  comentario NVARCHAR(255),
  created_at DATETIME NOT NULL
);

CREATE TABLE MovimientoLinea (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  id_movimiento INT NOT NULL,
  id_almacen_origen INT,
  id_almacen_destino INT,
  id_lote INT NOT NULL,
  cantidad_delta DECIMAL NOT NULL,
  precio_venta DECIMAL
);

CREATE TABLE Compra (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  id_usuario INT,
  id_movimiento INT,
  id_proveedor INT,
  fecha DATE
);

CREATE TABLE Consumo (
  id INT PRIMARY KEY IDENTITY(1,1), -- Cambio de AUTO_INCREMENT a IDENTITY(1,1)
  id_usuario INT,
  id_movimiento INT,
  id_departamento INT,
  fecha DATE
);
GO

--------------------------------------------------------------------------------

-- Creación de Foreign Keys (no requiere cambios sintácticos mayores)

ALTER TABLE Usuario ADD FOREIGN KEY (id_rol) REFERENCES Roles (id);

ALTER TABLE Lote ADD FOREIGN KEY (id_compra) REFERENCES Compra (id);

ALTER TABLE Compra ADD FOREIGN KEY (id_usuario) REFERENCES Usuario (id);

ALTER TABLE Consumo ADD FOREIGN KEY (id_usuario) REFERENCES Usuario (id);

ALTER TABLE Reactivo ADD FOREIGN KEY (id_marca) REFERENCES Marca (id);

ALTER TABLE Lote ADD FOREIGN KEY (id_reactivo) REFERENCES Reactivo (id);

ALTER TABLE Inventario_Almacen ADD FOREIGN KEY (id_almacen) REFERENCES Almacen (id);

ALTER TABLE Inventario_Almacen ADD FOREIGN KEY (id_lote) REFERENCES Lote (id);

ALTER TABLE Movimiento ADD FOREIGN KEY (id_usuario) REFERENCES Usuario (id);

ALTER TABLE Movimiento ADD FOREIGN KEY (id_tipo_accion) REFERENCES TipoAccion (id);

ALTER TABLE MovimientoLinea ADD FOREIGN KEY (id_movimiento) REFERENCES Movimiento (id);

ALTER TABLE MovimientoLinea ADD FOREIGN KEY (id_almacen_origen) REFERENCES Almacen (id);

ALTER TABLE MovimientoLinea ADD FOREIGN KEY (id_almacen_destino) REFERENCES Almacen (id);

ALTER TABLE MovimientoLinea ADD FOREIGN KEY (id_lote) REFERENCES Lote (id);

ALTER TABLE Compra ADD FOREIGN KEY (id_movimiento) REFERENCES Movimiento (id);

ALTER TABLE Compra ADD FOREIGN KEY (id_proveedor) REFERENCES Proveedor (id);

ALTER TABLE Consumo ADD FOREIGN KEY (id_movimiento) REFERENCES Movimiento (id);

ALTER TABLE Consumo ADD FOREIGN KEY (id_departamento) REFERENCES Departamento (id);
GO

--------------------------------------------------------------------------------

-- Inserción de Datos (no requiere cambios sintácticos mayores)

Insert INTO Roles (nombre) values ('ADMINISTRADOR');
Insert INTO Roles (nombre) values ('USUARIO');
Insert INTO Roles (nombre) values ('GERENTE');

-- SELECT para mostrar los datos insertados
SELECT * FROM Roles;
GO

Insert INTO TipoAccion (nombre) values ('INGRESO');
Insert INTO TipoAccion (nombre) values ('CONSUMO');
Insert INTO TipoAccion (nombre) values ('TRASLADO');
Insert INTO TipoAccion (nombre) values ('AJUSTE');

SELECT * FROM TipoAccion;
GO


select * from Usuario

