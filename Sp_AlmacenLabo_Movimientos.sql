use DBRESTALMACENLABORATORIO
GO

-- ============================================================================
-- PA_RegistrarIngresoMultiple
-- Descripción: Registra un ingreso de múltiples lotes en una sola transacción
-- Parámetros:
--   @id_usuario: ID del usuario que realiza el ingreso
--   @id_proveedor: ID del proveedor
--   @id_almacen_destino: ID del almacén destino
--   @referencia: Referencia del movimiento
--   @comentario: Comentario opcional
--   @lotes_json: JSON con array de lotes
-- Ejemplo JSON:
--   [{"id_reactivo":1,"cantidad":50,"precio_unitario":45.5,"fecha_expiracion":"2026-12-31"},...]
-- ============================================================================
CREATE OR ALTER PROCEDURE PA_RegistrarIngresoMultiple
    @id_usuario INT,
    @id_proveedor INT,
    @id_almacen_destino INT,
    @referencia NVARCHAR(255),
    @comentario NVARCHAR(255) = NULL,
    @lotes_json NVARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRANSACTION;

    BEGIN TRY
        -- Validaciones
        IF NOT EXISTS (SELECT 1 FROM Usuario WHERE id = @id_usuario)
        BEGIN
            RAISERROR(N'Usuario no existe', 16, 1);
        END

        IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = @id_proveedor)
        BEGIN
            RAISERROR(N'Proveedor no existe', 16, 1);
        END

        IF NOT EXISTS (SELECT 1 FROM Almacen WHERE id = @id_almacen_destino)
        BEGIN
            RAISERROR(N'Almacén destino no existe', 16, 1);
        END

        -- 1. Crear Movimiento
        INSERT INTO Movimiento (fecha, id_usuario, id_tipo_accion, referencia, comentario, created_at)
        VALUES (GETDATE(), @id_usuario, 1, @referencia, @comentario, GETDATE());

        DECLARE @id_movimiento INT = SCOPE_IDENTITY();

        -- 2. Crear Compra
        INSERT INTO Compra (id_usuario, id_movimiento, id_proveedor, fecha)
        VALUES (@id_usuario, @id_movimiento, @id_proveedor, GETDATE());

        DECLARE @id_compra INT = SCOPE_IDENTITY();

        -- 3. Procesar lotes desde JSON
        INSERT INTO Lote (id_reactivo, id_compra, cantidad_inicial, precio_unitario, fecha_expiracion, estado)
        SELECT
            id_reactivo,
            @id_compra,
            cantidad,
            precio_unitario,
            fecha_expiracion,
            1
        FROM OPENJSON(@lotes_json)
        WITH (
            id_reactivo INT '$.id_reactivo',
            cantidad DECIMAL(10,2) '$.cantidad',
            precio_unitario DECIMAL(10,2) '$.precio_unitario',
            fecha_expiracion DATE '$.fecha_expiracion'
        );

        -- 4. Crear MovimientoLinea para cada lote insertado
        INSERT INTO MovimientoLinea (id_movimiento, id_almacen_origen, id_almacen_destino, id_lote, cantidad_delta, precio_venta)
        SELECT
            @id_movimiento,
            NULL,
            @id_almacen_destino,
            l.id,
            l.cantidad_inicial,
            NULL
        FROM Lote l
        WHERE l.id_compra = @id_compra;

        -- 5. Retornar información del movimiento
        SELECT
            @id_movimiento AS ID_Movimiento,
            @id_compra AS ID_Compra,
            COUNT(*) AS Total_Lotes_Registrados,
            SUM(l.cantidad_inicial) AS Total_Unidades,
            SUM(l.cantidad_inicial * l.precio_unitario) AS Valor_Total
        FROM Lote l
        WHERE l.id_compra = @id_compra;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        DECLARE @ErrorMessage NVARCHAR(4000);
        DECLARE @ErrorSeverity INT;
        DECLARE @ErrorState INT;

        SELECT
            @ErrorMessage = ERROR_MESSAGE(),
            @ErrorSeverity = ERROR_SEVERITY(),
            @ErrorState = ERROR_STATE();

        RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
    END CATCH
END
GO

-- ============================================================================
-- PA_RegistrarConsumoMultiple
-- Descripción: Registra un consumo de múltiples lotes en una sola transacción
-- ============================================================================
CREATE OR ALTER PROCEDURE PA_RegistrarConsumoMultiple
    @id_usuario INT,
    @id_departamento INT,
    @id_almacen_origen INT,
    @referencia NVARCHAR(255),
    @comentario NVARCHAR(255) = NULL,
    @consumos_json NVARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRANSACTION;

    BEGIN TRY
        -- Validaciones
        IF NOT EXISTS (SELECT 1 FROM Usuario WHERE id = @id_usuario)
            RAISERROR(N'Usuario no existe', 16, 1);

        IF NOT EXISTS (SELECT 1 FROM Departamento WHERE id = @id_departamento)
            RAISERROR(N'Departamento no existe', 16, 1);

        IF NOT EXISTS (SELECT 1 FROM Almacen WHERE id = @id_almacen_origen)
            RAISERROR(N'Almacén origen no existe', 16, 1);

        -- 1. Crear Movimiento
        INSERT INTO Movimiento (fecha, id_usuario, id_tipo_accion, referencia, comentario, created_at)
        VALUES (GETDATE(), @id_usuario, 2, @referencia, @comentario, GETDATE());

        DECLARE @id_movimiento INT = SCOPE_IDENTITY();

        -- 2. Crear Consumo
        INSERT INTO Consumo (id_usuario, id_movimiento, id_departamento, fecha)
        VALUES (@id_usuario, @id_movimiento, @id_departamento, GETDATE());

        -- 3. Validar stock disponible ANTES de insertar
        DECLARE @lotes_invalidos TABLE (id_lote INT, stock_actual DECIMAL(10,2), cantidad_solicitada DECIMAL(10,2));

        INSERT INTO @lotes_invalidos
        SELECT
            j.id_lote,
            ISNULL(ia.stock, 0),
            j.cantidad
        FROM OPENJSON(@consumos_json)
        WITH (
            id_lote INT '$.id_lote',
            cantidad DECIMAL(10,2) '$.cantidad'
        ) j
        LEFT JOIN Inventario_Almacen ia ON ia.id_lote = j.id_lote AND ia.id_almacen = @id_almacen_origen
        WHERE ISNULL(ia.stock, 0) < j.cantidad;

        IF EXISTS (SELECT 1 FROM @lotes_invalidos)
        BEGIN
            SELECT
                'Stock insuficiente' AS Error,
                id_lote,
                stock_actual,
                cantidad_solicitada
            FROM @lotes_invalidos;

            RAISERROR(N'Stock insuficiente en uno o más lotes', 16, 1);
        END

        -- 4. Crear MovimientoLinea
        INSERT INTO MovimientoLinea (id_movimiento, id_almacen_origen, id_almacen_destino, id_lote, cantidad_delta, precio_venta)
        SELECT
            @id_movimiento,
            @id_almacen_origen,
            NULL,
            id_lote,
            cantidad,
            NULL
        FROM OPENJSON(@consumos_json)
        WITH (
            id_lote INT '$.id_lote',
            cantidad DECIMAL(10,2) '$.cantidad'
        );

        -- 5. Retornar información del consumo
        SELECT
            @id_movimiento AS ID_Movimiento,
            COUNT(*) AS Total_Lotes_Consumidos,
            SUM(cantidad_delta) AS Total_Unidades_Consumidas
        FROM MovimientoLinea
        WHERE id_movimiento = @id_movimiento;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        DECLARE @ErrorMessage NVARCHAR(4000);
        DECLARE @ErrorSeverity INT;
        DECLARE @ErrorState INT;

        SELECT
            @ErrorMessage = ERROR_MESSAGE(),
            @ErrorSeverity = ERROR_SEVERITY(),
            @ErrorState = ERROR_STATE();

        RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
    END CATCH
END
GO

-- ============================================================================
-- PA_RegistrarTrasladoMultiple
-- Descripción: Registra un traslado de múltiples lotes entre almacenes
-- ============================================================================
CREATE OR ALTER PROCEDURE PA_RegistrarTrasladoMultiple
    @id_usuario INT,
    @id_almacen_origen INT,
    @id_almacen_destino INT,
    @referencia NVARCHAR(255),
    @comentario NVARCHAR(255) = NULL,
    @traslados_json NVARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRANSACTION;

    BEGIN TRY
        -- Validaciones
        IF @id_almacen_origen = @id_almacen_destino
            RAISERROR(N'Los almacenes origen y destino no pueden ser iguales', 16, 1);

        -- 1. Crear Movimiento
        INSERT INTO Movimiento (fecha, id_usuario, id_tipo_accion, referencia, comentario, created_at)
        VALUES (GETDATE(), @id_usuario, 3, @referencia, @comentario, GETDATE());

        DECLARE @id_movimiento INT = SCOPE_IDENTITY();

        -- 2. Validar stock
        DECLARE @stock_invalido TABLE (id_lote INT, stock_actual DECIMAL(10,2), cantidad_solicitada DECIMAL(10,2));

        INSERT INTO @stock_invalido
        SELECT
            j.id_lote,
            ISNULL(ia.stock, 0),
            j.cantidad
        FROM OPENJSON(@traslados_json)
        WITH (id_lote INT, cantidad DECIMAL(10,2)) j
        LEFT JOIN Inventario_Almacen ia ON ia.id_lote = j.id_lote AND ia.id_almacen = @id_almacen_origen
        WHERE ISNULL(ia.stock, 0) < j.cantidad;

        IF EXISTS (SELECT 1 FROM @stock_invalido)
            RAISERROR(N'Stock insuficiente en almacén origen', 16, 1);

        -- 3. Crear MovimientoLinea
        INSERT INTO MovimientoLinea (id_movimiento, id_almacen_origen, id_almacen_destino, id_lote, cantidad_delta, precio_venta)
        SELECT
            @id_movimiento,
            @id_almacen_origen,
            @id_almacen_destino,
            id_lote,
            cantidad,
            NULL
        FROM OPENJSON(@traslados_json)
        WITH (id_lote INT, cantidad DECIMAL(10,2));

        -- 4. Retornar información
        SELECT
            @id_movimiento AS ID_Movimiento,
            COUNT(*) AS Total_Lotes_Trasladados,
            SUM(cantidad_delta) AS Total_Unidades
        FROM MovimientoLinea
        WHERE id_movimiento = @id_movimiento;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        DECLARE @ErrorMessage NVARCHAR(4000);
        DECLARE @ErrorSeverity INT;
        DECLARE @ErrorState INT;

        SELECT
            @ErrorMessage = ERROR_MESSAGE(),
            @ErrorSeverity = ERROR_SEVERITY(),
            @ErrorState = ERROR_STATE();

        RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
    END CATCH
END
GO

--------------------------------------------------------------------------------
-- PROCEDIMIENTO DE TRAZABILIDAD Y AUDITORÍA
--------------------------------------------------------------------------------

-- ============================================================================
-- PA_ObtenerLineaTiempoUsuario
-- Descripción: Obtiene la línea de tiempo completa de actividades de un usuario
--              Ideal para auditoría, trazabilidad y análisis de comportamiento
-- Parámetros:
--   @id_usuario: ID del usuario
--   @fecha_inicio: (Opcional) Fecha de inicio del período
--   @fecha_fin: (Opcional) Fecha de fin del período
--   @limite: (Opcional) Número máximo de registros a retornar (default 50)
-- Retorna: 5 ResultSets
--   1. Información del usuario
--   2. Línea de tiempo de actividades
--   3. Resumen estadístico del período
--   4. Distribución por tipo de acción
--   5. Top 10 reactivos más movidos
-- ============================================================================
CREATE OR ALTER PROCEDURE PA_ObtenerLineaTiempoUsuario
    @id_usuario INT,
    @fecha_inicio DATETIME = NULL,
    @fecha_fin DATETIME = NULL,
    @limite INT = 50
AS
BEGIN
    SET NOCOUNT ON;

    -- Validar usuario
    IF NOT EXISTS (SELECT 1 FROM Usuario WHERE id = @id_usuario)
    BEGIN
        RAISERROR(N'El usuario con ID %d no existe.', 16, 1, @id_usuario);
        RETURN;
    END

    -- Establecer fechas por defecto (últimos 6 meses)
    IF @fecha_inicio IS NULL
        SET @fecha_inicio = DATEADD(MONTH, -6, GETDATE());

    IF @fecha_fin IS NULL
        SET @fecha_fin = GETDATE();

    -- ========================================================================
    -- RESULTSET 1: Información del Usuario
    -- ========================================================================
    SELECT
        u.id AS ID_Usuario,
        u.nombre AS Nombre_Usuario,
        u.dni AS DNI,
        r.nombre AS Rol,
        u.fecha_nacimiento AS Fecha_Nacimiento
    FROM Usuario u
    INNER JOIN Roles r ON u.id_rol = r.id
    WHERE u.id = @id_usuario;

    -- ========================================================================
    -- RESULTSET 2: Línea de Tiempo de Actividades
    -- ========================================================================
    SELECT TOP (@limite)
        m.id AS ID_Movimiento,
        m.fecha AS Fecha_Hora,
        CONVERT(VARCHAR(10), m.fecha, 103) AS Fecha,
        CONVERT(VARCHAR(8), m.fecha, 108) AS Hora,
        ta.nombre AS Tipo_Accion,
        m.referencia AS Referencia,
        m.comentario AS Comentario,

        -- Detalles específicos por tipo de acción
        CASE ta.id
            WHEN 1 THEN CONCAT('Proveedor: ', p.nombre)
            WHEN 2 THEN CONCAT('Departamento: ', d.nombre)
            WHEN 3 THEN 'Traslado entre almacenes'
            WHEN 4 THEN 'Ajuste de inventario'
            ELSE 'N/A'
        END AS Detalle_Operacion,

        -- Resumen de ítems
        COUNT(DISTINCT ml.id) AS Total_Items,
        SUM(ABS(ml.cantidad_delta)) AS Total_Unidades,

        -- Almacenes involucrados (sin DISTINCT en STRING_AGG)
        STRING_AGG(ISNULL(ao.nombre, '(Sin origen)'), ', ') AS Almacenes_Origen,
        STRING_AGG(ISNULL(ad.nombre, '(Sin destino)'), ', ') AS Almacenes_Destino,

        -- Reactivos involucrados
        STRING_AGG(r.nombre, ', ') AS Reactivos_Involucrados,

        COUNT(DISTINCT r.id) AS Cantidad_Reactivos_Diferentes,

        -- Valor total
        SUM(ABS(ml.cantidad_delta) * ISNULL(ml.precio_venta, l.precio_unitario)) AS Valor_Total,

        -- Tiempo transcurrido
        DATEDIFF(DAY, m.fecha, GETDATE()) AS Dias_Transcurridos

    FROM Movimiento m
    INNER JOIN TipoAccion ta ON m.id_tipo_accion = ta.id
    LEFT JOIN MovimientoLinea ml ON m.id = ml.id_movimiento
    LEFT JOIN Lote l ON ml.id_lote = l.id
    LEFT JOIN Reactivo r ON l.id_reactivo = r.id
    LEFT JOIN Almacen ao ON ml.id_almacen_origen = ao.id
    LEFT JOIN Almacen ad ON ml.id_almacen_destino = ad.id
    LEFT JOIN Compra c ON m.id = c.id_movimiento
    LEFT JOIN Proveedor p ON c.id_proveedor = p.id
    LEFT JOIN Consumo co ON m.id = co.id_movimiento
    LEFT JOIN Departamento d ON co.id_departamento = d.id

    WHERE
        m.id_usuario = @id_usuario
        AND m.fecha BETWEEN @fecha_inicio AND @fecha_fin

    GROUP BY
        m.id, m.fecha, ta.id, ta.nombre, m.referencia, m.comentario,
        p.nombre, d.nombre

    ORDER BY m.fecha DESC;

    -- ========================================================================
    -- RESULTSET 3: Resumen Estadístico del Período
    -- ========================================================================
    SELECT
        COUNT(DISTINCT m.id) AS Total_Movimientos,

        -- Totales por tipo de acción
        SUM(CASE WHEN ta.id = 1 THEN 1 ELSE 0 END) AS Total_Ingresos,
        SUM(CASE WHEN ta.id = 2 THEN 1 ELSE 0 END) AS Total_Consumos,
        SUM(CASE WHEN ta.id = 3 THEN 1 ELSE 0 END) AS Total_Traslados,
        SUM(CASE WHEN ta.id = 4 THEN 1 ELSE 0 END) AS Total_Ajustes,

        -- Estadísticas de lotes y reactivos
        COUNT(DISTINCT ml.id_lote) AS Total_Lotes_Movidos,
        COUNT(DISTINCT r.id) AS Total_Reactivos_Diferentes,
        COUNT(DISTINCT COALESCE(ml.id_almacen_origen, ml.id_almacen_destino)) AS Total_Almacenes_Usados,

        -- Unidades y valores
        SUM(ABS(ml.cantidad_delta)) AS Total_Unidades_Movidas,
        AVG(ABS(ml.cantidad_delta)) AS Promedio_Unidades_Por_Movimiento,
        SUM(ABS(ml.cantidad_delta) * ISNULL(ml.precio_venta, l.precio_unitario)) AS Valor_Total_Movimientos,

        -- Rangos de fechas
        MIN(m.fecha) AS Primera_Actividad,
        MAX(m.fecha) AS Ultima_Actividad,
        DATEDIFF(DAY, MIN(m.fecha), MAX(m.fecha)) AS Dias_Activo,

        -- Promedio de actividad
        CAST(COUNT(DISTINCT m.id) * 1.0 / NULLIF(DATEDIFF(DAY, @fecha_inicio, @fecha_fin), 0) AS DECIMAL(10,2)) AS Promedio_Movimientos_Por_Dia

    FROM Movimiento m
    INNER JOIN TipoAccion ta ON m.id_tipo_accion = ta.id
    LEFT JOIN MovimientoLinea ml ON m.id = ml.id_movimiento
    LEFT JOIN Lote l ON ml.id_lote = l.id
    LEFT JOIN Reactivo r ON l.id_reactivo = r.id

    WHERE
        m.id_usuario = @id_usuario
        AND m.fecha BETWEEN @fecha_inicio AND @fecha_fin;

    -- ========================================================================
    -- RESULTSET 4: Distribución por Tipo de Acción
    -- ========================================================================
    SELECT
        ta.id AS ID_Tipo,
        ta.nombre AS Tipo_Accion,
        COUNT(m.id) AS Cantidad_Movimientos,
        SUM(ABS(ml.cantidad_delta)) AS Total_Unidades,
        SUM(ABS(ml.cantidad_delta) * ISNULL(ml.precio_venta, l.precio_unitario)) AS Valor_Total,
        CAST(COUNT(m.id) * 100.0 / SUM(COUNT(m.id)) OVER() AS DECIMAL(5,2)) AS Porcentaje_Movimientos,
        CAST(SUM(ABS(ml.cantidad_delta)) * 100.0 / SUM(SUM(ABS(ml.cantidad_delta))) OVER() AS DECIMAL(5,2)) AS Porcentaje_Unidades
    FROM Movimiento m
    INNER JOIN TipoAccion ta ON m.id_tipo_accion = ta.id
    LEFT JOIN MovimientoLinea ml ON m.id = ml.id_movimiento
    LEFT JOIN Lote l ON ml.id_lote = l.id
    WHERE
        m.id_usuario = @id_usuario
        AND m.fecha BETWEEN @fecha_inicio AND @fecha_fin
    GROUP BY ta.id, ta.nombre
    ORDER BY Cantidad_Movimientos DESC;

    -- ========================================================================
    -- RESULTSET 5: Top 10 Reactivos Más Movidos por el Usuario
    -- ========================================================================
    SELECT TOP 10
        r.id AS ID_Reactivo,
        r.nombre AS Nombre_Reactivo,
        ma.nombre AS Marca,
        COUNT(DISTINCT mov.id) AS Veces_Movido,
        SUM(ABS(ml.cantidad_delta)) AS Total_Unidades,
        AVG(ABS(ml.cantidad_delta)) AS Promedio_Unidades_Por_Movimiento,
        SUM(ABS(ml.cantidad_delta) * ISNULL(ml.precio_venta, l.precio_unitario)) AS Valor_Total,
        MIN(mov.fecha) AS Primera_Vez,
        MAX(mov.fecha) AS Ultima_Vez,
        DATEDIFF(DAY, MIN(mov.fecha), MAX(mov.fecha)) AS Dias_Entre_Primera_Y_Ultima
    FROM Movimiento mov
    INNER JOIN MovimientoLinea ml ON mov.id = ml.id_movimiento
    INNER JOIN Lote l ON ml.id_lote = l.id
    INNER JOIN Reactivo r ON l.id_reactivo = r.id
    LEFT JOIN Marca ma ON r.id_marca = ma.id
    WHERE
        mov.id_usuario = @id_usuario
        AND mov.fecha BETWEEN @fecha_inicio AND @fecha_fin
    GROUP BY r.id, r.nombre, ma.nombre
    ORDER BY Veces_Movido DESC, Total_Unidades DESC;
END
GO


exec PA_ObtenerLineaTiempoUsuario 1

--------------------------------------------------------------------------------
-- INFORMACIÓN SOBRE LOS PROCEDIMIENTOS ALMACENADOS
--------------------------------------------------------------------------------
PRINT '========================================================================';
PRINT 'PROCEDIMIENTOS ALMACENADOS DE MOVIMIENTOS CREADOS EXITOSAMENTE';
PRINT '========================================================================';
PRINT '';
PRINT '1. PA_RegistrarIngresoMultiple';
PRINT '   - Registra un ingreso de múltiples lotes en una transacción';
PRINT '   - Parámetros: @id_usuario, @id_proveedor, @id_almacen_destino,';
PRINT '                 @referencia, @comentario, @lotes_json';
PRINT '';
PRINT '2. PA_RegistrarConsumoMultiple';
PRINT '   - Registra un consumo de múltiples lotes en una transacción';
PRINT '   - Parámetros: @id_usuario, @id_departamento, @id_almacen_origen,';
PRINT '                 @referencia, @comentario, @consumos_json';
PRINT '';
PRINT '3. PA_RegistrarTrasladoMultiple';
PRINT '   - Registra un traslado de múltiples lotes entre almacenes';
PRINT '   - Parámetros: @id_usuario, @id_almacen_origen, @id_almacen_destino,';
PRINT '                 @referencia, @comentario, @traslados_json';
PRINT '';
PRINT '4. PA_ObtenerLineaTiempoUsuario (TRAZABILIDAD)';
PRINT '   - Obtiene la línea de tiempo completa de actividades de un usuario';
PRINT '   - Parámetros: @id_usuario, @fecha_inicio (opcional),';
PRINT '                 @fecha_fin (opcional), @limite (opcional, default 50)';
PRINT '   - Retorna 5 ResultSets:';
PRINT '     * Información del usuario';
PRINT '     * Línea de tiempo de actividades';
PRINT '     * Resumen estadístico del período';
PRINT '     * Distribución por tipo de acción';
PRINT '     * Top 10 reactivos más movidos';
PRINT '';
PRINT '========================================================================';
PRINT 'EJEMPLOS DE USO:';
PRINT '========================================================================';
PRINT '';
PRINT '-- Ver actividades del usuario 1 (últimos 6 meses)';
PRINT 'EXEC PA_ObtenerLineaTiempoUsuario @id_usuario = 1;';
PRINT '';
PRINT '-- Ver actividades en un período específico';
PRINT 'EXEC PA_ObtenerLineaTiempoUsuario';
PRINT '    @id_usuario = 1,';
PRINT '    @fecha_inicio = ''2025-01-01'',';
PRINT '    @fecha_fin = ''2025-10-21'',';
PRINT '    @limite = 100;';
PRINT '';
PRINT '========================================================================';
GO

