--------------------------------------------------------------------------------
-- Datos de Prueba
--------------------------------------------------------------------------------

-- Insertar 5 Proveedores
INSERT INTO Proveedor (nombre, ruc, telefono) VALUES
('Merck S.A.', '20123456789', '01-4567890'),
('Sigma-Aldrich Peru', '20234567890', '01-5678901'),
('Fisher Scientific', '20345678901', '01-6789012'),
('Qhemicals SAC', '20456789012', '01-7890123'),
('Reactivos del Peru', '20567890123', '01-8901234');

SELECT * FROM Proveedor;
GO

-- Insertar 5 Marcas
INSERT INTO Marca (nombre, estado) VALUES
('Merck', 1),
('Sigma-Aldrich', 1),
('Fisher', 1),
('J.T.Baker', 1),
('Panreac', 1);

SELECT * FROM Marca;
GO

-- Insertar 3 Departamentos
INSERT INTO Departamento (nombre, responsable, ubicacion) VALUES
('Laboratorio de Quimica Analitica', 'Dr. Juan Perez', 'Edificio A - Piso 2'),
('Laboratorio de Quimica Organica', 'Dra. Maria Garcia', 'Edificio B - Piso 1'),
('Laboratorio de Bioquimica', 'Dr. Carlos Rodriguez', 'Edificio C - Piso 3');

SELECT * FROM Departamento;
GO

-- Insertar 2 Almacenes
INSERT INTO Almacen (nombre, direccion, telefono) VALUES
('Almacen Central', 'Av. Principal 123', '01-2345678'),
('Almacen Secundario', 'Jr. Los Andes 456', '01-3456789');

SELECT * FROM Almacen;
GO

-- Insertar un Usuario para las transacciones
INSERT INTO Usuario (nombre, clave, dni, fecha_nacimiento, id_rol) VALUES
('Admin Usuario', 'password123', '12345678', '1990-01-01', 1);

SELECT * FROM Usuario;
GO

-- Insertar 5 Reactivos
INSERT INTO Reactivo (nombre, id_marca) VALUES
('Acido Sulfurico 98%', 1),
('Acido Clorhidrico 37%', 2),
('Hidroxido de Sodio', 3),
('Etanol Absoluto', 4),
('Acetona Grado Analitico', 5);

SELECT * FROM Reactivo;
GO

-- Insertar Movimiento de INGRESO
INSERT INTO Movimiento (fecha, id_usuario, id_tipo_accion, referencia, comentario, created_at) VALUES
(GETDATE(), 1, 1, 'COMP-2025-001', 'Compra inicial de reactivos', GETDATE());

DECLARE @idMovimiento INT = SCOPE_IDENTITY();
SELECT @idMovimiento AS 'ID Movimiento Ingreso';
GO

-- Insertar Compra relacionada con el Movimiento
DECLARE @idMovimiento INT = (SELECT TOP 1 id FROM Movimiento ORDER BY id DESC);

INSERT INTO Compra (id_usuario, id_movimiento, id_proveedor, fecha) VALUES
(1, @idMovimiento, 1, GETDATE());

DECLARE @idCompra INT = SCOPE_IDENTITY();
SELECT @idCompra AS 'ID Compra';
GO

-- Insertar 6 Lotes relacionados con la Compra
DECLARE @idCompra INT = (SELECT TOP 1 id FROM Compra ORDER BY id DESC);

-- Crear tabla temporal para almacenar los IDs de lotes insertados
DECLARE @LotesInsertados TABLE (
    id INT,
    id_reactivo INT,
    cantidad_inicial DECIMAL(10,2)
);

-- Insertar lotes y capturar sus IDs
INSERT INTO Lote (id_reactivo, id_compra, cantidad_inicial, precio_unitario, fecha_expiracion, estado)
OUTPUT INSERTED.id, INSERTED.id_reactivo, INSERTED.cantidad_inicial INTO @LotesInsertados
VALUES
(1, @idCompra, 50.00, 45.50, '2026-12-31', 1),
(2, @idCompra, 100.00, 35.00, '2027-06-30', 1),
(3, @idCompra, 75.00, 28.75, '2026-09-15', 1),
(4, @idCompra, 200.00, 15.25, '2027-03-20', 1),
(5, @idCompra, 150.00, 22.80, '2026-11-10', 1),
(1, @idCompra, 30.00, 47.00, '2027-01-25', 1);

SELECT * FROM Lote;
GO

-- Insertar MovimientoLinea relacionado al Movimiento de INGRESO
-- Estos registros actualizarán automáticamente el inventario mediante el trigger
DECLARE @idMovimiento INT = (SELECT TOP 1 id FROM Movimiento ORDER BY id DESC);
DECLARE @idAlmacenDestino INT = 1; -- Almacen Central

-- Crear tabla temporal para almacenar los IDs de lotes
DECLARE @LotesInsertados TABLE (
    id INT,
    id_reactivo INT,
    cantidad_inicial DECIMAL(10,2)
);

-- Obtener los últimos 6 lotes insertados en orden de inserción
INSERT INTO @LotesInsertados
SELECT TOP 6 id, id_reactivo, cantidad_inicial
FROM Lote
ORDER BY id DESC;

-- Extraer los IDs específicos (en orden inverso porque usamos ORDER BY id DESC)
DECLARE @idLote1 INT = (SELECT id FROM @LotesInsertados WHERE id_reactivo = 1 AND cantidad_inicial = 50.00);
DECLARE @idLote2 INT = (SELECT id FROM @LotesInsertados WHERE id_reactivo = 2 AND cantidad_inicial = 100.00);
DECLARE @idLote3 INT = (SELECT id FROM @LotesInsertados WHERE id_reactivo = 3 AND cantidad_inicial = 75.00);
DECLARE @idLote4 INT = (SELECT id FROM @LotesInsertados WHERE id_reactivo = 4 AND cantidad_inicial = 200.00);
DECLARE @idLote5 INT = (SELECT id FROM @LotesInsertados WHERE id_reactivo = 5 AND cantidad_inicial = 150.00);
DECLARE @idLote6 INT = (SELECT id FROM @LotesInsertados WHERE id_reactivo = 1 AND cantidad_inicial = 30.00);

INSERT INTO MovimientoLinea (id_movimiento, id_almacen_origen, id_almacen_destino, id_lote, cantidad_delta, precio_venta) VALUES
(@idMovimiento, NULL, @idAlmacenDestino, @idLote1, 50.00, NULL),
(@idMovimiento, NULL, @idAlmacenDestino, @idLote2, 100.00, NULL),
(@idMovimiento, NULL, @idAlmacenDestino, @idLote3, 75.00, NULL),
(@idMovimiento, NULL, @idAlmacenDestino, @idLote4, 200.00, NULL),
(@idMovimiento, NULL, @idAlmacenDestino, @idLote5, 150.00, NULL),
(@idMovimiento, NULL, @idAlmacenDestino, @idLote6, 30.00, NULL);

SELECT * FROM MovimientoLinea;
GO

-- Verificar el inventario actualizado por el trigger
SELECT
    ia.id,
    a.nombre AS Almacen,
    r.nombre AS Reactivo,
    m.nombre AS Marca,
    ia.stock,
    l.precio_unitario,
    l.fecha_expiracion
FROM Inventario_Almacen ia
INNER JOIN Almacen a ON ia.id_almacen = a.id
INNER JOIN Lote l ON ia.id_lote = l.id
INNER JOIN Reactivo r ON l.id_reactivo = r.id
INNER JOIN Marca m ON r.id_marca = m.id
ORDER BY a.nombre, r.nombre;
GO