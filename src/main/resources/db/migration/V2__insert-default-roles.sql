-- Inserción de roles por defecto
-- Auth Template v1.0.0

INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrador del sistema con acceso completo'),
('USER', 'Usuario estándar con permisos básicos'),
('MODERATOR', 'Moderador con permisos intermedios');

-- Usuario administrador por defecto
-- Password: admin123 (debe cambiarse en producción)
-- Hash BCrypt generado para 'admin123'
INSERT INTO users (username, email, password, first_name, last_name) VALUES
('admin', 'admin@template.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVklkC', 'Admin', 'User');

-- Asignar rol ADMIN al usuario admin
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1);