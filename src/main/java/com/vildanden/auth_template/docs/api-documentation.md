# 📋 API Documentation - Auth Template

## Endpoints Reference

### Authentication Endpoints

#### POST /api/auth/login
**Descripción:** Autenticar usuario y obtener tokens JWT

**Request Body:**
```json
{
  "usernameOrEmail": "string",
  "password": "string"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "string",
      "email": "string",
      "fullName": "string",
      "roles": ["ADMIN"]
    }
  }
}
```

**Errors:**
- `401`: Credenciales inválidas
- `400`: Errores de validación

---

#### POST /api/auth/register
**Descripción:** Registrar nuevo usuario

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "data": {
    "message": "Usuario registrado exitosamente",
    "user": {
      "id": 2,
      "username": "string",
      "email": "string",
      "fullName": "string",
      "roles": ["USER"]
    }
  }
}
```

---

### User Management Endpoints

#### GET /api/users/profile
**Descripción:** Obtener perfil del usuario autenticado
**Autenticación:** Bearer Token requerido

**Response (200):**
```json
{
  "success": true,
  "message": "Perfil del usuario",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "firstName": "string",
    "lastName": "string",
    "fullName": "string",
    "enabled": true,
    "roles": [
      {
        "id": 1,
        "name": "ADMIN",
        "description": "string"
      }
    ]
  }
}
```

---

#### GET /api/users (Admin only)
**Descripción:** Obtener lista paginada de usuarios
**Autenticación:** Bearer Token + Role ADMIN

**Query Parameters:**
- `page`: Número de página (default: 0)
- `size`: Tamaño de página (default: 10)
- `sortBy`: Campo de ordenamiento (default: id)
- `sortDir`: Dirección (ASC/DESC, default: ASC)

**Response (200):**
```json
{
  "success": true,
  "message": "Lista de usuarios",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "admin",
        "email": "admin@template.com",
        "firstName": "Admin",
        "lastName": "User",
        "fullName": "Admin User",
        "enabled": true,
        "roles": []
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

---

### Role Management Endpoints

#### GET /api/roles
**Descripción:** Obtener todos los roles
**Autenticación:** Bearer Token + Role ADMIN o MODERATOR

**Response (200):**
```json
{
  "success": true,
  "message": "Lista de roles",
  "data": [
    {
      "id": 1,
      "name": "ADMIN",
      "description": "Administrador del sistema",
      "userCount": 1
    }
  ]
}
```

---

### System Endpoints

#### GET /api/health
**Descripción:** Estado de salud del sistema
**Autenticación:** Público

**Response (200):**
```json
{
  "status": "UP",
  "version": "1.0.0",
  "environment": "dev",
  "timestamp": "2024-12-27T10:00:00",
  "details": {
    "database": "UP",
    "application": "Auth Template API"
  }
}
```

---

## Error Responses

### Estructura de Error Estándar
```json
{
  "success": false,
  "message": "Mensaje descriptivo del error",
  "error": "CODIGO_ERROR",
  "status": 400,
  "path": "/api/endpoint",
  "timestamp": "2024-12-27T10:00:00"
}
```

### Códigos de Error Comunes
- `VALIDATION_ERROR` (400): Errores de validación
- `INVALID_CREDENTIALS` (401): Credenciales incorrectas
- `ACCESS_DENIED` (403): Sin permisos suficientes
- `USER_NOT_FOUND` (404): Usuario no encontrado
- `BUSINESS_ERROR` (400): Error de lógica de negocio

---

## Authentication Flow

1. **Login**: `POST /api/auth/login`
2. **Obtener Access Token**: Guardar `accessToken` del response
3. **Usar Token**: Incluir en header `Authorization: Bearer {accessToken}`
4. **Renovar Token**: `POST /api/auth/refresh` antes que expire
5. **Logout**: Eliminar tokens del cliente

---

## Rate Limiting

- **Login**: 5 intentos por minuto por IP
- **Register**: 3 registros por hora por IP
- **General**: 100 requests por minuto por usuario autenticado

---

## Versioning

- **Current Version**: v1
- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`