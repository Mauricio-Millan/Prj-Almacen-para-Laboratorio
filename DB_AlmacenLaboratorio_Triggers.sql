USE DBRESTALMACENLABORATORIO
GO
-- Bloquear modificaciones en TipoAccion
CREATE TRIGGER TR_BloquearAlteracionTipoAccion
ON TipoAccion
INSTEAD OF UPDATE, DELETE
AS
BEGIN
    RAISERROR('No se permite modificar o eliminar tipos de acción', 16, 1);
    ROLLBACK TRANSACTION;
END;
GO

-- Bloquear modificaciones en Roles
CREATE TRIGGER TR_BloquearModifElimRoles
ON Roles
INSTEAD OF UPDATE, DELETE
AS
BEGIN
    RAISERROR('No se permite modificar o eliminar roles', 16, 1);
    ROLLBACK TRANSACTION;
END;
GO
-- Bloquear modificación directa del inventario
CREATE TRIGGER TR_BloquearModificacionDirectaInventario
ON Inventario_Almacen
INSTEAD OF UPDATE, DELETE, INSERT
AS
BEGIN
    -- Verificar si viene de un proceso autorizado
    IF CONTEXT_INFO() <> 0x4D4F56494D49454E544F41555448 -- "MOVIMIENTOAUTH"
    BEGIN
        RAISERROR('El inventario solo puede modificarse mediante movimientos autorizados', 16, 1);
        ROLLBACK TRANSACTION;
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
            -- DELETE
            DELETE ia
            FROM Inventario_Almacen ia
            INNER JOIN deleted d ON ia.id = d.id;
        END
    END
END;
GO

CREATE or ALTER TRIGGER TR_ValidarYActualizarInventario
ON MovimientoLinea
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Variables para el cursor
    DECLARE @idTipoAccion INT;
    DECLARE @cantidadDelta DECIMAL;
    DECLARE @stockOrigen DECIMAL;
    DECLARE @stockDestino DECIMAL;
    DECLARE @idLote INT;
    DECLARE @idAlmacenOrigen INT;
    DECLARE @idAlmacenDestino INT;
    DECLARE @cantidadInicialLote DECIMAL;
    DECLARE @idMovimientoLinea INT;

    -- Cursor para procesar todas las filas insertadas
    DECLARE cursor_movimientos CURSOR FOR
    SELECT
        i.id,
        m.id_tipo_accion,
        i.cantidad_delta,
        i.id_lote,
        i.id_almacen_origen,
        i.id_almacen_destino
    FROM inserted i
    INNER JOIN Movimiento m ON i.id_movimiento = m.id;

    OPEN cursor_movimientos;
    FETCH NEXT FROM cursor_movimientos INTO @idMovimientoLinea, @idTipoAccion, @cantidadDelta, @idLote, @idAlmacenOrigen, @idAlmacenDestino;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- Obtener cantidad inicial del lote
        SELECT @cantidadInicialLote = cantidad_inicial
        FROM Lote
        WHERE id = @idLote;

        -- Validaciones según tipo de acción

        -- 2. CONSUMO (Salidas desde origen, sin destino)
        IF @idTipoAccion = 2
        BEGIN
            -- Validar stock suficiente en origen
            SELECT @stockOrigen = ISNULL(stock, 0)
            FROM Inventario_Almacen
            WHERE id_almacen = @idAlmacenOrigen AND id_lote = @idLote;

            IF @stockOrigen < ABS(@cantidadDelta)
            BEGIN
                CLOSE cursor_movimientos;
                DEALLOCATE cursor_movimientos;
                RAISERROR('Stock insuficiente en almacén origen para consumo', 16, 1);
                ROLLBACK TRANSACTION;
                RETURN;
            END
        END

        -- 3. TRASLADO (Movimientos entre almacenes: origen -> destino)
        IF @idTipoAccion = 3
        BEGIN
            -- Validar stock suficiente en origen
            SELECT @stockOrigen = ISNULL(stock, 0)
            FROM Inventario_Almacen
            WHERE id_almacen = @idAlmacenOrigen AND id_lote = @idLote;

            IF @stockOrigen < ABS(@cantidadDelta)
            BEGIN
                CLOSE cursor_movimientos;
                DEALLOCATE cursor_movimientos;
                RAISERROR('Stock insuficiente en almacén origen para traslado', 16, 1);
                ROLLBACK TRANSACTION;
                RETURN;
            END
        END

        -- 4. AJUSTE (Correcciones de inventario en el almacén origen)
        IF @idTipoAccion = 4
        BEGIN
            IF @cantidadDelta > 0
            BEGIN
                -- Ajuste positivo: verificar que no exceda cantidad del lote
                SELECT @stockOrigen = ISNULL(stock, 0)
                FROM Inventario_Almacen
                WHERE id_almacen = @idAlmacenOrigen AND id_lote = @idLote;

                IF (@stockOrigen + @cantidadDelta) > @cantidadInicialLote
                BEGIN
                    CLOSE cursor_movimientos;
                    DEALLOCATE cursor_movimientos;
                    RAISERROR('El ajuste excede la cantidad inicial del lote', 16, 1);
                    ROLLBACK TRANSACTION;
                    RETURN;
                END
            END
            ELSE
            BEGIN
                -- Ajuste negativo: verificar stock suficiente
                SELECT @stockOrigen = ISNULL(stock, 0)
                FROM Inventario_Almacen
                WHERE id_almacen = @idAlmacenOrigen AND id_lote = @idLote;

                IF @stockOrigen < ABS(@cantidadDelta)
                BEGIN
                    CLOSE cursor_movimientos;
                    DEALLOCATE cursor_movimientos;
                    RAISERROR('Stock insuficiente para ajuste negativo', 16, 1);
                    ROLLBACK TRANSACTION;
                    RETURN;
                END
            END
        END

        -- Autorizar modificación del inventario
        DECLARE @contexto VARBINARY(128) = 0x4D4F56494D49454E544F41555448;
        SET CONTEXT_INFO @contexto;

        -- Actualizar inventario según tipo de acción

        -- 1. INGRESO/ABASTECIMIENTO (sin origen, solo destino)
        IF @idTipoAccion = 1
        BEGIN
            -- Crear o actualizar stock en almacén destino
            MERGE Inventario_Almacen AS target
            USING (SELECT @idAlmacenDestino AS id_almacen, @idLote AS id_lote, @cantidadDelta AS delta) AS source
            ON (target.id_almacen = source.id_almacen AND target.id_lote = source.id_lote)
            WHEN MATCHED THEN
                UPDATE SET stock = stock + source.delta
            WHEN NOT MATCHED THEN
                INSERT (id_almacen, id_lote, stock)
                VALUES (source.id_almacen, source.id_lote, source.delta);
        END

        -- 2. CONSUMO (solo origen, sin destino - reduce stock)
        IF @idTipoAccion = 2
        BEGIN
            -- Reducir en almacén origen
            UPDATE Inventario_Almacen
            SET stock = stock - ABS(@cantidadDelta)
            WHERE id_almacen = @idAlmacenOrigen AND id_lote = @idLote;
        END

        -- 3. TRASLADO (origen -> destino)
        IF @idTipoAccion = 3
        BEGIN
            -- Reducir en almacén origen
            UPDATE Inventario_Almacen
            SET stock = stock - ABS(@cantidadDelta)
            WHERE id_almacen = @idAlmacenOrigen AND id_lote = @idLote;

            -- Incrementar en almacén destino (crear si no existe)
            MERGE Inventario_Almacen AS target
            USING (SELECT @idAlmacenDestino AS id_almacen, @idLote AS id_lote, ABS(@cantidadDelta) AS delta) AS source
            ON (target.id_almacen = source.id_almacen AND target.id_lote = source.id_lote)
            WHEN MATCHED THEN
                UPDATE SET stock = stock + source.delta
            WHEN NOT MATCHED THEN
                INSERT (id_almacen, id_lote, stock)
                VALUES (source.id_almacen, source.id_lote, source.delta);
        END

        -- 4. AJUSTE (solo en origen - puede ser positivo o negativo)
        IF @idTipoAccion = 4
        BEGIN
            -- Ajustar stock en almacén origen (crear si no existe y es positivo)
            MERGE Inventario_Almacen AS target
            USING (SELECT @idAlmacenOrigen AS id_almacen, @idLote AS id_lote, @cantidadDelta AS delta) AS source
            ON (target.id_almacen = source.id_almacen AND target.id_lote = source.id_lote)
            WHEN MATCHED THEN
                UPDATE SET stock = stock + source.delta
            WHEN NOT MATCHED AND source.delta > 0 THEN
                INSERT (id_almacen, id_lote, stock)
                VALUES (source.id_almacen, source.id_lote, source.delta);
        END

        -- Limpiar contexto
        SET CONTEXT_INFO 0x0;

        -- Siguiente registro
        FETCH NEXT FROM cursor_movimientos INTO @idMovimientoLinea, @idTipoAccion, @cantidadDelta, @idLote, @idAlmacenOrigen, @idAlmacenDestino;
    END

    CLOSE cursor_movimientos;
    DEALLOCATE cursor_movimientos;
END;
GO

SELECT *
FROM sys.triggers;
GO