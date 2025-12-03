USE DBRESTALMACENLABORATORIO
GO
-- Bloquear modificaciones en TipoAccion
CREATE TRIGGER TR_BloquearAlteracionTipoAccion
ON TipoAccion
INSTEAD OF UPDATE, DELETE
AS
BEGIN
    -- Usar THROW en lugar de RAISERROR + ROLLBACK dentro de triggers
    THROW 50001, N'No se permite modificar o eliminar tipos de acción', 1;
END;
GO

-- Bloquear modificaciones en Roles
CREATE TRIGGER TR_BloquearModifElimRoles
ON Roles
INSTEAD OF UPDATE, DELETE
AS
BEGIN
    THROW 50002, N'No se permite modificar o eliminar roles', 1;
END;
GO
-- Bloquear modificación directa del inventario
-- sql
CREATE TRIGGER TR_BloquearModificacionDirectaInventario
ON Inventario_Almacen
INSTEAD OF UPDATE, DELETE, INSERT
AS
BEGIN
    -- Verificar si viene de un proceso autorizado
    IF CONTEXT_INFO() <> 0x4D4F56494D49454E544F41555448 -- "MOVIMIENTOAUTH"
    BEGIN
        THROW 50003, N'El inventario solo puede modificarse mediante movimientos autorizados', 1;
    END
    ELSE
    BEGIN
        -- Permitir la operación autorizada
        IF EXISTS(SELECT * FROM inserted) AND NOT EXISTS(SELECT * FROM deleted)
        BEGIN
            -- INSERT
            INSERT INTO Inventario_Almacen (id_almacen, id_lote, stock)
            SELECT id_almacen, id_lote, stock FROM inserted;
        END
        ELSE IF EXISTS(SELECT * FROM inserted) AND EXISTS(SELECT * FROM deleted)
        BEGIN
            -- UPDATE
            UPDATE ia
            SET stock = i.stock
            FROM Inventario_Almacen ia
            INNER JOIN inserted i ON ia.id = i.id;
        END
        ELSE
        BEGIN
            -- DELETE seguro: usar WHERE con subconsulta a la tabla `deleted`
            DELETE FROM Inventario_Almacen
            WHERE id IN (SELECT id FROM deleted);
        END
    END
END;
GO


CREATE OR ALTER TRIGGER TR_ValidarYActualizarInventario
ON MovimientoLinea
INSTEAD OF INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- 1) Validar stock suficiente para consumos/traslados/ajustes negativos
    IF EXISTS (
        SELECT 1
        FROM inserted i
        INNER JOIN Movimiento m ON i.id_movimiento = m.id
        LEFT JOIN Inventario_Almacen ia ON ia.id_almacen = i.id_almacen_origen AND ia.id_lote = i.id_lote
        WHERE i.id_almacen_origen IS NOT NULL
          AND m.id_tipo_accion IN (2, 3, 4) -- 2: consumo, 3: traslado, 4: ajuste (cuando es negativo)
          AND i.cantidad_delta < 0
          AND ABS(i.cantidad_delta) > ISNULL(ia.stock, 0)
    )
    BEGIN
        THROW 50011, N'La cantidad a mover supera el stock disponible en el almacén origen.', 1;
        RETURN;
    END

    -- 2) Validar que ajustes positivos no superen la cantidad inicial del lote
    IF EXISTS (
        SELECT 1
        FROM inserted i
        INNER JOIN Movimiento m ON i.id_movimiento = m.id
        INNER JOIN Lote l ON i.id_lote = l.id
        LEFT JOIN Inventario_Almacen ia ON ia.id_lote = i.id_lote AND ia.id_almacen = i.id_almacen_origen
        WHERE m.id_tipo_accion = 4 -- ajuste
          AND i.id_almacen_origen IS NOT NULL
          AND i.cantidad_delta > 0
          AND (ISNULL(ia.stock, 0) + i.cantidad_delta) > l.cantidad_inicial
    )
    BEGIN
        THROW 50012, N'El ajuste positivo no puede superar la cantidad total establecida en el lote.', 1;
        RETURN;
    END

    -- Establecer CONTEXT_INFO para autorizar la modificación en Inventario_Almacen
    DECLARE @context VARBINARY(128) = 0x4D4F56494D49454E544F41555448; -- "MOVIMIENTOAUTH"
    SET CONTEXT_INFO @context;

    -- Insertar las líneas de movimiento en la tabla (acepta múltiples filas)
    INSERT INTO MovimientoLinea (id_movimiento, id_almacen_origen, id_almacen_destino, id_lote, cantidad_delta, Precio_Venta)
    SELECT id_movimiento, id_almacen_origen, id_almacen_destino, id_lote, cantidad_delta, Precio_Venta
    FROM inserted;

    -- 1) INGRESO/ABASTECIMIENTO (tipo 1): sumar stock en almacén destino (crear si no existe)
    MERGE Inventario_Almacen AS target
    USING (
        SELECT i.id_almacen_destino AS id_almacen, i.id_lote, i.cantidad_delta AS delta
        FROM inserted i
        INNER JOIN Movimiento m ON i.id_movimiento = m.id
        WHERE m.id_tipo_accion = 1
          AND i.id_almacen_destino IS NOT NULL
    ) AS source
    ON (target.id_almacen = source.id_almacen AND target.id_lote = source.id_lote)
    WHEN MATCHED THEN
        UPDATE SET target.stock = target.stock + source.delta
    WHEN NOT MATCHED THEN
        INSERT (id_almacen, id_lote, stock)
        VALUES (source.id_almacen, source.id_lote, source.delta);

    -- 2) CONSUMO (tipo 2): restar en almacén origen
    UPDATE ia
    SET ia.stock = ia.stock - ABS(i.cantidad_delta)
    FROM Inventario_Almacen ia
    INNER JOIN inserted i ON ia.id_almacen = i.id_almacen_origen AND ia.id_lote = i.id_lote
    INNER JOIN Movimiento m ON i.id_movimiento = m.id
    WHERE m.id_tipo_accion = 2
      AND i.id_almacen_origen IS NOT NULL;

    -- 3) TRASLADO (tipo 3): restar en origen y sumar en destino
    -- Restar en origen
    UPDATE ia
    SET ia.stock = ia.stock - ABS(i.cantidad_delta)
    FROM Inventario_Almacen ia
    INNER JOIN inserted i ON ia.id_almacen = i.id_almacen_origen AND ia.id_lote = i.id_lote
    INNER JOIN Movimiento m ON i.id_movimiento = m.id
    WHERE m.id_tipo_accion = 3
      AND i.id_almacen_origen IS NOT NULL;

    -- Sumar en destino (crear si no existe)
    MERGE Inventario_Almacen AS target_dest
    USING (
        SELECT i.id_almacen_destino AS id_almacen, i.id_lote, ABS(i.cantidad_delta) AS delta
        FROM inserted i
        INNER JOIN Movimiento m ON i.id_movimiento = m.id
        WHERE m.id_tipo_accion = 3
          AND i.id_almacen_destino IS NOT NULL
    ) AS source_dest
    ON (target_dest.id_almacen = source_dest.id_almacen AND target_dest.id_lote = source_dest.id_lote)
    WHEN MATCHED THEN
        UPDATE SET target_dest.stock = target_dest.stock + source_dest.delta
    WHEN NOT MATCHED THEN
        INSERT (id_almacen, id_lote, stock)
        VALUES (source_dest.id_almacen, source_dest.id_lote, source_dest.delta);

    -- 4) AJUSTE (tipo 4): ajustar en almacén origen (puede ser positivo o negativo)
    -- Para ajustes positivos y negativos: actualizar stock existente o crear nuevo registro
    MERGE Inventario_Almacen AS target_aj
    USING (
        SELECT i.id_almacen_origen AS id_almacen, i.id_lote, i.cantidad_delta AS delta
        FROM inserted i
        INNER JOIN Movimiento m ON i.id_movimiento = m.id
        WHERE m.id_tipo_accion = 4
          AND i.id_almacen_origen IS NOT NULL
    ) AS source_aj
    ON (target_aj.id_almacen = source_aj.id_almacen AND target_aj.id_lote = source_aj.id_lote)
    WHEN MATCHED THEN
        -- Actualizar stock: sumar el delta (puede ser positivo o negativo)
        UPDATE SET target_aj.stock = target_aj.stock + source_aj.delta
    WHEN NOT MATCHED AND source_aj.delta > 0 THEN
        -- Solo crear nuevo registro si el ajuste es positivo
        INSERT (id_almacen, id_lote, stock)
        VALUES (source_aj.id_almacen, source_aj.id_lote, source_aj.delta);

    -- Limpiar CONTEXT_INFO
    SET CONTEXT_INFO 0x00;
END;
GO

SELECT *
FROM sys.triggers;
GO