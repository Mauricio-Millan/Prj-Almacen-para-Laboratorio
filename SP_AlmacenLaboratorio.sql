USE DBRESTALMACENLABORATORIO
GO

--------------------------------------------------------------------------------
-- PROCEDIMIENTOS ALMACENADOS PARA DBRESTALMACENLABORATORIO
-- Adaptados desde DBRESTALMACEN
-- Fecha: 2025-01-21
--------------------------------------------------------------------------------

-- ============================================================================
-- PA_ConsultarInventarioAlmacenDetallado
-- Descripción: Consulta detallada del inventario de un almacén específico
--              con información de reactivos, lotes y stock
-- Parámetros:
--   @id_almacen: ID del almacén a consultar
--   @nombre_reactivo: (Opcional) Filtro por nombre de reactivo
-- ============================================================================
CREATE OR ALTER PROCEDURE PA_ConsultarInventarioAlmacenDetallado
    @id_almacen INT,
    @nombre_reactivo NVARCHAR(255) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Validar que el almacén exista
    IF NOT EXISTS (SELECT 1 FROM Almacen WHERE id = @id_almacen)
    BEGIN
        RAISERROR(N'El almacén con ID %d no existe.', 16, 1, @id_almacen);
        RETURN;
    END

    -- 1. Información del almacén
    SELECT
        a.id AS ID_Almacen,
        a.nombre AS Nombre_Almacen,
        a.direccion AS Direccion,
        a.telefono AS Telefono
    FROM Almacen a
    WHERE a.id = @id_almacen;

    -- 2. Contenido del inventario con filtro opcional por reactivo
    SELECT
        ia.id AS ID_Inventario,
        r.id AS ID_Reactivo,
        r.nombre AS Nombre_Reactivo,
        m.nombre AS Marca,
        l.id AS Numero_Lote,
        l.precio_unitario AS Precio_Unitario,
        CONVERT(VARCHAR(10), l.fecha_expiracion, 103) AS Fecha_Expiracion,
        l.cantidad_inicial AS Cantidad_Inicial_Lote,
        ia.stock AS Stock_Actual,
        CASE
            WHEN ia.stock = 0 THEN 'Sin Stock'
            WHEN ia.stock < (l.cantidad_inicial * 0.2) THEN 'Stock Bajo'
            WHEN ia.stock >= (l.cantidad_inicial * 0.2) AND ia.stock < (l.cantidad_inicial * 0.5) THEN 'Stock Medio'
            ELSE 'Stock Normal'
        END AS Estado_Stock,
        CASE
            WHEN l.fecha_expiracion < GETDATE() THEN 'Expirado'
            WHEN l.fecha_expiracion <= DATEADD(MONTH, 3, GETDATE()) THEN 'Próximo a Expirar'
            ELSE 'Vigente'
        END AS Estado_Expiracion,
        DATEDIFF(DAY, GETDATE(), l.fecha_expiracion) AS Dias_Para_Expiracion
    FROM
        Inventario_Almacen ia
        INNER JOIN Lote l ON ia.id_lote = l.id
        INNER JOIN Reactivo r ON l.id_reactivo = r.id
        LEFT JOIN Marca m ON r.id_marca = m.id
    WHERE
        ia.id_almacen = @id_almacen
        AND (@nombre_reactivo IS NULL OR r.nombre LIKE '%' + @nombre_reactivo + '%')
        AND l.estado = 1
    ORDER BY
        r.nombre, l.id;

    -- 3. Resumen del almacén
    SELECT
        COUNT(DISTINCT r.id) AS Total_Reactivos_Distintos,
        COUNT(DISTINCT l.id) AS Total_Lotes,
        SUM(ia.stock) AS Total_Unidades,
        SUM(ia.stock * l.precio_unitario) AS Valor_Total_Inventario,
        AVG(ia.stock) AS Promedio_Stock_Por_Lote,
        SUM(CASE WHEN ia.stock = 0 THEN 1 ELSE 0 END) AS Lotes_Sin_Stock,
        SUM(CASE WHEN ia.stock > 0 THEN 1 ELSE 0 END) AS Lotes_Con_Stock,
        SUM(CASE WHEN ia.stock < (l.cantidad_inicial * 0.2) AND ia.stock > 0 THEN 1 ELSE 0 END) AS Lotes_Stock_Bajo,
        SUM(CASE WHEN l.fecha_expiracion < GETDATE() THEN 1 ELSE 0 END) AS Lotes_Expirados,
        SUM(CASE WHEN l.fecha_expiracion <= DATEADD(MONTH, 5, GETDATE()) AND l.fecha_expiracion >= GETDATE() THEN 1 ELSE 0 END) AS Lotes_Proximos_Expirar
    FROM
        Inventario_Almacen ia
        INNER JOIN Lote l ON ia.id_lote = l.id
        INNER JOIN Reactivo r ON l.id_reactivo = r.id
    WHERE
        ia.id_almacen = @id_almacen
        AND (@nombre_reactivo IS NULL OR r.nombre LIKE '%' + @nombre_reactivo + '%')
        AND l.estado = 1;
END
GO

-- ============================================================================
-- PA_ConsultarReactivoPorAlmacen
-- Descripción: Consulta la disponibilidad de un reactivo específico en
--              todos los almacenes o en uno específico
-- Parámetros:
--   @id_reactivo: ID del reactivo a consultar
--   @id_almacen: (Opcional) ID del almacén específico
-- ============================================================================
CREATE OR ALTER PROCEDURE PA_ConsultarReactivoPorAlmacen
    @id_reactivo INT,
    @id_almacen INT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Validar que el reactivo exista
    IF NOT EXISTS (SELECT 1 FROM Reactivo WHERE id = @id_reactivo)
    BEGIN
        RAISERROR(N'El reactivo con ID %d no existe.', 16, 1, @id_reactivo);
        RETURN;
    END

    -- Información del reactivo
    SELECT
        r.id AS ID_Reactivo,
        r.nombre AS Nombre_Reactivo,
        m.nombre AS Marca
    FROM Reactivo r
    LEFT JOIN Marca m ON r.id_marca = m.id
    WHERE r.id = @id_reactivo;

    -- Disponibilidad por almacén y lote
    SELECT
        a.id AS ID_Almacen,
        a.nombre AS Nombre_Almacen,
        a.direccion AS Direccion,
        l.id AS Numero_Lote,
        l.cantidad_inicial AS Cantidad_Inicial,
        ia.stock AS Stock_Actual,
        l.precio_unitario AS Precio_Unitario,
        CONVERT(VARCHAR(10), l.fecha_expiracion, 103) AS Fecha_Expiracion,
        DATEDIFF(DAY, GETDATE(), l.fecha_expiracion) AS Dias_Para_Expiracion,
        CASE
            WHEN l.fecha_expiracion < GETDATE() THEN 'Expirado'
            WHEN l.fecha_expiracion <= DATEADD(MONTH, 3, GETDATE()) THEN 'Próximo a Expirar'
            ELSE 'Vigente'
        END AS Estado_Expiracion,
        CASE
            WHEN ia.stock = 0 THEN 'Sin Stock'
            WHEN ia.stock < (l.cantidad_inicial * 0.2) THEN 'Stock Bajo'
            ELSE 'Stock Normal'
        END AS Estado_Stock
    FROM
        Inventario_Almacen ia
        INNER JOIN Almacen a ON ia.id_almacen = a.id
        INNER JOIN Lote l ON ia.id_lote = l.id
    WHERE
        l.id_reactivo = @id_reactivo
        AND (@id_almacen IS NULL OR a.id = @id_almacen)
        AND l.estado = 1
    ORDER BY
        a.nombre, l.fecha_expiracion DESC;

    -- Resumen de disponibilidad total
    SELECT
        COUNT(DISTINCT ia.id_almacen) AS Total_Almacenes_Con_Reactivo,
        COUNT(DISTINCT l.id) AS Total_Lotes,
        SUM(ia.stock) AS Stock_Total_Disponible,
        SUM(ia.stock * l.precio_unitario) AS Valor_Total,
        MIN(l.precio_unitario) AS Precio_Minimo,
        MAX(l.precio_unitario) AS Precio_Maximo,
        AVG(l.precio_unitario) AS Precio_Promedio
    FROM
        Inventario_Almacen ia
        INNER JOIN Lote l ON ia.id_lote = l.id
    WHERE
        l.id_reactivo = @id_reactivo
        AND (@id_almacen IS NULL OR ia.id_almacen = @id_almacen)
        AND l.estado = 1;
END
GO

-- ============================================================================
-- PA_ConsultarLotesProximosExpirar
-- Descripción: Consulta los lotes que están próximos a expirar
-- Parámetros:
--   @dias_alerta: Número de días de anticipación para la alerta
--   @id_almacen: (Opcional) Filtrar por almacén específico
-- ============================================================================
CREATE OR ALTER PROCEDURE PA_ConsultarLotesProximosExpirar
    @dias_alerta INT = 90,
    @id_almacen INT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        a.id AS ID_Almacen,
        a.nombre AS Nombre_Almacen,
        r.id AS ID_Reactivo,
        r.nombre AS Nombre_Reactivo,
        m.nombre AS Marca,
        l.id AS Numero_Lote,
        l.fecha_expiracion AS Fecha_Expiracion,
        DATEDIFF(DAY, GETDATE(), l.fecha_expiracion) AS Dias_Para_Expiracion,
        ia.stock AS Stock_Actual,
        l.precio_unitario AS Precio_Unitario,
        (ia.stock * l.precio_unitario) AS Valor_Total,
        CASE
            WHEN l.fecha_expiracion < GETDATE() THEN 'EXPIRADO'
            WHEN DATEDIFF(DAY, GETDATE(), l.fecha_expiracion) <= 30 THEN 'URGENTE'
            WHEN DATEDIFF(DAY, GETDATE(), l.fecha_expiracion) <= 60 THEN 'ALTA'
            ELSE 'MEDIA'
        END AS Prioridad
    FROM
        Inventario_Almacen ia
        INNER JOIN Almacen a ON ia.id_almacen = a.id
        INNER JOIN Lote l ON ia.id_lote = l.id
        INNER JOIN Reactivo r ON l.id_reactivo = r.id
        LEFT JOIN Marca m ON r.id_marca = m.id
    WHERE
        l.fecha_expiracion <= DATEADD(DAY, @dias_alerta, GETDATE())
        AND (@id_almacen IS NULL OR a.id = @id_almacen)
        AND l.estado = 1
        AND ia.stock > 0
    ORDER BY
        l.fecha_expiracion ASC, ia.stock DESC;

    -- Resumen de pérdidas potenciales
    SELECT
        COUNT(DISTINCT l.id) AS Total_Lotes_En_Riesgo,
        SUM(ia.stock) AS Total_Unidades_En_Riesgo,
        SUM(ia.stock * l.precio_unitario) AS Valor_Total_En_Riesgo,
        SUM(CASE WHEN l.fecha_expiracion < GETDATE() THEN ia.stock ELSE 0 END) AS Unidades_Ya_Expiradas,
        SUM(CASE WHEN l.fecha_expiracion < GETDATE() THEN (ia.stock * l.precio_unitario) ELSE 0 END) AS Valor_Ya_Expirado
    FROM
        Inventario_Almacen ia
        INNER JOIN Lote l ON ia.id_lote = l.id
    WHERE
        l.fecha_expiracion <= DATEADD(DAY, @dias_alerta, GETDATE())
        AND (@id_almacen IS NULL OR ia.id_almacen = @id_almacen)
        AND l.estado = 1
        AND ia.stock > 0;
END
GO

-- ============================================================================
-- PA_ConsultarHistorialMovimientos
-- Descripción: Consulta el historial de movimientos de un reactivo o almacén
-- Parámetros:
--   @id_reactivo: (Opcional) ID del reactivo
--   @id_almacen: (Opcional) ID del almacén
--   @fecha_inicio: (Opcional) Fecha de inicio del período
--   @fecha_fin: (Opcional) Fecha de fin del período
--   @id_tipo_accion: (Opcional) Tipo de acción (1=INGRESO, 2=CONSUMO, 3=TRASLADO, 4=AJUSTE)
-- ============================================================================
CREATE OR ALTER PROCEDURE PA_ConsultarHistorialMovimientos
    @id_reactivo INT = NULL,
    @id_almacen INT = NULL,
    @fecha_inicio DATETIME = NULL,
    @fecha_fin DATETIME = NULL,
    @id_tipo_accion INT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Establecer fechas por defecto si no se proporcionan
    IF @fecha_inicio IS NULL
        SET @fecha_inicio = DATEADD(MONTH, -3, GETDATE());

    IF @fecha_fin IS NULL
        SET @fecha_fin = GETDATE();

    SELECT
        m.id AS ID_Movimiento,
        m.fecha AS Fecha,
        ta.nombre AS Tipo_Accion,
        u.nombre AS Usuario,
        m.referencia AS Referencia,
        m.comentario AS Comentario,
        r.nombre AS Nombre_Reactivo,
        ma.nombre AS Marca,
        l.id AS Numero_Lote,
        ao.nombre AS Almacen_Origen,
        ad.nombre AS Almacen_Destino,
        ml.cantidad_delta AS Cantidad,
        ml.precio_venta AS Precio_Venta,
        (ml.cantidad_delta * ISNULL(ml.precio_venta, l.precio_unitario)) AS Valor_Total
    FROM
        Movimiento m
        INNER JOIN TipoAccion ta ON m.id_tipo_accion = ta.id
        INNER JOIN Usuario u ON m.id_usuario = u.id
        INNER JOIN MovimientoLinea ml ON m.id = ml.id_movimiento
        INNER JOIN Lote l ON ml.id_lote = l.id
        INNER JOIN Reactivo r ON l.id_reactivo = r.id
        LEFT JOIN Marca ma ON r.id_marca = ma.id
        LEFT JOIN Almacen ao ON ml.id_almacen_origen = ao.id
        LEFT JOIN Almacen ad ON ml.id_almacen_destino = ad.id
    WHERE
        (@id_reactivo IS NULL OR r.id = @id_reactivo)
        AND (@id_almacen IS NULL OR ml.id_almacen_origen = @id_almacen OR ml.id_almacen_destino = @id_almacen)
        AND m.fecha BETWEEN @fecha_inicio AND @fecha_fin
        AND (@id_tipo_accion IS NULL OR m.id_tipo_accion = @id_tipo_accion)
    ORDER BY
        m.fecha DESC, m.id DESC;

    -- Resumen del período
    SELECT
        ta.nombre AS Tipo_Accion,
        COUNT(DISTINCT m.id) AS Total_Movimientos,
        SUM(ml.cantidad_delta) AS Total_Unidades,
        SUM(ml.cantidad_delta * ISNULL(ml.precio_venta, l.precio_unitario)) AS Valor_Total
    FROM
        Movimiento m
        INNER JOIN TipoAccion ta ON m.id_tipo_accion = ta.id
        INNER JOIN MovimientoLinea ml ON m.id = ml.id_movimiento
        INNER JOIN Lote l ON ml.id_lote = l.id
        INNER JOIN Reactivo r ON l.id_reactivo = r.id
    WHERE
        (@id_reactivo IS NULL OR r.id = @id_reactivo)
        AND (@id_almacen IS NULL OR ml.id_almacen_origen = @id_almacen OR ml.id_almacen_destino = @id_almacen)
        AND m.fecha BETWEEN @fecha_inicio AND @fecha_fin
        AND (@id_tipo_accion IS NULL OR m.id_tipo_accion = @id_tipo_accion)
    GROUP BY
        ta.id, ta.nombre
    ORDER BY
        ta.id;
END
GO

-- ============================================================================
-- PA_ConsultarStockBajo
-- Descripción: Consulta los reactivos con stock bajo en los almacenes
-- Parámetros:
--   @porcentaje_minimo: Porcentaje mínimo respecto a cantidad inicial (default 20%)
--   @id_almacen: (Opcional) Filtrar por almacén específico
-- ============================================================================
CREATE OR ALTER PROCEDURE PA_ConsultarStockBajo
    @porcentaje_minimo DECIMAL(5,2) = 20.0,
    @id_almacen INT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        a.id AS ID_Almacen,
        a.nombre AS Nombre_Almacen,
        r.id AS ID_Reactivo,
        r.nombre AS Nombre_Reactivo,
        m.nombre AS Marca,
        l.id AS Numero_Lote,
        l.cantidad_inicial AS Cantidad_Inicial,
        ia.stock AS Stock_Actual,
        CAST((ia.stock * 100.0 / l.cantidad_inicial) AS DECIMAL(5,2)) AS Porcentaje_Stock,
        l.precio_unitario AS Precio_Unitario,
        CONVERT(VARCHAR(10), l.fecha_expiracion, 103) AS Fecha_Expiracion,
        DATEDIFF(DAY, GETDATE(), l.fecha_expiracion) AS Dias_Para_Expiracion,
        CASE
            WHEN ia.stock = 0 THEN 'SIN STOCK'
            WHEN (ia.stock * 100.0 / l.cantidad_inicial) < 10 THEN 'CRÍTICO'
            WHEN (ia.stock * 100.0 / l.cantidad_inicial) < @porcentaje_minimo THEN 'BAJO'
            ELSE 'NORMAL'
        END AS Nivel_Alerta
    FROM
        Inventario_Almacen ia
        INNER JOIN Almacen a ON ia.id_almacen = a.id
        INNER JOIN Lote l ON ia.id_lote = l.id
        INNER JOIN Reactivo r ON l.id_reactivo = r.id
        LEFT JOIN Marca m ON r.id_marca = m.id
    WHERE
        (ia.stock * 100.0 / l.cantidad_inicial) <= @porcentaje_minimo
        AND (@id_almacen IS NULL OR a.id = @id_almacen)
        AND l.estado = 1
    ORDER BY
        (ia.stock * 100.0 / l.cantidad_inicial) ASC, a.nombre, r.nombre;

    -- Resumen
    SELECT
        COUNT(DISTINCT ia.id) AS Total_Lotes_Stock_Bajo,
        COUNT(DISTINCT r.id) AS Total_Reactivos_Afectados,
        SUM(CASE WHEN ia.stock = 0 THEN 1 ELSE 0 END) AS Lotes_Sin_Stock,
        SUM(CASE WHEN ia.stock > 0 AND (ia.stock * 100.0 / l.cantidad_inicial) < 10 THEN 1 ELSE 0 END) AS Lotes_Criticos
    FROM
        Inventario_Almacen ia
        INNER JOIN Lote l ON ia.id_lote = l.id
        INNER JOIN Reactivo r ON l.id_reactivo = r.id
    WHERE
        (ia.stock * 100.0 / l.cantidad_inicial) <= @porcentaje_minimo
        AND (@id_almacen IS NULL OR ia.id_almacen = @id_almacen)
        AND l.estado = 1;
END
GO

-- ============================================================================
-- PA_ReporteValorInventarioPorAlmacen
-- Descripción: Genera un reporte del valor del inventario por almacén
-- Parámetros:
--   @id_almacen: (Opcional) Filtrar por almacén específico
-- ============================================================================
CREATE OR ALTER PROCEDURE PA_ReporteValorInventarioPorAlmacen
    @id_almacen INT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        a.id AS ID_Almacen,
        a.nombre AS Nombre_Almacen,
        a.direccion AS Direccion,
        COUNT(DISTINCT r.id) AS Total_Reactivos,
        COUNT(DISTINCT l.id) AS Total_Lotes,
        SUM(ia.stock) AS Total_Unidades,
        SUM(ia.stock * l.precio_unitario) AS Valor_Total_Inventario,
        AVG(l.precio_unitario) AS Precio_Promedio_Unitario,
        MIN(l.precio_unitario) AS Precio_Minimo,
        MAX(l.precio_unitario) AS Precio_Maximo
    FROM
        Almacen a
        LEFT JOIN Inventario_Almacen ia ON a.id = ia.id_almacen
        LEFT JOIN Lote l ON ia.id_lote = l.id AND l.estado = 1
        LEFT JOIN Reactivo r ON l.id_reactivo = r.id
    WHERE
        (@id_almacen IS NULL OR a.id = @id_almacen)
    GROUP BY
        a.id, a.nombre, a.direccion
    ORDER BY
        Valor_Total_Inventario DESC;

    -- Detalle por reactivo en cada almacén
    IF @id_almacen IS NOT NULL
    BEGIN
        SELECT
            r.id AS ID_Reactivo,
            r.nombre AS Nombre_Reactivo,
            m.nombre AS Marca,
            COUNT(DISTINCT l.id) AS Total_Lotes,
            SUM(ia.stock) AS Stock_Total,
            AVG(l.precio_unitario) AS Precio_Promedio,
            SUM(ia.stock * l.precio_unitario) AS Valor_Total
        FROM
            Inventario_Almacen ia
            INNER JOIN Lote l ON ia.id_lote = l.id
            INNER JOIN Reactivo r ON l.id_reactivo = r.id
            LEFT JOIN Marca m ON r.id_marca = m.id
        WHERE
            ia.id_almacen = @id_almacen
            AND l.estado = 1
        GROUP BY
            r.id, r.nombre, m.nombre
        ORDER BY
            Valor_Total DESC;
    END
END
GO

--------------------------------------------------------------------------------
-- INFORMACIÓN SOBRE LOS PROCEDIMIENTOS ALMACENADOS
--------------------------------------------------------------------------------
PRINT '========================================================================';
PRINT 'PROCEDIMIENTOS ALMACENADOS CREADOS EXITOSAMENTE';
PRINT '========================================================================';
PRINT '';
PRINT '1. PA_ConsultarInventarioAlmacenDetallado';
PRINT '   - Consulta detallada del inventario de un almacén';
PRINT '   - Parámetros: @id_almacen, @nombre_reactivo (opcional)';
PRINT '';
PRINT '2. PA_ConsultarReactivoPorAlmacen';
PRINT '   - Consulta disponibilidad de un reactivo en almacenes';
PRINT '   - Parámetros: @id_reactivo, @id_almacen (opcional)';
PRINT '';
PRINT '3. PA_ConsultarLotesProximosExpirar';
PRINT '   - Consulta lotes próximos a expirar';
PRINT '   - Parámetros: @dias_alerta (default 90), @id_almacen (opcional)';
PRINT '';
PRINT '4. PA_ConsultarHistorialMovimientos';
PRINT '   - Consulta historial de movimientos';
PRINT '   - Parámetros: @id_reactivo, @id_almacen, @fecha_inicio, @fecha_fin, @id_tipo_accion (todos opcionales)';
PRINT '';
PRINT '5. PA_ConsultarStockBajo';
PRINT '   - Consulta reactivos con stock bajo';
PRINT '   - Parámetros: @porcentaje_minimo (default 20), @id_almacen (opcional)';
PRINT '';
PRINT '6. PA_ReporteValorInventarioPorAlmacen';
PRINT '   - Genera reporte del valor del inventario';
PRINT '   - Parámetros: @id_almacen (opcional)';
PRINT '';
PRINT '========================================================================';
GO
EXEC PA_ConsultarInventarioAlmacenDetallado @id_almacen = 1;

