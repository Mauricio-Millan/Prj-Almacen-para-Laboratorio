-- MySQL Script para el proyecto almacen de laboratorio

drop database if exists  DBRESTALMACENLABORATORIO;

CREATE DATABASE DBRESTALMACENLABORATORIO;
USE DBRESTALMACENLABORATORIO;




CREATE TABLE `Roles` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nombre` varchar(255)
);

CREATE TABLE `Usuario` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nombre` nvarchar(255) NOT NULL,
  `clave` varchar(200),
  `dni` varchar(255),
  `fecha_nacimiento` date,
  `id_rol` int
);

CREATE TABLE `TipoAccion` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nombre` nvarchar(255) NOT NULL
);

CREATE TABLE `Almacen` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nombre` nvarchar(255) NOT NULL,
  `direccion` varchar(255),
  `telefono` varchar(255)
);

CREATE TABLE `Marca` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nombre` nvarchar(255),
  `estado` bit
);

CREATE TABLE `Proveedor` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nombre` nvarchar(255),
  `ruc` varchar(255),
  `telefono` varchar(255)
);

CREATE TABLE `Departamento` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nombre` nvarchar(255),
  `responsable` varchar(255),
  `ubicacion` varchar(255)
);

CREATE TABLE `Reactivo` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nombre` nvarchar(255) NOT NULL,
  `id_marca` int
);

CREATE TABLE `Lote` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `id_reactivo` int NOT NULL,
  `id_compra` int NOT NULL,
  `cantidad_inicial` decimal(10,2),
  `precio_unitario` decimal(10,2),
  `fecha_expiracion` date,
  `estado` bit
);

CREATE TABLE `Inventario_Almacen` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `id_almacen` int NOT NULL,
  `id_lote` int NOT NULL,
  `stock` decimal NOT NULL DEFAULT 0
);

CREATE TABLE `Movimiento` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `fecha` datetime NOT NULL,
  `id_usuario` int NOT NULL,
  `id_tipo_accion` int NOT NULL,
  `referencia` nvarchar(255),
  `comentario` nvarchar(255),
  `created_at` datetime NOT NULL
);

CREATE TABLE `MovimientoLinea` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `id_movimiento` int NOT NULL,
  `id_almacen_origen` int,
  `id_almacen_destino` int,
  `id_lote` int NOT NULL,
  `cantidad_delta` decimal NOT NULL,
  `precio_venta` decimal
);

CREATE TABLE `Compra` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `id_usuario` int,
  `id_movimiento` int,
  `id_proveedor` int,
  `fecha` date
);

CREATE TABLE `Consumo` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `id_usuario` int,
  `id_movimiento` int,
  `id_departamento` int,
  `fecha` date
);

ALTER TABLE `Usuario` ADD FOREIGN KEY (`id_rol`) REFERENCES `Roles` (`id`);

ALTER TABLE `Lote` ADD FOREIGN KEY (`id_compra`) REFERENCES `Compra` (`id`);

ALTER TABLE `Compra` ADD FOREIGN KEY (`id_usuario`) REFERENCES `Usuario` (`id`);

ALTER TABLE `Consumo` ADD FOREIGN KEY (`id_usuario`) REFERENCES `Usuario` (`id`);

ALTER TABLE `Reactivo` ADD FOREIGN KEY (`id_marca`) REFERENCES `Marca` (`id`);

ALTER TABLE `Lote` ADD FOREIGN KEY (`id_reactivo`) REFERENCES `Reactivo` (`id`);

ALTER TABLE `Inventario_Almacen` ADD FOREIGN KEY (`id_almacen`) REFERENCES `Almacen` (`id`);

ALTER TABLE `Inventario_Almacen` ADD FOREIGN KEY (`id_lote`) REFERENCES `Lote` (`id`);

ALTER TABLE `Movimiento` ADD FOREIGN KEY (`id_usuario`) REFERENCES `Usuario` (`id`);

ALTER TABLE `Movimiento` ADD FOREIGN KEY (`id_tipo_accion`) REFERENCES `TipoAccion` (`id`);

ALTER TABLE `MovimientoLinea` ADD FOREIGN KEY (`id_movimiento`) REFERENCES `Movimiento` (`id`);

ALTER TABLE `MovimientoLinea` ADD FOREIGN KEY (`id_almacen_origen`) REFERENCES `Almacen` (`id`);

ALTER TABLE `MovimientoLinea` ADD FOREIGN KEY (`id_almacen_destino`) REFERENCES `Almacen` (`id`);

ALTER TABLE `MovimientoLinea` ADD FOREIGN KEY (`id_lote`) REFERENCES `Lote` (`id`);

ALTER TABLE `Compra` ADD FOREIGN KEY (`id_movimiento`) REFERENCES `Movimiento` (`id`);

ALTER TABLE `Compra` ADD FOREIGN KEY (`id_proveedor`) REFERENCES `Proveedor` (`id`);

ALTER TABLE `Consumo` ADD FOREIGN KEY (`id_movimiento`) REFERENCES `Movimiento` (`id`);

ALTER TABLE `Consumo` ADD FOREIGN KEY (`id_departamento`) REFERENCES `Departamento` (`id`);


Insert into Roles (nombre) values ('ADMINISTRADOR');
Insert into Roles (nombre) values ('USUARIO');
Insert into Roles (nombre) values ('Gerente');
select * from Roles;