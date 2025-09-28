# 🔐 Auth Template - Sistema de Autenticación JWT

Template reutilizable de autenticación JWT construido con Spring Boot 3.x para proyectos futuros.

## 📋 Características

- ✅ **Autenticación JWT completa** (Access + Refresh tokens)
- ✅ **Gestión de usuarios y roles** con permisos granulares
- ✅ **API REST documentada** con DTOs type-safe
- ✅ **Spring Security configurado** con filtros personalizados
- ✅ **Base de datos MySQL** con migraciones Flyway
- ✅ **Manejo global de excepciones** con respuestas consistentes
- ✅ **Collections Insomnia/Postman** incluidas
- ✅ **Tests unitarios** básicos
- ✅ **Arquitectura en capas** bien definida

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 17 | Lenguaje principal |
| **Spring Boot** | 3.5.6 | Framework principal |
| **Spring Security** | 6.x | Autenticación y autorización |
| **JWT (JJWT)** | 0.12.6 | Tokens de autenticación |
| **MySQL** | 8.x | Base de datos |
| **Flyway** | - | Migraciones de BD |
| **Lombok** | - | Reducción de código boilerplate |
| **Maven** | - | Gestión de dependencias |

## 🏗️ Arquitectura

```
src/main/java/com/vildanden/auth_template/
├── 📁 config/           # Configuraciones (JWT, Security)
├── 📁 controller/       # Controladores REST
├── 📁 dto/             # Data Transfer Objects
│   ├── auth/           # DTOs de autenticación
│   ├── user/           # DTOs de usuarios
│   ├── role/           # DTOs de roles
│   └── common/         # DTOs comunes
├── 📁 entity/          # Entidades JPA
├── 📁 exception/       # Manejo de excepciones
├── 📁 repository/      # Repositorios JPA
├── 📁 security/        # Filtros y servicios JWT
├── 📁 service/         # Lógica de negocio
└── 📁 util/           # Utilidades y mappers
```

## 🚀 Configuración e Instalación

### Prerrequisitos

- ☕ **Java 17+**
- 🗄️ **MySQL 8.0+**
- 📦 **Maven 3.6+**
- 🔧 **IntelliJ IDEA** (recomendado)

### 1. Clonar y Configurar

```bash
# Clonar el proyecto
git clone <repository-url>
cd auth-template

# Crear base de datos
mysql -u root -p
CREATE DATABASE auth_template_db;
```

### 2. Configurar application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_template_db
    username: root
    password: tu_password
```

### 3. Ejecutar Migraciones

```bash
# Ejecutar migraciones Flyway
mvn flyway:migrate

# O desde IntelliJ, ejecutar la aplicación
# Las migraciones se ejecutan automáticamente
```

### 4. Ejecutar la Aplicación

```bash
mvn spring-boot:run

# Disponible en: http://localhost:8080/api
```

## 📡 Endpoints Principales

### 🔓 Públicos (Sin autenticación)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Login de usuario |
| `POST` | `/api/auth/register` | Registro de nuevo usuario |
| `POST` | `/api/auth/refresh` | Renovar access token |
| `GET` | `/api/health` | Estado del sistema |
| `GET` | `/api/info` | Información de la aplicación |

### 🔒 Autenticados (Bearer Token requerido)

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| `GET` | `/api/users/profile` | Info usuario actual | Cualquiera |
| `POST` | `/api/auth/change-password` | Cambiar contraseña | Cualquiera |
| `GET` | `/api/users/profile` | Perfil del usuario | Cualquiera |
| `PUT` | `/api/users/profile` | Actualizar perfil | Cualquiera |

### 👑 Administración (Solo ADMIN)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/users` | Lista usuarios (paginada) |
| `POST` | `/api/users` | Crear usuario |
| `PUT` | `/api/users/{id}` | Actualizar usuario |
| `DELETE` | `/api/users/{id}` | Eliminar usuario |
| `GET` | `/api/roles` | Lista roles |
| `POST` | `/api/roles` | Crear rol |
| `GET` | `/api/admin/stats` | Estadísticas del sistema |

## 🔧 Uso Rápido

### 1. Login Inicial

```bash
# Usuario admin por defecto (cambiar en producción)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@template.com",
      "fullName": "Admin User",
      "roles": ["ADMIN"]
    }
  }
}
```

### 2. Usar Token en Peticiones

```bash
# Obtener información del usuario autenticado
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 3. Registrar Nuevo Usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

## 🗃️ Estructura de Base de Datos

### Tablas Principales

| Tabla | Descripción |
|-------|-------------|
| `users` | Información de usuarios |
| `roles` | Roles del sistema |
| `user_roles` | Relación N:M usuarios-roles |

### Roles por Defecto

- **ADMIN**: Acceso completo al sistema
- **USER**: Usuario estándar con permisos básicos
- **MODERATOR**: Permisos intermedios

### Usuario por Defecto

```
Username: admin
Password: admin123
Email: admin@template.com
Role: ADMIN
```

## 📁 Collections API

El proyecto incluye collections para testing:

- **📄 `auth-template-insomnia.json`** - Collection completa para Insomnia
- **📄 Variables incluidas**: `baseUrl`, `accessToken`, `refreshToken`
- **📋 Casos de uso**: Login, registro, CRUD usuarios/roles, admin

### Importar en Insomnia

1. Abrir Insomnia
2. `Application Menu` → `Data` → `Import Data`
3. Seleccionar `auth-template-insomnia.json`
4. Configurar variables de entorno si es necesario

## 🔍 Testing

### Tests Unitarios

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=AuthServiceTest
```

### Tests de Integración

```bash
# Health check
curl http://localhost:8080/api/health

# Información del sistema
curl http://localhost:8080/api/info
```

## 🛡️ Seguridad

### Configuración JWT

```yaml
jwt:
  secret: "tu-clave-secreta-muy-larga-y-segura"
  expiration: 86400000    # 24 horas
  refresh-expiration: 604800000  # 7 días
```

### Roles y Permisos

```java
// Verificar permisos en controladores
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")

// Verificar en el código
if (userPrincipal.hasRole("ADMIN")) {
    // Lógica para administradores
}
```

## 🚀 Personalización para Nuevos Proyectos

### 1. Cambiar Package Base

```bash
# Cambiar de: com.vildanden.auth_template
# A tu package: com.tuempresa.tuproyecto
```

### 2. Configurar Base de Datos

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tu_base_datos
```

### 3. Personalizar DTOs

- Agregar campos específicos a `UserResponseDTO`
- Crear DTOs adicionales para tu dominio
- Extender `BaseEntity` para nuevas entidades

### 4. Agregar Nuevos Roles

```sql
INSERT INTO roles (name, description) VALUES 
('CLIENTE', 'Cliente del sistema'),
('PROVEEDOR', 'Proveedor de servicios');
```

## 📊 Monitoreo y Logs

### Endpoints de Salud

- `GET /api/health` - Estado general
- `GET /api/admin/stats` - Estadísticas (ADMIN)
- `GET /api/admin/system-info` - Info detallada (ADMIN)

### Configuración de Logs

```yaml
logging:
  level:
    com.vildanden.auth_template: DEBUG
    org.springframework.security: INFO
```

## 🤝 Contribución

1. Fork el proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto es un template de código abierto para uso educativo y comercial.

## 👨‍💻 Autor

**Guido Alfredo Albarracín**
- Desarrollador Backend Java/Spring Boot
- Profesor de Informática y Tecnología
- San Miguel de Tucumán, Argentina

---

## 📋 TODO / Mejoras Futuras

- [ ] Implementar blacklist de tokens para logout seguro
- [ ] Agregar rate limiting para endpoints sensibles
- [ ] Implementar notificaciones por email
- [ ] Agregar métricas con Micrometer/Actuator
- [ ] Dockerizar la aplicación
- [ ] Agregar más tests de integración
- [ ] Implementar cache con Redis
- [ ] Agregar documentación OpenAPI/Swagger