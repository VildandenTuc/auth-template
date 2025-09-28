-- Limpiar entrada problemática de flyway_schema_history y actualizar password
-- Esto elimina la referencia conflictiva de la migración V3 anterior

-- Eliminar la entrada problemática de flyway_schema_history si existe
DELETE FROM flyway_schema_history WHERE version = '3';

-- Actualizar password del usuario admin con hash correcto
-- Hash BCrypt válido para 'admin123'
UPDATE users
SET password = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HHWuWWdUL6SupkmqQF2l.'
WHERE username = 'admin' AND id = 1;